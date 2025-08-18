package com.yy.ppm.business.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationDTO;
import com.yy.ppm.business.bean.dto.trustTradeReservation.TBusTrustTradeReservationQueryDTO;
import com.yy.ppm.business.bean.po.TBusAssignFleetPO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservatCarPO;
import com.yy.ppm.business.bean.po.TBusTrustTradeReservationPO;
import com.yy.ppm.business.mapper.TBusTrustTradeReservationMapper;
import com.yy.ppm.business.service.TBusTrustTradeReservationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-05 15:37
 */
@Service
public class TBusTrustTradeReservationServiceImpl implements TBusTrustTradeReservationService {

    @Autowired
    private TBusTrustTradeReservationMapper tBusTrustTradeReservationMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<TBusTrustCargoDTO> listTrustCargo(String keyword, Long trustCargoId, PageParameter parameter) {
        if (StringUtils.isBlank(keyword) && trustCargoId == null) {
            return Collections.emptyList();
        }

        Pages<TBusTrustCargoDTO> pages = PageHelperUtils.limit(parameter, () -> {
            Page<TBusTrustCargoDTO> page = tBusTrustTradeReservationMapper.listTrustCargo(keyword, trustCargoId);
            page.forEach(v1 -> {
                if (v1.getTbtShipvoyageId() != null) {
                    v1.setShipNameVoyage(v1.getTbtShipName() + "_" + v1.getTdsiVoyage());
                }
            });
            return page;
        });
        return pages.getPages();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertTrustTradeReservation(@RequestBody TBusTrustTradeReservationDTO trustTradeReservation) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.ASSIGN_FLEET_AND_TRUST_TRADE_RESERVATION_KEY.getCode() + trustTradeReservation.getTrustCargoId())
                .build().run(() -> {
                    List<TBusAssignFleetPO> assignFleetList = tBusTrustTradeReservationMapper.getAssignFleetList(trustTradeReservation.getTrustCargoId(), trustTradeReservation.getCustomerId());
                    TBusAssignFleetPO assignFleet = CollectionUtils.isNotEmpty(assignFleetList)?assignFleetList.get(0):null;
                    List<TBusTrustTradeReservationPO> currentTrustTradeReservation = tBusTrustTradeReservationMapper.listTrustTradeReservationByTrustCargoIdAndCustomerId(trustTradeReservation.getTrustCargoId(), trustTradeReservation.getCustomerId());
                    int currentQuantity = currentTrustTradeReservation.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
                    int toBeInsertedQuantity = Optional.ofNullable(trustTradeReservation.getQuantity()).orElse(0);
                    int totalQuantity = currentQuantity + toBeInsertedQuantity;
                    int planQuantity = ObjectUtils.isEmpty(assignFleet)?0:Optional.ofNullable(assignFleet.getQuantity()).orElse(0);
                    if (totalQuantity > planQuantity) {
                        throw new BusinessRuntimeException("超出计划件数");
                    }
                    BigDecimal currentTon = currentTrustTradeReservation.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal toBeInsertedTon = Optional.ofNullable(trustTradeReservation.getTon()).orElse(BigDecimal.ZERO);
                    BigDecimal totalTon = currentTon.add(toBeInsertedTon);
                    BigDecimal planTon = Optional.ofNullable(assignFleet.getTon()).orElse(BigDecimal.ZERO);
                    if (totalTon.compareTo(planTon) > 0) {
                        throw new BusinessRuntimeException("超出计划重量");
                    }

                    trustTradeReservation.setId(snowflake.nextId());
                    tBusTrustTradeReservationMapper.insertTrustTradeReservation(trustTradeReservation);

                    if (CollectionUtils.isNotEmpty(trustTradeReservation.getCars())) {
                        trustTradeReservation.getCars().forEach(v1 -> {
                            v1.setId(snowflake.nextId());
                            v1.setTrustId(trustTradeReservation.getTrustId());
                            v1.setTrustTradeReservationId(trustTradeReservation.getId());
                        });
                        tBusTrustTradeReservationMapper.insertTBusTrustTradeReservatCar(trustTradeReservation.getCars());
                    }
                });
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void insertTrustTradeReservationCar(@RequestBody TBusTrustTradeReservationDTO trustTradeReservation) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.ASSIGN_FLEET_AND_TRUST_TRADE_RESERVATION_KEY.getCode() + trustTradeReservation.getTrustCargoId())
                .build().run(() -> {
                    TBusTrustTradeReservationDTO dto = new TBusTrustTradeReservationDTO();
                    dto.setTrustId(trustTradeReservation.getTrustId());
                    dto.setTrustCargoId(trustTradeReservation.getTrustCargoId());
                    dto.setCustomerId(trustTradeReservation.getCustomerId());

                    List<TBusTrustTradeReservationDTO> dtos = tBusTrustTradeReservationMapper.listByCondition(dto);
                    TBusTrustTradeReservationDTO tradeReservationDTO = CollectionUtils.isNotEmpty(dtos)?dtos.get(0):new TBusTrustTradeReservationDTO();
                    if (CollectionUtils.isNotEmpty(trustTradeReservation.getCars())) {
                        trustTradeReservation.getCars().forEach(v1 -> {
                            v1.setId(snowflake.nextId());
                            v1.setTrustId(trustTradeReservation.getTrustId());
                            v1.setTrustTradeReservationId(tradeReservationDTO.getId());
                        });
                        tBusTrustTradeReservationMapper.insertTBusTrustTradeReservatCar(trustTradeReservation.getCars());
                    }
                });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void updateTrustTradeReservation(@RequestBody TBusTrustTradeReservationDTO trustTradeReservation) {
        DistributedLock.newBuilder().store(redisTemplate)
                .key(DistributedLockKeyPrefixEnum.ASSIGN_FLEET_AND_TRUST_TRADE_RESERVATION_KEY.getCode() + trustTradeReservation.getTrustCargoId())
                .build().run(() -> {
                    List<TBusAssignFleetPO> assignFleetList = tBusTrustTradeReservationMapper.getAssignFleetList(trustTradeReservation.getTrustCargoId(), trustTradeReservation.getCustomerId());
                    TBusAssignFleetPO assignFleet = CollectionUtils.isNotEmpty(assignFleetList)?assignFleetList.get(0):null;
                    List<TBusTrustTradeReservationPO> currentTrustTradeReservation = tBusTrustTradeReservationMapper.listTrustTradeReservationByTrustCargoIdAndCustomerId(trustTradeReservation.getTrustCargoId(), trustTradeReservation.getCustomerId());
                    int currentQuantity = currentTrustTradeReservation.stream().mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
                    int currentToBeUpdatedQuantity = currentTrustTradeReservation.stream().filter(v1 -> trustTradeReservation.getId().equals(v1.getId())).mapToInt(v1 -> Optional.ofNullable(v1.getQuantity()).orElse(0)).sum();
                    int toBeUpdatedQuantity = Optional.ofNullable(trustTradeReservation.getQuantity()).orElse(0);
                    int totalQuantity = currentQuantity - currentToBeUpdatedQuantity + toBeUpdatedQuantity;
                    int planQuantity = Optional.ofNullable(assignFleet.getQuantity()).orElse(0);
                    if (totalQuantity > planQuantity) {
                        throw new BusinessRuntimeException("超出计划件数");
                    }
                    BigDecimal currentTon = currentTrustTradeReservation.stream().map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal currentToBeUpdatedTon = currentTrustTradeReservation.stream().filter(v1 -> trustTradeReservation.getId().equals(v1.getId())).map(v1 -> Optional.ofNullable(v1.getTon()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal toBeUpdatedTon = Optional.ofNullable(trustTradeReservation.getTon()).orElse(BigDecimal.ZERO);
                    BigDecimal totalTon = currentTon.subtract(currentToBeUpdatedTon).add(toBeUpdatedTon);
                    BigDecimal planTon = Optional.ofNullable(assignFleet.getTon()).orElse(BigDecimal.ZERO);
                    if (totalTon.compareTo(planTon) > 0) {
                        throw new BusinessRuntimeException("超出计划重量");
                    }

                    tBusTrustTradeReservationMapper.updateTrustTradeReservation(trustTradeReservation);

                    tBusTrustTradeReservationMapper.deleteTBusTrustTradeReservatCar(Collections.singletonList(trustTradeReservation.getId()));

                    if (CollectionUtils.isNotEmpty(trustTradeReservation.getCars())) {
                        trustTradeReservation.getCars().forEach(v1 -> {
                            v1.setId(snowflake.nextId());
                            v1.setTrustId(trustTradeReservation.getTrustId());
                            v1.setTrustTradeReservationId(trustTradeReservation.getId());
                        });
                        tBusTrustTradeReservationMapper.insertTBusTrustTradeReservatCar(trustTradeReservation.getCars());
                    }
                });
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteTrustTradeReservation(List<Long> trustTradeReservationIds) {
        tBusTrustTradeReservationMapper.deleteTrustTradeReservation(trustTradeReservationIds);

        tBusTrustTradeReservationMapper.deleteTBusTrustTradeReservatCar(trustTradeReservationIds);
    }

    @Override
    public Pages<TBusTrustTradeReservationDTO> listTrustTradeReservation(TBusTrustTradeReservationQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            Page<TBusTrustTradeReservationDTO> page = tBusTrustTradeReservationMapper.listTrustTradeReservation(query);
            page.forEach(v1 -> {
                if (v1.getTbtShipvoyageId() != null) {
                    v1.setShipNameVoyage(v1.getTbtShipName() + "_" + v1.getTdsiVoyage());
                }
            });
            return page;
        });
    }

    @Override
    public List<TBusTrustTradeReservatCarPO> parseCars(MultipartFile file) {
        List<TBusTrustTradeReservatCarPO> cars = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) {
                    break;
                }
                XSSFCell cell0 = row.getCell(0);
                if (cell0 == null) {
                    continue;
                }
                if (cell0.getCellType() != CellType.STRING) {
                    continue;
                }
                if (cell0.getStringCellValue().isEmpty()) {
                    continue;
                }
                TBusTrustTradeReservatCarPO car = new TBusTrustTradeReservatCarPO();
                car.setCarNo(cell0.getStringCellValue());
                XSSFCell cell1 = row.getCell(1);
                if (cell1 != null && cell1.getCellType() == CellType.STRING) {
                    car.setDriverName(cell1.getStringCellValue());
                }
                XSSFCell cell2 = row.getCell(2);
                if (cell2 != null && cell2.getCellType() == CellType.STRING) {
                    car.setDriverIdCard(cell2.getStringCellValue());
                }
                XSSFCell cell3 = row.getCell(3);
                if (cell3 != null && cell3.getCellType() == CellType.STRING) {
                    car.setDriverPhone(cell3.getStringCellValue());
                }
                cars.add(car);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return cars;
    }

    @Override
    public Map<String, Object> getAvailableQuantityAndTon(Long assignFleetId, Long trustTradeReservationId) {
        Map<String, Object> result = tBusTrustTradeReservationMapper.getAvailableQuantityAndTon(assignFleetId, trustTradeReservationId);
        result.put("quantity", Integer.parseInt(String.valueOf(result.remove("quantity"))));
        BigDecimal ton = new BigDecimal(String.valueOf(result.remove("ton")));
        result.put("ton", ton.setScale(4, RoundingMode.HALF_UP));
        return result;
    }
}
