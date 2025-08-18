package com.yy.ppm.statement.service.impl.storageAmountCalculate;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.condition.EnvironmentCondition;
import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.CargoInfoSourceEnum;
import com.yy.ppm.statement.bean.dto.storageFee.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.TCostStorageAmtCalcRecPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettleDetailPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import com.yy.ppm.statement.mapper.storageAmountCalculate.StorageAmountCalculateMapper;
import com.yy.ppm.statement.service.impl.storageFee.StorageFeeServiceImpl;
import com.yy.ppm.statement.service.storageAmountCalculate.StorageAmountCalculateService;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Component
@Conditional(EnvironmentCondition.class)
public class StorageAmountCalculateServiceImpl implements StorageAmountCalculateService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private StorageAmountCalculateMapper storageAmountCalculateMapper;

    private static final int CURSOR_LIMIT = 1_000;

    private static final ThreadPoolTaskExecutor TASK_EXECUTOR = new ThreadPoolTaskExecutor();

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    static {
        TASK_EXECUTOR.setQueueCapacity(Integer.MAX_VALUE);
        TASK_EXECUTOR.setCorePoolSize(CORE_SIZE * 2);
        TASK_EXECUTOR.setMaxPoolSize(CORE_SIZE * 2);
        TASK_EXECUTOR.setThreadNamePrefix("STORAGE_AMOUNT_CALCULATE_TASK_");
        TASK_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        TASK_EXECUTOR.initialize();
        TASK_EXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
    }

    @Autowired
    private Snowflake snowflake;

    @Getter
    enum FailedEnum {

        _10("10", "找不到匹配的合同"),

        _90("90", "OTHER");

        private final String code;

        private final String remark;

        FailedEnum(String code, String remark) {
            this.code = code;
            this.remark = remark;
        }
    }

    @Autowired
    private StorageFeeServiceImpl storageFeeServiceImpl;

    private static final int TASK_TIMEOUT = 120;

    private static final MicroLogger LOGGER = new MicroLogger(StorageAmountCalculateServiceImpl.class);

