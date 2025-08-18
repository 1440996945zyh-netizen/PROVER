package com.yy.ppm.gis.service.impl;

import static com.yy.common.util.DateUtil.localDate2Date;
import static com.yy.common.util.DateUtil.str2LocalDate;
import static com.yy.common.util.str.StringUtil.getDouble;
import static com.yy.common.util.str.StringUtil.getInt;
import static com.yy.common.util.str.StringUtil.getLong;
import static com.yy.common.util.str.StringUtil.getString;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;

import com.yy.common.log.MicroLogger;
import com.yy.common.util.*;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.geojson.GeoJsonUtils;
import com.yy.ppm.produce.bean.SyncDTO;
import com.yy.ppm.gis.dto.onSiteDynamics.*;
import com.yy.ppm.machine.bean.dto.TMacTerminalStackPositionDTO;
import com.yy.ppm.master.bean.dto.MStorageStackDTO;
import com.yy.ppm.runpile.mapper.TRunPileMapper;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.util.Lists;
import com.yy.common.util.ShipDrawer.ShipPolygon;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.gis.dto.onSiteDynamics.Car;
import com.yy.ppm.gis.dto.onSiteDynamics.CarHistory;
import com.yy.ppm.gis.dto.onSiteDynamics.InoutDetailQueryDTO;
import com.yy.ppm.gis.dto.onSiteDynamics.Ship;
import com.yy.ppm.gis.dto.onSiteDynamics.Stack;
import com.yy.ppm.gis.dto.onSiteDynamics.Stack.StackCoordinate;
import com.yy.ppm.gis.mapper.OnSiteDynamicsMapper;
import com.yy.ppm.gis.service.OnSiteDynamicsService;
import com.yy.ppm.produce.bean.dto.portStorage.CleanPortStorageDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.service.TPrdPortStorageService;
import com.yy.ppm.runpile.bean.po.MStorageStackPositionPO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-06 17:18
 */
@Service
public class OnSiteDynamicsServiceImpl implements OnSiteDynamicsService {

    @Autowired
    private OnSiteDynamicsMapper onSiteDynamicsMapper;
    @Resource
    private TRunPileMapper tRunPileMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private TPrdPortStorageService tPrdPortStorageService;

    @Autowired
    private PublicService publicService;
    @Resource
    private SecurityUtils securityUtils;

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(OnSiteDynamicsServiceImpl.class);


