package com.yy.ppm.produce.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Maps;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtil;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SpringUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.concurrent.DistributedReentrantLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.CleanMassSignEnum;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.enums.InoutStorageEnum;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.produce.bean.dto.portStorage.*;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.mapper.TPrdPortStorageMapper;
import com.yy.ppm.produce.service.TPrdPortStorageService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import cn.hutool.core.bean.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:41
 */
@Service
public class TPrdPortStorageServiceImpl implements TPrdPortStorageService {

    @Autowired
    private TPrdPortStorageMapper tPrdPortStorageMapper;

    private static final String JZ_PROCESS_DETAIL_CODE = "10090001";

    private static final String JZ_PROCESS_DETAIL_NAME = "记账（子过程）";

    @Autowired
    private BusinessCommonService businessCommonService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String QC_PROCESS_DETAIL_CODE = "10090002";

    private static final String QC_PROCESS_DETAIL_NAME = "清场（子过程）";

    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final int CURSOR_LIMIT = 5_000;

    @Autowired
    private PublicService publicService;

    @Override
    public Pages<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdPortStorageMapper.listPortStorage(query);
        });
    }

    @Override
    public Pages<TPrdPortStorageGbCargoInfoDTO> listPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdPortStorageMapper.listPortStorageGbCargoInfo(query);
        });
    }

    @Override
    public Pages<TPrdPortStorageGbCargoOwnerDTO> listPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdPortStorageMapper.listPortStorageGbCargoOwner(query);
        });
    }

    @Override
    public Pages<TPrdPortStorageGbCargoDTO> listPortStorageGbCargo(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tPrdPortStorageMapper.listPortStorageGbCargo(query);
        });
    }

    @Override
    public Map<String, Object> summaryQuantityTon(TPrdPortStorageQueryDTO query) {
        Map<String, Object> totalQuantityTon = tPrdPortStorageMapper.summaryQuantityTon(query);
        return Optional.ofNullable(totalQuantityTon).orElse(Collections.emptyMap());
    }

    @Override
    public void insertPortStorage(List<TPrdPortStorageDetailPO> portStorageDetails) {
        Map<String,String> maps = Maps.newHashMap();
        maps.put("10090001","记账（子过程）");
        maps.put("10520001","集改散（子过程）");
        portStorageDetails.forEach(v1 -> {
            Map<String, Object> map = publicService.getDateAndShift(null);
            v1.setWorkDate(DateUtils.parseDate((String) map.get("workDate"), "yyyy-MM-dd"));
            v1.setClassCode((String) map.get("classCode"));
            v1.setClassName((String) map.get("className"));
//            v1.setProcessDetailCode(JZ_PROCESS_DETAIL_CODE);
            v1.setProcessDetailName(maps.get(v1.getProcessDetailCode()));
            v1.setInoutStorageCode(InoutStorageEnum._30.getCode());
            v1.setInoutStorageName(InoutStorageEnum._30.getLabel());
            v1.setInoutDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
            v1.setCleanMassSign(CleanMassSignEnum._0.getCode());
        });
        businessCommonService.insertPortStorageDetail(portStorageDetails);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, noRollbackFor = BusinessRuntimeException.class)
    public void cleanPortStorage(CleanPortStorageDTO cleanPortStorage) {
        DistributedReentrantLock.newBuilder().store(redisTemplate)
                .key(
                        DistributedLockKeyPrefixEnum.PORT_STORAGE_KEY.getCode()
                                + String.format("%s-%s-%s-%s", cleanPortStorage.getCargoInfoId(), cleanPortStorage.getStorehouseId(), cleanPortStorage.getRegionId(), cleanPortStorage.getMassId())
                )
                .build().run(() -> {
                    List<TPrdPortStorageDetailPO> portStorageDetails = tPrdPortStorageMapper.listPortStorageDetail(
                            cleanPortStorage.getCargoInfoId(), cleanPortStorage.getStorehouseId(), cleanPortStorage.getRegionId()
                            , cleanPortStorage.getMassId(), null, null, null, null, null
                    );
                    if (CleanMassSignEnum._1.getCode().equals(portStorageDetails.get(0).getCleanMassSign())) {
                        throw new BusinessRuntimeException("当前港存已清场");
                    }
                    BigDecimal totalTon = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add).multiply(BigDecimal.valueOf(-1));
//                    if (totalTon.compareTo(BigDecimal.ZERO) == 0) {
//                        throw new BusinessRuntimeException("当前港存无需清场");
//                    }

                    TPrdPortStorageDetailPO portStorageDetail = new TPrdPortStorageDetailPO();
                    portStorageDetail.setCargoInfoId(cleanPortStorage.getCargoInfoId());
                    portStorageDetail.setWorkDate(cleanPortStorage.getWorkDate());
                    portStorageDetail.setClassCode(cleanPortStorage.getClassCode());
                    portStorageDetail.setClassName(cleanPortStorage.getClassName());
                    portStorageDetail.setProcessDetailCode(QC_PROCESS_DETAIL_CODE);
                    portStorageDetail.setProcessDetailName(QC_PROCESS_DETAIL_NAME);
                    portStorageDetail.setStorehouseId(cleanPortStorage.getStorehouseId());
                    portStorageDetail.setStorehouseName(portStorageDetails.get(0).getStorehouseName());
                    portStorageDetail.setRegionId(cleanPortStorage.getRegionId());
                    portStorageDetail.setRegionName(portStorageDetails.get(0).getRegionName());
                    portStorageDetail.setMassId(cleanPortStorage.getMassId());
                    portStorageDetail.setMassName(portStorageDetails.get(0).getMassName());
                    if (!portStorageDetails.stream().allMatch(v1 -> v1.getQuantity() == null)) {
                        portStorageDetail.setQuantity(
                                portStorageDetails.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum() * -1
                        );
                    }
                    portStorageDetail.setTon(totalTon);
                    portStorageDetail.setInoutStorageCode(InoutStorageEnum._50.getCode());
                    portStorageDetail.setInoutStorageName(InoutStorageEnum._50.getLabel());
                    portStorageDetail.setInoutDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
                    portStorageDetail.setCompanyId(portStorageDetails.get(0).getCompanyId());
                    portStorageDetail.setCompanyName(portStorageDetails.get(0).getCompanyName());
                    portStorageDetail.setCleanMassSign(CleanMassSignEnum._1.getCode());
                    businessCommonService.insertPortStorageDetail(Collections.singletonList(portStorageDetail));

                    Map<String, Object> param = new HashMap<>();
                    param.put("cargoInfoId", cleanPortStorage.getCargoInfoId());
                    param.put("storehouseId", cleanPortStorage.getStorehouseId());
                    param.put("regionId", cleanPortStorage.getRegionId());
                    param.put("massId", cleanPortStorage.getMassId());
                    tPrdPortStorageMapper.cleanPortStorage(param);
                    tPrdPortStorageMapper.cleanPortStorageDetail(param);
                });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, noRollbackFor = BusinessRuntimeException.class)
    public void cancelCleanPortStorage(CancelCleanPortStorageDTO cancelCleanPortStorage) {
        DistributedReentrantLock.newBuilder().store(redisTemplate)
                .key(
                        DistributedLockKeyPrefixEnum.PORT_STORAGE_KEY.getCode()
                                + String.format("%s-%s-%s-%s", cancelCleanPortStorage.getCargoInfoId(), cancelCleanPortStorage.getStorehouseId(), cancelCleanPortStorage.getRegionId(), cancelCleanPortStorage.getMassId())
                )
                .build().run(() -> {
                    List<TPrdPortStorageDetailPO> portStorageDetails = tPrdPortStorageMapper.listPortStorageDetail(
                            cancelCleanPortStorage.getCargoInfoId(), cancelCleanPortStorage.getStorehouseId(), cancelCleanPortStorage.getRegionId()
                            , cancelCleanPortStorage.getMassId(), null, null, null, null, null
                    );
                    if (CleanMassSignEnum._0.getCode().equals(portStorageDetails.get(0).getCleanMassSign())) {
                        throw new BusinessRuntimeException("当前港存未清场，无需撤销");
                    }
                    int count = tPrdPortStorageMapper.getIsClearByCargoInfoID(cancelCleanPortStorage.getCargoInfoId());
                    if(count>0){
                        throw new BusinessRuntimeException("当前票货已完货，无法撤销");
                    }

                    Map<String, Object> param = new HashMap<>();
                    param.put("cargoInfoId", cancelCleanPortStorage.getCargoInfoId());
                    param.put("storehouseId", cancelCleanPortStorage.getStorehouseId());
                    param.put("regionId", cancelCleanPortStorage.getRegionId());
                    param.put("massId", cancelCleanPortStorage.getMassId());
                    tPrdPortStorageMapper.cancelCleanPortStorage(param);
                    tPrdPortStorageMapper.cancelCleanPortStorageDetail(param);

//                    TPrdPortStorageDetailPO portStorageDetail = portStorageDetails.stream()
//                            .filter(v1 -> InoutStorageEnum._50.getCode().equals(v1.getInoutStorageCode()))
//                            .findFirst()
//                            .orElseThrow(null);
//                    businessCommonService.deletePortStorageDetail(Collections.singletonList(portStorageDetail.getId()));
            //清场逻辑调整为 将原来的用于平帐的清场记录的量变为0作业过程变更为撤销清场； 主表上的量加上去
                    TPrdPortStorageDetailPO tPrdPortStorageDetailPO = portStorageDetails.stream()
                            .filter(v1 -> InoutStorageEnum._50.getCode().equals(v1.getInoutStorageCode()))
                            .sorted(Comparator.comparing(TPrdPortStorageDetailPO::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                            .findFirst()
                            .orElseThrow(null);
                    //调整流水子表
                    tPrdPortStorageDetailPO.setProcessDetailCode("10090003");
                    tPrdPortStorageDetailPO.setProcessDetailName("撤销清场（子过程）");
                    Map<String, Object> dateAndShift = publicService.getDateAndShift(LocalDateTime.now());
                    tPrdPortStorageDetailPO.setClassCode(String.valueOf(dateAndShift.get("classCode")));
                    tPrdPortStorageDetailPO.setClassName(String.valueOf(dateAndShift.get("className")));
                    tPrdPortStorageDetailPO.setWorkDate(DateUtil.localDate2Date(DateUtil.str2LocalDate(String.valueOf(dateAndShift.get("workDate")))));
                    tPrdPortStorageDetailPO.setInoutStorageCode(InoutStorageEnum._80.getCode());
                    tPrdPortStorageDetailPO.setInoutStorageName(InoutStorageEnum._80.getLabel());
                    tPrdPortStorageMapper.revokeCleanStorageDetail(tPrdPortStorageDetailPO);
                    //调整流水主表
                    TPrdPortStorageDTO prdPortStorageDTO = tPrdPortStorageMapper.getPortStorage(tPrdPortStorageDetailPO.getPortStorageId());
                    if(tPrdPortStorageDetailPO.getQuantity()==null){
                        tPrdPortStorageDetailPO.setQuantity(0);
                    }
                   if(tPrdPortStorageDetailPO.getTon()==null){
                       tPrdPortStorageDetailPO.setTon(BigDecimal.ZERO);
                   }
                    if("1".equals(tPrdPortStorageDetailPO.getInoutType())){
                        prdPortStorageDTO.setTon(prdPortStorageDTO.getTon()==null?BigDecimal.ZERO.subtract(tPrdPortStorageDetailPO.getTon()):prdPortStorageDTO.getTon().subtract(tPrdPortStorageDetailPO.getTon()));
                        prdPortStorageDTO.setQuantity(prdPortStorageDTO.getQuantity()==null? 0-tPrdPortStorageDetailPO.getQuantity():(prdPortStorageDTO.getQuantity()-tPrdPortStorageDetailPO.getQuantity()));
                    }else if("2".equals(tPrdPortStorageDetailPO.getInoutType())){
                        prdPortStorageDTO.setTon(prdPortStorageDTO.getTon()==null?BigDecimal.ZERO.add(tPrdPortStorageDetailPO.getTon()):prdPortStorageDTO.getTon().add(tPrdPortStorageDetailPO.getTon()));
                        prdPortStorageDTO.setQuantity(prdPortStorageDTO.getQuantity()==null? 0+tPrdPortStorageDetailPO.getQuantity():(prdPortStorageDTO.getQuantity()+tPrdPortStorageDetailPO.getQuantity()));
                    }
                    tPrdPortStorageMapper.revokeCleanStorage(prdPortStorageDTO);
                });
    }

    @Override
    public Map<String, Object> getInoutDetail(InoutDetailQueryDTO query) {
        List<TPrdPortStorageDetailPO> portStorageDetails = tPrdPortStorageMapper.listPortStorageDetail(
                query.getCargoInfoId(), query.getStorehouseId(), query.getRegionId(), query.getMassId(), query.getBeginWorkDate()
                , query.getBeginClassCode(), query.getEndWorkDate(), query.getEndClassCode(), query.getProcessDetailCode()
        );
        for (TPrdPortStorageDetailPO portStorageDetail : portStorageDetails) {
            if(StringUtil.isEmpty(portStorageDetail.getCreateByName())){
                portStorageDetail.setCreateByName(portStorageDetail.getStorageCreateByName());
            }
            if(ObjectUtil.isEmpty(portStorageDetail.getCreateTime())){
                portStorageDetail.setCreateTime(portStorageDetail.getStorageCreateTime());
            }
        }
        Map<Boolean, List<TPrdPortStorageDetailPO>> groupByCompareToZero = portStorageDetails.stream()
                .collect(Collectors.groupingBy(v1 -> v1.getTon().compareTo(BigDecimal.ZERO) > 0));

        List<TPrdPortStorageDetailPO> in = Optional.ofNullable(groupByCompareToZero.get(true)).orElse(Collections.emptyList());
        List<TPrdPortStorageDetailPO> out = Optional.ofNullable(groupByCompareToZero.get(false)).orElse(Collections.emptyList()).stream().peek(v1 -> {
            if (v1.getQuantity() != null) {
                v1.setQuantity(v1.getQuantity() * -1);
            }
            if (v1.getTon() != null) {
                v1.setTon(v1.getTon().multiply(BigDecimal.valueOf(-1)));
            }
        }).collect(Collectors.toList());

        Integer inQuantity = null;
        if (!in.stream().allMatch(v1 -> v1.getQuantity() == null)) {
            inQuantity = in.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
        }
        BigDecimal inTon = in.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer outQuantity = null;
        if (!out.stream().allMatch(v1 -> v1.getQuantity() == null)) {
            outQuantity = out.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
        }
        BigDecimal outTon = out.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer balanceQuantity = null;
        if (!(inQuantity == null && outQuantity == null)) {
            balanceQuantity = Optional.ofNullable(inQuantity).orElse(0) - Optional.ofNullable(outQuantity).orElse(0);
        }
        BigDecimal balanceTon = inTon.subtract(outTon);

        HashMap<String, Object> result = new HashMap<>();
        result.put("in", in);
        result.put("inQuantity", inQuantity);
        result.put("inTon", inTon);
        result.put("out", out);
        result.put("outQuantity", outQuantity);
        result.put("outTon", outTon);
        result.put("balanceQuantity", balanceQuantity);
        result.put("balanceTon", balanceTon);
        return result;
    }

    @Override
    public byte[] exportPortStorage(TPrdPortStorageQueryDTO query) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdPortStorageDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdPortStorageDTO> cursor = tPrdPortStorageMapper.cursorListPortStorage(query)) {
                    Iterator<TPrdPortStorageDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<TPrdPortStorageDTO> portStorages = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            portStorages.add(iterator.next());
                        }
                        excelWriter.write(portStorages, writeSheet);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    public byte[] exportPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdPortStorageGbCargoInfoDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdPortStorageGbCargoInfoDTO> cursor = tPrdPortStorageMapper.cursorListPortStorageGbCargoInfo(query)) {
                    Iterator<TPrdPortStorageGbCargoInfoDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<TPrdPortStorageGbCargoInfoDTO> portStorages = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            portStorages.add(iterator.next());
                        }
                        excelWriter.write(portStorages, writeSheet);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    public byte[] exportPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdPortStorageGbCargoOwnerDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdPortStorageGbCargoOwnerDTO> cursor = tPrdPortStorageMapper.cursorListPortStorageGbCargoOwner(query)) {
                    Iterator<TPrdPortStorageGbCargoOwnerDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<TPrdPortStorageGbCargoOwnerDTO> portStorages = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            portStorages.add(iterator.next());
                        }
                        excelWriter.write(portStorages, writeSheet);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    public byte[] exportPortStorageGbCargo(TPrdPortStorageQueryDTO query) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TPrdPortStorageGbCargoDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TPrdPortStorageGbCargoDTO> cursor = tPrdPortStorageMapper.cursorListPortStorageGbCargo(query)) {
                    Iterator<TPrdPortStorageGbCargoDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<TPrdPortStorageGbCargoDTO> portStorages = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            portStorages.add(iterator.next());
                        }
                        excelWriter.write(portStorages, writeSheet);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    public Map<String, Object> getCargoInoutDetail(InoutDetailQueryDTO query) {
        List<TPrdPortStorageDetailPO> portStorageDetails = tPrdPortStorageMapper.getCargoInoutDetail(
                query.getCargoInfoId(), query.getBeginWorkDate(),query.getInoutType()
                , query.getBeginClassCode(), query.getEndWorkDate(), query.getEndClassCode(), query.getProcessDetailCode()
        );
        Map<String, List<TPrdPortStorageDetailPO>> groupByCompareToZero = portStorageDetails.stream()
                .collect(Collectors.groupingBy(TPrdPortStorageDetailPO::getInoutType));

        List<TPrdPortStorageDetailPO> in = Optional.ofNullable(groupByCompareToZero.get("入库")).orElse(Collections.emptyList());
        List<TPrdPortStorageDetailPO> out = Optional.ofNullable(groupByCompareToZero.get("出库")).orElse(Collections.emptyList()).stream().peek(v1 -> {
            if (v1.getQuantity() != null) {
                v1.setQuantity(v1.getQuantity() * -1);
            }
            if (v1.getTon() != null) {
                v1.setTon(v1.getTon().multiply(BigDecimal.valueOf(-1)));
            }
        }).collect(Collectors.toList());

        Integer inQuantity = null;
        if (!in.stream().allMatch(v1 -> v1.getQuantity() == null)) {
            inQuantity = in.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
        }
        BigDecimal inTon = in.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer outQuantity = null;
        if (!out.stream().allMatch(v1 -> v1.getQuantity() == null)) {
            outQuantity = out.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
        }
        BigDecimal outTon = out.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer balanceQuantity = null;
        if (!(inQuantity == null && outQuantity == null)) {
            balanceQuantity = Optional.ofNullable(inQuantity).orElse(0) - Optional.ofNullable(outQuantity).orElse(0);
        }
        BigDecimal balanceTon = inTon.subtract(outTon);

        HashMap<String, Object> result = new HashMap<>();
        result.put("inOut", portStorageDetails);
        result.put("inQuantity", inQuantity);
        result.put("inTon", inTon);
        result.put("outQuantity", outQuantity);
        result.put("outTon", outTon);
        result.put("balanceQuantity", balanceQuantity);
        result.put("balanceTon", balanceTon);
        return result;
    }

    @Override
    @SneakyThrows
    public byte[] stackSigns(List<StackSignReq> reqList, HttpServletResponse response) {
        reqList.forEach(o->{
            if(StringUtils.isNotBlank(o.getSheetName())){
                throw new BusinessRuntimeException("电子货垛牌:不应出现的请求参数");
            }
        });
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        reqList.stream().forEach(o->o.setSheetName(o.getCargoInfoId().toString()+"_"+o.getStackId().toString()));
        List<StackSignReq> resultData = tPrdPortStorageMapper.getStackSigns(reqList);
        Map<String, StackSignReq> collect = reqList.stream().collect(Collectors.toMap(o -> o.getCargoInfoId().toString() + "_" + o.getStackId().toString(), Function.identity(), (k1, k2) -> k1));
        InputStream
            ipsSource = SpringUtils.getBean(this.getClass()).createSheetFromTemplate(
                    this.getClass().getClassLoader().getResourceAsStream("exceltemplates/stackSignModel.xlsx"),
                    resultData.stream().filter(o->StringUtils.isNotBlank(o.getSheetName())).map(StackSignReq::getSheetName).collect(Collectors.toList()));
       List<CellRangeAddress> addressList = new ArrayList<>();
        addressList.add(new CellRangeAddress(                1,                1,                3,                4));
        addressList.add(new CellRangeAddress(                0,                0,                3,                4));
        addressList.add(new CellRangeAddress(1,1,6,7));
        addressList.add(new CellRangeAddress(0,0,6,7));

        try(ExcelWriter writer = EasyExcel.write().file(response.getOutputStream()).withTemplate(ipsSource).build()
            ){
            for (StackSignReq stackSignReq : resultData) {
                WriteSheet sheetWriter = EasyExcel.writerSheet(stackSignReq.getSheetName())
                        .registerWriteHandler(new SheetWriteHandler() {
                    @Override
                    public void afterSheetCreate(SheetWriteHandlerContext context) {
                        Sheet sheet = context.getWriteSheetHolder().getSheet();
                        for (CellRangeAddress cellAddresses : addressList) {
                            sheet.addMergedRegion(cellAddresses);
                        }
                        SheetWriteHandler.super.afterSheetCreate(context);
                    }
                }).build();
                stackSignReq.setShipNameVoyage(collect.get(stackSignReq.getCargoInfoId().toString()+"_"+stackSignReq.getStackId().toString()).getShipNameVoyage());
                writer.fill(BeanUtil.beanToMap(stackSignReq),sheetWriter);
            }
        }
        return ops.toByteArray();
    }

    @SneakyThrows
    public static InputStream createSheetFromTemplate(InputStream inputStream, List<String> sheetNames) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        workbook.setSheetName(0, sheetNames.get(0));
        for (int i = 1; i < sheetNames.size(); i++) {
            int num = i + 1;
            workbook.cloneSheet(0, sheetNames.get(i));
        }
        //写到流里
        workbook.write(bos);
        byte[] bArray = bos.toByteArray();
        InputStream is = new ByteArrayInputStream(bArray);
        return is;
    }
}