//    @Scheduled(cron = "*/30 * * * * *")
    //定时任务 计算堆存费
    @Override
    @Scheduled(cron = "0 0 21 * * *")
    public void calculate() {
        LOGGER.enter("开始计算金额");
        long l = System.currentTimeMillis();
        //清空之前两天的
        try {
            LocalDate twoDaysAgo = LocalDate.now().minus(2, ChronoUnit.DAYS);

            storageAmountCalculateMapper.delByTimeNow(twoDaysAgo.toString());
        }catch (Exception e){
            LOGGER.error("堆存费计算金额删除老数据报错："+e.getMessage());
        }
        transactionTemplate.executeWithoutResult(status -> {
            try (Cursor<TBusCargoInfoDTO> cursor = storageAmountCalculateMapper.cursorListCargoInfo(null)) {
                Iterator<TBusCargoInfoDTO> iterator = cursor.iterator();
                while (iterator.hasNext()) {
                    List<TBusCargoInfoDTO> cargoInfos = new ArrayList<>();
                    for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                        cargoInfos.add(iterator.next());
                    }
                    List<Long> cargoInfoIds = cargoInfos.stream().map(TBusCargoInfoDTO::getId).collect(Collectors.toList());
                    List<Map<String, Object>> contracts = storageAmountCalculateMapper.listContract(cargoInfoIds, null);
                    @SuppressWarnings("unchecked")
                    CompletableFuture<TCostStorageAmtCalcRecPO>[] futures = cargoInfos.stream()
                            .map(v1 -> TASK_EXECUTOR.submitListenable(() -> {
                                        try {
                                            TCostStorageAmtCalcRecPO storageAmtCalcRec = new TCostStorageAmtCalcRecPO();
                                            storageAmtCalcRec.setId(snowflake.nextId());
                                            storageAmtCalcRec.setCargoInfoId(v1.getId());
                                            storageAmtCalcRec.setOverdueDays(v1.getOverdueDays());
                                            storageAmtCalcRec.setFreeDays(v1.getFreeDays());
                                            storageAmtCalcRec.setCreateTime(new Date());
                                                storageAmtCalcRec.setIsSettlement(CommonEnum.YesNoMode.NO.getCode());
                                                List<TCostStorageSettleDetailDTO> details;
                                                try {
                                                    details = storageFeeServiceImpl.listDetailContainsHistory(v1.getId(), CommonEnum.YesNoMode.NO.getCode(), v1.getIsClear(), null,CommonEnum.YesNoMode.NO.getCode(),null);
                                                    if(!details.isEmpty()){
                                                        List<TCostStorageSettleDetailDTO> timeList = details.stream().sorted(Comparator.comparing(TCostStorageSettleDetailDTO::getDate)).collect(Collectors.toList());
                                                        storageAmtCalcRec.setCalcStartTime(timeList.get(0).getDate());
                                                        storageAmtCalcRec.setCalcEndTime(timeList.get(timeList.size()-1).getDate());
                                                    }
                                                } catch (Exception e) {
                                                    storageAmtCalcRec.setFailedCode(FailedEnum._90.getCode());
                                                    if (e.getMessage() != null) {
                                                        storageAmtCalcRec.setFailedRemark(e.getMessage().length() > 255 ? e.getMessage().substring(0, 254) : e.getMessage());
                                                    } else {
                                                        String errorText = StringUtil.getErrorText(e);
                                                        storageAmtCalcRec.setFailedRemark(errorText.length() > 255 ? errorText.substring(0, 254) : errorText);
                                                    }
                                                    return storageAmtCalcRec;
                                                }
                                                List<Map<String, Object>> tempContracts = contracts.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("cargoInfoId"))))).collect(Collectors.toList());
                                                if (tempContracts.isEmpty()) {
                                                    //判断该客户有没有合同
                                                    List<Long> contractIds = storageAmountCalculateMapper.getContractByCus(v1.getId());
                                                    if(!contractIds.isEmpty()){
                                                        //判断这些合同有没有在有效期内
                                                        contractIds = storageAmountCalculateMapper.getContractWithNotInTime(v1.getId());
                                                        if(!contractIds.isEmpty()){
                                                            //判断票货相关的作业的公司
                                                            contractIds = storageAmountCalculateMapper.getContractWithCompany(contractIds,v1.getId());
                                                            if(!contractIds.isEmpty()){
                                                                //判断合同有没有堆存费费率
                                                                contractIds = storageAmountCalculateMapper.getContractWithRate(contractIds,v1.getId());
                                                                if(!contractIds.isEmpty()){
                                                                    storageAmtCalcRec.setFailedCode(FailedEnum._10.getCode());
                                                                    storageAmtCalcRec.setFailedRemark(FailedEnum._10.getRemark()+contractIds.stream().map(o->String.valueOf(o)).collect(Collectors.joining(",")));
                                                                }else{
                                                                    storageAmtCalcRec.setFailedRemark("合同没有维护货物的堆存费费率");
                                                                }
                                                            }else{
                                                                storageAmtCalcRec.setFailedRemark("作业公司与合同的作业公司不匹配");
                                                            }
                                                        }else{
                                                            storageAmtCalcRec.setFailedRemark("合同有效期不匹配");
                                                        }
                                                    }else{
                                                        storageAmtCalcRec.setFailedRemark("系统中没有维护货主的合同");
                                                    }
                                                    return storageAmtCalcRec;
                                                }
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> contract = tempContracts.stream()
                                                        .min((o1, o2) -> BooleanUtils.toInteger(
                                                                (int) ((Map<String, Object>) o1.get("rate")).get("freeStorageDays") > (int) ((Map<String, Object>) o2.get("rate")).get("freeStorageDays"), 1, -1
                                                        )).get();
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> rate = (Map<String, Object>) contract.get("rate");
                                                try {
                                                    storageFeeServiceImpl.calculateAmount(
                                                            details,
                                                            v1.getId(),
                                                            (Integer) rate.get("freeStorageDays"),
                                                            (BigDecimal) rate.get("rate"),
                                                            (BigDecimal) rate.get("tax"),
                                                            CommonEnum.YesNoMode.NO.getCode(),
                                                            null
                                                    );
                                                    storageAmtCalcRec.setAmount(details.stream().map(TCostStorageSettleDetailPO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
                                                } catch (Exception e){
                                                    LOGGER.error("堆存费定时计算异常",Arrays.asList(e.getStackTrace()).get(0).toString());
                                                    storageAmtCalcRec.setFailedRemark(
                                                            e.getStackTrace()!=null ?   (Collections.singletonList(e.getStackTrace()).get(0).toString().length()>254?
                                                                                                                                Collections.singletonList(e.getStackTrace()).get(0).toString().substring(0,254) :
                                                                                                                                Collections.singletonList(e.getStackTrace()).get(0).toString()) :   null);
                                                }
                                            return storageAmtCalcRec;
                                        } catch (Exception ex) {
                                            throw new BusinessRuntimeException(StringUtil.getErrorText(ex));
                                        }
                                    })
                                    .completable())
                            .toArray(CompletableFuture[]::new);
                    try {
                        CompletableFuture.allOf(futures).get(TASK_TIMEOUT, TimeUnit.SECONDS);
                    } catch (Exception ex) {
                        for (CompletableFuture<?> v1 : futures) {
                            v1.cancel(false);
                        }
                        LOGGER.error("金额计算失败：" + Collections.singletonList(ex.getMessage()).get(0).toString());
                        if (ex instanceof TimeoutException) {
                            throw new BusinessRuntimeException("金额计算超时");
                        } else {
                            throw new BusinessRuntimeException("金额计算失败");
                        }
                    }
                    List<TCostStorageAmtCalcRecPO> storageAmtCalcRecs = Arrays.stream(futures)
                            .map(v1 -> {
                                try {
                                    return v1.get();
                                } catch (Exception ex) {
                                    // Not reachable
                                    return null;
                                }
                            })
                            .collect(Collectors.toList());
                    if (!storageAmtCalcRecs.isEmpty()) {
                        try {
                            storageAmountCalculateMapper.insertStorageAmountCalculateRecord(storageAmtCalcRecs);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        });
        long l1 = System.currentTimeMillis();
        LOGGER.exit("金额计算成功 用时："+(l1-l)+"毫秒");
    }
}
