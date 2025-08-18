package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.ppm.business.mapper.TBusTrustTradeReservationMapper;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.assignFleet.TBusTrustCargoQueryDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationDTO;
import com.yy.ppm.business.bean.po.TBusAssignFleetPO;
import com.yy.ppm.business.mapper.TBusAssignFleetMapper;
import com.yy.ppm.business.service.TBusAssignFleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 09:31
 */
@Service
public class TBusAssignFleetServiceImpl implements TBusAssignFleetService {

    @Autowired
    private TBusAssignFleetMapper tBusAssignFleetMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Pages<TBusTrustCargoDTO> listTrustCargo(TBusTrustCargoQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            Page<TBusTrustCargoDTO> page = tBusAssignFleetMapper.listTrustCargo(query);
            page.getResult().forEach(v1 -> {
                if (v1.getTbtShipvoyageId() != null) {
                    v1.setShipNameVoyage(v1.getTbtShipName() + "_" + v1.getTdsiVoyage());
                }
                String customerNames = v1.getAssignFleets().stream().map(TBusAssignFleetPO::getCustomerName).collect(Collectors.joining("、"));
                v1.setCustomerNames(customerNames);
            });
            return page;
        });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateAssignFleet(Long trustCargoId, List<TBusAssignFleetPO> assignFleets) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.ASSIGN_FLEET_AND_TRUST_TRADE_RESERVATION_KEY.getCode() + trustCargoId)
                .build().run(() -> {
                    List<TBusAssignFleetPO> currentAssignFleets = tBusAssignFleetMapper.listAssignFleet(trustCargoId);

                    List<TBusTrustTradeReservationDTO> trustTradeReservations;
                    if (currentAssignFleets.isEmpty()) {
                        trustTradeReservations = Collections.emptyList();
                    } else {
                        List<Long> customerIds = currentAssignFleets.stream().map(TBusAssignFleetPO::getCustomerId).collect(Collectors.toList());
                        trustTradeReservations = tBusAssignFleetMapper.listTrustTradeReservation(trustCargoId, customerIds);
                    }
                    List<TBusAssignFleetPO> toBeInserted = new ArrayList<>();
                    List<TBusAssignFleetPO> toBeUpdated = new ArrayList<>();
                    List<TBusAssignFleetPO> toBeDeleted;
                    for (TBusAssignFleetPO v1 : assignFleets) {
                        TBusAssignFleetPO currentAssignFleet = currentAssignFleets.stream().filter(v2 -> v1.getCustomerId().equals(v2.getCustomerId())).findFirst().orElse(null);
                        if (currentAssignFleet == null) {
                            toBeInserted.add(v1);
                        } else {
                            if (!v1.equals(currentAssignFleet)) {
                                boolean anyMatch = trustTradeReservations.stream().anyMatch(v2 -> v1.getCustomerId().equals(v2.getCustomerId()));
                                if (anyMatch) {
                                    throw new BusinessRuntimeException("已进行集疏港预约的车队禁止修改");
                                }
                                toBeUpdated.add(v1);
                            }
                        }
                    }
                    toBeDeleted = currentAssignFleets.stream()
                            .filter(v1 -> assignFleets.stream().noneMatch(v2 -> v1.getCustomerId().equals(v2.getCustomerId())))
                            .peek(v1 -> {
                                boolean anyMatch = trustTradeReservations.stream().anyMatch(v2 -> v1.getCustomerId().equals(v2.getCustomerId()));
                                if (anyMatch) {
                                    throw new BusinessRuntimeException("已进行集疏港预约的车队禁止删除");
                                }
                            })
                            .collect(Collectors.toList());

                    List<TBusAssignFleetPO> doNothing = assignFleets.stream().filter(v1 -> currentAssignFleets.stream().anyMatch(v1::equals)).collect(Collectors.toList());
                    TBusTrustCargoDTO trustCargo = tBusAssignFleetMapper.getTrustCargo(trustCargoId);
                    int toBeInsertedQuantity = toBeInserted.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
                    int toBeUpdatedQuantity = toBeUpdated.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
                    int toBeDeletedQuantity = toBeDeleted.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
                    int doNothingQuantity = doNothing.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
                    int totalQuantity = toBeInsertedQuantity + toBeUpdatedQuantity - toBeDeletedQuantity + doNothingQuantity;
                    int planQuantity = ObjectUtils.isEmpty(trustCargo)?0:Optional.ofNullable(trustCargo.getTbtPlanQuantity()).orElse(0);
                    if (totalQuantity > planQuantity) {
                        throw new BusinessRuntimeException("超出计划件数");
                    }
                    BigDecimal toBeInsertedTon = toBeInserted.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal toBeUpdatedTon = toBeUpdated.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal toBeDeletedTon = toBeDeleted.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal doNothingTon = doNothing.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalTon = toBeInsertedTon.add(toBeUpdatedTon).subtract(toBeDeletedTon).add(doNothingTon);
                    BigDecimal planTon = ObjectUtils.isEmpty(trustCargo)?new BigDecimal(0):Optional.ofNullable(trustCargo.getTbtPlanTon()).orElse(BigDecimal.ZERO);
                    if (totalTon.compareTo(planTon) > 0) {
                        throw new BusinessRuntimeException("超出计划重量");
                    }

                    if (!toBeInserted.isEmpty()) {
                        toBeInserted.forEach(v1 -> {
                            v1.setId(snowflake.nextId());
                        });
                        tBusAssignFleetMapper.insertAssignFleet(toBeInserted);
                    }
                    toBeUpdated.forEach(v1 -> {
                        tBusAssignFleetMapper.updateAssignFleet(v1);
                    });
                    if (!toBeDeleted.isEmpty()) {
                        tBusAssignFleetMapper.deleteAssignFleet(trustCargoId, toBeDeleted.stream().map(TBusAssignFleetPO::getCustomerId).collect(Collectors.toList()));
                    }
                });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertAssignFleet(Long trustCargoId, List<TBusAssignFleetPO> assignFleets) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.ASSIGN_FLEET_AND_TRUST_TRADE_RESERVATION_KEY.getCode() + trustCargoId)
                .build().run(() -> {
                    List<TBusAssignFleetPO> currentAssignFleets = tBusAssignFleetMapper.listAssignFleet(trustCargoId);
//                    List<TBusTrustTradeReservationDTO> trustTradeReservations;
//                    if (currentAssignFleets.isEmpty()) {
//                        trustTradeReservations = Collections.emptyList();
//                    } else {
//                        List<Long> customerIds = currentAssignFleets.stream().map(TBusAssignFleetPO::getCustomerId).collect(Collectors.toList());
//                        trustTradeReservations = tBusAssignFleetMapper.listTrustTradeReservation(trustCargoId, customerIds);
//                    }
                    List<TBusAssignFleetPO> toBeInserted = new ArrayList<>();
                    List<TBusAssignFleetPO> toBeUpdated = new ArrayList<>();
                    List<TBusAssignFleetPO> toBeDeleted;
                    for (TBusAssignFleetPO v1 : assignFleets) {
                        toBeInserted.add(v1);
                    }
                    toBeDeleted = currentAssignFleets.stream()
                            .filter(v1 -> assignFleets.stream().noneMatch(v2 -> v1.getCustomerId().equals(v2.getCustomerId())))
                            .collect(Collectors.toList());
//                    List<TBusAssignFleetPO> doNothing = assignFleets.stream().filter(v1 -> currentAssignFleets.stream().anyMatch(v1::equals)).collect(Collectors.toList());
//                    TBusTrustCargoDTO trustCargo = tBusAssignFleetMapper.getTrustCargo(trustCargoId);
//                    int toBeInsertedQuantity = toBeInserted.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
//                    int toBeUpdatedQuantity = toBeUpdated.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
//                    int toBeDeletedQuantity = toBeDeleted.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
//                    int doNothingQuantity = doNothing.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
//                    int totalQuantity = toBeInsertedQuantity + toBeUpdatedQuantity - toBeDeletedQuantity + doNothingQuantity;
//                    int planQuantity = ObjectUtils.isEmpty(trustCargo)?0:Optional.ofNullable(trustCargo.getTbtPlanQuantity()).orElse(0);
//                    if (totalQuantity > planQuantity) {
//                        throw new BusinessRuntimeException("超出计划件数");
//                    }
//                    BigDecimal toBeInsertedTon = toBeInserted.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    BigDecimal toBeUpdatedTon = toBeUpdated.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    BigDecimal toBeDeletedTon = toBeDeleted.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    BigDecimal doNothingTon = doNothing.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
//                    BigDecimal totalTon = toBeInsertedTon.add(toBeUpdatedTon).subtract(toBeDeletedTon).add(doNothingTon);
//                    BigDecimal planTon = ObjectUtils.isEmpty(trustCargo)?new BigDecimal(0):Optional.ofNullable(trustCargo.getTbtPlanTon()).orElse(BigDecimal.ZERO);
//                    if (totalTon.compareTo(planTon) > 0) {
//                        throw new BusinessRuntimeException("超出计划重量");
//                    }
                    if (!toBeInserted.isEmpty()) {
                        toBeInserted.forEach(v1 -> {
                            v1.setId(snowflake.nextId());
                        });
                        tBusAssignFleetMapper.insertAssignFleet(toBeInserted);
                    }
                    toBeUpdated.forEach(v1 -> {
                        tBusAssignFleetMapper.updateAssignFleet(v1);
                    });
                    if (!toBeDeleted.isEmpty()) {
                        tBusAssignFleetMapper.deleteAssignFleet(trustCargoId, toBeDeleted.stream().map(TBusAssignFleetPO::getCustomerId).collect(Collectors.toList()));
                    }
                });
    }
}
