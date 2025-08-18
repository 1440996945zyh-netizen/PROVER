package com.yy.ppm.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.github.pagehelper.Page;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SpringUtils;
import com.yy.common.util.ValidatorUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.cargoInfo.CleanAllPortStorageDTO;
import com.yy.ppm.business.bean.dto.cargoInfo.ExportDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.business.mapper.TBusCargoInfoMapper;
import com.yy.ppm.business.mapper.TBusCargoTransferMapper;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.business.service.TBusCargoInfoService;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.produce.bean.dto.portStorage.CancelCleanPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.CleanPortStorageDTO;
import com.yy.ppm.produce.service.TPrdPortStorageService;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cursor.Cursor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 票货信息表(TBusCargoInfo)ServiceImpl
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
@Service
@Slf4j
public class TBusCargoInfoServiceImpl implements TBusCargoInfoService {

    @Resource
    private TBusCargoInfoMapper tBusCargoInfoMapper;
    @Resource
    private TBusCargoTransferMapper tBusCargoTransferMapper;
    @Resource
    private CommonService commonService;
    @Resource
    private BusinessCommonService businessCommonService;
    @Resource
    private Snowflake snowflake;

    @Autowired
    private TPrdPortStorageService tPrdPortStorageService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Resource
    private TBusTrustMapper tBusTrustMapper;

