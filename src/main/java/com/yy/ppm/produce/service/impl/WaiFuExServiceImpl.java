package com.yy.ppm.produce.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.google.common.collect.Lists;
import com.yy.common.enums.CommonEnum;
import com.yy.common.util.DateUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.SpringUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.master.service.WaifuProcessPriceService;
import com.yy.ppm.produce.bean.dto.WfSettlementInsertDto;
import com.yy.ppm.produce.bean.dto.WorkTicketTableDTO;
import com.yy.ppm.produce.bean.dto.workTicket.WaiFuUpdateDto;
import com.yy.ppm.produce.mapper.TPrdWorkTicketMapper;
import com.yy.ppm.produce.mapper.TPrdWorkTicketNewMapper;
import com.yy.ppm.produce.service.TPrdWorkTicketNewService;
import com.yy.ppm.produce.service.TPrdWorkTicketService;
import com.yy.ppm.produce.service.WaiFuExService;
import com.yy.ppm.statement.bean.dto.storageFee.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.TCostStorageAmtCalcRecPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettleDetailPO;
import com.yy.ppm.statement.service.impl.storageAmountCalculate.StorageAmountCalculateServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WaiFuExServiceImpl implements WaiFuExService {
    @Resource
    private TPrdWorkTicketNewMapper workTicketMapper;
    @Autowired
    private TPrdWorkTicketNewService workTicketService;
    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private Snowflake snowflake;

    @Getter
    enum statusEnum {

        _10("10", "待审核"),
        _20("20", "生产已审核"),

        _30("30", "Hr已审核");

        private final String code;

        private final String remark;

        statusEnum(String code, String remark) {
            this.code = code;
            this.remark = remark;
        }
    }

    /**
     * 获取部门信息
     * @return
     */
    @Override
    public List<Map<String ,Object>> getDeptList() {
//        List<Map<String ,Object>> result =  workTicketMapper.getDeptsForEx();
        return workTicketMapper.getDeptsForEx();
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> prdEx(WorkTicketTableDTO query) {
        if("0".equals(securityUtils.getUserInfo().getIsSuperadmin())&&query.getDeptId() ==null ){
            throw new BusinessRuntimeException("请先选择作业部门");
        }

        //判断当前登陆人的部门
        securityUtils.getLoginUserId();
        //获取当前登录人所属的公司



//        Map<String, Object> deptLevel2InfoByUserId = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());
        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
            Map<String, Object> deptLevel1InfoByUserId = workTicketMapper.getDeptLevel1InfoByUserId(securityUtils.getLoginUserId());
            Map<String, Object> deptLevel1InfoByDeptId = workTicketMapper.getDeptLevel1InfoByDeptId(query.getDeptId());
            if(CollectionUtils.isEmpty(deptLevel1InfoByUserId)){
                throw new BusinessRuntimeException("获取当前登录人的作业公司信息失败");
            }

            if(CollectionUtils.isEmpty(deptLevel1InfoByDeptId)){
                throw new BusinessRuntimeException("获取当前选择的审核部门的作业公司信息失败");
            }

            if(!String.valueOf(deptLevel1InfoByDeptId.get("deptId")).equals(String.valueOf(deptLevel1InfoByUserId.get("deptId")))){
                throw new BusinessRuntimeException("当前登录人属于"+String.valueOf(deptLevel1InfoByUserId.get("deptName"))+"不能跨作业公司审核");
            }
        }


        HashMap<String, Object> result = new HashMap<>();
        WorkTicketTableDTO workTicketTableDTO = new WorkTicketTableDTO();
        LocalDateTime begainTime = LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(query.getEndTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime currentTime = begainTime;
        List<String> checkDateList = new ArrayList<>();
        while (!currentTime.isAfter(endTime)){
            System.out.println("开始"+currentTime);
            checkDateList.add(currentTime.getYear() + "-" + String.format("%02d", currentTime.getMonthValue()));
            currentTime = currentTime.plusMonths(1);
            System.out.println("结束"+currentTime);
        }
        checkDateList.add(endTime.getYear() + "-" + String.format("%02d", endTime.getMonthValue()));
        checkDateList = checkDateList.stream().distinct().collect(Collectors.toList());
//校验是否存在hr审核的数据
            BeanUtils.copyProperties(query,workTicketTableDTO);
            workTicketTableDTO.setStartTime(null);
            workTicketTableDTO.setEndTime(null);
            workTicketTableDTO.setCheckDateList(checkDateList);
            workTicketTableDTO.setStatus(statusEnum._30.getCode());
            List<WorkTicketTableDTO> tmpFuZhu = workTicketMapper.getWaiFuTicketTableForEx(workTicketTableDTO);
            if(tmpFuZhu!=null && tmpFuZhu.size()>0){
                throw new BusinessRuntimeException(checkDateList.stream()
                        .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经Hr审核的数据");
            }
            List<WorkTicketTableDTO> tmpNormal = workTicketMapper.getWorkTicketTableForEx(workTicketTableDTO);
            if(tmpNormal!=null && tmpNormal.size()>0){
                    throw new BusinessRuntimeException(checkDateList.stream()
                            .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经Hr审核的数据");
            }
            workTicketTableDTO.setStatus(statusEnum._20.getCode());

            tmpFuZhu = workTicketMapper.getWaiFuTicketTableForEx(workTicketTableDTO);
            if(tmpFuZhu!=null && tmpFuZhu.size()>0){
                throw new BusinessRuntimeException(checkDateList.stream()
                        .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经生成审核的数据，请勿重复审核");
            }
            tmpNormal = workTicketMapper.getWorkTicketTableForEx(workTicketTableDTO);
            if(tmpNormal!=null && tmpNormal.size()>0){
                    throw new BusinessRuntimeException(checkDateList.stream()
                            .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经生成审核的数据，请勿重复审核");
            }
        query.setStatus(statusEnum._20.getCode());
        query.setUserBy(securityUtils.getLoginUserId());
        query.setUserByName(securityUtils.getLoginUserName());
        query.setUserTime(new Date());
        query.setUserBy2(null);
        query.setUserByName2(null);
        query.setUserTime2(null);
        workTicketMapper.updateWaifuStatus(query);
        workTicketMapper.updateWaifuFuZhuStatus(query);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> v2PrdEx(WorkTicketTableDTO query) {
        Map<String, Object> result = new HashMap<>();
        if("0".equals(securityUtils.getUserInfo().getIsSuperadmin())&&query.getDeptId() ==null ){
            throw new BusinessRuntimeException("请先选择作业部门");
        }

        //判断当前登陆人的部门
        securityUtils.getLoginUserId();
        //获取当前登录人所属的公司

        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
            Map<String, Object> deptLevel1InfoByUserId = workTicketMapper.getDeptLevel1InfoByUserId(securityUtils.getLoginUserId());
            Map<String, Object> deptLevel1InfoByDeptId = workTicketMapper.getDeptLevel1InfoByDeptId(query.getDeptId());
            if(CollectionUtils.isEmpty(deptLevel1InfoByUserId)){
                throw new BusinessRuntimeException("获取当前登录人的作业公司信息失败");
            }

            if(CollectionUtils.isEmpty(deptLevel1InfoByDeptId)){
                throw new BusinessRuntimeException("获取当前选择的审核部门的作业公司信息失败");
            }

            if(!String.valueOf(deptLevel1InfoByDeptId.get("deptId")).equals(String.valueOf(deptLevel1InfoByUserId.get("deptId")))){
                throw new BusinessRuntimeException("当前登录人属于"+String.valueOf(deptLevel1InfoByUserId.get("deptName"))+"不能跨作业公司审核");
            }
        }
        List<WorkTicketTableDTO> workTiccketTableV2 = workTicketService.getWorkTiccketTableV2(query);
        if(CollectionUtils.isEmpty(workTiccketTableV2)){
            throw  new BusinessRuntimeException("没有要审核的数据");
        }
        for (WorkTicketTableDTO tmpDto : workTiccketTableV2) {
            if("20".equals(tmpDto.getExternalStatus())){
                throw  new BusinessRuntimeException("存在已经生产审核的数据，请先撤销生产审核");
            }
            if("30".equals(tmpDto.getExternalStatus())){
                throw new BusinessRuntimeException("存在已经Hr审核的数据，请先撤销Hr审核");
            }

        }
        List<Long> collect = workTiccketTableV2.stream().map(WorkTicketTableDTO::getTicketDetailId).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collect)){
            throw new BusinessRuntimeException("更新缺失必要操作参数，请联系管理员");
        }
        List<List<Long>> lists = this.splitList(collect, 150);
        Date date = new Date();
        //此处循环大概在 4-5次左右

        for (List<Long> list : lists) {
            workTicketMapper.updateWaiFuTicketPrdStatus(list,statusEnum._20.getCode(),securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),date);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> prdExV(WorkTicketTableDTO query) {
        if("0".equals(securityUtils.getUserInfo().getIsSuperadmin())&&query.getDeptId() ==null ){
            throw new BusinessRuntimeException("请先选择作业部门");
        }
        //判断当前登陆人的部门
        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
            Map<String, Object> deptLevel1InfoByUserId = workTicketMapper.getDeptLevel1InfoByUserId(securityUtils.getLoginUserId());
            Map<String, Object> deptLevel1InfoByDeptId = workTicketMapper.getDeptLevel1InfoByDeptId(query.getDeptId());
            if(CollectionUtils.isEmpty(deptLevel1InfoByUserId)){
                throw new BusinessRuntimeException("获取当前登录人的作业公司信息失败");
            }

            if(CollectionUtils.isEmpty(deptLevel1InfoByDeptId)){
                throw new BusinessRuntimeException("获取当前选择的审核部门的作业公司信息失败");
            }

            if(!String.valueOf(deptLevel1InfoByDeptId.get("deptId")).equals(String.valueOf(deptLevel1InfoByUserId.get("deptId")))){
                throw new BusinessRuntimeException("当前登录人属于"+String.valueOf(deptLevel1InfoByUserId.get("deptName"))+"不能跨作业公司审核");
            }
        }

        HashMap<String, Object> result = new HashMap<>();

        //校验是否存在Hr审核的数据
        WorkTicketTableDTO workTicketTableDTO = new WorkTicketTableDTO();
        LocalDateTime begainTime = LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(query.getEndTime().toInstant(), ZoneId.systemDefault());
        LocalDateTime currentTime = begainTime;
        List<String> checkDateList = new ArrayList<>();
        while (!currentTime.isAfter(endTime)){
            System.out.println("开始"+currentTime);
            checkDateList.add(currentTime.getYear() + "-" + String.format("%02d", currentTime.getMonthValue()));
            currentTime = currentTime.plusMonths(1);
            System.out.println("结束"+currentTime);
        }
        checkDateList.add(endTime.getYear() + "-" + String.format("%02d", endTime.getMonthValue()));
        checkDateList = checkDateList.stream().distinct().collect(Collectors.toList());
//校验是否存在hr审核的数据
        BeanUtils.copyProperties(query,workTicketTableDTO);
        workTicketTableDTO.setStartTime(null);
        workTicketTableDTO.setEndTime(null);
        workTicketTableDTO.setCheckDateList(checkDateList);
        workTicketTableDTO.setStatus(statusEnum._30.getCode());
        List<WorkTicketTableDTO> tmpNormal = workTicketMapper.getWorkTicketTableForEx(workTicketTableDTO);
        List<WorkTicketTableDTO> tmpFuZhu = workTicketMapper.getWaiFuTicketTableForEx(workTicketTableDTO);
        if(tmpFuZhu!=null && tmpFuZhu.size()>0){
            throw new BusinessRuntimeException(checkDateList.stream()
                    .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经Hr审核的数据");
        }
        if(tmpNormal!=null && tmpNormal.size()>0){
            throw new BusinessRuntimeException(checkDateList.stream()
                    .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经Hr审核的数据");
        }

        //回滚状态
        query.setStatus(statusEnum._10.getCode());
        query.setUserBy(null);
        query.setUserByName(null);
        query.setUserTime(null);
        query.setUserBy2(null);
        query.setUserByName2(null);
        query.setUserTime2(null);
        workTicketMapper.updateWaifuStatus(query);
        workTicketMapper.updateWaifuFuZhuStatus(query);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> v2PrdExV(WorkTicketTableDTO query) {
        Map<String, Object> result = new HashMap<>();
        if("0".equals(securityUtils.getUserInfo().getIsSuperadmin())&&query.getDeptId() ==null ){
            throw new BusinessRuntimeException("请先选择作业部门");
        }

        //判断当前登陆人的部门
        securityUtils.getLoginUserId();
        //获取当前登录人所属的公司

        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
            Map<String, Object> deptLevel1InfoByUserId = workTicketMapper.getDeptLevel1InfoByUserId(securityUtils.getLoginUserId());
            Map<String, Object> deptLevel1InfoByDeptId = workTicketMapper.getDeptLevel1InfoByDeptId(query.getDeptId());
            if(CollectionUtils.isEmpty(deptLevel1InfoByUserId)){
                throw new BusinessRuntimeException("获取当前登录人的作业公司信息失败");
            }

            if(CollectionUtils.isEmpty(deptLevel1InfoByDeptId)){
                throw new BusinessRuntimeException("获取当前选择的审核部门的作业公司信息失败");
            }

            if(!String.valueOf(deptLevel1InfoByDeptId.get("deptId")).equals(String.valueOf(deptLevel1InfoByUserId.get("deptId")))){
                throw new BusinessRuntimeException("当前登录人属于"+String.valueOf(deptLevel1InfoByUserId.get("deptName"))+"不能跨作业公司审核");
            }
        }
        List<WorkTicketTableDTO> workTiccketTableV2 = workTicketService.getWorkTiccketTableV2(query);
        if(CollectionUtils.isEmpty(workTiccketTableV2)){
            throw  new BusinessRuntimeException("没有要审核的数据");
        }
        for (WorkTicketTableDTO tmpDto : workTiccketTableV2) {
            if("10".equals(tmpDto.getExternalStatus())){
                throw  new BusinessRuntimeException("存在未进行生产审核的数据，无需撤销");
            }
            if("30".equals(tmpDto.getExternalStatus())){
                throw new BusinessRuntimeException("存在已经Hr审核的数据，请先撤销Hr审核");
            }

        }
        List<Long> collect = workTiccketTableV2.stream().map(WorkTicketTableDTO::getTicketDetailId).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collect)){
            throw new BusinessRuntimeException("更新缺失必要操作参数，请联系管理员");
        }
        List<List<Long>> lists = this.splitList(collect, 150);
        Date date = new Date();
        //此处循环大概在 4-5次左右

        for (List<Long> list : lists) {
            workTicketMapper.updateWaiFuTicketPrdStatus(list,statusEnum._10.getCode(),securityUtils.getLoginUserId(),null,date);
        }

        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> hrEx(WorkTicketTableDTO query) {

        HashMap<String, Object> result = new HashMap<>();
        WorkTicketTableDTO workTicketTableDTO = new WorkTicketTableDTO();
        List<String> checkDateList = new ArrayList<>();
        checkDateList.add(LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getYear() + "-" + String.format("%02d", LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getMonthValue()));
        checkDateList = checkDateList.stream().distinct().collect(Collectors.toList());
        //校验是否存在hr审核的数据
            BeanUtils.copyProperties(query,workTicketTableDTO);
            workTicketTableDTO.setStartTime(null);
            workTicketTableDTO.setEndTime(null);
            workTicketTableDTO.setCheckDateList(checkDateList);
            workTicketTableDTO.setStatus(statusEnum._30.getCode());
            List<WorkTicketTableDTO> tmpNormal = workTicketMapper.getWorkTicketTableForEx(workTicketTableDTO);
            List<WorkTicketTableDTO> tmpFuZhu = workTicketMapper.getWaiFuTicketTableForEx(workTicketTableDTO);
            if(tmpFuZhu!=null && tmpFuZhu.size()>0){
                throw new BusinessRuntimeException(checkDateList.stream()
                        .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经Hr审核的数据，轻忽重复审核");
            }
            if(tmpNormal!=null && tmpNormal.size()>0){
                throw new BusinessRuntimeException(checkDateList.stream()
                        .map(o->o.toString()).collect(Collectors.joining(","))+"存在已经Hr审核的数据，轻忽重复审核");
            }
            workTicketTableDTO.setStatus(statusEnum._10.getCode());
            tmpNormal = workTicketMapper.getWorkTicketTableForEx(workTicketTableDTO);
            tmpFuZhu = workTicketMapper.getWaiFuTicketTableForEx(workTicketTableDTO);
            if(tmpFuZhu!=null && tmpFuZhu.size()>0){
                throw new BusinessRuntimeException(checkDateList.stream()
                        .map(o->o.toString()).collect(Collectors.joining(","))+"存在尚未进行生产审核的数据");
            }
            if(tmpNormal!=null && tmpNormal.size()>0){
                throw new BusinessRuntimeException(checkDateList.stream()
                        .map(o->o.toString()).collect(Collectors.joining(","))+"存在尚未进行生产审核的数据");
            }
        query.setStatus(statusEnum._30.getCode());
        query.setUserBy(securityUtils.getLoginUserId());
        query.setUserByName(securityUtils.getLoginUserName());
        query.setUserTime(new Date());
        query.setUserBy2(null);
        query.setUserByName2(null);
        query.setUserTime2(null);
        workTicketMapper.updateWaifuStatusHr30(query);
        workTicketMapper.updateWaifuFuZhuStatusHr30(query);

        List<Map<String, String>> waifuPackageCodeList = SpringUtils.getBean(WaifuProcessPriceService.class).waifuPackageCodeList();

        Map<String, String> waifuPackageCodeMap = waifuPackageCodeList.stream().collect(Collectors.toMap(o -> o.get("label"), v -> v.get("value"), (k1, k2) -> k1));

        workTicketMapper.deleteSettlement(query.getStartTime());


        List<WorkTicketTableDTO> workTiccketTableV2 = SpringUtils.getBean(TPrdWorkTicketNewService.class).getWorkTiccketTableV2(query);
        if (CollectionUtils.isEmpty(workTiccketTableV2)){
            return result;
        }
        Map<String, List<WorkTicketTableDTO>> collect = workTiccketTableV2.stream().collect(
                Collectors.groupingBy(o -> String.valueOf(o.getDeptId()) + "_" + o.getPackingNameWaiBao()
                + "_" + o.getProcessDetailCode() + "_" + o.getWorkPositionCode()+"_"+String.valueOf(o.getEquipmentTypeName())));
        ArrayList<WfSettlementInsertDto> wfSettlementInsertDtos = new ArrayList<>(collect.size());
        for (String s : collect.keySet()) {
            List<WorkTicketTableDTO> workTicketTableDTOS = collect.get(s);
            if(CollectionUtils.isEmpty(workTicketTableDTOS)){

            }else{
                WfSettlementInsertDto wfSettlementInsertDto = new WfSettlementInsertDto();
                wfSettlementInsertDto.setId(snowflake.nextId());
                wfSettlementInsertDto.setDeptId(workTicketTableDTOS.get(0).getDeptId());
                wfSettlementInsertDto.setDeptName(workTicketTableDTOS.get(0).getDeptName());
                wfSettlementInsertDto.setProcessCode(workTicketTableDTOS.get(0).getProcessCode());
                wfSettlementInsertDto.setProcessName(workTicketTableDTOS.get(0).getProcessName());
                wfSettlementInsertDto.setProcessDetailCode(workTicketTableDTOS.get(0).getProcessDetailCode());
                wfSettlementInsertDto.setProcessDetailName(workTicketTableDTOS.get(0).getProcessDetailName());
                wfSettlementInsertDto.setWorkPositionCode(workTicketTableDTOS.get(0).getWorkPositionCode());
                wfSettlementInsertDto.setWorkPositionName(workTicketTableDTOS.get(0).getWorkPositionName());
                wfSettlementInsertDto.setWorkTon(workTicketTableDTOS.stream().map(o->new BigDecimal(
                        o.getWorkTon()==null?"0":o.getWorkTon())
                ).reduce(BigDecimal.ZERO,BigDecimal::add));
                try {
                    Date workDate = workTicketTableDTOS.get(0).getWorkDate();
                    wfSettlementInsertDto.setSettlementDate(
                            DateUtils.parseDate(DateUtils.formatDate(workDate,"yyyy-MM"),"yyyy-MM")
                    );
                }catch (Exception e){
                    throw new BusinessRuntimeException("日期格式转换错误");
                }

                if("人员分配".equals(workTicketTableDTOS.get(0).getAllotType())){
                    wfSettlementInsertDto.setDistributeType("3");//特殊处理  人员3 机械 2
                }else if("机械分配".equals(workTicketTableDTOS.get(0).getAllotType())){
                    wfSettlementInsertDto.setDistributeType("2");//特殊处理  人员3 机械 2
                }
                wfSettlementInsertDto.setOutwardTypeCode(waifuPackageCodeMap.get(workTicketTableDTOS.get(0).getPackingNameWaiBao()));//外包合同分类
                wfSettlementInsertDto.setOutwardTypeName(workTicketTableDTOS.get(0).getPackingNameWaiBao());//外包合同分类
                wfSettlementInsertDto.setIsDaoyun("0");
                wfSettlementInsertDto.setMechanicalType("行吊".equals(workTicketTableDTOS.get(0).getEquipmentTypeName())?"1":"0");//是否行吊
                wfSettlementInsertDto.setDdStatus("0");
                wfSettlementInsertDto.setKcStatus("0");
                wfSettlementInsertDto.setLzStatus("0");
                wfSettlementInsertDto.setWfStatus("0");
                wfSettlementInsertDtos.add(wfSettlementInsertDto);
            }
        }
        workTicketMapper.insertWfSettlementBatch(wfSettlementInsertDtos);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> hrExV(WorkTicketTableDTO query) {
        HashMap<String, Object> result = new HashMap<>();

        //校验是否存在Hr审核的数据
        WorkTicketTableDTO workTicketTableDTO = new WorkTicketTableDTO();
        List<String> checkDateList = new ArrayList<>();
        checkDateList.add(LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getYear() + "-" + String.format("%02d", LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getMonthValue()));
        checkDateList = checkDateList.stream().distinct().collect(Collectors.toList());
        //校验是否存在hr审核的数据
        BeanUtils.copyProperties(query,workTicketTableDTO);
        workTicketTableDTO.setStartTime(null);
        workTicketTableDTO.setEndTime(null);
        workTicketTableDTO.setCheckDateList(checkDateList);


        workTicketTableDTO.setStatus(statusEnum._10.getCode());
        List<WorkTicketTableDTO> tmpNormal = workTicketMapper.getWorkTicketTableForEx(workTicketTableDTO);
        List<WorkTicketTableDTO> tmpFuZhu = workTicketMapper.getWaiFuTicketTableForEx(workTicketTableDTO);
        if(tmpFuZhu!=null && tmpFuZhu.size()>0){
            throw new BusinessRuntimeException(checkDateList.stream()
                    .map(o->o.toString()).collect(Collectors.joining(","))+"存在尚未进行生产审核数据");
        }
        if(tmpNormal!=null && tmpNormal.size()>0){
            throw new BusinessRuntimeException(checkDateList.stream()
                    .map(o->o.toString()).collect(Collectors.joining(","))+"存在尚未进行生产审核的数据");
        }
        query.setStatus(statusEnum._20.getCode());
        query.setUserBy(null);
        query.setUserByName(null);
        query.setUserTime(null);
        query.setUserBy2(null);
        query.setUserByName2(null);
        query.setUserTime2(null);
        workTicketMapper.updateWaifuStatusHr(query);
        workTicketMapper.updateWaifuFuZhuStatusHr(query);

        workTicketMapper.deleteSettlement(query.getStartTime());
        return result;
    }



    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> v2HrEx(WorkTicketTableDTO query) {
        if(query.getStartTime()==null){
            throw new BusinessRuntimeException("请先选择审核时间！");
        }

        Map<String, Object> result = new HashMap<>();


        List<String> checkDateList = new ArrayList<>();
        checkDateList.add(LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getYear() + "-" + String.format("%02d", LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getMonthValue()));
//        query.setCheckDateList(checkDateList);
        query.setEndTime(null);
        query.setEndDate(null);
        query.setStartDate(null);
        query.setStartTimeMonth(null);
        query.setExFlag("1");
        List<WorkTicketTableDTO> workTiccketTableV2 = workTicketService.getWorkTiccketTableV2(query);
        if(CollectionUtils.isEmpty(workTiccketTableV2)){
            throw  new BusinessRuntimeException("没有要审核的数据");
        }
        for (WorkTicketTableDTO tmpDto : workTiccketTableV2) {
            if("10".equals(tmpDto.getExternalStatus())){
                throw  new BusinessRuntimeException("存在未进行生产审核的数据，无需撤销");
            }
            if("30".equals(tmpDto.getExternalStatus())){
                throw new BusinessRuntimeException("存在已经Hr审核的数据，请先撤销Hr审核");
            }
        }
        List<Long> collectIds = workTiccketTableV2.stream().map(WorkTicketTableDTO::getTicketDetailId).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collectIds)){
            throw new BusinessRuntimeException("更新缺失必要操作参数，请联系管理员");
        }
        List<List<Long>> lists = this.splitList(collectIds, 150);
        List<List<WorkTicketTableDTO>> ticketTables = this.splitList(workTiccketTableV2, 80);

        Date date = new Date();
        //此处循环大概在 4-5次左右

        for (List<WorkTicketTableDTO> list : ticketTables) {
//            workTicketMapper.updateWaiFuTicketHrStatus(list,statusEnum._30.getCode(),securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),date);
            workTicketMapper.updateWaiFuTicketHr(list,statusEnum._30.getCode(),securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),date);
        }
        workTicketMapper.deleteSettlement(query.getStartTime());
        List<Map<String, String>> waifuPackageCodeList = SpringUtils.getBean(WaifuProcessPriceService.class).waifuPackageCodeList();

        Map<String, String> waifuPackageCodeMap = waifuPackageCodeList.stream().collect(Collectors.toMap(o -> o.get("label"), v -> v.get("value"), (k1, k2) -> k1));

        if (CollectionUtils.isEmpty(workTiccketTableV2)){
            throw new BusinessRuntimeException("没有要更新的数据");
        }
        Map<String, List<WorkTicketTableDTO>> collect = workTiccketTableV2.stream().collect(
                Collectors.groupingBy(o -> String.valueOf(o.getDeptId()) + "_" + o.getPackingNameWaiBao()
                + "_" + o.getProcessDetailCode() + "_" + o.getWorkPositionCode()+"_"+String.valueOf("行吊".equals(o.getEquipmentTypeName())?"1":"0")));
        ArrayList<WfSettlementInsertDto> wfSettlementInsertDtos = new ArrayList<>(collect.size());
        for (String s : collect.keySet()) {
            List<WorkTicketTableDTO> workTicketTableDTOS = collect.get(s);
            if(CollectionUtils.isEmpty(workTicketTableDTOS)){

            }else{
                WfSettlementInsertDto wfSettlementInsertDto = new WfSettlementInsertDto();
                wfSettlementInsertDto.setId(snowflake.nextId());
                wfSettlementInsertDto.setDeptId(workTicketTableDTOS.get(0).getDeptId());
                wfSettlementInsertDto.setDeptName(workTicketTableDTOS.get(0).getDeptName());
                wfSettlementInsertDto.setProcessCode(workTicketTableDTOS.get(0).getProcessCode());
                wfSettlementInsertDto.setProcessName(workTicketTableDTOS.get(0).getProcessName());
                wfSettlementInsertDto.setProcessDetailCode(workTicketTableDTOS.get(0).getProcessDetailCode());
                wfSettlementInsertDto.setProcessDetailName(workTicketTableDTOS.get(0).getProcessDetailName());
                wfSettlementInsertDto.setWorkPositionCode(workTicketTableDTOS.get(0).getWorkPositionCode());
                wfSettlementInsertDto.setWorkPositionName(workTicketTableDTOS.get(0).getWorkPositionName());
                wfSettlementInsertDto.setWorkTon(workTicketTableDTOS.stream().map(o->new BigDecimal(
                        o.getWorkTon()==null?"0":o.getWorkTon())
                ).reduce(BigDecimal.ZERO,BigDecimal::add));
                try {
                    Date workDate = workTicketTableDTOS.get(0).getWorkDate();
                    wfSettlementInsertDto.setSettlementDate(
                            DateUtils.parseDate(DateUtils.formatDate(workDate,"yyyy-MM"),"yyyy-MM")
                    );
                }catch (Exception e){
                    throw new BusinessRuntimeException("日期格式转换错误");
                }

                if("人员分配".equals(workTicketTableDTOS.get(0).getAllotType())){
                    wfSettlementInsertDto.setDistributeType("3");//特殊处理  人员3 机械 2
                }else if("机械分配".equals(workTicketTableDTOS.get(0).getAllotType())){
                    wfSettlementInsertDto.setDistributeType("2");//特殊处理  人员3 机械 2
                }
                wfSettlementInsertDto.setOutwardTypeCode(waifuPackageCodeMap.get(workTicketTableDTOS.get(0).getPackingNameWaiBao()));//外包合同分类
                wfSettlementInsertDto.setOutwardTypeName(workTicketTableDTOS.get(0).getPackingNameWaiBao());//外包合同分类
                wfSettlementInsertDto.setIsDaoyun("0");
                wfSettlementInsertDto.setMechanicalType("行吊".equals(workTicketTableDTOS.get(0).getEquipmentTypeName())?"1":"0");//是否行吊
                wfSettlementInsertDto.setDdStatus("0");
                wfSettlementInsertDto.setKcStatus("0");
                wfSettlementInsertDto.setLzStatus("0");
                wfSettlementInsertDto.setWfStatus("0");
                wfSettlementInsertDtos.add(wfSettlementInsertDto);
            }
        }
        workTicketMapper.insertWfSettlementBatch(wfSettlementInsertDtos);
        //审核完插入到一个新表 用来做查询
        query.setStatus(statusEnum._30.getCode());
        List<WorkTicketTableDTO> workTiccketTableV21 = workTicketService.getWorkTiccketTableV2(query);
        for(WorkTicketTableDTO s : workTiccketTableV21){
            s.setId(s.getTicketDetailId());
        }
        List<List<WorkTicketTableDTO>> splitWorkTicketTableV21 = this.splitList(workTiccketTableV21, 100);
        for(List<WorkTicketTableDTO> list : splitWorkTicketTableV21){
            workTicketMapper.insertWfHRNewBatch(list);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public Map<String, Object> v2HrExV(WorkTicketTableDTO query) {


        if(query.getStartTime()==null){
            throw new BusinessRuntimeException("请先选择审核时间！");
        }
        Map<String, Object> result = new HashMap<>();

        Date tmpStartDate = query.getStartTime();
        List<String> checkDateList = new ArrayList<>();
        checkDateList.add(LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getYear() + "-" + String.format("%02d", LocalDateTime.ofInstant(query.getStartTime().toInstant(), ZoneId.systemDefault()).getMonthValue()));
        query.setCheckDateList(checkDateList);
        query.setEndTime(null);
        query.setEndDate(null);
        query.setStartDate(null);
        query.setStartTimeMonth(null);
        query.setExFlag("1");
        List<WorkTicketTableDTO> workTiccketTableV2 = workTicketService.getWorkTiccketTableV2(query);
        if(CollectionUtils.isEmpty(workTiccketTableV2)){
            throw  new BusinessRuntimeException("没有要销审的数据");
        }
        for (WorkTicketTableDTO tmpDto : workTiccketTableV2) {
            if("10".equals(tmpDto.getExternalStatus())){
                throw  new BusinessRuntimeException("存在未进行生产审核的数据，无需撤销Hr审核");
            }
            if("20".equals(tmpDto.getExternalStatus())){
                throw new BusinessRuntimeException("存在未进行Hr审核的数据，无需撤销Hr审核");
            }
        }
        List<Long> collectIds = workTiccketTableV2.stream().map(WorkTicketTableDTO::getTicketDetailId).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collectIds)){
            throw new BusinessRuntimeException("更新缺失必要操作参数，请联系管理员");
        }
        List<List<Long>> lists = this.splitList(collectIds, 150);
        List<List<WorkTicketTableDTO>> ticketTables = this.splitList(workTiccketTableV2, 80);

        Date date = new Date();

        //此处循环大概在 4-5次左右
        for (List<WorkTicketTableDTO> list : ticketTables) {
//            workTicketMapper.updateWaiFuTicketHrStatus(list,statusEnum._20.getCode(),securityUtils.getLoginUserId(),
//                    null,date);

            workTicketMapper.updateWaiFuTicketHrV(list,null,statusEnum._20.getCode(),securityUtils.getLoginUserId(),null,date);

        }
        workTicketMapper.deleteSettlement(tmpStartDate);


        workTicketMapper.deleteWfHRNewBatch(tmpStartDate);
        return result;
    }

    @Override
    public Map<String, Object> getNowUser() {
        Map<String, Object> result = new HashMap<>();
        if(!"1".equals(securityUtils.getUserInfo().getIsSuperadmin())){
            result = workTicketMapper.getDeptLevel2InfoByUserId(securityUtils.getLoginUserId());
            if(CollectionUtils.isEmpty(result)){
                throw new BusinessRuntimeException("获取部门信息失败");
            }
        }
        result.put("isSuperadmin",securityUtils.getUserInfo().getIsSuperadmin());
        return result;
    }

    @Override
    public Map<String, Object> isTrueCompany(WorkTicketTableDTO query) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("flag",true);

        Map<String, Object> deptLevel1InfoByUserId = workTicketMapper.getDeptLevel1InfoByUserId(securityUtils.getLoginUserId());
        Map<String, Object> deptLevel1InfoByDeptId = workTicketMapper.getDeptLevel1InfoByDeptId(query.getDeptId());
        if(CollectionUtils.isEmpty(deptLevel1InfoByUserId)){
            throw new BusinessRuntimeException("获取当前登录人的作业公司信息失败");
        }

        if(CollectionUtils.isEmpty(deptLevel1InfoByDeptId)){
            throw new BusinessRuntimeException("获取当前选择的审核部门的作业公司信息失败");
        }

        if(!String.valueOf(deptLevel1InfoByDeptId.get("deptId")).equals(String.valueOf(deptLevel1InfoByUserId.get("deptId")))){
            result.put("flag",false);
        }
        result.put("company",deptLevel1InfoByUserId.get("deptName"));
        return result;
    }

    @Override
    public Map<String, Object> v2HrNewEx(WorkTicketTableDTO query) {
        if(query.getStartTime()==null){
            throw new BusinessRuntimeException("请先选择审核时间！");
        }
        Map<String, Object> result = new HashMap<>();
        query.setEndTime(null);
        query.setEndDate(null);
        query.setStartDate(null);
        query.setStartTimeMonth(null);
        query.setExFlag("1");
        List<WorkTicketTableDTO> workTiccketTableV2 = workTicketService.getWfHrNewTable(query);

        List<Long> collectIds = workTiccketTableV2.stream().map(WorkTicketTableDTO::getTicketDetailId).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collectIds)){
            throw new BusinessRuntimeException("更新缺失必要操作参数，请联系管理员");
        }
        List<List<Long>> lists = this.splitList(collectIds, 150);
        List<List<WorkTicketTableDTO>> ticketTables = this.splitList(workTiccketTableV2, 80);

        Date date = new Date();
        //此处循环大概在 4-5次左右

        for (List<WorkTicketTableDTO> list : ticketTables) {
            workTicketMapper.updateNewWaiFuCalHr(list,"1",securityUtils.getLoginUserId(),securityUtils.getLoginUserName(),date);
        }
        return result;
    }

    @Override
    public Map<String, Object> v2HrNewExV(WorkTicketTableDTO query) {
        if(query.getStartTime()==null){
            throw new BusinessRuntimeException("请先选择审核时间！");
        }
        Map<String, Object> result = new HashMap<>();
        query.setEndTime(null);
        query.setEndDate(null);
        query.setStartDate(null);
        query.setStartTimeMonth(null);
        query.setExFlag("1");
        List<WorkTicketTableDTO> workTiccketTableV2 = workTicketService.getWfHrNewTable(query);

        List<Long> collectIds = workTiccketTableV2.stream().map(WorkTicketTableDTO::getTicketDetailId).filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collectIds)){
            throw new BusinessRuntimeException("更新缺失必要操作参数，请联系管理员");
        }
        List<List<Long>> lists = this.splitList(collectIds, 150);
        List<List<WorkTicketTableDTO>> ticketTables = this.splitList(workTiccketTableV2, 80);

        Date date = new Date();
        //此处循环大概在 4-5次左右

        for (List<WorkTicketTableDTO> list : ticketTables) {
            workTicketMapper.updateNewWaiFuCalHr(list,"0",null,null,null);
        }
        return result;
    }

    private <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return Lists.newArrayList();
        }
        List<List<T>> result = Lists.newArrayList();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }
}