    @Override
    public List<Ship> listShip() {
        List<Map<String, Object>> vesselVoyages = onSiteDynamicsMapper.listVesselVoyage();
        if (vesselVoyages.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> ids = vesselVoyages.stream().map(v1 -> getString(v1.get("id"))).collect(Collectors.toList());
        List<Map<String, Object>> vesselVoyageSupplements = onSiteDynamicsMapper.listVesselVoyageSupplement(ids);

        List<String> bollardCodes = vesselVoyageSupplements.stream()
                .flatMap(v1 -> Stream.of(getString(v1.get("beginBollard")), getString(v1.get("endBollard")))).distinct().collect(Collectors.toList());
        List<Map<String, Object>> berthBollards = onSiteDynamicsMapper.listBerthBollard(bollardCodes);

        return vesselVoyages.stream().map(v1 -> {
            Map<String, Object> vesselVoyageSupplement = vesselVoyageSupplements.stream()
                    .filter(v2 -> v1.get("id").equals(v2.get("voyageId")))
                    .findFirst().orElse(Collections.emptyMap());
            Map<String, Object> beginBollard = berthBollards.stream()
                    .filter(v2 -> vesselVoyageSupplement.get("beginBollard").equals(v2.get("bollardCode")))
                    .findFirst().orElse(Collections.emptyMap());
            Map<String, Object> endBollard = berthBollards.stream()
                    .filter(v2 -> vesselVoyageSupplement.get("endBollard").equals(v2.get("bollardCode")))
                    .findFirst().orElse(Collections.emptyMap());

            Ship ship = new Ship();
            ship.setId(getString(v1.get("id")));
            ship.setVesselName(getString(v1.get("vesselName")));
            ship.setBeginBollard(getString(vesselVoyageSupplement.get("beginBollard")));
            ship.setEndBollard(getString(vesselVoyageSupplement.get("endBollard")));
            ship.setGunwale(getString(vesselVoyageSupplement.get("gunwale")));
            ship.setWidth(30.);
            ship.setBeginBollardName(getString(beginBollard.get("bollardName")));
            ship.setBeginBollardLon(getDouble(beginBollard.get("lon")));
            ship.setBeginBollardLat(getDouble(beginBollard.get("lat")));
            ship.setEndBollardName(getString(endBollard.get("bollardName")));
            ship.setEndBollardLon(getDouble(endBollard.get("lon")));
            ship.setEndBollardLat(getDouble(endBollard.get("lat")));

            // 画船
            ShipPolygon polygon = ShipDrawer.newBuilder()
                    .beginBollardLon(ship.getBeginBollardLon())
                    .beginBollardLat(ship.getBeginBollardLat())
                    .endBollardLon(ship.getEndBollardLon())
                    .endBollardLat(ship.getEndBollardLat())
                    .gunwale(ship.getGunwale())
                    .width(ship.getWidth())
                    .build()
                    .draw();
            BeanUtil.copyProperties(polygon, ship);

            return ship;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Pile> listPile() {
        List<Pile> pileResult = onSiteDynamicsMapper.listPileWithBerth();
        //List<Map<String, Object>> tmpReult=  onSiteDynamicsMapper.listPileWithBerth();
        return pileResult;
    }

    @Override
    public List<Stack> listStack() {
//        List<Map<String, Object>> storageStack = onSiteDynamicsMapper.listStorageStack();
//        if (storageStack.isEmpty()) {
//            return Collections.emptyList();
//        }
//        List<String> storageCodes = storageStack.stream().map(v1 -> getString(v1.get("storageCode"))).collect(Collectors.toList());
//        List<Map<String, Object>> storages = onSiteDynamicsMapper.listStorage(storageCodes);
//        List<String> stackCodes = storageStack.stream().map(v1 -> getString(v1.get("stackCode"))).collect(Collectors.toList());
//        List<Map<String, Object>> storageStackBoundayrs = onSiteDynamicsMapper.listStorageStackBoundayr(stackCodes);

    	List<Map<String, Object>> storageStack = onSiteDynamicsMapper.listStorageStack2();
        return storageStack.stream().map(v1 -> {


//            Map<String, Object> tempStorages = storages.stream()
//                    .filter(v2 -> v1.get("storageCode").equals(v2.get("storageCode")))
//                    .findFirst().orElse(Collections.emptyMap());
//            List<Map<String, Object>> tempStorageStackBoundayrs = storageStackBoundayrs.stream()
//                    .filter(v2 -> v1.get("stackCode").equals(v2.get("stackCode")))
//                    .collect(Collectors.toList());

            Stack stack = new Stack();
            stack.setId(getLong(v1.get("id")));
            stack.setStorageCode(getString(v1.get("storageCode")));
            stack.setStackCode(getString(v1.get("stackCode")));
            stack.setStackName(getString(v1.get("stackName")));
            stack.setWorkAreaCd(getString(v1.get("workAreaCd")));

            stack.setStorageId(getLong(v1.get("id1")));
            stack.setStorageCode(getString(v1.get("storageCode1")));
            stack.setStorageName(getString(v1.get("storageName")));
            stack.setStorageTypeCode(getString(v1.get("storageTypeCode")));
            stack.setStorageTypeName(getString(v1.get("storageTypeName")));
            stack.setCreateByName(getString(v1.get("createByName")));
            stack.setPositionTime(getString(v1.get("positionTime")));

            String position = getString(v1.get("position"));

            if(StringUtils.isNotBlank(position)) {
				try {
					String[][] parseArr = JSONUtils.NON_NULL.toJavaObject(clobToString((Clob)v1.get("position")), String[][].class);

	                List<StackCoordinate> coordinates = Lists.newArrayList();
	                for (int i=0; i< parseArr.length; i++) {
	                    StackCoordinate coordinate = new StackCoordinate();
	                    coordinate.setSeqNo(i + 1);
	                    coordinate.setLon(getDouble(parseArr[i][0]));
	                    coordinate.setLat(getDouble(parseArr[i][1]));
	                    coordinates.add(coordinate);
	    			}

	                stack.setCoordinates(coordinates);

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            stack.setCargoColor(getString(v1.get("cargoColor")));
            stack.setCargoInfos(getString(v1.get("cargoInfos")));
            stack.setSideLength(getString(v1.get("sideLength")));
            Object area;
            if ((area = v1.get("area")) != null) {
                stack.setArea(new BigDecimal(getString(area)));
            }

            return stack;
        }).collect(Collectors.toList());
    }

	@Override
	public List<Stack> listStackForMac() {

		Map<String, Object> paramMap = publicService.getDateAndShift(null);

		List<Map<String, Object>> storageStack = onSiteDynamicsMapper.listStorageStackForMac(paramMap.get("workDate").toString(),
				paramMap.get("classCode").toString());
		return storageStack.stream().map(v1 -> {
			Stack stack = new Stack();
			stack.setId(getLong(v1.get("id")));
			stack.setStackName(getString(v1.get("stackName3")));
			stack.setCarCount(getInt(v1.get("carCount") == null?"0":v1.get("carCount")));

			String position = getString(v1.get("position"));

			if(StringUtils.isNotBlank(position)) {
				try {
					String[][] parseArr = JSONUtils.NON_NULL.toJavaObject(clobToString((Clob)v1.get("position")), String[][].class);

	                List<StackCoordinate> coordinates = Lists.newArrayList();
	                for (int i=0; i< parseArr.length; i++) {
	                    StackCoordinate coordinate = new StackCoordinate();
	                    coordinate.setSeqNo(i + 1);
	                    coordinate.setLon(getDouble(parseArr[i][0]));
	                    coordinate.setLat(getDouble(parseArr[i][1]));
	                    coordinates.add(coordinate);
	    			}
	                stack.setCoordinates(coordinates);
				} catch (Exception e) {
				}
			}
            stack.setCargoInfos(getString(v1.get("cargoInfos")));
            stack.setMacCode(getString(v1.get("macCode")));
			return stack;
		}).collect(Collectors.toList());
	}

    @Override
    public Map<String, Object> getStack(Long id) {
        return onSiteDynamicsMapper.getStack(id);
    }

	@Override
	public Map<String, Object> getStackForMacApp(Long id) {
        return onSiteDynamicsMapper.getStackForMacApp(id);
	}

    public static String clobToString(Clob clob) throws SQLException, IOException {
    	StringBuffer sb = new StringBuffer();
    	Reader reader = clob.getCharacterStream();
    	BufferedReader br = new BufferedReader(reader);
    	String line = null;
    	while ((line = br.readLine()) != null) {
    	sb.append(line);
    	}
    	return sb.toString();
    }


    @Override
    public List<Car> listCar() {
        List<Map<String, Object>> locations = onSiteDynamicsMapper.listLocation();

        ZoneId zoneId = ZoneId.systemDefault();

        return locations.stream().map(v1 -> {
            Map<String, Object> location = locations.stream()
                    .filter(v2 -> v1.get("macId").equals(v2.get("macId")))
                    .findFirst().orElse(Collections.emptyMap());

            Car car = new Car();
            car.setMacId(getString(location.get("macId")));
            car.setMacName(getString(location.get("macName")));
            car.setMacTypeCode(getString(location.get("macTypeCode")));
            car.setMacTypeName(getString(location.get("macTypeName")));
            car.setLon(getDouble(location.get("lon")));
            car.setLat(getDouble(location.get("lat")));
            car.setSpeed(getInt(location.get("speed")));
            car.setDirection(getString(location.get("direction")));
            car.setGpsTime(DateUtils.formatDate((Date) location.get("gpsTime"), "yyyy-MM-dd HH:mm:ss"));

            if(car.getGpsTime() == null) {
            	car.setOnline("0");
            } else {
            	Instant instant = ((Date) location.get("gpsTime")).toInstant();
                LocalDateTime start = instant.atZone(zoneId).toLocalDateTime();
        		LocalDateTime end = LocalDateTime.now();
        		Duration duration = Duration.between(start, end);
        		long millis = duration.toMillis();
            	if(millis <= 10000) {
            		car.setOnline("1");
            	}
            }
            return car;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CarHistory> listCarHistory(String macId, Date beginTime, @NotNull Date endTime) {
        List<Map<String, Object>> historyList = onSiteDynamicsMapper.listLocationHistory(macId, beginTime, endTime);
        return historyList.stream().map(v1 -> {
            CarHistory history = new CarHistory();
            history.setMacId(getString(v1.get("macId")));
            history.setLon(getDouble(v1.get("lon")));
            history.setLat(getDouble(v1.get("lat")));
            history.setSpeed(getInt(v1.get("speed")));
            history.setDirection(getString(v1.get("direction")));
            history.setGpsTime(DateUtils.formatDate((Date) v1.get("gpsTime"), "yyyy-MM-dd HH:mm:ss"));
            return history;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void insertStorageStackPosition(MStorageStackPositionPO storageStackPosition) {
        storageStackPosition.setId(snowflake.nextId());
        storageStackPosition.setPositionFrom("2");
        storageStackPosition.setPositionTime(new Date());
        storageStackPosition.setDelFlag("0");
        isConflict(storageStackPosition);

        SyncDTO dto = new SyncDTO();
        dto.setId(snowflake.nextId());
        dto.setBizId(storageStackPosition.getStackId());
        //dto.setBizType(BusSyncEnum.LIBRARY_PLACE.getCode());
        dto.setIsDelete("0");

        onSiteDynamicsMapper.insertStackYardCoordinate(storageStackPosition);
    }

    /**
     * 判断跑垛机建立的垛位区域是否与已存在的区域冲突
     * @param
     */
    public void isConflict(MStorageStackPositionPO storageStackPosition){
        String wktPolygon = formatToWktPolygon(storageStackPosition.getPosition());
        List<TMacTerminalStackPositionDTO> stackPositionList = tRunPileMapper.listByCondition(storageStackPosition);
        stackPositionList.forEach(e->{
            try {
                LOGGER.info(e.getStackName());
                LOGGER.info(e.getPosition());
                if(GeoJsonUtils.isConflict(wktPolygon,formatToWktPolygon(e.getPosition()))){
                    throw new BusinessRuntimeException("垛位重合，请重新确定点位");
                }
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private String formatToWktPolygon(String wktPolygon){
        List<String> list = new ArrayList<>(Arrays.asList(wktPolygon.replaceAll("\\[\\[","").replaceAll("]]","").split("],\\[")));
        if(!list.isEmpty() && !list.get(0).equals(list.get(list.size()-1))){
            String firstStr = list.get(0);
            list.add(list.size(),firstStr);
            wktPolygon = "POLYGON ((";
            for (int i =0;i<list.size();i++) {
                String s = list.get(i);
                s = s.replaceAll(","," ");
                if(i==(list.size()-1)){
                    wktPolygon+=s+"))";
                }else{
                    wktPolygon +=s+",";
                }
            }
            return wktPolygon;
        }else{
            return wktPolygon.replaceAll(","," ")
                    .replaceAll("] \\[",",")
                    .replaceAll("] \\[",",")
                    .replaceAll("\\[\\[","POLYGON ((")
                    .replaceAll("]]","))");
        }
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void deleteStorageStackPosition(Long stackId) {
        //删除添加删除人删除时间
        List<MStorageStackDTO> list = onSiteDynamicsMapper.getStackListByStackId(stackId);
        List<Long> ids = list.stream().map(MStorageStackDTO::getId).collect(Collectors.toList());
        onSiteDynamicsMapper.deleteStorageStackPosition(stackId,ids);
        onSiteDynamicsMapper.addDelUserInfo(list.get(0).getId(),securityUtils.getLoginUserId(),new Date());

        List<TPrdPortStorageDetailPO> portStorageDetails = onSiteDynamicsMapper.listPortStorageDetail(stackId);
        Map<Map<String, Object>, List<TPrdPortStorageDetailPO>> groupByPortStorageCompositeKey = portStorageDetails.stream()
                .collect(Collectors.groupingBy(v1 -> {
                    Map<String, Object> portStorageCompositeKey = new HashMap<>();
                    portStorageCompositeKey.put("cargoInfoId", v1.getCargoInfoId());
                    portStorageCompositeKey.put("storehouseId", v1.getStorehouseId());
                    portStorageCompositeKey.put("regionId", v1.getRegionId());
                    portStorageCompositeKey.put("massId", v1.getMassId());
                    return portStorageCompositeKey;
                }));
        List<Map<String, Object>> compositeKeys = new ArrayList<>(groupByPortStorageCompositeKey.keySet());

        Map<String, Object> dateAndShift = publicService.getDateAndShift(null);
        Date workDate = localDate2Date(str2LocalDate(String.valueOf(dateAndShift.get("workDate"))));
        String classCode = String.valueOf(dateAndShift.get("classCode"));
        String className = String.valueOf(dateAndShift.get("className"));

        compositeKeys.forEach(v1 -> {
            CleanPortStorageDTO cleanPortStorage = new CleanPortStorageDTO();
            cleanPortStorage.setCargoInfoId(Long.valueOf(String.valueOf(v1.get("cargoInfoId"))));
            cleanPortStorage.setStorehouseId(Long.valueOf(String.valueOf(v1.get("storehouseId"))));
            cleanPortStorage.setRegionId(Long.valueOf(String.valueOf(v1.get("regionId"))));
            cleanPortStorage.setMassId(Long.valueOf(String.valueOf(v1.get("massId"))));
            cleanPortStorage.setWorkDate(workDate);
            cleanPortStorage.setClassCode(classCode);
            cleanPortStorage.setClassName(className);
            try {
                tPrdPortStorageService.cleanPortStorage(cleanPortStorage);
            } catch (BusinessRuntimeException ignored) {
            }
        });
        try {
            SyncDTO syncDto = new SyncDTO();
            syncDto.setId(snowflake.nextId());
            syncDto.setBizId(stackId);
           // syncDto.setBizType(BusSyncEnum.LIBRARY_PLACE.getCode());
            syncDto.setIsDelete("0");
        } catch (Exception e) {
            // 为避免对原有业务产生影响，此处捕获异常不做处理
            LOGGER.error("deleteStorageStackPosition:" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getInoutDetail(InoutDetailQueryDTO query) {
        List<TPrdPortStorageDetailPO> portStorageDetails = onSiteDynamicsMapper._listPortStorageDetail(
                query.getCargoInfoNo(), query.getMassId(), query.getBeginWorkDate()
                , query.getBeginClassCode(), query.getEndWorkDate(), query.getEndClassCode(), query.getProcessDetailCode()
        );

        String cargoInfoLabel = EMPTY;
        if (!portStorageDetails.isEmpty()) {
            cargoInfoLabel = onSiteDynamicsMapper.getCargoInfoLabel(portStorageDetails.get(0).getCargoInfoId());
        }

        Map<Boolean, List<TPrdPortStorageDetailPO>> groupByCompareToZero = portStorageDetails.stream()
                .collect(Collectors.groupingBy(v1 -> v1.getTon().compareTo(BigDecimal.ZERO) > 0));

        List<TPrdPortStorageDetailPO> in = Optional.ofNullable(groupByCompareToZero.get(true)).orElse(Collections.emptyList());
        List<TPrdPortStorageDetailPO> out = Optional.ofNullable(groupByCompareToZero.get(false)).orElse(Collections.emptyList());

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
            balanceQuantity = Optional.ofNullable(inQuantity).orElse(0) + Optional.ofNullable(outQuantity).orElse(0);
        }
        BigDecimal balanceTon = inTon.add(outTon);

        HashMap<String, Object> result = new HashMap<>();
        result.put("in", in);
        result.put("inQuantity", inQuantity);
        result.put("inTon", inTon);
        result.put("out", out);
        result.put("outQuantity", outQuantity);
        result.put("outTon", outTon);
        result.put("balanceQuantity", balanceQuantity);
        result.put("balanceTon", balanceTon);
        result.put("cargoInfoLabel", cargoInfoLabel);
        return result;
    }
}