    private static final int CURSOR_LIMIT = 300;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TBusCargoInfoDTO> getList(TBusCargoInfoSearchDTO searchDTO) {

        Pages<TBusCargoInfoDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            Page<TBusCargoInfoDTO> page = tBusCargoInfoMapper.getList(searchDTO);

            List<Long> idList = page.stream()
                    .map(TBusCargoInfoDTO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            List<Map<String, String>> kmMap = Collections.emptyList();
            if (!idList.isEmpty()) {
                kmMap = tBusCargoInfoMapper.getTrustKM(idList);
            }
            List<Map<String, String>> finalKmMap = kmMap;
            page.forEach(e->{
                finalKmMap.forEach(item->{
                    if(String.valueOf(item.get("key")).equals(String.valueOf(e.getId()))){
                        e.setTrustNos(item.get("value"));
                    }
                });
            });
            if (!page.isEmpty()) {
                List<Long> ids = page.stream().map(TBusCargoInfoDTO::getId).collect(Collectors.toList());
                List<Map<String, Object>> handoverlistTons = tBusCargoInfoMapper.listHandoverlistTon(ids);
                //List<Map<String, Object>> trustCargoTons = tBusCargoInfoMapper.listTrustCargoTon(ids);
                List<Map<String, Object>> trustCargoTonsJG = tBusCargoInfoMapper.listTrustCargoTonJG(ids);
                List<Map<String, Object>> trustCargoTonsSG = tBusCargoInfoMapper.listTrustCargoTonSG(ids);
                //List<Map<String, Object>> weightGoodss = tBusCargoInfoMapper.listWeightGoodss(ids);
                List<Map<String, Object>> weightGoodssJG = tBusCargoInfoMapper.listWeightGoodssJG(ids);
                List<Map<String, Object>> weightGoodssSG = tBusCargoInfoMapper.listWeightGoodssSG(ids);
                List<Map<String, Object>> releaseNameTimes = tBusCargoInfoMapper.listReleaseNameTime(ids);

                page.forEach(v1 -> {
                    List<Map<String, Object>> _handoverlistTons = handoverlistTons.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).collect(Collectors.toList());
                    //Map<String, Object> trustCargoTon = trustCargoTons.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    Map<String, Object> trustCargoTonJG = trustCargoTonsJG.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    Map<String, Object> trustCargoTonSG = trustCargoTonsSG.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    //Map<String, Object> weightGoods = weightGoodss.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    Map<String, Object> weightGoodsJG = weightGoodssJG.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    Map<String, Object> weightGoodsSG = weightGoodssSG.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                    v1.setHandoverlistTon(_handoverlistTons.stream().filter(v2 -> !"卸船".equals(v1.getType()) || "卸".equals(v2.get("loadUnload"))).map(v2 -> new BigDecimal(String.valueOf(v2.get("handoverlistTon")))).reduce(BigDecimal.ZERO, BigDecimal::add));
                    //v1.setTrustCargoTon(trustCargoTon.get("trustCargoTon") == null ? null : new BigDecimal(String.valueOf(trustCargoTon.get("trustCargoTon"))));
                    v1.setTrustCargoTonJG(trustCargoTonJG.get("trustCargoTon") == null ? null : new BigDecimal(String.valueOf(trustCargoTonJG.get("trustCargoTon"))));
                    v1.setTrustCargoTonSG(trustCargoTonSG.get("trustCargoTon") == null ? null : new BigDecimal(String.valueOf(trustCargoTonSG.get("trustCargoTon"))));
                    //v1.setWeightGoods(weightGoods.get("weightGoods") == null ? null : new BigDecimal(String.valueOf(weightGoods.get("weightGoods"))));
                    v1.setWeightGoodsJG(weightGoodsJG.get("weightGoods") == null ? null : new BigDecimal(String.valueOf(weightGoodsJG.get("weightGoods"))));
                    v1.setWeightGoodsSG(weightGoodsSG.get("weightGoods") == null ? null : new BigDecimal(String.valueOf(weightGoodsSG.get("weightGoods"))));

//                    v1.setBalanceTon(Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getTrustCargoTon()).orElse(BigDecimal.ZERO)));
                    v1.setBalanceTonJG(Optional.ofNullable(v1.getTrustCargoTonJG()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getWeightGoodsJG()).orElse(BigDecimal.ZERO)));
                    v1.setBalanceTonSG( Optional.ofNullable(v1.getTrustCargoTonSG()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getWeightGoodsSG()).orElse(BigDecimal.ZERO)));
                    if (CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsClear())) {
                        v1.setBalancePortStorageTon(BigDecimal.ZERO);
                    } /*else {
                        v1.setBalancePortStorageTon(Optional.ofNullable(v1.getHandoverlistTon()).orElse(BigDecimal.ZERO).
                                subtract((Optional.ofNullable(v1.getWeightGoodsJG()).orElse(BigDecimal.ZERO)).add((Optional.ofNullable(v1.getWeightGoodsSG()).orElse(BigDecimal.ZERO)))).
                                multiply("卸船".equals(v1.getType()) ? BigDecimal.ONE : BigDecimal.valueOf(-1)));
                    }*/
                    List<Map<String, Object>> tempReleaseNameTimes = releaseNameTimes.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).collect(Collectors.toList());
                    String releaseByNames = tempReleaseNameTimes.stream().map(v2 -> String.valueOf(v2.get("releaseByName"))).distinct().collect(Collectors.joining(", "));
                    String releaseTimes = tempReleaseNameTimes.stream().map(v2 -> String.valueOf(v2.get("releaseTime"))).findFirst().orElse(null);
                    v1.setReleaseByNames(releaseByNames);
                    v1.setReleaseTimes(releaseTimes);
                });
                //特殊处理，给票货管理排序-start
                Map<Integer,List<TBusCargoInfoDTO>> cargoInFoMap = page.stream().collect(Collectors.groupingBy(TBusCargoInfoDTO::getOrderNum));
                if(CollectionUtil.isNotEmpty(cargoInFoMap.get(-555))){
                    cargoInFoMap.get(-555).sort(Comparator.comparing(TBusCargoInfoDTO::getOverdueDaysInt).reversed());
                }
                if(CollectionUtil.isNotEmpty(cargoInFoMap.get(-666))){
                    cargoInFoMap.get(-666).sort(Comparator.comparing(TBusCargoInfoDTO::getOverdueDaysInt).reversed());
                }
                if(CollectionUtil.isNotEmpty(cargoInFoMap.get(-777))){
                    cargoInFoMap.get(-777).sort(Comparator.comparing(TBusCargoInfoDTO::getOverdueDaysInt).reversed());
                }
                if(CollectionUtil.isNotEmpty(cargoInFoMap.get(-888))){
                    cargoInFoMap.get(-888).sort(Comparator.comparing(TBusCargoInfoDTO::getOverdueDaysInt).reversed());
                }
                List<Integer> sortList = Arrays.asList(-555,-666,-777,-888,-900,-999);
                page.clear();
                sortList.forEach(k->
                    {
                        if(CollectionUtil.isNotEmpty(cargoInFoMap.get(k))){
                            page.addAll(cargoInFoMap.get(k));
                        }
                    }
                );
                //特殊处理，给票货管理排序-end
            }
            return page;
        });

        return pages;
    }

    @Override
    public byte[] export(TBusCargoInfoSearchDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, ExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<ExportDTO> cursor = tBusCargoInfoMapper.cursorListCargoInfo(searchDTO)) {
                    Iterator<ExportDTO> iterator = cursor.iterator();
                    while (iterator.hasNext()) {
                        List<ExportDTO> cargoInfos = new ArrayList<>();
                        for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                            cargoInfos.add(iterator.next());
                        }

                        List<Long> ids = cargoInfos.stream().map(ExportDTO::getId).collect(Collectors.toList());
                        List<Map<String, Object>> handoverlistTons = tBusCargoInfoMapper.listHandoverlistTon(ids);
                        List<Map<String, Object>> trustCargoTons = tBusCargoInfoMapper.listTrustCargoTon(ids);
                        List<Map<String, Object>> weightGoodss = tBusCargoInfoMapper.listWeightGoodss(ids);
                        List<Map<String, Object>> releaseNameTimes = tBusCargoInfoMapper.listReleaseNameTime(ids);

                        cargoInfos.forEach(v1 -> {
                            List<Map<String, Object>> _handoverlistTons = handoverlistTons.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).collect(Collectors.toList());
                            Map<String, Object> trustCargoTon = trustCargoTons.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                            Map<String, Object> weightGoods = weightGoodss.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(Collections.emptyMap());
                            v1.setHandoverlistTon(_handoverlistTons.stream().filter(v2 -> !"卸船".equals(v1.getType()) || "卸".equals(v2.get("loadUnload"))).map(v2 -> new BigDecimal(String.valueOf(v2.get("handoverlistTon")))).reduce(BigDecimal.ZERO, BigDecimal::add));
                            v1.setTrustCargoTon(trustCargoTon.get("trustCargoTon") == null ? null : new BigDecimal(String.valueOf(trustCargoTon.get("trustCargoTon"))));
                            v1.setWeightGoods(weightGoods.get("weightGoods") == null ? null : new BigDecimal(String.valueOf(weightGoods.get("weightGoods"))));
                            v1.setBalanceTon(Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getTrustCargoTon()).orElse(BigDecimal.ZERO)));
                            if (CommonEnum.YesNoMode.YES.getCode().equals(v1.getIsClear())) {
                                v1.setBalancePortStorageTon(BigDecimal.ZERO);
                            } else {
                                v1.setBalancePortStorageTon(Optional.ofNullable(v1.getHandoverlistTon()).orElse(BigDecimal.ZERO).subtract(Optional.ofNullable(v1.getWeightGoods()).orElse(BigDecimal.ZERO)).multiply("卸船".equals(v1.getType()) ? BigDecimal.ONE : BigDecimal.valueOf(-1)));
                            }
                            List<Map<String, Object>> tempReleaseNameTimes = releaseNameTimes.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).collect(Collectors.toList());
                            String releaseByNames = tempReleaseNameTimes.stream().map(v2 -> String.valueOf(v2.get("releaseByName"))).distinct().collect(Collectors.joining(", "));
                            String releaseTimes = tempReleaseNameTimes.stream().map(v2 -> String.valueOf(v2.get("releaseTime"))).findFirst().orElse(null);
                            v1.setReleaseByNames(releaseByNames);
                            v1.setReleaseTimes(releaseTimes);
                        });

                        excelWriter.write(cargoInfos, writeSheet);
                    }
                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }

    @Override
    public List<Map<String, Object>> getPoundbillList(PoundbillSearchDTO searchDTO) {
        List<Map<String, Object>> list = tBusCargoInfoMapper.getPoundbillList(searchDTO);
        list.stream().forEach(map -> {
            String startTime = StringUtil.isNotEmpty(String.valueOf((Date) map.get("WEIGH_IN_DT"))) ?
                    String.valueOf((Date) map.get("WEIGH_IN_DT")).substring(0, 19) : "";
            String endTime = StringUtil.isNotEmpty(String.valueOf((Date) map.get("WEIGH_OUT_DT")).substring(0, 19)) ? String.valueOf((Date) map.get("WEIGH_OUT_DT")).substring(0, 19) : "";
            map.put("WEIGH_IN_DT", startTime);
            map.put("WEIGH_OUT_DT", endTime);
        });
        return list;
    }

    @Override
    public Map<String, Object> summary(TBusCargoInfoSearchDTO searchDTO) {
        return tBusCargoInfoMapper.summary(searchDTO);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TBusCargoInfoDTO getDetail(Long id) {
        return tBusCargoInfoMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TBusCargoInfoDTO dto) {

        // TODO 修改的条件，剩余量如何处理？

        // 验证票货是否重复
        Map<String, Object> checkMap = BeanUtil.beanToMap(dto);
        Long targetBusCargoInoId = businessCommonService.getBusCargoInfoId(checkMap);

        if (targetBusCargoInoId != null) {
            throw new BusinessRuntimeException("票货信息重复~");
        }

        // 有航次的场合
        if (dto.getShipvoyageItemId() != null) {
            Map<String, Object> map = businessCommonService.getVoyageInfoByItemId(dto.getShipvoyageItemId());
            if (map == null) {
                throw new BusinessRuntimeException("航次信息错误~");
            }
            dto.setScn(StringUtil.getString(map.get("scn")));
            dto.setShipName(StringUtil.getString(map.get("shipName")));
            dto.setShipvoyageId(StringUtil.getLong(map.get("shipvoyageId")));
        }

        // 新增
        if (dto.getId() == null) {

            dto.setId(snowflake.nextId());
            // 货权量
            dto.setRightsQuantity(dto.getTon());
            // 剩余货权量
            dto.setSurplusRightsQuantity(dto.getTon());
            // 票货编号
            dto.setCargoInfoNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.MAIN_CARGO_INFO, null));
            return tBusCargoInfoMapper.insert(dto) == 1;

            // 修改
        } else {
            return tBusCargoInfoMapper.update(dto) == 1;
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tBusCargoInfoMapper.deleteById(id) == 1;

    }

    /**
     * 货权转移记录
     *
     * @param cargoInfoId
     * @return 对象列表
     */
    @Override
    public List<TBusCargoTransferDTO> getTransferList(Long cargoInfoId) {
        return tBusCargoTransferMapper.getList(cargoInfoId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cleanAllPortStorage(CleanAllPortStorageDTO cleanAllPortStorage) {
        TBusCargoInfoDTO cargoInfo = tBusCargoInfoMapper.getById(cleanAllPortStorage.getCargoInfoId());
        if ("1".equals(cargoInfo.getIsClear())) {
            throw new BusinessRuntimeException("不能重复完货");
        }
        //完货 关闭该票货的集疏港计划
        List<Long> trustCargoIdList = tBusCargoInfoMapper.getTrustCargoIdByCargoInfoId(cleanAllPortStorage.getCargoInfoId());
        TBusTrustCargoDTO  tBusTrustCargoDTO = new TBusTrustCargoDTO();
        for(Long trustCargoId : trustCargoIdList) {
            tBusTrustCargoDTO.setId(trustCargoId);
            tBusTrustCargoDTO.setIsStop("20");
            tBusTrustMapper.isStopStatus(tBusTrustCargoDTO);
        }


        TBusCargoInfoPO tempCargoInfo = new TBusCargoInfoPO();
        tempCargoInfo.setId(cleanAllPortStorage.getCargoInfoId());
        tempCargoInfo.setRealClearDate(cleanAllPortStorage.getWorkDate());
        tBusCargoInfoMapper.clean(tempCargoInfo);

        List<Map<String, Object>> masses = tBusCargoInfoMapper.listAllMass(cleanAllPortStorage.getCargoInfoId());
        masses.forEach(v1 -> {
            CleanPortStorageDTO cleanPortStorage = new CleanPortStorageDTO();
            cleanPortStorage.setCargoInfoId(cleanAllPortStorage.getCargoInfoId());
            cleanPortStorage.setStorehouseId(Long.valueOf(String.valueOf(v1.get("storehouseId"))));
            cleanPortStorage.setRegionId(Long.valueOf(String.valueOf(v1.get("regionId"))));
            cleanPortStorage.setMassId(Long.valueOf(String.valueOf(v1.get("massId"))));
            cleanPortStorage.setWorkDate(cleanAllPortStorage.getWorkDate());
            cleanPortStorage.setClassCode(cleanAllPortStorage.getClassCode());
            cleanPortStorage.setClassName(cleanAllPortStorage.getClassName());
            try {
                tPrdPortStorageService.cleanPortStorage(cleanPortStorage);
            } catch (BusinessRuntimeException ignored) {
            }
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelCleanAllPortStorage(Long id) {
        TBusCargoInfoDTO cargoInfo = tBusCargoInfoMapper.getById(id);
        if ("0".equals(cargoInfo.getIsClear())) {
            throw new BusinessRuntimeException("票货尚未完货，无需撤销");
        }

        List<TCostStorageSettlePO> storageSettles = tBusCargoInfoMapper.listStorageSettle(id);
        if (!storageSettles.isEmpty()) {
            throw new BusinessRuntimeException("票货堆存费已最终结算，无法撤销");
        }

        tBusCargoInfoMapper.cancelClean(id);

        List<Map<String, Object>> masses = tBusCargoInfoMapper.listAllMass(id);
        masses.forEach(v1 -> {
            CancelCleanPortStorageDTO cancelCleanPortStorage = new CancelCleanPortStorageDTO();
            cancelCleanPortStorage.setCargoInfoId(id);
            cancelCleanPortStorage.setStorehouseId(Long.valueOf(String.valueOf(v1.get("storehouseId"))));
            cancelCleanPortStorage.setRegionId(Long.valueOf(String.valueOf(v1.get("regionId"))));
            cancelCleanPortStorage.setMassId(Long.valueOf(String.valueOf(v1.get("massId"))));
            try {
                tPrdPortStorageService.cancelCleanPortStorage(cancelCleanPortStorage);
            } catch (BusinessRuntimeException ignored) {
            }
        });
    }

    /**
     * 通过票货ID获取货物信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> getCargoListByCargoCode(Long id,String businessType) {
        List<CargoListInfoDTO> cargoInfoList = tBusCargoInfoMapper.getCargoListByCargoId(id,businessType);
        HashMap<String, Object> result = new HashMap<>();
        cargoInfoList.forEach(o -> {
            if (!"10".equals(o.getStatus())) {
                result.put("resMsg", "已开始作业，不能进行重复导入！");
            }
        });
        if (!CollectionUtils.isEmpty(result)) {
            return result;
        }
        int cargoListCountByCargoId = tBusCargoInfoMapper.getCargoListCountByCargoId(id,businessType);
        if (cargoListCountByCargoId != 0) {
            result.put("resMsg", "jg0001");
        }
        if (!CollectionUtils.isEmpty(result)) {
            return result;
        }
        result.put("resMsg", "jg0000");
        return result;
    }

    public List<CargoListInfoDTO> importExcelDate(MultipartFile file) {
        List<CargoListInfoDTO> cargoListInfoDTOS = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            EasyExcel.read(is, CargoListInfoDTO.class, new PageReadListener<CargoListInfoDTO>(dataList -> {
                cargoListInfoDTOS.addAll(dataList);
            })).sheet().doRead();
            log.debug(cargoListInfoDTOS + "objects");

        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return cargoListInfoDTOS;
    }

    /**
     * 导入票货对应的货物清单
     *
     * @param id
     * @param file
     * @return
     */
    @Override
    @Transactional
    public boolean importCargoList(Long id, MultipartFile file) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择票货数据");
        }
        //非空校验
        //excel批量导入
        List<CargoListInfoDTO> cargoListInfoDTOS = SpringUtils.getBean(this.getClass()).importExcelDate(file);
        cargoListInfoDTOS.forEach(o -> {
            o.setCargoInfoId(id);
            o.setStatus("10");
            ValidatorUtils.FieldBean bean = ValidatorUtils.validator(o);
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        });

        if (CollectionUtils.isEmpty(cargoListInfoDTOS)) {
            throw new BusinessRuntimeException("excel中数据为空，请检查文件中是否存在数据");
        }
        List<String> sourceCoilNum = cargoListInfoDTOS.stream().map(CargoListInfoDTO::getCoilNum).distinct().collect(Collectors.toList());
        if (sourceCoilNum.size() != cargoListInfoDTOS.size()) {
            throw new BusinessRuntimeException("卷刚号重复!");
        }

        List<CargoListInfoDTO> cargoInfoList = tBusCargoInfoMapper.getCargoListByCargoId(id,"1");

        if (!CollectionUtils.isEmpty(cargoInfoList)) {

            cargoInfoList.forEach(o -> {
                if (!"10".equals(o.getStatus())) {
                    throw new BusinessRuntimeException("已有卷钢开始作业，不能进行重复导入！");
                }
            });

            List<String> targetCoilNum = cargoInfoList.stream().map(CargoListInfoDTO::getCoilNum).collect(Collectors.toList());
            Map<String, List<CargoListInfoDTO>> targetCoilNumMap = cargoInfoList.stream().collect(Collectors.groupingBy(CargoListInfoDTO::getCoilNum));
            Map<String, List<CargoListInfoDTO>> tmpCoilNumMap = cargoListInfoDTOS.stream().collect(Collectors.groupingBy(CargoListInfoDTO::getCoilNum));
            List<CargoListInfoDTO> coilNumAdd = new ArrayList<>();
            List<CargoListInfoDTO> coilNumUpdate = new ArrayList<>();
            List<CargoListInfoDTO> coilNumDel = new ArrayList<>();
            tmpCoilNumMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(targetCoilNumMap.get(k))) {
                    coilNumAdd.addAll(v);
                } else {
                    coilNumUpdate.addAll(v);
                }
            });
            targetCoilNumMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(tmpCoilNumMap.get(k))) {
                    coilNumDel.addAll(v);
                }
            });
            if (!CollectionUtils.isEmpty(coilNumDel)) {
                coilNumDel.forEach(o -> {
                    o.setId(targetCoilNumMap.get(o.getCoilNum()).get(0).getId());
                });
                tBusCargoInfoMapper.deleteCargoCoilnumByCoilList(coilNumDel);
            }
            if (!CollectionUtils.isEmpty(coilNumUpdate)) {
                coilNumUpdate.forEach(o -> {
                    o.setId(targetCoilNumMap.get(o.getCoilNum()).get(0).getId());
                });
                tBusCargoInfoMapper.updateBatchByCoilList(coilNumUpdate);

            }
            if (!CollectionUtils.isEmpty(coilNumAdd)) {
                coilNumAdd.forEach(o -> {
                    o.setId(snowflake.nextId());
                    o.setBusinessType("1");
                });
                tBusCargoInfoMapper.importCargoList(coilNumAdd);
            }

        } else {
            //直接添加的
            cargoListInfoDTOS.forEach(o -> {
                o.setId(snowflake.nextId());
                o.setBusinessType("1");
            });
            int count = tBusCargoInfoMapper.importCargoList(cargoListInfoDTOS);

            if (count != cargoListInfoDTOS.size()) {
                throw new BusinessRuntimeException("导入失败！");
            }
        }

        return true;
    }

    /**
     * 票货对应的货物清单列表查询
     *
     * @param id
     * @return
     */
    @Override
    public List<CargoListInfoDTO> getCargoListInfoByCargoId(Long id,String businessType) {
        return tBusCargoInfoMapper.getCargoListDataByCargoId(id,businessType);
    }

    /**
     * 卷钢导入模板下载
     *
     * @param response
     */
    @Override
    public void exportTemplate(HttpServletResponse response) {


        List<CargoListInfoDTOTemplate> resultList = new ArrayList<>();
        try {

            WriteCellStyle headCellStyle = new WriteCellStyle();
            headCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            WriteFont writeFont = new WriteFont();
            writeFont.setFontName("微软雅黑");              //字体
            writeFont.setFontHeightInPoints((short) 10);  //设置字体大小
            writeFont.setBold(false);                     //是否加粗
            headCellStyle.setWriteFont(writeFont);
            headCellStyle.setFillPatternType(FillPatternType.NO_FILL);
            HorizontalCellStyleStrategy handler = new HorizontalCellStyleStrategy();
            handler.setHeadWriteCellStyle(headCellStyle);
            //导出模板
            EasyExcelFactory.write(response.getOutputStream(), CargoListInfoDTOTemplate.class).registerWriteHandler(handler).sheet("卷钢导入模板").doWrite(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<TBusCargoInfoDTO> getCargoListByCustomerId(Long customerId, Long shipvoyageItemId) {
        return tBusCargoInfoMapper.getCargoListByCustomerId(customerId, shipvoyageItemId);
    }

    @Override
    public void exportBoxTemplate(HttpServletResponse response) {
        List<CargoBpxListInfoDTOTemplate> resultList = new ArrayList<>();
        try {
            WriteCellStyle headCellStyle = new WriteCellStyle();
            headCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            WriteFont writeFont = new WriteFont();
            writeFont.setFontName("微软雅黑");              //字体
            writeFont.setFontHeightInPoints((short) 10);  //设置字体大小
            writeFont.setBold(false);                     //是否加粗
            headCellStyle.setWriteFont(writeFont);
            headCellStyle.setFillPatternType(FillPatternType.NO_FILL);
            HorizontalCellStyleStrategy handler = new HorizontalCellStyleStrategy();
            handler.setHeadWriteCellStyle(headCellStyle);
            //导出模板
            EasyExcelFactory.write(response.getOutputStream(), CargoBpxListInfoDTOTemplate.class).registerWriteHandler(handler).sheet("集装箱导入模板").doWrite(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public boolean importCargoBoxList(Long id, MultipartFile file) {
        if (id == null) {
            throw new BusinessRuntimeException("请选择票货数据");
        }
        //非空校验
        //excel批量导入
        List<CargoBoxListInfoDTO> cargoBoxListInfoDTOS = SpringUtils.getBean(this.getClass()).importBoxExcelDate(file);
        cargoBoxListInfoDTOS.forEach(o -> {
            o.setCargoInfoId(id);
            o.setStatus("10");
            ValidatorUtils.FieldBean bean = ValidatorUtils.validator(o);
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        });

        if (CollectionUtils.isEmpty(cargoBoxListInfoDTOS)) {
            throw new BusinessRuntimeException("excel中数据为空，请检查文件中是否存在数据");
        }
        List<String> sourceBoxNum = cargoBoxListInfoDTOS.stream().map(CargoBoxListInfoDTO::getCoilNum).distinct().collect(Collectors.toList());
        if (sourceBoxNum.size() != cargoBoxListInfoDTOS.size()) {
            throw new BusinessRuntimeException("箱号重复!");
        }

        List<CargoBoxListInfoDTO> cargoBoxInfoList = tBusCargoInfoMapper.getCargoBoxListByCargoId(id,"2");

        if (!CollectionUtils.isEmpty(cargoBoxInfoList)) {

            cargoBoxInfoList.forEach(o -> {
                if (!"10".equals(o.getStatus())) {
                    throw new BusinessRuntimeException("已有箱号开始作业，不能进行重复导入！");
                }
            });

            List<String> targetBoxNum = cargoBoxInfoList.stream().map(CargoBoxListInfoDTO::getCoilNum).collect(Collectors.toList());
            Map<String, List<CargoBoxListInfoDTO>> targetBoxNumMap = cargoBoxInfoList.stream().collect(Collectors.groupingBy(CargoBoxListInfoDTO::getCoilNum));
            Map<String, List<CargoBoxListInfoDTO>> tmpBoxNumMap = cargoBoxListInfoDTOS.stream().collect(Collectors.groupingBy(CargoBoxListInfoDTO::getCoilNum));
            List<CargoBoxListInfoDTO> boxNumAdd = new ArrayList<>();
            List<CargoBoxListInfoDTO> boxNumUpdate = new ArrayList<>();
            List<CargoBoxListInfoDTO> boxNumDel = new ArrayList<>();
            tmpBoxNumMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(targetBoxNumMap.get(k))) {
                    boxNumAdd.addAll(v);
                } else {
                    boxNumUpdate.addAll(v);
                }
            });
            targetBoxNumMap.forEach((k, v) -> {
                if (CollectionUtils.isEmpty(tmpBoxNumMap.get(k))) {
                    boxNumDel.addAll(v);
                }
            });
            if (!CollectionUtils.isEmpty(boxNumDel)) {
                boxNumDel.forEach(o -> {
                    o.setId(targetBoxNumMap.get(o.getCoilNum()).get(0).getId());
                });
                tBusCargoInfoMapper.deleteCargoBoxnumByBoxList(boxNumDel);
            }
            if (!CollectionUtils.isEmpty(boxNumUpdate)) {
                boxNumUpdate.forEach(o -> {
                    o.setId(targetBoxNumMap.get(o.getCoilNum()).get(0).getId());
                });
                tBusCargoInfoMapper.updateBatchByBoxList(boxNumUpdate);

            }
            if (!CollectionUtils.isEmpty(boxNumAdd)) {
                boxNumAdd.forEach(o -> {
                    o.setId(snowflake.nextId());
                    o.setBusinessType("2");
                });
                tBusCargoInfoMapper.importCargoBoxList(boxNumAdd);
            }

        } else {
            //直接添加的
            cargoBoxListInfoDTOS.forEach(o -> {
                o.setId(snowflake.nextId());
                o.setBusinessType("2");
            });
            int count = tBusCargoInfoMapper.importCargoBoxList(cargoBoxListInfoDTOS);

            if (count != cargoBoxListInfoDTOS.size()) {
                throw new BusinessRuntimeException("导入失败！");
            }
        }

        return true;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean isLogoutStatus(TBusCargoInfoDTO dto) {
        int count = 0;
        //作废
        if(dto.getIsLogout().equals("10")) {
            //检验是否存在过磅记录
            int count1 = tBusCargoInfoMapper.getPoundByCargoInfoId(dto.getId());
            if (count1 > 0) {
                throw new BusinessRuntimeException("存在过磅记录 无法作废");
            }
            //检验是否存在货转
            int count2 = tBusCargoInfoMapper.getCargoTransferByCargoInfoId(dto.getId());
            if (count2 > 0) {
                throw new BusinessRuntimeException("存在货转 无法作废");
            }
            //检验是否存在混配
            int count3 = tBusCargoInfoMapper.getMixByCargoInfoId(dto.getId());
            if (count3 > 0) {
                throw new BusinessRuntimeException("存在混配 无法作废");
            }
            //检验是否存在港存
            int count4 = tBusCargoInfoMapper.getPortStorageByCargoInfoId(dto.getId());
            if (count4 > 0) {
                throw new BusinessRuntimeException("存在港存 无法作废");
            }
            //todo.校验是否已经进行理货
            int count5 = tBusCargoInfoMapper.getTallyByCargoInfoId(dto.getId());
            if (count5 > 0) {
                throw new BusinessRuntimeException("已经理货 无法作废");
            }
            //作废 关闭该票货的集疏港计划
            List<Long> trustCargoIdList = tBusCargoInfoMapper.getTrustCargoIdByCargoInfoId(dto.getId());
            TBusTrustCargoDTO tBusTrustCargoDTO = new TBusTrustCargoDTO();
            for (Long trustCargoId : trustCargoIdList) {
                tBusTrustCargoDTO.setId(trustCargoId);
                tBusTrustCargoDTO.setIsStop("20");
                tBusTrustMapper.isStopStatus(tBusTrustCargoDTO);
            }
            dto.setIsSettleStorage("0");
            count = tBusCargoInfoMapper.isLogoutStatus(dto);
        }else{
            //撤销作废
            dto.setIsSettleStorage("1");
            count = tBusCargoInfoMapper.isLogoutStatus(dto);
        }

        return count>0;
    }

    @Override
    public boolean updateIsHq(TBusCargoInfoDTO tBusCargoInfoDTO) {

        return tBusCargoInfoMapper.updateIsHq(tBusCargoInfoDTO) == 1;
    }

    public List<CargoBoxListInfoDTO> importBoxExcelDate(MultipartFile file) {
        List<CargoBoxListInfoDTO> cargoBoxListInfoDTOS= new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            EasyExcel.read(is, CargoBoxListInfoDTO.class, new PageReadListener<CargoBoxListInfoDTO>(dataList -> {
                cargoBoxListInfoDTOS.addAll(dataList);
            })).sheet().doRead();
            log.debug(cargoBoxListInfoDTOS + "objects");

        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return cargoBoxListInfoDTOS;
    }
}

