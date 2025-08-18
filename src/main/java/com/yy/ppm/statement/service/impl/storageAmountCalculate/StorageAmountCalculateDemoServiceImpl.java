package com.yy.ppm.statement.service.impl.storageAmountCalculate;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.common.condition.EnvironmentCondition;
import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.statement.bean.dto.StorageDemoDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.TCostStorageAmtCalcRecPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettleDetailPO;
import com.yy.ppm.statement.mapper.storageAmountCalculate.StorageAmountCalculateMapper;
import com.yy.ppm.statement.service.impl.storageFee.StorageFeeServiceImpl;
import com.yy.ppm.statement.service.storageAmountCalculate.StorageAmountCalculateDemoService;
import com.yy.ppm.statement.service.storageAmountCalculate.StorageAmountCalculateService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.cursor.Cursor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Slf4j
public class StorageAmountCalculateDemoServiceImpl implements StorageAmountCalculateDemoService {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private StorageAmountCalculateMapper storageAmountCalculateMapper;

    private static final int CURSOR_LIMIT = 100;

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

    private static final MicroLogger LOGGER = new MicroLogger(StorageAmountCalculateDemoServiceImpl.class);

//    @Scheduled(cron = "*/30 * * * * *")
    //定时任务 计算堆存费
    @Override
    @Scheduled(cron = "0 30 20 * * *")
    public void calculate() {
        LOGGER.enter("开始计算金额");
        long l = System.currentTimeMillis();
        Date tmp_date = new Date();
        //清空之前两天的
        try {
            LocalDate twoDaysAgo = LocalDate.now().minus(2, ChronoUnit.DAYS);

            storageAmountCalculateMapper.delByTimeNowDemo(twoDaysAgo.toString());
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
                    CompletableFuture<StorageDemoDTO>[] futures = cargoInfos.stream()
                            .map(v1 -> TASK_EXECUTOR.submitListenable(() -> {
                                        try {
                                            StorageDemoDTO storageDemoDTO = new StorageDemoDTO();
//                                            TCostStorageAmtCalcRecPO storageAmtCalcRec = new TCostStorageAmtCalcRecPO();
                                            storageDemoDTO.setId(snowflake.nextId());
                                            storageDemoDTO.setCargoInfoId(v1.getId());
                                            storageDemoDTO.setOverDays(String.valueOf(v1.getOverdueDays()));
                                            storageDemoDTO.setFreeDays(v1.getFreeDays());
                                            storageDemoDTO.setCreateTime(tmp_date);
                                            storageDemoDTO.setIsClear(v1.getIsClear());
                                            storageDemoDTO.setCargoOwnerId(v1.getCargoOwnerId());
                                            storageDemoDTO.setCargoOwnerName(v1.getCargoOwnerName());
                                            storageDemoDTO.setCompanyName(v1.getCompanyName());
                                            storageDemoDTO.setCargoCategoryName(v1.getCargoCategoryName());
                                            storageDemoDTO.setCargoTypeName(v1.getCargoTypeName());
                                            storageDemoDTO.setCargoName(v1.getCargoName());
                                            storageDemoDTO.setSettleStatus(v1.getSettleStatus());
                                            storageDemoDTO.setTradeType(v1.getTradeType());
                                            storageDemoDTO.setShipName(v1.getShipNameVoyage());
                                            storageDemoDTO.setScn(v1.getScn());
                                            storageDemoDTO.setSource(v1.getSource());
                                            storageDemoDTO.setCargoInfoTime(v1.getCreateTime());
                                            storageDemoDTO.setWorkType(v1.getWorkTypeLabel());
                                            storageDemoDTO.setFlowDirection(v1.getFlowDirection());
                                            storageDemoDTO.setCargoInfoNo(v1.getCargoInfoNo());
                                            storageDemoDTO.setOverDays(v1.getOverdueDays()==null?"":String.valueOf(v1.getOverdueDays()));
                                            storageDemoDTO.setFreeDays(v1.getFreeDays());
                                            storageDemoDTO.setOverDate(v1.getOverDate());
                                            storageDemoDTO.setCreateTime(tmp_date);


                                                List<TCostStorageSettleDetailDTO> details;
                                                try {
                                                    details = storageFeeServiceImpl.listDetailContainsHistory(v1.getId(), CommonEnum.YesNoMode.NO.getCode(), v1.getIsClear(), null,CommonEnum.YesNoMode.NO.getCode(),null);
                                                    if(!details.isEmpty()){
                                                        List<TCostStorageSettleDetailDTO> timeList = details.stream().sorted(Comparator.comparing(TCostStorageSettleDetailDTO::getDate)).collect(Collectors.toList());
                                                        storageDemoDTO.setCalStartTime(timeList.get(0).getDate());
                                                        storageDemoDTO.setCalEndTime(timeList.get(timeList.size()-1).getDate());
                                                    }
                                                } catch (Exception e) {
                                                    if (e.getMessage() != null) {
                                                        storageDemoDTO.setRemark(e.getMessage().length() > 255 ? e.getMessage().substring(0, 254) : e.getMessage());
                                                    } else {
                                                        String errorText = StringUtil.getErrorText(e);
                                                        storageDemoDTO.setRemark(errorText.length() > 255 ? errorText.substring(0, 254) : errorText);
                                                    }
                                                    return storageDemoDTO;
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
                                                                    storageDemoDTO.setRemark(FailedEnum._10.getCode());
                                                                    storageDemoDTO.setRemark(FailedEnum._10.getRemark()+contractIds.stream().map(o->String.valueOf(o)).collect(Collectors.joining(",")));
                                                                }else{
                                                                    storageDemoDTO.setRemark("合同没有维护货物的堆存费费率");
                                                                }
                                                            }else{
                                                                storageDemoDTO.setRemark("作业公司与合同的作业公司不匹配");
                                                            }
                                                        }else{
                                                            storageDemoDTO.setRemark("合同有效期不匹配");
                                                        }
                                                    }else{
                                                        storageDemoDTO.setRemark("系统中没有维护货主的合同");
                                                    }
                                                    return storageDemoDTO;
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
                                                    storageDemoDTO.setAmount(String.valueOf(details.stream().map(TCostStorageSettleDetailPO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)));
                                                } catch (Exception e){
                                                    LOGGER.error("堆存费定时计算异常",Arrays.asList(e.getStackTrace()).get(0).toString());
                                                    storageDemoDTO.setRemark(
                                                            e.getStackTrace()!=null ?   (Collections.singletonList(e.getStackTrace()).get(0).toString().length()>254?
                                                                                                                                Collections.singletonList(e.getStackTrace()).get(0).toString().substring(0,254) :
                                                                                                                                Collections.singletonList(e.getStackTrace()).get(0).toString()) :   null);
                                                }
                                            return storageDemoDTO;
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
                    List<StorageDemoDTO> storageAmtCalcRecs = Arrays.stream(futures)
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
                            log.info("开始插入{}",storageAmtCalcRecs.get(0).toString());
                            storageAmountCalculateMapper.insertDemo(storageAmtCalcRecs);
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

    @Override
    public Pages<StorageDemoDTO> getList(StorageDemoDTO dto) {
        return PageHelperUtils.limit(dto, () -> {
            Page<StorageDemoDTO> listDemo = storageAmountCalculateMapper.getListDemo(dto);
            listDemo.stream().forEach(o->o.setShipName("_".equals(o.getShipName())?"":o.getShipName()));
            return listDemo;
        });
    }
}
