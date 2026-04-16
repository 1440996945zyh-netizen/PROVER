package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.equipment.bean.dto.EContractInfoDTO;
import com.yy.ppm.equipment.bean.dto.ECostBudgetManagementDTO;
import com.yy.ppm.equipment.bean.dto.ECostSettlementApplyDTO;
import com.yy.ppm.equipment.mapper.ECostBudgetManagementMapper;
import com.yy.ppm.equipment.service.ECostBudgetManagementService;
import com.yy.ppm.system.service.SysNotificationService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预算管理 Service 实现
 *
 * 核心业务规则：
 * 1. 年份不能为空，且必须为4位数字
 * 2. 维修单位不能为空
 * 3. 费用类型不能为空
 * 4. 预算金额不能为空，且不能小于0
 * 5. 同一个年份下，同维修单位的费用类型不允许重复
 */
@RequiredArgsConstructor
@Service
public class ECostBudgetManagementServiceImpl implements ECostBudgetManagementService {

    @Resource
    private ECostBudgetManagementMapper mapper;

    @Resource
    private SysNotificationService sysNotificationService;

    @Resource
    private Snowflake snowflake;

    /**
     * 分页查询预算管理列表
     *
     * @param searchDTO 查询条件
     * @param parameter 分页参数
     * @return 分页结果
     */
    @Override
    public Pages<ECostBudgetManagementDTO> list(ECostBudgetManagementDTO searchDTO, PageParameter parameter) {
        ECostBudgetManagementDTO dto = (searchDTO == null) ? new ECostBudgetManagementDTO() : searchDTO;
        return PageHelperUtils.limit(parameter, () -> mapper.selectList(dto));
    }

    /**
     * 根据主键ID查询详情
     *
     * @param id 主键ID
     * @return 详情数据
     */
    @Override
    public ECostBudgetManagementDTO get(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        return mapper.selectById(id);
    }

    /**
     * 新增预算管理
     *
     * @param dto 请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void add(ECostBudgetManagementDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("参数不能为空");
        }

        // 统一参数校验
        validate(dto, false);

        // 若前端未传主键，则由后端自动生成雪花ID
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
        }

        mapper.add(dto);
    }

    /**
     * 修改预算管理
     *
     * @param dto 请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void update(ECostBudgetManagementDTO dto) {
        if (dto == null || dto.getId() == null) {
            throw new BusinessRuntimeException("id不能为空");
        }

        // 统一参数校验
        validate(dto, true);

        mapper.update(dto);
    }

    /**
     * 删除预算管理
     *
     * @param id 主键ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        if (id == null) {
            throw new BusinessRuntimeException("id不能为空");
        }
        mapper.delete(id);
    }



    /**
     *  根据业务id与角色获取用户
     *
     * @param id 主键ID
     */

    @Override
    public List<ECostBudgetManagementDTO> getWarningUser(ECostBudgetManagementDTO dto) {


        List<ECostBudgetManagementDTO> result = mapper.getWarningUser(dto);
        return result;
    }

    /**
     * 统一业务校验
     *
     * @param dto 请求参数
     * @param isUpdate 是否为修改操作
     */
    private void validate(ECostBudgetManagementDTO dto, boolean isUpdate) {
        // 校验年份
        if (StringUtils.isBlank(dto.getYear())) {
            throw new BusinessRuntimeException("年份不能为空");
        }
        if (!dto.getYear().matches("^\\d{4}$")) {
            throw new BusinessRuntimeException("年份格式不正确，请输入4位年份");
        }

        // 校验维修单位
        if (dto.getMaintenanceUnitId() == null) {
            throw new BusinessRuntimeException("维修单位不能为空");
        }
        if (StringUtils.isBlank(dto.getMaintenanceUnitName())) {
            throw new BusinessRuntimeException("维修单位名称不能为空");
        }

        // 校验费用类型
        if (StringUtils.isBlank(dto.getCostType())) {
            throw new BusinessRuntimeException("费用类型不能为空");
        }

        // 校验预算金额
        if (dto.getAmount() == null) {
            throw new BusinessRuntimeException("预算金额不能为空");
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuntimeException("预算金额不能小于0");
        }

        // 同一年份下，同一个维修单位的同一费用类型只能有一条
        // 修改时要排除自己
        Long count = mapper.countDuplicate(dto.getYear(), dto.getMaintenanceUnitId(), dto.getCostType(), isUpdate ? dto.getId() : null);
        if (count != null && count > 0) {
            throw new BusinessRuntimeException("同一个年份下，同维修单位的费用类型不允许重复");
        }
    }



