package com.yy.ppm.produce.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.OddStatusEnum;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.po.MWorkSchedulePO;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailLogPO;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.mapper.*;
import com.yy.ppm.produce.service.TPrdOddWorkPlanService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.mapper.SysDeptMapper;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName 零工申请
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2023年112月12日 11:21:00
 */
@Service
public class TPrdOddWorkPlanServiceImpl implements TPrdOddWorkPlanService {

    @Autowired
    private TPrdOddWorkPlanMapper tPrdOddWorkPlanMapper;
    @Autowired
    private TPrdOddWorkPlanLogMapper tPrdOddWorkPlanLogMapper;
    @Resource
    private Snowflake snowflake;
    @Resource
    private SecurityUtils securityUtils;
    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private PublicMapper publicMapper;
    @Resource
    private TPrdOddWorkPlanDetailMapper tPrdOddWorkPlanDetailMapper;
    @Resource
    private TPrdOddWorkPlanDetailLogMapper tPrdOddWorkPlanDetailLogMapper;
    @Resource
    private TPrdSalaryMapper tPrdSalaryMapper;

    public static final String CLASS_TYPE_01 = "01";
    public static final String CLASS_TYPE_02 = "02";
    public static final String INOUT_TYPE_IN = "I"; // 部门类型：内部
    public static final String INOUT_TYPE_OUT = "O"; // 部门类型：外部
    public static final String DEPT_LJD = "LJD"; // 流机队
    public static final String DEPT_ZXD = "装卸队"; // 装卸队
    public static final String ODD_TYPE_JX = "1"; // 机械
    public static final String ODD_TYPE_RG = "2"; // 人工
    public static final String PAGE_TYPE_APPLY = "1"; // 申请页面
    public static final String YES_NO_YES = "1"; // 是
    public static final String YES_NO_NO = "0"; // 否
    public static final String MAC_TYPE_0016 = "0016"; // 挖掘机
    // TODO 零工状态改成枚举
    public static final String OPERATE_TYPE_CREATE = "新增";
    public static final String OPERATE_TYPE_UPDATE = "修改";
    public static final String OPERATE_TYPE_CONFIRM = "确认";
    public static final String OPERATE_TYPE_CANCEL_CONFIRM = "取消确认";
    public static final String OPERATE_TYPE_FIRST_APPROVE = "一级审核";
    public static final String OPERATE_TYPE_CANCEL_FIRST_APPROVE = "撤销一级审核";
    public static final String OPERATE_TYPE_SECOND_APPROVE = "二级审核";
    public static final String OPERATE_TYPE_CANCEL_SECOND_APPROVE = "撤销二级审核";
    public static final String OPERATE_TYPE_THIRD_APPROVE = "三级审核";
    public static final String OPERATE_TYPE_CANCEL_THIRD_APPROVE = "撤销三级审核";
    public static final String OPERATE_TYPE_REJECT = "驳回";
    public static final String OPERATE_TYPE_ABANDONED = "作废";
    // 计件
    public static final String PIECE_PROJECT_CODE = "50";
    public static final String PIECE_PROJECT_NAME = "劳务";
    public static final String PROCESS_MAIN_CODE = "1015";
    public static final String PROCESS_MAIN_NAME = "零工";
    public static final String PROCESS_DETAIL_CODE = "10150005";
    public static final String PROCESS_DETAIL_NAME = "零工";
    public static final String SALARY_STATUS_CODE = "10";
    public static final String SALARY_STATUS_NAME = "待审核";
    public static final String IS_ODD_YES = "1";
    public static final String SALARY_TYPE_CODE = "98";
    public static final String SALARY_TYPE_NAME = "零工";

    @Override
    public Pages<TPrdOddResultDTO> getList(TPrdOddSearchDTO dto, PageParameter parameter) {
        UserInfo userInfo = securityUtils.getUserInfo();
        SysDeptDTO deptDTO = sysDeptMapper.getById(userInfo.getDeptId());
        if (PAGE_TYPE_APPLY.equals(dto.getPageType())) {
            if (INOUT_TYPE_IN.equals(deptDTO.getInOutType())) { // 内部部门
                Map<Long, SysDeptDTO> ljdDeptIds = getLJDDeptIds();
                Map<Long, SysDeptDTO> zxdDeptIds = getZXDDeptIds();
                // 判断登录用户属于流机队还是装卸队，流机队查机械零工，装卸队查人工零工；
                if (ljdDeptIds.containsKey(deptDTO.getId())) {
                    dto.setDefaultOddType(ODD_TYPE_JX);
                } else if (zxdDeptIds.containsKey(deptDTO.getId())) {
                    dto.setDefaultOddType(ODD_TYPE_RG);
                } else { // 暂：非流机队、非装卸队默认查询所有数据
                    // dto.setDefaultOddType("0");
                }
            } else if (INOUT_TYPE_OUT.equals(deptDTO.getInOutType())) { // 外部公司，只能查看作业单位是自己的
                List<String> deptIds = new ArrayList<>();
                if (deptDTO.getDeptLevel() == 4) {
                    SysDeptDTO parentDeptDTO = sysDeptMapper.getById(deptDTO.getParentId());
                    deptIds.add(parentDeptDTO.getDeptNo());
                } else if (deptDTO.getDeptLevel() == 3) {
                    deptIds.add(deptDTO.getDeptNo());
                } else {
                    deptIds.add(deptDTO.getDeptNo());
                    List<SysDeptDTO> deptList =
                            sysDeptMapper.selectChildrenDeptById("%" + StringUtil.getString(deptDTO.getId()) + "%", null);
                    deptIds.addAll(deptList.stream().map(item -> item.getDeptNo()).collect(Collectors.toList()));
                }
                dto.setDeptNos(deptIds);
            }
        } else { // 审核页面，用户只能看到自己部门申请的零工
            List<Long> deptIds = new ArrayList<>();
            deptIds.add(deptDTO.getId());
            List<SysDeptDTO> deptList =
                    sysDeptMapper.selectChildrenDeptById("%" + StringUtil.getString(deptDTO.getId()) + "%", null);
            deptIds.addAll(deptList.stream().map(item -> item.getId()).collect(Collectors.toList()));
            dto.setDeptIds(deptIds);
        }
        if (dto.getCreateFromDeptId() != null) {
            List<Long> createByDeptIds = new ArrayList<>();
            createByDeptIds.add(dto.getCreateFromDeptId());
            List<SysDeptDTO> deptList =
                    sysDeptMapper.selectChildrenDeptById("%" + StringUtil.getString(dto.getCreateFromDeptId()) + "%", null);
            createByDeptIds.addAll(deptList.stream().map(item -> item.getId()).collect(Collectors.toList()));
            dto.setCreateByDeptIds(createByDeptIds);
        }
        Pages<TPrdOddResultDTO> pageResult = PageHelperUtils.limit(parameter, () -> {
            return tPrdOddWorkPlanMapper.getList(dto);
        });
        // 更新作业开始结束时间
        if (pageResult != null && !CollectionUtils.isEmpty(pageResult.getPages())) {
            List<Long> ids = pageResult.getPages().stream().map(TPrdOddResultDTO::getId).collect(Collectors.toList());
            List<TPrdOddDetailResultDTO> reportDurationList = tPrdOddWorkPlanDetailMapper.getReportDuration(ids);
            Map<Long, TPrdOddDetailResultDTO> reportDurationMap = reportDurationList.stream()
                    .collect(Collectors.toMap(item -> item.getOddPlanId(), item -> item));
            pageResult.getPages().stream().forEach(item -> {
                item.setReportTime(null);
                if (reportDurationMap.containsKey(item.getId())) {
                    item.setReportTime(reportDurationMap.get(item.getId()).getReportTime());
                }
                // 根据状态隐藏审核人-驳回
                hiddenApproveByStatus(item);
            });
        }
        return pageResult;
    }

