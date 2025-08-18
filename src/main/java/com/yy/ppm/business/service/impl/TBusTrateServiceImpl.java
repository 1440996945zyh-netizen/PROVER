package com.yy.ppm.business.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.trate.*;
import com.yy.ppm.business.mapper.TBusTrateMapper;
import com.yy.ppm.business.service.TBusTrateService;
import com.yy.ppm.common.enums.TrateStatusEnum;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-11-08 11:49
 */
@Service
public class TBusTrateServiceImpl implements TBusTrateService {

    @Autowired
    private TBusTrateMapper tBusTrateMapper;

    @Autowired
    private Snowflake snowflake;

    @Override
    public void verifyUnique(TBusTrateDTO trate) {
        verifyUnique(trate, null);
    }

    @Override
    public void verifyUnique(TBusTrateDTO trate, Long ignoreId) {
        Date startTime = trate.getStartTime();
        Date endTime = trate.getEndTime();
        trate.getContracts().stream().flatMap(v1 ->
                trate.getCustomers().stream().flatMap(v2 ->
                        trate.getItems().stream().flatMap(v3 ->
                                v3.getCargos().stream().flatMap(v4 -> Stream.of(new HashMap<String, Object>() {{
                                    put("contractNo", v1.getContractNo());
                                    put("customerId", v2.getCustomerId());
                                    put("customerName", v2.getCustomerName());
                                    put("cargoCode", v4.getCargoCode());
                                    put("cargoName", v4.getCargoName());
                                }}))
                        )
                )
        ).forEach(v1 -> {
            boolean bool = tBusTrateMapper.verifyUnique(
                    String.valueOf(v1.get("contractNo")), Long.valueOf(String.valueOf(v1.get("customerId"))),
                    startTime, endTime,
                    String.valueOf(v1.get("cargoCode")),
                    ignoreId
            );
            if (bool) {
                throw new BusinessRuntimeException(
                        String.format(
                                "已存在的阶梯费率<br/>客户：%s<br/>合同编号：%s<br/>有效期：%s至%s<br/>货物：%s",
                                v1.get("customerName"),
                                v1.get("contractNo"),
                                new DateTime(startTime).toString("yyyy-MM-dd"),
                                new DateTime(endTime).toString("yyyy-MM-dd"),
                                v1.get("cargoName")
                        )
                );
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void insertTrate(TBusTrateDTO trate) {
        trate.setId(snowflake.nextId());
        tBusTrateMapper.insertTrate(trate);

        trate.getContracts().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setTrateId(trate.getId());
        });
        tBusTrateMapper.insertTrateContract(trate.getContracts());

        trate.getCustomers().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setTrateId(trate.getId());
        });
        tBusTrateMapper.insertTrateCustomer(trate.getCustomers());

        trate.getItems().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setTrateId(trate.getId());
        });
        tBusTrateMapper.insertTrateItem(trate.getItems());

        List<TBusTrateItemCargoDTO> cargos = trate.getItems().stream().flatMap(v1 -> {
            return v1.getCargos().stream().peek(v2 -> {
                v2.setId(snowflake.nextId());
                v2.setTrateItemId(v1.getId());
            });
        }).collect(Collectors.toList());
        tBusTrateMapper.insertTrateCargo(cargos);

        List<TBusTrateItemDetailDTO> details = trate.getItems().stream().flatMap(v1 -> {
            return Optional.ofNullable(v1.getDetails()).orElse(Collections.emptyList()).stream().peek(v2 -> {
                v2.setId(snowflake.nextId());
                v2.setTrateItemId(v1.getId());
            });
        }).collect(Collectors.toList());
        if (!details.isEmpty()) {
            tBusTrateMapper.insertTrateDetail(details);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void deleteTrate(Long id) {
        TBusTrateDTO trate = tBusTrateMapper.getTrate(id);
        if (TrateStatusEnum.已发布.getCode().equals(trate.getStatus())) {
            throw new BusinessRuntimeException("当前阶梯费率已发布，无法删除");
        }
        tBusTrateMapper.deleteTrate(id);
        tBusTrateMapper.deleteTrateContract(id);
        tBusTrateMapper.deleteTrateCustomer(id);
        tBusTrateMapper.deleteTrateCargo(id);
        tBusTrateMapper.deleteTrateDetail(id);
        tBusTrateMapper.deleteTrateItem(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void updateTrate(TBusTrateDTO trate) {
        TBusTrateDTO tempTrate = tBusTrateMapper.getTrate(trate.getId());
        if (tempTrate == null) {
            throw new BusinessRuntimeException("不存在的阶梯费率");
        }
        List<Long> itemIds = tBusTrateMapper.listUsedItem(trate.getId());
        itemIds.forEach(v1 -> {
            if (trate.getItems().stream().map(TBusTrateItemDTO::getId).noneMatch(v1::equals)) {
                TBusTrateItemDTO item = tempTrate.getItems().stream().filter(v2 -> v1.equals(v2.getId())).findFirst().orElseThrow(BusinessRuntimeException::new);
                String cargoNames = item.getCargos().stream().map(TBusTrateItemCargoDTO::getCargoName).collect(Collectors.joining(","));
                throw new BusinessRuntimeException("修改失败：条目【" + cargoNames + "/" + item.getTradeType() + "/" + item.getImpExp() + "】已关联合同，无法删除");
            }
        });

        tBusTrateMapper.updateTrate(trate);

        tBusTrateMapper.deleteTrateContract(trate.getId());
        tBusTrateMapper.deleteTrateCustomer(trate.getId());
        tBusTrateMapper.deleteTrateCargo(trate.getId());
        tBusTrateMapper.deleteTrateDetail(trate.getId());

        trate.getContracts().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setTrateId(trate.getId());
        });
        tBusTrateMapper.insertTrateContract(trate.getContracts());

        trate.getCustomers().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setTrateId(trate.getId());
        });
        tBusTrateMapper.insertTrateCustomer(trate.getCustomers());

        List<TBusTrateItemDTO> toBeInsertedItems = trate.getItems().stream().filter(v1 -> v1.getId() == null).collect(Collectors.toList());
        trate.getItems().stream().filter(v1 -> v1.getId() == null).forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setTrateId(trate.getId());
        });
        if (!toBeInsertedItems.isEmpty()) {
            tBusTrateMapper.insertTrateItem(toBeInsertedItems);
        }
        List<TBusTrateItemDTO> toBeUpdatedItems = trate.getItems().stream().filter(v1 -> v1.getId() != null).collect(Collectors.toList());
        if (!toBeUpdatedItems.isEmpty()) {
            toBeUpdatedItems.forEach(v1 -> tBusTrateMapper.updateTrateItem(v1));
        }
        List<TBusTrateItemDTO> toBeDeletedItems = tempTrate.getItems().stream().filter(v1 -> trate.getItems().stream().noneMatch(v2 -> v1.getId().equals(v2.getId()))).collect(Collectors.toList());
        if (!toBeDeletedItems.isEmpty()) {
            tBusTrateMapper.deleteTrateItemById(toBeDeletedItems.stream().map(TBusTrateItemDTO::getId).collect(Collectors.toList()));
        }

        List<TBusTrateItemCargoDTO> cargos = trate.getItems().stream().flatMap(v1 -> {
            return v1.getCargos().stream().peek(v2 -> {
                v2.setId(snowflake.nextId());
                v2.setTrateItemId(v1.getId());
            });
        }).collect(Collectors.toList());
        tBusTrateMapper.insertTrateCargo(cargos);

        List<TBusTrateItemDetailDTO> details = trate.getItems().stream().flatMap(v1 -> {
            return Optional.ofNullable(v1.getDetails()).orElse(Collections.emptyList()).stream().peek(v2 -> {
                v2.setId(snowflake.nextId());
                v2.setTrateItemId(v1.getId());
            });
        }).collect(Collectors.toList());
        if (!details.isEmpty()) {
            tBusTrateMapper.insertTrateDetail(details);
        }
    }

    @Override
    public Pages<TBusTrateDTO> listTrate(PageParameter parameter, TBusTrateQueryDTO query) {
        return PageHelperUtils.limit(parameter, () -> {
            Page<TBusTrateDTO> page = tBusTrateMapper.listTrate(query);
            if (!page.isEmpty()) {
                List<Long> ids = page.stream().map(TBusTrateDTO::getId).collect(Collectors.toList());
                List<TBusTrateContractDTO> contracts = tBusTrateMapper.listTrateContract(ids);
                List<TBusTrateCustomerDTO> customers = tBusTrateMapper.listTrateCustomer(ids);
                List<TBusTrateItemDTO> items = tBusTrateMapper.listTrateItem(ids);
                List<Long> trateItemIds = items.stream().map(TBusTrateItemDTO::getId).collect(Collectors.toList());
                List<TBusTrateItemCargoDTO> cargos = tBusTrateMapper.listTrateItemCargo(trateItemIds);
                List<TBusTrateItemDetailDTO> details = tBusTrateMapper.listTrateItemDetail(trateItemIds);
                page.forEach(v1 -> {
                    List<TBusTrateContractDTO> tempContracts = contracts.stream().filter(v2 -> v1.getId().equals(v2.getTrateId())).collect(Collectors.toList());
                    v1.setContracts(tempContracts);
                    List<TBusTrateCustomerDTO> tempCustomers = customers.stream().filter(v2 -> v1.getId().equals(v2.getTrateId())).collect(Collectors.toList());
                    v1.setCustomers(tempCustomers);
                    List<TBusTrateItemDTO> tempItems = items.stream().filter(v2 -> v1.getId().equals(v2.getTrateId())).collect(Collectors.toList());
                    v1.setItems(tempItems);
                    v1.getItems().forEach(v2 -> {
                        List<TBusTrateItemCargoDTO> tempCargos = cargos.stream().filter(v3 -> v2.getId().equals(v3.getTrateItemId())).collect(Collectors.toList());
                        v2.setCargos(tempCargos);
                    });
                    v1.getItems().forEach(v2 -> {
                        List<TBusTrateItemDetailDTO> tempDetails = details.stream().filter(v3 -> v2.getId().equals(v3.getTrateItemId())).collect(Collectors.toList());
                        v2.setDetails(tempDetails);
                    });
                });
            }
            page.forEach(v1 -> {
                String contractNos = v1.getContracts().stream().map(TBusTrateContractDTO::getContractNo).collect(Collectors.joining(","));
                String customerNames = v1.getCustomers().stream().map(TBusTrateCustomerDTO::getCustomerName).collect(Collectors.joining(","));
                v1.setContractNos(contractNos);
                v1.setCustomerNames(customerNames);
            });
            return page;
        });
    }

    @Override
    public void release(Long id) {
        tBusTrateMapper.release(id);
    }

    @Override
    public void cancelRelease(Long id) {
        boolean isUsedByContract = tBusTrateMapper.isUsedByContract(id);
        if (isUsedByContract) {
            throw new BusinessRuntimeException("当前阶梯费率已关联合同，无法撤销发布");
        }
        tBusTrateMapper.cancelRelease(id);
    }

    @Override
    public void updateOriginAccNumber(Long trateItemId, BigDecimal originAccNumber) {
        tBusTrateMapper.updateOriginAccNumber(trateItemId, originAccNumber);
    }
}