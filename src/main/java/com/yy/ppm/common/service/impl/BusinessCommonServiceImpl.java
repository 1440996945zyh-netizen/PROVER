package com.yy.ppm.common.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.ppm.produce.bean.SyncDTO;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.common.enums.CleanMassSignEnum;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.enums.InoutStorageEnum;
import com.yy.ppm.common.enums.InoutTypeEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.NumUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.concurrent.DistributedReentrantLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.BusinessCommonMapper;
import com.yy.ppm.common.service.BusinessCommonService;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.bean.po.TPrdPortStoragePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessCommonServiceImpl implements BusinessCommonService {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(BusinessCommonServiceImpl.class);

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private BusinessCommonMapper businessMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 门机设备类型编号
     */
    private final String MAC_TYPE_CODE_MJ = "0005";


    /**
     * 票货验证
     *
     * @return 返回票货ID，id为null说明没有票货
     */
    @Override
    public Long getBusCargoInfoId(Map<String, Object> map) {

        Long id = businessMapper.getBusCargoInfoId(map);

        return id;
    }

    /**
     * 组合票货 修改剩余量
     *
     * @return
     */
    @Override
    public int updateSurplusBusCargoInfo(Long id, Long quantity, BigDecimal ton, String flag) {

        Map<String, Object> map = new HashMap<>();

        // 件数为空
        if (quantity == null) {
            quantity = 0L;
        }

        map.put("id", id);
        map.put("quantity", quantity);
        map.put("ton", ton);

        // 减少时
        if ("-".equals(flag)) {
            map.put("quantity", quantity * -1);
            map.put("ton", NumUtils.multiply(ton, new BigDecimal("-1"), 4));
        }

        map.put("loginUserId", securityUtils.getLoginUserId());
        map.put("loginUserName", securityUtils.getLoginUserName());
        map.put("now", new Date());
        return businessMapper.updateSurplusBusCargoInfo(map);
    }

    /****                调度                ***/
    /**
     * 根据航次子表信息获取航次主、子信息
     *
     * @return
     */
    @Override
    public Map<String, Object> getVoyageInfoByItemId(Long id) {
        Map<String, Object> map = businessMapper.getVoyageInfoByItemId(id);
        return map;
    }

    /**
     * 机械配工信息
     */
    @Override
    public List<Map<String, Object>> getWorkPlanEquipmentDispatch(Long workPlanId, String workPositionCode) {
        List<Map<String, Object>> list = businessMapper.getWorkPlanEquipmentDispatch(workPlanId, workPositionCode);
        return list;
    }


    /****                调度                ***/

    /**
     * 新增港存明细
     *
     * @param portStorageDetails
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertPortStorageDetail(List<TPrdPortStorageDetailPO> portStorageDetails) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(portStorageDetails);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        portStorageDetails.forEach(v1 -> {
            /*if (v1.getTon().compareTo(BigDecimal.ZERO) == 0) {
                throw new BusinessRuntimeException("错误的吨数（数量）");
            }*/
            if (InoutStorageEnum._70.getCode().equals(v1.getInoutStorageCode())) {
                if (v1.getCargoMixRecordId() == null && v1.getCargoMixDetailId() == null || v1.getCargoMixRecordId() != null && v1.getCargoMixDetailId() != null) {
                    throw new BusinessRuntimeException("混配时票货混配记录ID与票货混配明细ID必须有且仅能有其一");
                }
            }
        });

        List<Long> storehouseIds = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getStorehouseId).distinct().collect(Collectors.toList());
        List<Long> regionIds = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getRegionId).distinct().collect(Collectors.toList());
        List<Long> massIds = portStorageDetails.stream().map(TPrdPortStorageDetailPO::getMassId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> storehouses = businessMapper.listStorehouse(storehouseIds);
        List<Map<String, Object>> regions = businessMapper.listRegion(regionIds);
        List<Map<String, Object>> masses = businessMapper.listMass(massIds);
        portStorageDetails.forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setInoutType(v1.getTon().compareTo(BigDecimal.ZERO) > 0 ? InoutTypeEnum._2.getCode() : InoutTypeEnum._1.getCode());
            v1.setCleanMassSign(CleanMassSignEnum._0.getCode());
            Map<String, Object> storehouse = storehouses.stream().filter(v2 -> v1.getStorehouseId().equals(Long.valueOf(String.valueOf(v2.get("storehouseId"))))).findFirst().orElseThrow(BusinessRuntimeException::new);
            v1.setStorehouseName(String.valueOf(storehouse.get("storehouseName")));
            Map<String, Object> region = regions.stream().filter(v2 -> v1.getRegionId().equals(Long.valueOf(String.valueOf(v2.get("regionId"))))).findFirst().orElseThrow(BusinessRuntimeException::new);
            v1.setRegionName(String.valueOf(region.get("regionName")));
            Map<String, Object> mass = masses.stream().filter(v2 -> v1.getMassId().equals(Long.valueOf(String.valueOf(v2.get("massId"))))).findFirst().orElseThrow(BusinessRuntimeException::new);
            v1.setMassName(String.valueOf(mass.get("massName")));
        });

        Map<Map<String, Object>, List<TPrdPortStorageDetailPO>> groupByPortStorageCompositeKey = portStorageDetails.stream()
                .collect(Collectors.groupingBy(v1 -> {
                    Map<String, Object> portStorageCompositeKey = new HashMap<>();
                    portStorageCompositeKey.put("cargoInfoId", v1.getCargoInfoId());
                    portStorageCompositeKey.put("storehouseId", v1.getStorehouseId());
                    portStorageCompositeKey.put("regionId", v1.getRegionId());
                    portStorageCompositeKey.put("massId", v1.getMassId());
                    return portStorageCompositeKey;
                }));

        for (TPrdPortStorageDetailPO po : portStorageDetails) {
            SyncDTO syncDto = new SyncDTO();
            syncDto.setId(snowflake.nextId());
            syncDto.setBizId(po.getId());
            //syncDto.setBizType(BusSyncEnum.GOODS_DETAIL.getCode());
            syncDto.setIsDelete("0");
            //SpringUtils.getBean(BusSyncServiceImpl.class).insertBatch(syncDto);
        }
        groupByPortStorageCompositeKey.keySet().forEach(v1 -> DistributedReentrantLock.newBuilder().store(redisTemplate)
                .key(
                        DistributedLockKeyPrefixEnum.PORT_STORAGE_KEY.getCode()
                                + String.format("%s-%s-%s-%s", v1.get("cargoInfoId"), v1.get("storehouseId"), v1.get("regionId"), v1.get("massId"))
                )
                .build().run(() -> {
                    TPrdPortStoragePO portStorage = businessMapper.getPortStorage(v1);
                    List<TPrdPortStorageDetailPO> currentPortStorageDetails = groupByPortStorageCompositeKey.get(v1);
                    if (portStorage == null) {
                        portStorage = new TPrdPortStoragePO();
                        portStorage.setId(snowflake.nextId());
                        portStorage.setCompanyId(currentPortStorageDetails.get(0).getCompanyId());
                        portStorage.setCompanyName(currentPortStorageDetails.get(0).getCompanyName());
                        portStorage.setCargoInfoId(currentPortStorageDetails.get(0).getCargoInfoId());
                        portStorage.setStorehouseId(currentPortStorageDetails.get(0).getStorehouseId());
                        portStorage.setStorehouseName(currentPortStorageDetails.get(0).getStorehouseName());
                        portStorage.setRegionId(currentPortStorageDetails.get(0).getRegionId());
                        portStorage.setRegionName(currentPortStorageDetails.get(0).getRegionName());
                        portStorage.setMassId(currentPortStorageDetails.get(0).getMassId());
                        portStorage.setMassName(currentPortStorageDetails.get(0).getMassName());
                        if (!currentPortStorageDetails.stream().allMatch(v2 -> v2.getQuantity() == null)) {
                            portStorage.setQuantity(currentPortStorageDetails.stream().mapToInt(v2 -> Optional.ofNullable(v2.getQuantity()).orElse(0)).sum());
                        }
                        portStorage.setTon(currentPortStorageDetails.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add));
                        portStorage.setCleanMassSign(CleanMassSignEnum._0.getCode());
                        businessMapper.insertPortStorage(portStorage);
                    } else {
                        if (CleanMassSignEnum._1.getCode().equals(portStorage.getCleanMassSign())) {
                            TBusCargoInfoDTO  dto = businessMapper.getCargoInfoById(portStorage.getCargoInfoId());
                            throw new BusinessRuntimeException(dto.getCargoInfoNo()+" "+portStorage.getRegionName()+"/"+portStorage.getMassName()+" 当前港存已清场");
                        }
                        List<TPrdPortStorageDetailPO> tempPortStorageDetails = businessMapper.listPortStorageDetail(portStorage.getId());
                        tempPortStorageDetails.addAll(currentPortStorageDetails);

                        if (!tempPortStorageDetails.stream().allMatch(v2 -> v2.getQuantity() == null)) {
                            portStorage.setQuantity(tempPortStorageDetails.stream().mapToInt(v2 -> Optional.ofNullable(v2.getQuantity()).orElse(0)).sum());
                        } else {
                            portStorage.setQuantity(null);
                        }
                        portStorage.setTon(tempPortStorageDetails.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add));
                        businessMapper.updatePortStorage(portStorage);
                    }
                    TPrdPortStoragePO finalPortStorage = portStorage;
                    currentPortStorageDetails.forEach(v2 -> v2.setPortStorageId(finalPortStorage.getId()));
                    businessMapper.insertPortStorageDetail(currentPortStorageDetails);
                }));
    }

    /**
     * 删除港存明细
     *
     * @param portStorageDetailIds
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, noRollbackFor = BusinessRuntimeException.class)
    public void deletePortStorageDetail(List<Long> portStorageDetailIds) {
        List<TPrdPortStorageDetailPO> portStorageDetails = businessMapper.listPortStorageDetailById(portStorageDetailIds);

        Map<Map<String, Object>, List<TPrdPortStorageDetailPO>> groupByPortStorageCompositeKey = portStorageDetails.stream()
                .collect(Collectors.groupingBy(v1 -> {
                    Map<String, Object> portStorageCompositeKey = new HashMap<>();
                    portStorageCompositeKey.put("cargoInfoId", v1.getCargoInfoId());
                    portStorageCompositeKey.put("storehouseId", v1.getStorehouseId());
                    portStorageCompositeKey.put("regionId", v1.getRegionId());
                    portStorageCompositeKey.put("massId", v1.getMassId());
                    return portStorageCompositeKey;
                }));

        groupByPortStorageCompositeKey.keySet().forEach(v1 -> DistributedReentrantLock.newBuilder().store(redisTemplate)
                .key(
                        DistributedLockKeyPrefixEnum.PORT_STORAGE_KEY.getCode()
                                + String.format("%s-%s-%s-%s", v1.get("cargoInfoId"), v1.get("storehouseId"), v1.get("regionId"), v1.get("massId"))
                )
                .build().run(() -> {
                    TPrdPortStoragePO portStorage = businessMapper.getPortStorage(v1);
                    if (CleanMassSignEnum._1.getCode().equals(portStorage.getCleanMassSign())) {
                        throw new BusinessRuntimeException("当前港存已清场");
                    }

                    List<TPrdPortStorageDetailPO> currentPortStorageDetails = groupByPortStorageCompositeKey.get(v1);
                    List<Long> currentPortStorageDetailIds = currentPortStorageDetails.stream().map(TPrdPortStorageDetailPO::getId).collect(Collectors.toList());
                    businessMapper.deletePortStorageDetail(currentPortStorageDetailIds);

                    List<TPrdPortStorageDetailPO> tempPortStorageDetails = businessMapper.listPortStorageDetail(portStorage.getId());
                    if (tempPortStorageDetails.isEmpty()) {
                        businessMapper.deletePortStorage(portStorage.getId());
                    } else {
                        if (!tempPortStorageDetails.stream().allMatch(v2 -> v2.getQuantity() == null)) {
                            portStorage.setQuantity(tempPortStorageDetails.stream().mapToInt(v2 -> Optional.ofNullable(v2.getQuantity()).orElse(0)).sum());
                        } else {
                            portStorage.setQuantity(null);
                        }
                        portStorage.setTon(tempPortStorageDetails.stream().map(TPrdPortStorageDetailPO::getTon).reduce(BigDecimal.ZERO, BigDecimal::add));
                        businessMapper.updatePortStorage(portStorage);
                    }
                }));
    }
}