    /**
     * 预算管理预警金额预警消息：每天零点跑一次
     */
    public Integer generateWarningAmount() {

        Integer count = 0;

        //循环预算管理数据
        List<ECostBudgetManagementDTO> list = mapper.getBudgetManagementList();



        for (ECostBudgetManagementDTO dto : list) {


            //查询预算信息是否已生成预警
            int countNotification=mapper.countNotification(dto.getId());
            if (countNotification>0){
                continue;
            }



            BigDecimal amountSum=BigDecimal.ZERO;

            //判断费用类型
            if ("01".equals(dto.getCostType())){

                // 结算申请金额
                BigDecimal settlementAmount = mapper.getSettlementAmount(dto);

                amountSum = amountSum.add(settlementAmount);

                //项目合同信息金额list
                List<EContractInfoDTO> eContractInfoDTOList = mapper.getSettlementApply(dto);

                //循环合同信息
                for (EContractInfoDTO eContractInfoDTO : eContractInfoDTOList) {

                    //一个月的预算金额
                    long diff = getMonthDiff(eContractInfoDTO.getEndDate(), eContractInfoDTO.getStartDate());

                    BigDecimal result = eContractInfoDTO.getContractAmount().divide(BigDecimal.valueOf(diff), 4, RoundingMode.HALF_UP);

                    //判断年份属于开始或结束年
                    if (dto.getYear().equals(eContractInfoDTO.getStartYear())){

                        //计算年底的月份
                        int monthsNum = getRemainMonthsToEndYear(eContractInfoDTO.getStartDate());

                        //金额相加
                        amountSum = amountSum.add(result.multiply(BigDecimal.valueOf(monthsNum)));


                    }else if (dto.getYear().equals(eContractInfoDTO.getEndYear())){

                        //计算年初的月份
                        int monthsNum = getMonthsFromStartOfYear(eContractInfoDTO.getEndDate());

                        //金额相加
                        amountSum = amountSum.add(result.multiply(BigDecimal.valueOf(monthsNum)));

                    }



                }



            }else if ("02".equals(dto.getCostType())){


                //材料费 物资采购的含税金额
                BigDecimal materialPurchaseAmount = mapper.getMaterialPurchaseAmount(dto);
                amountSum = amountSum.add(materialPurchaseAmount);


            }



            //比较金额是否大于预警金额
            if (amountSum.compareTo(dto.getWarningAmount()) > 0){

                //分割预警人员ids
                List<Long> warningUserIds = Arrays.stream(dto.getWarningUserId().split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList());

                sysNotificationService.sendNotification(
                        "预算预警",
                        dto.getMaintenanceUnitName() + "预算金额预警：" + dto.getMaintenanceUnitName() + "费用金额已超过预警金额，请及时处理！",
                        warningUserIds,
                        dto.getId());

                //生成条数
                count++;
            }



        }



        return  count;
    }


    /**
     * 计算两个 Date 年月相差的月份（绝对值）
     */
    public static long getMonthDiff(Date date1, Date date2) {
        YearMonth ym1 = YearMonth.from(date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        YearMonth ym2 = YearMonth.from(date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return Math.abs(ChronoUnit.MONTHS.between(ym1, ym2));
    }


    public static int getRemainMonthsToEndYear(Date date) {
        YearMonth nowYm = YearMonth.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        int year = nowYm.getYear();
        YearMonth endYm = YearMonth.of(year, 12);

        return endYm.getMonthValue() - nowYm.getMonthValue() + 1;
    }

    public static int getMonthsFromStartOfYear(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        // 月份从1开始，直接就是到年初的月份数
        return localDate.getMonthValue();
    }
}