    private void hiddenApproveByStatus(TPrdOddResultDTO item) {
        if (!StringUtils.isEmpty(item.getStatus())) {
            int status = Integer.parseInt(item.getStatus());
            if (status < 60) {
                item.setThirdApproveBy(null);
                item.setThirdApproveByName(null);
                item.setThirdApproveTime(null);
            }
            if (status < 50) {
                item.setSecondApproveBy(null);
                item.setSecondApproveByName(null);
                item.setSecondApproveTime(null);
            }
            if (status < 40) {
                item.setFirstApproveBy(null);
                item.setFirstApproveByName(null);
                item.setFirstApproveTime(null);
            }
            if (status < 30) {
                item.setConfirmBy(null);
                item.setConfirmByName(null);
                item.setConfirmTime(null);
            }
            if (status < 20) {
                item.setUpdateBy(null);
                item.setUpdateByName(null);
                item.setUpdateTime(null);
            }
        }
    }

    @Override
    public List<TPrdOddLogResultDTO> getLogList(Long id, PageParameter parameter) {
        return tPrdOddWorkPlanMapper.getLogList(id);
    }

    /**
     * 查询流机队所有部门id
     * @return
     */
    private Map<Long, SysDeptDTO> getLJDDeptIds() {
        SysDeptDTO deptDTO = sysDeptMapper.getByDeptNo(DEPT_LJD);
        List<SysDeptDTO> deptList =
                sysDeptMapper.selectChildrenDeptById("%" + StringUtil.getString(deptDTO.getId()) + "%", null);
        Map<Long, SysDeptDTO> map = deptList.stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
        map.put(deptDTO.getId(), deptDTO);
        return map;
    }
    /**
     * 查询装卸队所有部门id
     * @return
     */
    private Map<Long, SysDeptDTO> getZXDDeptIds() {
        SysDeptDTO deptDTO = sysDeptMapper.getByDeptNo(DEPT_ZXD);
        List<SysDeptDTO> deptList =
                sysDeptMapper.selectChildrenDeptById("%" + StringUtil.getString(deptDTO.getId()) + "%", null);
        Map<Long, SysDeptDTO> map = deptList.stream().collect(Collectors.toMap(item -> item.getId(), item -> item));
        map.put(deptDTO.getId(), deptDTO);
        return map;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void doSave(TPrdOddSaveDTO dto) {
        UserInfo userInfo = securityUtils.getUserInfo();
        if (OddStatusEnum.CONFIRM.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("零工申请已确认，不允许修改");
        }
        if (OddStatusEnum.FIRST_APPROVE.getCode().equals(dto.getStatus())
            || OddStatusEnum.SECOND_APPROVE.getCode().equals(dto.getStatus())
            || OddStatusEnum.THIRD_APPROVE.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("零工申请已审批，不允许修改");
        }
        if(("000100010005".equals(dto.getWorkDeptId()))&&!CollectionUtils.isEmpty(dto.getWorkTimeTable())&& StringUtils.isEmpty(dto.getOddUserIds())){
            throw new BusinessRuntimeException("请选择作业人员");
        }
        // 校验设备是否已安排作业计划
        isOverLap(dto);
        if (ODD_TYPE_JX.equals(dto.getOddType())) {
            if (!CollectionUtils.isEmpty(dto.getWorkTimeTable())) {
                List<TPrdOddWorkPlanDetailPO> detailList = dto.getWorkTimeTable();
                if (!StringUtils.isEmpty(dto.getMacId())) {
                    String[] macArr = dto.getMacId().split(",");
                    String[] macNoArr = dto.getMacNo().split(",");
                    for (int i = 0; i < macArr.length; i++) {
                        String macId = macArr[i];
                        for (TPrdOddWorkPlanDetailPO detail : detailList) {
                            if (detail.getReportStartTime() != null || detail.getReportEndTime() != null) {
                                List<TPrdOddDetailResultDTO> oddList =
                                        tPrdOddWorkPlanMapper.getOddByMacTime(macId, dto.getId(),
                                                detail.getReportStartTime(), detail.getReportEndTime());
                                if (!CollectionUtils.isEmpty(oddList)) {
                                    throw new BusinessRuntimeException(dto.getMacTypeName() + "【" + macNoArr[i]
                                            + "】已在零工单[" + oddList.get(0).getOddPlanNo() + "]中相同时间作业");
                                }
                            }
                        }
                    }
                }
            }
            // 如果机械类型是挖掘机，判断台时是否正确
            if (MAC_TYPE_0016.equals(dto.getMacTypeCode())) {
                if (!CollectionUtils.isEmpty(dto.getWorkTimeTable())) {
                    List<TPrdOddWorkPlanDetailPO> detailList = dto.getWorkTimeTable();
                    if (!StringUtils.isEmpty(dto.getMacId())) {
                        String[] macArr = dto.getMacId().split(",");
                        String[] macNoArr = dto.getMacNo().split(",");
                        for (int i = 0; i < macArr.length; i++) {
                            String macId = macArr[i];
                            for (TPrdOddWorkPlanDetailPO detail : detailList) {
                                if (detail.getHourMeterStart() != null || detail.getHourMeterEnd() != null) {
                                    List<TPrdOddDetailResultDTO> oddList =
                                            tPrdOddWorkPlanMapper.getOddByMacHour(macId, dto.getId(),
                                                    detail.getHourMeterStart(), detail.getHourMeterEnd());
                                    if (!CollectionUtils.isEmpty(oddList)) {
                                        throw new BusinessRuntimeException(dto.getMacTypeName() + "【" + macNoArr[i]
                                                + "】已在零工单[" + oddList.get(0).getOddPlanNo() + "]中录入了重复的台时");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        dto.setIsReject(YES_NO_NO);
        if (dto.getWorkDuration() != null && dto.getWorkDuration().compareTo(BigDecimal.ZERO) > 0) {
            dto.setStatus(OddStatusEnum.REPORT.getCode());
            dto.setReportUserId(userInfo.getId());
            dto.setReportUserName(userInfo.getUserName());
            dto.setReportTime(new Date());
        } else {
            dto.setReportUserId(null);
            dto.setReportUserName(null);
            dto.setReportTime(null);
            dto.setStatus(OddStatusEnum.APPLY.getCode());
        }
        Long operateId = snowflake.nextId();
        String operateType = OPERATE_TYPE_CREATE;
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            dto.setOddPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.ODD_PLAN, null));
            dto.setCreateFromDept(userInfo.getDeptId());
            dto.setCreateFromDeptName(userInfo.getDeptName());
            tPrdOddWorkPlanMapper.insert(dto);
        } else {
            operateType = OPERATE_TYPE_UPDATE;
            insertBeforeLog(dto.getId(), operateId, operateType);
            TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(dto.getId());
            if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请已作废，不允许操作");
            }
            tPrdOddWorkPlanMapper.update(dto);
        }
        // 更新计划详情
        dto.getWorkTimeTable().stream().forEach(item -> {
            item.setOddPlanId(dto.getId());
            item.setId(snowflake.nextId());
        });
        tPrdOddWorkPlanDetailMapper.deleteByOddPlanId(dto.getId());
        if (!CollectionUtils.isEmpty(dto.getWorkTimeTable())) {
            tPrdOddWorkPlanDetailMapper.insert(dto.getWorkTimeTable());
        }
        insertAfterLog(dto.getId(), operateId, operateType);
    }

    @Override
    public void confirm(TPrdOddSaveDTO dto) {
        if (dto.getId() != null) {
            TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(dto.getId());
            if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请已作废，不允许操作");
            }
        }
        if (OddStatusEnum.CONFIRM.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("零工申请已确认");
        }
        if (!OddStatusEnum.REPORT.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("零工申请未处于填报状态，无法确认");
        }
        if (!OddStatusEnum.REPORT.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("零工申请未处于填报状态，无法确认");
        }
        dto.setStatus(OddStatusEnum.CONFIRM.getCode());
        tPrdOddWorkPlanMapper.updateConfirm(dto);
        insertAfterLog(dto.getId(), snowflake.nextId(), OPERATE_TYPE_CONFIRM);
    }

    @Override
    public void cancelConfirm(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选中要取消的零工作业单");
        }
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(id);
        if (detail == null) {
            throw new BusinessRuntimeException("零工作业单不存在");
        }
        if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请已作废，不允许操作");
        }
        if (!OddStatusEnum.CONFIRM.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工作业单未确认或已审核，无法取消");
        }
        TPrdOddSaveDTO dto = new TPrdOddSaveDTO();
        dto.setId(detail.getId());
        dto.setStatus(OddStatusEnum.REPORT.getCode());
        tPrdOddWorkPlanMapper.cancelConfirm(dto);
        insertAfterLog(dto.getId(), snowflake.nextId(), OPERATE_TYPE_CANCEL_CONFIRM);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void firstApprove(TPrdOddSaveDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessRuntimeException("请选中要审核的零工作业单");
        }
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(dto.getId());
        if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请已作废，不允许操作");
        }
        if (!OddStatusEnum.CONFIRM.getCode().equals(dto.getStatus())) {
            throw new BusinessRuntimeException("零工申请未处于确认状态，无法审批");
        }
        // 校验设备是否已安排作业计划
        isOverLap(dto);
        if (ODD_TYPE_JX.equals(dto.getOddType())) {
            if (!CollectionUtils.isEmpty(dto.getWorkTimeTable())) {
                List<TPrdOddWorkPlanDetailPO> detailList = dto.getWorkTimeTable();
                if (!StringUtils.isEmpty(dto.getMacId())) {
                    String[] macArr = dto.getMacId().split(",");
                    String[] macNoArr = dto.getMacNo().split(",");
                    for (int i = 0; i < macArr.length; i++) {
                        String macId = macArr[i];
                        for (TPrdOddWorkPlanDetailPO detailPo : detailList) {
                            if (detailPo.getReportStartTime() != null || detailPo.getReportEndTime() != null) {
                                List<TPrdOddDetailResultDTO> oddList =
                                        tPrdOddWorkPlanMapper.getOddByMacTime(macId, dto.getId(),
                                                detailPo.getReportStartTime(), detailPo.getReportEndTime());
                                if (!CollectionUtils.isEmpty(oddList)) {
                                    throw new BusinessRuntimeException(dto.getMacTypeName() + "【" + macNoArr[i]
                                            + "】已在零工单[" + oddList.get(0).getOddPlanNo() + "]中相同时间作业");
                                }
                            }
                        }
                    }
                }
            }
        }
        // 如果机械类型是挖掘机，判断台时是否正确
        if (MAC_TYPE_0016.equals(dto.getMacTypeCode())) {
            if (!CollectionUtils.isEmpty(dto.getWorkTimeTable())) {
                List<TPrdOddWorkPlanDetailPO> detailList = dto.getWorkTimeTable();
                if (!StringUtils.isEmpty(dto.getMacId())) {
                    String[] macArr = dto.getMacId().split(",");
                    String[] macNoArr = dto.getMacNo().split(",");
                    for (int i = 0; i < macArr.length; i++) {
                        String macId = macArr[i];
                        for (TPrdOddWorkPlanDetailPO detailPo : detailList) {
                            if (detailPo.getHourMeterStart() != null || detailPo.getHourMeterEnd() != null) {
                                List<TPrdOddDetailResultDTO> oddList =
                                        tPrdOddWorkPlanMapper.getOddByMacHour(macId, dto.getId(),
                                                detailPo.getHourMeterStart(), detailPo.getHourMeterEnd());
                                if (!CollectionUtils.isEmpty(oddList)) {
                                    throw new BusinessRuntimeException(dto.getMacTypeName() + "【" + macNoArr[i]
                                            + "】已在零工单[" + oddList.get(0).getOddPlanNo() + "]中录入了重复的台时");
                                }
                            }
                        }
                    }
                }
            }
        }
        Long operateId = snowflake.nextId();
        insertBeforeLog(dto.getId(), operateId, OPERATE_TYPE_FIRST_APPROVE);
        dto.setStatus(OddStatusEnum.FIRST_APPROVE.getCode());
        dto.setIsReject(YES_NO_NO);
        tPrdOddWorkPlanMapper.updateFirstApprove(dto);
        // 更新计划详情
        dto.getWorkTimeTable().stream().forEach(item -> {
            item.setOddPlanId(dto.getId());
            item.setId(snowflake.nextId());
        });
        tPrdOddWorkPlanDetailMapper.deleteByOddPlanId(dto.getId());
        if (!CollectionUtils.isEmpty(dto.getWorkTimeTable())) {
            tPrdOddWorkPlanDetailMapper.insert(dto.getWorkTimeTable());
        }
        insertAfterLog(dto.getId(), operateId, OPERATE_TYPE_FIRST_APPROVE);
    }

    /**
     * 插入修改前日志
     * @param oddId 零工id
     * @param operateId 操作id
     * @param operateType 操作类型
     */
    private void insertBeforeLog(Long oddId, Long operateId, String operateType) {
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(oddId);
        TPrdOddLogSaveDTO logBeforeDto = new TPrdOddLogSaveDTO();
        BeanUtils.copyProperties(detail, logBeforeDto);
        logBeforeDto.setId(snowflake.nextId());
        logBeforeDto.setOperateId(operateId);
        logBeforeDto.setOddId(detail.getId());
        logBeforeDto.setOperateType(operateType);
        logBeforeDto.setLogType(YES_NO_NO);
        tPrdOddWorkPlanLogMapper.insert(logBeforeDto);
        List<TPrdOddWorkPlanDetailPO> detailList = tPrdOddWorkPlanDetailMapper.getList(detail.getId());
        List<TPrdOddWorkPlanDetailLogPO> logList = new ArrayList<>(detailList.size());
        detailList.stream().forEach(item -> {
            TPrdOddWorkPlanDetailLogPO po = new TPrdOddWorkPlanDetailLogPO();
            BeanUtils.copyProperties(item, po);
            po.setId(snowflake.nextId());
            po.setOperateId(operateId);
            po.setLogType(YES_NO_NO);
            po.setParentId(logBeforeDto.getId());
            logList.add(po);
        });
        if (!CollectionUtils.isEmpty(logList)) {
            tPrdOddWorkPlanDetailLogMapper.insert(logList);
        }
    }
    /**
     * 插入修改后日志
     * @param oddId 零工id
     * @param operateId 操作id
     * @param operateType 操作类型
     */
    private void insertAfterLog(Long oddId, Long operateId, String operateType) {
        UserInfo userInfo = securityUtils.getUserInfo();
        TPrdOddResultDTO afterDetail = tPrdOddWorkPlanMapper.getDetail(oddId);
        TPrdOddLogSaveDTO logAfterDto = new TPrdOddLogSaveDTO();
        BeanUtils.copyProperties(afterDetail, logAfterDto);
        logAfterDto.setId(snowflake.nextId());
        logAfterDto.setOperateId(operateId);
        logAfterDto.setOddId(oddId);
        logAfterDto.setOperateType(operateType);
        logAfterDto.setLogType(YES_NO_YES);
        tPrdOddWorkPlanLogMapper.insert(logAfterDto);
        List<TPrdOddWorkPlanDetailPO> detailList = tPrdOddWorkPlanDetailMapper.getList(oddId);
        List<TPrdOddWorkPlanDetailLogPO> logList = new ArrayList<>(detailList.size());
        detailList.stream().forEach(item -> {
            TPrdOddWorkPlanDetailLogPO po = new TPrdOddWorkPlanDetailLogPO();
            BeanUtils.copyProperties(item, po);
            po.setId(snowflake.nextId());
            po.setOperateId(operateId);
            po.setLogType(YES_NO_YES);
            po.setParentId(logAfterDto.getId());
            logList.add(po);
        });
        if (!CollectionUtils.isEmpty(logList)) {
            tPrdOddWorkPlanDetailLogMapper.insert(logList);
        }
    }

    @Override
    public void cancelFirstApprove(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("请选中要取消的零工作业单");
        }
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(id);
        if (detail == null) {
            throw new BusinessRuntimeException("零工作业单不存在");
        }
        if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请已作废，不允许操作");
        }
        if (!OddStatusEnum.FIRST_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工作业单未审核或已二级审核，无法取消");
        }
        TPrdOddSaveDTO dto = new TPrdOddSaveDTO();
        dto.setId(detail.getId());
        dto.setStatus(OddStatusEnum.CONFIRM.getCode());
        tPrdOddWorkPlanMapper.cancelFirstApprove(dto);
        insertAfterLog(dto.getId(), snowflake.nextId(), OPERATE_TYPE_CANCEL_FIRST_APPROVE);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void secondApprove(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessRuntimeException("请选中要审核的零工作业单");
        }
        ids.stream().forEach(item -> {
            TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(item);
            if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请已作废，不允许操作");
            }
            if (!OddStatusEnum.FIRST_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请状态异常，无法审批");
            }
        });
        TPrdOddSaveDTO dto = new TPrdOddSaveDTO();
        dto.setStatus(OddStatusEnum.SECOND_APPROVE.getCode());
        dto.setIds(ids);
        tPrdOddWorkPlanMapper.updateSecondApprove(dto);
        ids.stream().forEach(item -> {
            insertAfterLog(item, snowflake.nextId(), OPERATE_TYPE_SECOND_APPROVE);
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelSecondApprove(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessRuntimeException("请选中要取消的零工作业单");
        }
        ids.stream().forEach(item -> {
            TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(item);
            if (detail == null) {
                throw new BusinessRuntimeException("零工作业单不存在");
            }
            if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请已作废，不允许操作");
            }
            if (!OddStatusEnum.SECOND_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工作业单未二级审核，无法取消");
            }
        });
        TPrdOddSaveDTO dto = new TPrdOddSaveDTO();
        dto.setStatus(OddStatusEnum.FIRST_APPROVE.getCode());
        dto.setIds(ids);
        tPrdOddWorkPlanMapper.cancelSecondApprove(dto);
        ids.stream().forEach(item -> {
            insertAfterLog(item, snowflake.nextId(), OPERATE_TYPE_CANCEL_SECOND_APPROVE);
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean deleteById(Long id) {
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(id);
        Long loginUserId = securityUtils.getLoginUserId();
        if (detail == null) {
            throw new BusinessRuntimeException("零工作业单不存在");
        }
        if (!loginUserId.equals(detail.getCreateBy())) {
            throw new BusinessRuntimeException("零工作业单非本人创建，无法删除");
        }
        if (OddStatusEnum.CONFIRM.getCode().equals(detail.getStatus())
            || OddStatusEnum.FIRST_APPROVE.getCode().equals(detail.getStatus())
            || OddStatusEnum.SECOND_APPROVE.getCode().equals(detail.getStatus())
            || OddStatusEnum.THIRD_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工作业单已审批，无法删除");
        }
        tPrdOddWorkPlanDetailMapper.deleteByOddPlanId(id);
        return tPrdOddWorkPlanMapper.deleteById(id) == 1;
    }

    @Override
    public List<SysDeptDTO> getDeptByType(Integer level, String type) {
        return tPrdOddWorkPlanMapper.getDeptByType(level, type);
    }

    @Override
    public TPrdOddResultDTO getDetail(Long id) {
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(id);
        detail.setWorkTimeTable(tPrdOddWorkPlanDetailMapper.getList(detail.getId()));
        return detail;
    }

    @Override
    public void reject(TPrdOddSaveDTO dto) {
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(dto.getId());
        if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请已作废，不允许操作");
        }
        if (OddStatusEnum.APPLY.getCode().equals(detail.getStatus())
            || OddStatusEnum.REPORT.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请未确认，无法驳回");
        }
        if (OddStatusEnum.THIRD_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请已三级审批，无法驳回");
        }
        if (OddStatusEnum.FIRST_APPROVE.getCode().equals(detail.getStatus())
            || OddStatusEnum.SECOND_APPROVE.getCode().equals(detail.getStatus())) {
            dto.setStatus(OddStatusEnum.CONFIRM.getCode());
        } else {
            dto.setStatus(OddStatusEnum.APPLY.getCode());
        }
        dto.setIsReject(YES_NO_YES);
        tPrdOddWorkPlanMapper.updateReject(dto);
        insertAfterLog(dto.getId(), snowflake.nextId(), OPERATE_TYPE_REJECT);
    }

    @Override
    public void abandoned(TPrdOddSaveDTO dto) {
        TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(dto.getId());
        if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请已作废，不允许操作");
        }
        if (OddStatusEnum.THIRD_APPROVE.getCode().equals(detail.getStatus())) {
            throw new BusinessRuntimeException("零工申请已三级审批，无法作废");
        }
        dto.setStatus(OddStatusEnum.ABANDONED_APPROVE.getCode());
        tPrdOddWorkPlanMapper.updateAbandoned(dto);
        insertAfterLog(dto.getId(), snowflake.nextId(), OPERATE_TYPE_ABANDONED);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void thirdApprove(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessRuntimeException("请选中要审核的零工作业单");
        }
        List<TPrdOddResultDTO> oddList = tPrdOddWorkPlanMapper.getListByIds(ids);
        Map<Long, TPrdOddResultDTO> oddMap = oddList.stream()
                .collect(Collectors.toMap(item -> item.getId(), item -> item));
        List<String> deptNos = oddList.stream().map(TPrdOddResultDTO::getWorkDeptId).collect(Collectors.toList());
        List<SysDeptDTO> byDeptNos = sysDeptMapper.getByDeptNos(deptNos);
        Map<String, SysDeptDTO> deptMap = byDeptNos.stream().collect(Collectors.toMap(item -> item.getDeptNo(), item -> item));
        ids.stream().forEach(item -> {
            TPrdOddResultDTO detail = oddMap.get(item);
            if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请已作废，不允许操作");
            }
            if (OddStatusEnum.THIRD_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请已审核，请勿重复审核");
            }
            if (!OddStatusEnum.SECOND_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请未二级审核，无法三级审核");
            }
            if(detail.getWorkDeptName().equals("固机队") && StringUtils.isEmpty(detail.getOddUserIds())){
                throw new BusinessRuntimeException("作业部门是固机队，但未查询到人员信息");
            }
            // 内部部门分配工时
            if (deptMap.containsKey(detail.getWorkDeptId())) {
                SysDeptDTO deptDTO = deptMap.get(detail.getWorkDeptId());
                if (INOUT_TYPE_IN.equals(deptDTO.getInOutType())) {
                    BigDecimal currHour = BigDecimal.ZERO;
                    if(detail.getWorkDeptName().equals("固机队")){
                            currHour = detail.getWorkHours().divide(BigDecimal.valueOf(detail.getWorkerAmount()), 2, BigDecimal.ROUND_HALF_UP);

                        String[] split = detail.getOddUserIds().split(",");
                        String deptId = tPrdOddWorkPlanMapper.getDeptIdByDeptNo(detail.getWorkDeptId());
                        List<UserInfoDTO> listUser = tPrdOddWorkPlanMapper.getUserInfoByUserIds(deptId);
                        Map<Long, UserInfoDTO> userInfoMap = listUser.stream().collect(Collectors.toMap(UserInfoDTO::getId, Function.identity()));
                        for (String s : split) {
                            DeptDTO dept = tPrdOddWorkPlanMapper.getDeptByUserId(s);
                            UserInfoDTO userInfo = userInfoMap.get(Long.parseLong(s));
                            List<TPrdSalaryPO> salaryList = new ArrayList();
                            TPrdSalaryPO salaryPO = new TPrdSalaryPO();
                            salaryPO.setId(snowflake.nextId());
                            salaryPO.setCompanyId(userInfo.getDeptId()); // sql映射companyId->deptId
                            salaryPO.setCompanyName(userInfo.getDeptName()); // sql映射companyName->deptName
                            salaryPO.setWorkDate(detail.getClassDate());
                            salaryPO.setClassCode(detail.getClassType());
                            salaryPO.setClassName("01".equals(detail.getClassType())?"白班":("02".equals(detail.getClassType())?"夜班":""));
                            salaryPO.setPieceProjectCode(PIECE_PROJECT_CODE);
                            salaryPO.setPieceProjectName(PIECE_PROJECT_NAME);
                            salaryPO.setProcessDetailCode(PROCESS_DETAIL_CODE);
                            salaryPO.setProcessDetailName(PROCESS_DETAIL_NAME);
                            salaryPO.setDeptId(dept.getDeptId());
                            salaryPO.setDeptName(dept.getDeptName());
                            salaryPO.setUserBy(Long.parseLong(s));
                            salaryPO.setUserByName(userInfo.getUserName());
                            salaryPO.setCoefficient(100);
                            salaryPO.setTon(currHour);
                            salaryPO.setSalaryStatusCode(SALARY_STATUS_CODE);
                            salaryPO.setSalaryStatusName(SALARY_STATUS_NAME);
                            salaryPO.setSalaryTypeCode(SALARY_TYPE_CODE);
                            salaryPO.setSalaryTypeName(SALARY_TYPE_NAME);
                            salaryPO.setProcessCode(PROCESS_MAIN_CODE);
                            salaryPO.setProcessName(PROCESS_MAIN_NAME);
                            salaryPO.setOddPlanId(item);
                            salaryPO.setIsOdd(IS_ODD_YES);
                            salaryList.add(salaryPO);
                            tPrdSalaryMapper.insertSalary(salaryList);
                        }

                    }
                    else {
                        // 根据班次日期查询考勤
                        List<TOddWorkPlanAttendanceDTO> tOddWorkPlanAttendanceDTOS
                                = tPrdOddWorkPlanMapper.queryAttendanceByOdd(detail.getClassType(), detail.getWorkDeptId(),
                                detail.getClassDate());

                        if(CollectionUtils.isEmpty(tOddWorkPlanAttendanceDTOS)){
                            throw new BusinessRuntimeException(detail.getWorkDeptName()+"在"+detail.getClassDate()+":"+detail.getClassType()+"没有出勤点名信息");
                        }
                        if (!CollectionUtils.isEmpty(tOddWorkPlanAttendanceDTOS)) {
                            // 按照系数分配工时
                            BigDecimal sum = BigDecimal.ZERO;
                            for (TOddWorkPlanAttendanceDTO v1 : tOddWorkPlanAttendanceDTOS) {
                                sum = sum.add(BigDecimal.valueOf(v1.getCoefficient()));
                            }
                            List<TPrdSalaryPO> salaryList = new ArrayList();
                            for (TOddWorkPlanAttendanceDTO v2 : tOddWorkPlanAttendanceDTOS) {
                                 currHour = detail.getWorkHours().multiply(BigDecimal.valueOf(v2.getCoefficient()))
                                        .divide(sum, 2, BigDecimal.ROUND_HALF_UP);
                                TPrdSalaryPO salaryPO = new TPrdSalaryPO();
                                salaryPO.setId(snowflake.nextId());
                                salaryPO.setCompanyId(v2.getCompanyId());
                                salaryPO.setCompanyName(v2.getCompanyName());
                                salaryPO.setWorkDate(v2.getWorkDate());
                                salaryPO.setClassCode(v2.getClassCode());
                                salaryPO.setClassName(v2.getClassName());
                                salaryPO.setPieceProjectCode(PIECE_PROJECT_CODE);
                                salaryPO.setPieceProjectName(PIECE_PROJECT_NAME);
                                salaryPO.setProcessDetailCode(PROCESS_DETAIL_CODE);
                                salaryPO.setProcessDetailName(PROCESS_DETAIL_NAME);
                                salaryPO.setDeptId(v2.getDeptId());
                                salaryPO.setDeptName(v2.getDeptName());
                                salaryPO.setUserBy(v2.getUserId());
                                salaryPO.setUserByName(v2.getUserName());
                                salaryPO.setCoefficient(v2.getCoefficient());
                                salaryPO.setTon(currHour);
                                salaryPO.setSalaryStatusCode(SALARY_STATUS_CODE);
                                salaryPO.setSalaryStatusName(SALARY_STATUS_NAME);
                                salaryPO.setSalaryTypeCode(SALARY_TYPE_CODE);
                                salaryPO.setSalaryTypeName(SALARY_TYPE_NAME);
                                salaryPO.setProcessCode(PROCESS_MAIN_CODE);
                                salaryPO.setProcessName(PROCESS_MAIN_NAME);
                                salaryPO.setOddPlanId(item);
                                salaryPO.setIsOdd(IS_ODD_YES);
                                salaryList.add(salaryPO);
                            }
                            tPrdSalaryMapper.insertSalary(salaryList);
                        }
                    }

                }
            } else {
                throw new BusinessRuntimeException("未找到作业部门");
            }
        });
        TPrdOddSaveDTO dto = new TPrdOddSaveDTO();
        dto.setStatus(OddStatusEnum.THIRD_APPROVE.getCode());
        dto.setIds(ids);
        tPrdOddWorkPlanMapper.updateThirdApprove(dto);
        ids.stream().forEach(item -> {
            insertAfterLog(item, snowflake.nextId(), OPERATE_TYPE_THIRD_APPROVE);
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelThirdApprove(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessRuntimeException("请选中要取消的零工作业单");
        }
        ids.stream().forEach(item -> {
            TPrdOddResultDTO detail = tPrdOddWorkPlanMapper.getDetail(item);
            if (detail == null) {
                throw new BusinessRuntimeException("零工作业单不存在");
            }
            if (OddStatusEnum.ABANDONED_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工申请已作废，不允许操作");
            }
            if (!OddStatusEnum.THIRD_APPROVE.getCode().equals(detail.getStatus())) {
                throw new BusinessRuntimeException("零工作业单未三级审批，无法取消");
            }
        });
        List<TPrdSalaryPO> salaryPOList = tPrdSalaryMapper.queryByOddId(ids);
        if (!CollectionUtils.isEmpty(salaryPOList)) {
            throw new BusinessRuntimeException("零工计件已审核，无法取消");
        }
        tPrdSalaryMapper.deleteByOddId(ids);
        TPrdOddSaveDTO dto = new TPrdOddSaveDTO();
        dto.setStatus(OddStatusEnum.SECOND_APPROVE.getCode());
        dto.setIds(ids);
        tPrdOddWorkPlanMapper.cancelThirdApprove(dto);
        ids.stream().forEach(item -> {
            insertAfterLog(item, snowflake.nextId(), OPERATE_TYPE_CANCEL_THIRD_APPROVE);
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void autoOddPlanNo() {
        TPrdOddSearchDTO dto = new TPrdOddSearchDTO();
        List<TPrdOddResultDTO> list = tPrdOddWorkPlanMapper.getAllList(dto);
        list.stream().forEach(item -> {
            TPrdOddSaveDTO saveDTO = new TPrdOddSaveDTO();
            saveDTO.setId(item.getId());
            saveDTO.setOddPlanNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.ODD_PLAN, null));
            tPrdOddWorkPlanMapper.updateOddPlanNo(saveDTO);
        });
    }

    /**
     * 判断填报时间是否存在重叠,内部部门判断是否跨班次并设置班次
     * @param dto
     */
    private void isOverLap(TPrdOddSaveDTO dto) {
        List<TPrdOddWorkPlanDetailPO> detailList = dto.getWorkTimeTable();
        SysDeptDTO deptDTO = sysDeptMapper.getOneByDeptNo(dto.getWorkDeptId());
        if (!CollectionUtils.isEmpty(detailList)) {
            // 内部部门校验填报时间是否跨班次，并判断班次是白班还是夜班，并记录班次日期
            if (INOUT_TYPE_IN.equals(deptDTO.getInOutType())) {
                String classType = "";
                // 取最早填报开始时间和最晚填报结束时间
                detailList.sort(new Comparator<TPrdOddWorkPlanDetailPO>() {
                    @Override
                    public int compare(TPrdOddWorkPlanDetailPO o1, TPrdOddWorkPlanDetailPO o2) {
                        return o1.getReportStartTime().compareTo(o2.getReportStartTime());
                    }
                });
                // 最早的开始时间
                TPrdOddWorkPlanDetailPO startTimePO = detailList.get(0);
                detailList.sort(new Comparator<TPrdOddWorkPlanDetailPO>() {
                    @Override
                    public int compare(TPrdOddWorkPlanDetailPO o1, TPrdOddWorkPlanDetailPO o2) {
                        return o2.getReportEndTime().compareTo(o1.getReportEndTime());
                    }
                });
                // 最晚的结束时间
                TPrdOddWorkPlanDetailPO endTimePO = detailList.get(0);
                if (startTimePO != null && endTimePO != null) {
                    List<MWorkSchedulePO> mWorkScheduleList = publicMapper.listWorkSchedule();
                    MWorkSchedulePO endItem = mWorkScheduleList.stream()
                            .filter(v1 -> CLASS_TYPE_02.equals(v1.getWorkScheduleCode())).findFirst().get();
                    int addDayNum = addDayNum(startTimePO.getReportStartTime(), endItem);
                    mWorkScheduleList.stream().forEach(item -> {
                        Date startDate = DateUtils.mergeDateTime(startTimePO.getReportStartTime(), item.getStartTime());
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(startDate);
                        calendar1.add(Calendar.DAY_OF_MONTH, addDayNum + Integer.parseInt(item.getStartDayType()) - 1);
                        Date currStartDate = calendar1.getTime();
                        Date endDate = DateUtils.mergeDateTime(startTimePO.getReportStartTime(), item.getEndTime());
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.setTime(endDate);
                        calendar2.add(Calendar.DAY_OF_MONTH, addDayNum + Integer.parseInt(item.getEndDayType()) - 1);
                        Date currEndDate = calendar2.getTime();
                        if ( !((startTimePO.getReportStartTime().compareTo(currStartDate) >= 0
                            && endTimePO.getReportEndTime().compareTo(currEndDate) <= 0)
                            || (startTimePO.getReportStartTime().compareTo(currStartDate) < 0
                                && endTimePO.getReportEndTime().compareTo(currStartDate) <= 0)
                            || (startTimePO.getReportStartTime().compareTo(currEndDate) >= 0
                                && endTimePO.getReportEndTime().compareTo(currEndDate) > 0))) {
                            throw new BusinessRuntimeException("零工填报时间不能跨班次，请确认");
                        }
                        if (startTimePO.getReportStartTime().compareTo(currStartDate) >= 0
                                && endTimePO.getReportEndTime().compareTo(currEndDate) <= 0) {
                            dto.setClassType(item.getWorkScheduleCode());
                        }
                    });
                    if (StringUtils.isEmpty(dto.getClassType())) {
                        throw new BusinessRuntimeException("零工填报时间不能跨班次，请确认");
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTimePO.getReportStartTime());
                    calendar.add(Calendar.DAY_OF_MONTH, addDayNum);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    dto.setClassDate(calendar.getTime());
                }
            }
            for (int index1 = 0; index1 < detailList.size(); index1++) {
                for (int index2 = 0; index2 < detailList.size(); index2++) {
                    TPrdOddWorkPlanDetailPO po1 = detailList.get(index1);
                    TPrdOddWorkPlanDetailPO po2 = detailList.get(index2);
                    if (po1.getReportStartTime() != null && po1.getReportEndTime() != null) {
                        if (po1.getReportStartTime().after(po1.getReportEndTime())) {
                            throw new BusinessRuntimeException("填报开始时间不能晚于结束时间，请确认");
                        }
                    }
                    if (po1.getHourMeterStart() != null && po1.getHourMeterEnd() != null) {
                        if (po1.getHourMeterStart().compareTo(po1.getHourMeterEnd()) > 0) {
                            throw new BusinessRuntimeException("台时开始值不能大于台时结束值，请确认");
                        }
                    }
                    if (index1 == index2) {
                        continue;
                    }
                    if (po1.getReportStartTime() != null && po1.getReportEndTime() != null
                        && po2.getReportStartTime() != null && po2.getReportEndTime() != null) {
                        if (po1.getReportStartTime().after(po2.getReportStartTime())
                                && po1.getReportStartTime().before(po2.getReportEndTime())) {
                            throw new BusinessRuntimeException("填报时间存在重叠，请确认");
                        }
                        if (po1.getReportEndTime().after(po2.getReportStartTime())
                                && po1.getReportEndTime().before(po2.getReportEndTime())) {
                            throw new BusinessRuntimeException("填报时间存在重叠，请确认");
                        }
                        if (po1.getReportStartTime().before(po2.getReportStartTime())
                                && po1.getReportEndTime().after(po2.getReportEndTime())) {
                            throw new BusinessRuntimeException("填报时间存在重叠，请确认");
                        }
                    }
                    if (po1.getHourMeterStart() != null && po1.getHourMeterEnd() != null
                            && po2.getHourMeterStart() != null && po2.getHourMeterEnd() != null) {
                        if (po1.getHourMeterStart().compareTo(po2.getHourMeterStart()) > 0
                                && po1.getHourMeterStart().compareTo(po2.getHourMeterEnd()) < 0) {
                            throw new BusinessRuntimeException("台时存在重叠，请确认");
                        }
                        if (po1.getHourMeterEnd().compareTo(po2.getHourMeterStart()) > 0
                                && po1.getHourMeterEnd().compareTo(po2.getHourMeterEnd()) < 0) {
                            throw new BusinessRuntimeException("台时存在重叠，请确认");
                        }
                        if (po1.getHourMeterStart().compareTo(po2.getHourMeterStart()) < 0
                                && po1.getHourMeterEnd().compareTo(po2.getHourMeterEnd()) > 0) {
                            throw new BusinessRuntimeException("台时存在重叠，请确认");
                        }
                    }
                }
            }

        }
    }
    private int addDayNum (Date date, MWorkSchedulePO endItem) {
        LocalDateTime currDateTime = DateUtils.dateToLocalDateTime(date);
        LocalTime currTime = currDateTime.toLocalTime();
        LocalDateTime endDateTime = DateUtils.dateToLocalDateTime(endItem.getEndTime());
        LocalTime endTime = endDateTime.toLocalTime();
        if (currTime.isBefore(endTime)) {
            return -1;
        } else {
            return 0;
        }
    }


}
