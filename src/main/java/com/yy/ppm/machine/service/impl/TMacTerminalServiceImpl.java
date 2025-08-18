package com.yy.ppm.machine.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.concurrent.DistributedLock;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.appWork.mapper.TallyMapper;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.mapper.TBusCustomerMapper;
import com.yy.ppm.common.enums.DistributedLockKeyPrefixEnum;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.machine.bean.dto.*;
import com.yy.ppm.machine.bean.po.TMacWorkNowPO;
import com.yy.ppm.machine.bean.po.TMacWorkTimePO;
import com.yy.ppm.machine.enums.PlanTypeEnum;
import com.yy.ppm.machine.mapper.TMacTerminalMapper;
import com.yy.ppm.machine.service.TMacTerminalService;
import com.yy.ppm.produce.bean.po.TPrdDispatchSecondaryPO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TMacTerminalServiceImpl implements TMacTerminalService {

    private static final MicroLogger LOGGER = new MicroLogger(TMacTerminalServiceImpl.class);

    @Resource
    private TMacTerminalMapper tMacTerminalMapper;

    @Resource
    private PublicService publicService;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

	@Resource
	private SysParameterMapper sysParameterMapper;

	@Resource
	private TBusCustomerMapper customerMapper;

	@Resource
	private TallyMapper tallyMapper;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;


	/**
     * 根据imei号查询设备
     */
	@Override
	public TMacTerminalDTO getMachineByImei(String imei) {
		return tMacTerminalMapper.getMachineByImei(imei);
	}
	/**
     * 根据imei号查询设备
     */
	@Override
	public List<TPortVehicleNumDTO> queryPortVehicleNum() {
		return tMacTerminalMapper.queryPortVehicleNum();
	}

	@Override
	public Integer getMacDistance() {
		return tMacTerminalMapper.getMacDistance();
	}

	@Override
	public List<TPrdWorkPlanDTO> getWorkPlan(String imei, String macCode, String portCode) {

		TMacTerminalDTO macTerminalDTO = null;
		if(StringUtils.isNoneBlank(imei)) {
			macTerminalDTO = tMacTerminalMapper.getMachineByImei(imei);
			if(macTerminalDTO != null) {
				macCode = macTerminalDTO.getMacCode();
			}
		}

		// 根据系统当前时间获取日期、班次(workDate、classCode)
		Map<String, Object> paramMap = publicService.getDateAndShift(null);
		paramMap.put("macCode", macCode);
		paramMap.put("portCode", portCode);

		List<TMacTerminalWorkPlanDTO> resultList_ = Lists.newArrayList();

		// 查询直取装卸船
		List<TMacTerminalWorkPlanDTO> shipWorkPlanList = tMacTerminalMapper.getShipWorkPlanList(paramMap);
		if(CollectionUtils.isNotEmpty(shipWorkPlanList)) {
			for (TMacTerminalWorkPlanDTO shipWorkPlan : shipWorkPlanList) {
				// 根据trustId 查询对应的集疏港的trustId planNo
				List<String> businessNoList = tMacTerminalMapper.getBusinessNoList(shipWorkPlan.getTrustId());
				if(CollectionUtils.isNotEmpty(businessNoList)) {
					List<TMacTerminalWorkPlanDTO> shipDataList = tMacTerminalMapper.getShipDataWorkPlanList(securityUtils.getLoginUserId(), businessNoList, portCode, null);
					if(CollectionUtils.isNotEmpty(shipDataList)) {
						resultList_.addAll(shipWorkPlanList);
					}
				}
			}
		}

		List<TMacTerminalWorkPlanDTO> dataList = tMacTerminalMapper.getWorkPlanByCondition(paramMap);
		resultList_.addAll(dataList);

		List<Long> workPlanList = resultList_.stream().map(p -> p.getWorkPlanId()).collect(Collectors.toList());

		if(CollectionUtils.isEmpty(workPlanList)) {
			return Lists.newArrayList();
		} else {
			paramMap.put("workPlanIds", workPlanList);
			List<TPrdWorkPlanDTO> resultList = tMacTerminalMapper.getWorkPlan(paramMap);
			for (TPrdWorkPlanDTO prdWorkPlanDTO : resultList) {
				prdWorkPlanDTO.setWorkPlanLabel(
						(StringUtils.isNotBlank(prdWorkPlanDTO.getTrustNo())?prdWorkPlanDTO.getTrustNo():"*") + "  "
						+ (StringUtils.isNotBlank(prdWorkPlanDTO.getShipName())?prdWorkPlanDTO.getShipName():"*") + "  "
						+ (StringUtils.isNotBlank(prdWorkPlanDTO.getScn())?prdWorkPlanDTO.getScn():"*"));
				prdWorkPlanDTO.setWorkPlanLabel2(
						(StringUtils.isNotBlank(prdWorkPlanDTO.getCargoName())?prdWorkPlanDTO.getCargoName():"*") + "  "
						+ (StringUtils.isNotBlank(prdWorkPlanDTO.getMassNamesSource())?prdWorkPlanDTO.getMassNamesSource():"*"));
				prdWorkPlanDTO.setWorkPlanLabel3(prdWorkPlanDTO.getCargoOwnerName());
				prdWorkPlanDTO.setWorkPlanLabel4(prdWorkPlanDTO.getPoundRemark());
			}
			return resultList;
		}
	}

	@Override
	public List<TPrdWorkPlanDTO> getDaoYunWorkPlan(String imei, String macCode, String portCode) {
		TMacTerminalDTO macTerminalDTO = null;
		if(StringUtils.isNoneBlank(imei)) {
			macTerminalDTO = tMacTerminalMapper.getMachineByImei(imei);
			if(macTerminalDTO != null) {
				macCode = macTerminalDTO.getMacCode();
			}
		}
		// 根据系统当前时间获取日期、班次(workDate、classCode)
		Map<String, Object> paramMap = publicService.getDateAndShift(null);
		paramMap.put("macCode", macCode);
		paramMap.put("portCode", portCode);
		// 查询直取装卸船
		List<TMacTerminalWorkPlanDTO> shipWorkPlanList = tMacTerminalMapper.getDyWorkPlanList(paramMap);
		List<Long> workPlanList = shipWorkPlanList.stream().map(p -> p.getWorkPlanId()).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(workPlanList)) {
			return Lists.newArrayList();
		} else {
			paramMap.put("workPlanIds", workPlanList);
			List<TPrdWorkPlanDTO> resultList = tMacTerminalMapper.getWorkPlan(paramMap);
			for (TPrdWorkPlanDTO prdWorkPlanDTO : resultList) {
				prdWorkPlanDTO.setWorkPlanLabel(
						(StringUtils.isNotBlank(prdWorkPlanDTO.getTrustNo())?prdWorkPlanDTO.getTrustNo():"*") + "  "
						+ (StringUtils.isNotBlank(prdWorkPlanDTO.getShipName())?prdWorkPlanDTO.getShipName():"*") + "  "
						+ (StringUtils.isNotBlank(prdWorkPlanDTO.getScn())?prdWorkPlanDTO.getScn():"*"));
				prdWorkPlanDTO.setWorkPlanLabel2(
						(StringUtils.isNotBlank(prdWorkPlanDTO.getCargoName())?prdWorkPlanDTO.getCargoName():"*") + "  "
						+ (StringUtils.isNotBlank(prdWorkPlanDTO.getMassNamesSource())?prdWorkPlanDTO.getMassNamesSource():"*"));
				prdWorkPlanDTO.setWorkPlanLabel3(prdWorkPlanDTO.getCargoOwnerName());
				prdWorkPlanDTO.setWorkPlanLabel4(prdWorkPlanDTO.getPoundRemark());
			}
			return resultList;
		}
	}

	public List<TMacTerminalWorkPlanDTO> getDyWorkPlanByCondition(String imei, String macCode, String workPlanId, String carNo, String portCode) {

		TMacTerminalDTO macTerminalDTO = null;
		if(StringUtils.isNoneBlank(imei)) {
			macTerminalDTO = tMacTerminalMapper.getMachineByImei(imei);
			if(macTerminalDTO != null) {
				macCode = macTerminalDTO.getMacCode();
			}
		}

		// 根据系统当前时间获取日期、班次(workDate、classCode)
		Map<String, Object> paramMap = publicService.getDateAndShift(null);
		paramMap.put("macCode", macCode);
		paramMap.put("workPlanId", workPlanId);
		paramMap.put("carNo", carNo);
		paramMap.put("portCode", portCode);
		paramMap.put("loginUserId", securityUtils.getLoginUserId());

		List<TMacTerminalWorkPlanDTO> resultList = Lists.newArrayList();

		List<TMacTerminalWorkPlanDTO> dataListPre = tMacTerminalMapper.getWorkPlanByCondition(paramMap);

		List<TMacTerminalWorkPlanDTO> dataList = Lists.newArrayList();

		if(CollectionUtils.isNotEmpty(dataListPre)) {
			for (TMacTerminalWorkPlanDTO data : dataListPre) {
				if(data.getCreateBy() == null || data.getCreateBy().equals(securityUtils.getLoginUserId())) {
					dataList.add(data);
				}
			}
		}
		// 判断集疏运车辆是否正在作业
		for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : dataList) {
			if(tMacTerminalWorkPlanDTO.getWorkTimeStart() == null
					&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
				tMacTerminalWorkPlanDTO.setWorkTimeStatus("10");
				resultList.add(tMacTerminalWorkPlanDTO);
			} else if(tMacTerminalWorkPlanDTO.getWorkTimeStart() != null
					&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
				tMacTerminalWorkPlanDTO.setWorkTimeStatus("20");
				resultList.add(tMacTerminalWorkPlanDTO);
			}
		}

		// 判断集疏运车辆是否在港超时1小时
		for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : resultList) {
			tMacTerminalWorkPlanDTO.setInPortStatus("10");
			if(tMacTerminalWorkPlanDTO.getInPortTime() != null) {
				long minutes = checkDateTimePhaseDiffMinutes(tMacTerminalWorkPlanDTO.getInPortTime(), new Date());
				if(minutes >= 30 && minutes < 60){
					tMacTerminalWorkPlanDTO.setInPortStatus("30");
				} else if(minutes >= 60) {
					tMacTerminalWorkPlanDTO.setInPortStatus("20");
				} else {
					tMacTerminalWorkPlanDTO.setInPortStatus("10");
				}
			}
		}
		Collections.sort(resultList, new Comparator<TMacTerminalWorkPlanDTO>() {
			@Override
			public int compare(TMacTerminalWorkPlanDTO u1, TMacTerminalWorkPlanDTO u2) {
				if(Integer.parseInt(u1.getWorkTimeStatus()) > Integer.parseInt(u2.getWorkTimeStatus())) {
					return -1;
				} else if(Integer.parseInt(u1.getWorkTimeStatus()) < Integer.parseInt(u2.getWorkTimeStatus())) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return resultList;
	}

	@Override
	public List<Map<String,Object>> getDyPlanByCondition(String imei, String workDate, String classCode) {
		List<Map<String,Object>> resultList = Lists.newArrayList();
		List<Map<String,Object>> tempList = tMacTerminalMapper.getDyPlanList(imei,workDate,classCode);
		Map<String,List<Map<String,Object>>> tempMap = tempList.stream().collect(Collectors.groupingBy(e->String.valueOf(e.get("workPlanId")) + String.valueOf(e.get("cargoInfoNo"))));
		Map<String,List<Map<String,Object>>> mapList = tempList.stream().collect(Collectors.groupingBy(e->String.valueOf(e.get("workPlanId"))));
		tempMap.forEach((k,v)->{
			Map<String,Object> map = v.get(0);
			Map<String,Object> item = Maps.newHashMap();
			item.put("workPlanId",map.get("workPlanId"));
			item.put("workDate",map.get("workDate"));
			item.put("processName",map.get("processName"));
			item.put("processCode",map.get("processCode"));
			item.put("className",map.get("className"));
			item.put("classCode",map.get("classCode"));
			item.put("trustId",map.get("trustId"));
			item.put("trustNo",map.get("trustNo"));
			item.put("cargoName",map.get("cargoName"));
			item.put("cargoInfoNo",map.get("cargoInfoNo"));
			item.put("cargoOwnerId",map.get("cargoOwnerId"));
			item.put("cargoOwnerName",map.get("cargoOwnerName"));
			item.put("itemList",v);
			resultList.add(item);
		});
		return resultList;
	}

	@Override
	public Map<String,Object> getPickUpFrom(String imei, Long workPlanId) {
		Map<String,Object> resultMap = Maps.newHashMap();
		if(StringUtils.isBlank(imei)){
			throw new BusinessRuntimeException("imei为空");
		}
		if(ObjectUtil.isEmpty(workPlanId)){
			throw new BusinessRuntimeException("计划id为空");
		}
		List<Map<String,Object>> pickUpFromList = tMacTerminalMapper.getPickUpFrom(imei,workPlanId);
		for (Map<String, Object> e : pickUpFromList) {
			e.put("workDate",String.valueOf(pickUpFromList.get(0).get("workDate")).substring(0,10));
		}

		if(pickUpFromList.isEmpty()){
			throw new BusinessRuntimeException("当前装载机在当班次没有配工");
		}
		Map<String,Object> pickUpFrom = pickUpFromList.get(0);
		List<Map<String,Object>> cargoInfoList = tMacTerminalMapper.getCargoNameList(workPlanId);
		List<Map<String,Object>> transportEquipmentList = tMacTerminalMapper.getTransportEquipment(workPlanId);
		resultMap.put("pickUpFrom",pickUpFrom);
		resultMap.put("cargoInfoList",cargoInfoList);
		resultMap.put("transportEquipmentList",transportEquipmentList);
		return resultMap;
	}

	@Override
	public Map<String,Object> getPortStorage(String cargoInfoId,String workPlanId) {
		Map<String,Object> resultMap = Maps.newHashMap();
		if(ObjectUtil.isEmpty(cargoInfoId)){
			throw new BusinessRuntimeException("票货id为空");
		}
		List<Map<String,Object>> portStorageList = tMacTerminalMapper.getPortStorage("1",cargoInfoId,workPlanId);
		if(portStorageList.isEmpty()){
			throw new BusinessRuntimeException("当前货物没有港存 或者 当前计划没有配置起始垛位");
		}
		List<Map<String,Object>> locationList = tMacTerminalMapper.getPlanLocation("2",cargoInfoId,workPlanId);
		if(locationList.isEmpty()){
			throw new BusinessRuntimeException("当前计划没有配置目标垛位");
		}
		resultMap.put("sourceList",portStorageList);
		resultMap.put("targetList",locationList);
		return resultMap;
	}

	@Override
	public Map<String,Object> getTallyByPlanId(String workDate,String classCode,String trustId,String imei) {
		Map<String,Object> resultMap = Maps.newHashMap();
		List<Map<String,Object>> tallyList = tMacTerminalMapper.getTallyByPlanId(workDate,classCode,trustId,imei);
		Map<String,Object> sumTon = tMacTerminalMapper.getTallySum(workDate,classCode,trustId,imei);
		if(tallyList.isEmpty()){
			throw new BusinessRuntimeException("当前无理货记录");
		}
		resultMap.put("tallyList",tallyList);
		resultMap.put("sumTon",sumTon);
		return resultMap;
	}

	public Map<String,Object> getPlanLocation(String cargoInfoId,String workPlanId) {
		Map<String,Object> resultMap = Maps.newHashMap();
		List<Map<String,Object>> locationList = tMacTerminalMapper.getPlanLocation("2",cargoInfoId,workPlanId);
		if(locationList.isEmpty()){
			throw new BusinessRuntimeException("当前计划没有配置目标垛位");
		}
		resultMap.put("locationList",locationList);
		return resultMap;
	}

	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public Boolean tallyListDelete(String tallyId) {
		Long createBy = securityUtils.getLoginUserId();
		String createByName = securityUtils.getLoginUserName();
		Date createTime = new Date();
		//删除理货
		int count = tMacTerminalMapper.tallyListDelete(tallyId,createBy,createByName,createTime);
		//获取要删除的港存详情
		List<Map<String,Object>> portStorageList = tMacTerminalMapper.getPortStorageList(tallyId);
//		Map<String, List<Map<String,Object>>> storageMap = portStorageList.stream().collect(Collectors.groupingBy(e->String.valueOf(e.get("portStorageId"))));
		List<String> ids = portStorageList.stream().map(e->String.valueOf(e.get("portStorageId"))).collect(Collectors.toList());
//		List<Map<String,Object>> sumPortStorages2 = tMacTerminalMapper.getSumPortStorage(ids);
		//删除详情
		tMacTerminalMapper.deleteStorageDetail(tallyId);
		List<Map<String,Object>> sumPortStorages = tMacTerminalMapper.getSumPortStorage(ids);
		sumPortStorages.forEach(map->{
//			BigDecimal tempTon = new BigDecimal(String.valueOf(storageMap.get(String.valueOf(map.get("portStorageId"))).get(0).get("ton"))) ;
			tMacTerminalMapper.updatePortStorage(map.get("portStorageId"),new BigDecimal(String.valueOf(map.get("ton"))),createBy,createByName,createTime);
		});
		return count == 1;
	}


	@Override
	public List<TMacTerminalWorkPlanDTO> getWorkPlanByCondition(String imei, String macCode, String workPlanId,
			String carNo, String portCode) {

		TMacTerminalDTO macTerminalDTO = null;
		if(StringUtils.isNoneBlank(imei)) {
			macTerminalDTO = tMacTerminalMapper.getMachineByImei(imei);
			if(macTerminalDTO != null) {
				macCode = macTerminalDTO.getMacCode();
			}
		}

		// 根据系统当前时间获取日期、班次(workDate、classCode)
		Map<String, Object> paramMap = publicService.getDateAndShift(null);
		paramMap.put("macCode", macCode);
		paramMap.put("workPlanId", workPlanId);
		paramMap.put("carNo", carNo);
		paramMap.put("portCode", portCode);
		paramMap.put("loginUserId", securityUtils.getLoginUserId());

		List<TMacTerminalWorkPlanDTO> resultList = Lists.newArrayList();

		// 查询直取装卸船
		List<TMacTerminalWorkPlanDTO> shipWorkPlanList = tMacTerminalMapper.getShipWorkPlanList(paramMap);
		if(CollectionUtils.isNotEmpty(shipWorkPlanList)) {
			for (TMacTerminalWorkPlanDTO shipWorkPlan : shipWorkPlanList) {
				// 根据trustId 查询对应的集疏港的trustId planNo
				List<String> businessNoList = tMacTerminalMapper.getBusinessNoList(shipWorkPlan.getTrustId());
				if(CollectionUtils.isNotEmpty(businessNoList)) {
					List<TMacTerminalWorkPlanDTO> shipDataList = tMacTerminalMapper.getShipDataWorkPlanList(securityUtils.getLoginUserId(), businessNoList, portCode, carNo);
					if(CollectionUtils.isNotEmpty(shipDataList)) {

						try {
							for (TMacTerminalWorkPlanDTO shipData : shipDataList) {
								// 将作业线信息补充完整
								shipData.setTrustId(shipWorkPlan.getTrustId());
								shipData.setPlanType(shipWorkPlan.getPlanType());
								shipData.setMassNamesSource(shipWorkPlan.getMassNamesSource());
								shipData.setMassNamesTarget(shipWorkPlan.getMassNamesTarget());
								shipData.setWorkPlanId(shipWorkPlan.getWorkPlanId());
								shipData.setWorkMacName(shipData.getWorkMacName() + "(直取)");
							}

							// 判断集疏运车辆是否正在作业
							for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : shipDataList) {

								if(tMacTerminalWorkPlanDTO.getCreateBy() == null || tMacTerminalWorkPlanDTO.getCreateBy().equals(securityUtils.getLoginUserId())) {

									if(tMacTerminalWorkPlanDTO.getWorkTimeStart() == null
											&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
										tMacTerminalWorkPlanDTO.setWorkTimeStatus("10");
										resultList.add(tMacTerminalWorkPlanDTO);
									} else if(tMacTerminalWorkPlanDTO.getWorkTimeStart() != null
											&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
										tMacTerminalWorkPlanDTO.setWorkTimeStatus("20");
										resultList.add(tMacTerminalWorkPlanDTO);
									}
								}
							}
						} catch (Exception e) {
							LOGGER.error("车载处理直取车辆异常：" + e.getMessage());
						}
					}
				}
			}
		}

		List<TMacTerminalWorkPlanDTO> dataListPre = tMacTerminalMapper.getWorkPlanByCondition(paramMap);

		List<TMacTerminalWorkPlanDTO> dataList = Lists.newArrayList();

		if(CollectionUtils.isNotEmpty(dataListPre)) {
			for (TMacTerminalWorkPlanDTO data : dataListPre) {
				if(data.getCreateBy() == null || data.getCreateBy().equals(securityUtils.getLoginUserId())) {
					dataList.add(data);
				}
			}
		}

		// 判断集疏运车辆是否正在作业
		for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : dataList) {
			if(tMacTerminalWorkPlanDTO.getWorkTimeStart() == null
					&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
				tMacTerminalWorkPlanDTO.setWorkTimeStatus("10");
				resultList.add(tMacTerminalWorkPlanDTO);
			} else if(tMacTerminalWorkPlanDTO.getWorkTimeStart() != null
					&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
				tMacTerminalWorkPlanDTO.setWorkTimeStatus("20");
				resultList.add(tMacTerminalWorkPlanDTO);
			}
		}

		// 判断集疏运车辆是否在港超时1小时
		for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : resultList) {
			tMacTerminalWorkPlanDTO.setInPortStatus("10");
			if(tMacTerminalWorkPlanDTO.getInPortTime() != null) {
				long minutes = checkDateTimePhaseDiffMinutes(tMacTerminalWorkPlanDTO.getInPortTime(), new Date());
				if(minutes >= 30 && minutes < 60){
					tMacTerminalWorkPlanDTO.setInPortStatus("30");
				} else if(minutes >= 60) {
					tMacTerminalWorkPlanDTO.setInPortStatus("20");
				} else {
					tMacTerminalWorkPlanDTO.setInPortStatus("10");
				}
			}
		}
		Collections.sort(resultList, new Comparator<TMacTerminalWorkPlanDTO>() {
	        @Override
	        public int compare(TMacTerminalWorkPlanDTO u1, TMacTerminalWorkPlanDTO u2) {
	        	if(Integer.parseInt(u1.getWorkTimeStatus()) > Integer.parseInt(u2.getWorkTimeStatus())) {
	        		return -1;
	        	} else if(Integer.parseInt(u1.getWorkTimeStatus()) < Integer.parseInt(u2.getWorkTimeStatus())) {
	        		return 1;
	        	} else {
		            return 0;
	        	}
	        }
	    });
		return resultList;
	}

	@Override
	public List<TMacTerminalWorkPlanDTO> getWorkPlanByConditionForAppPC(String carNo, String workAreaCd, String portCode) {

		// 根据系统当前时间获取日期、班次(workDate、classCode)
		Map<String, Object> paramMap = publicService.getDateAndShift(null);
		paramMap.put("carNo", carNo);
		paramMap.put("workAreaCd", workAreaCd);
		paramMap.put("portCode", portCode);

		List<TMacTerminalWorkPlanDTO> resultList = Lists.newArrayList();

		// 查询直取装卸船
		List<TMacTerminalWorkPlanDTO> shipWorkPlanList = tMacTerminalMapper.getShipWorkPlanList(paramMap);
		if(CollectionUtils.isNotEmpty(shipWorkPlanList)) {
			for (TMacTerminalWorkPlanDTO shipWorkPlan : shipWorkPlanList) {
				// 根据trustId 查询对应的集疏港的trustId planNo
				List<String> businessNoList = tMacTerminalMapper.getBusinessNoList(shipWorkPlan.getTrustId());
				if(CollectionUtils.isNotEmpty(businessNoList)) {
					List<TMacTerminalWorkPlanDTO> shipDataList = tMacTerminalMapper.getShipDataWorkPlanListForAppPC(securityUtils.getLoginUserId(), businessNoList, portCode, carNo);
					if(CollectionUtils.isNotEmpty(shipDataList)) {

						for (TMacTerminalWorkPlanDTO shipData : shipDataList) {
							// 将作业线信息补充完整
							shipData.setTrustId(shipWorkPlan.getTrustId());
							shipData.setPlanType(shipWorkPlan.getPlanType());
							shipData.setMassNamesSource(shipWorkPlan.getMassNamesSource());
							shipData.setMassNamesTarget(shipWorkPlan.getMassNamesTarget());
							shipData.setWorkPlanId(shipWorkPlan.getWorkPlanId());
							shipData.setWorkMacName(shipData.getWorkMacName() + "(直取)");
						}

						// 判断集疏运车辆是否正在作业
						for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : shipDataList) {
							if(tMacTerminalWorkPlanDTO.getWorkTimeStart() == null
									&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
								tMacTerminalWorkPlanDTO.setWorkTimeStatus("10");
								resultList.add(tMacTerminalWorkPlanDTO);
							} else if(tMacTerminalWorkPlanDTO.getWorkTimeStart() != null
									&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
								tMacTerminalWorkPlanDTO.setWorkTimeStatus("20");
								resultList.add(tMacTerminalWorkPlanDTO);
							}
						}
					}
				}
			}
		}

		List<TMacTerminalWorkPlanDTO> dataList = tMacTerminalMapper.getWorkPlanByConditionForAppPC(paramMap);

		// 判断集疏运车辆是否正在作业
		for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : dataList) {
			if(tMacTerminalWorkPlanDTO.getWorkTimeStart() == null
					&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
				tMacTerminalWorkPlanDTO.setWorkTimeStatus("10");
				resultList.add(tMacTerminalWorkPlanDTO);
			} else if(tMacTerminalWorkPlanDTO.getWorkTimeStart() != null
					&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
				tMacTerminalWorkPlanDTO.setWorkTimeStatus("20");
				resultList.add(tMacTerminalWorkPlanDTO);
			}
		}

		// 判断集疏运车辆是否在港超时1小时
		for (TMacTerminalWorkPlanDTO tMacTerminalWorkPlanDTO : resultList) {
			tMacTerminalWorkPlanDTO.setInPortStatus("10");
			if(tMacTerminalWorkPlanDTO.getInPortTime() != null) {
				long minutes = checkDateTimePhaseDiffMinutes(tMacTerminalWorkPlanDTO.getInPortTime(), new Date());
				if(minutes >= 30 && minutes < 60){
					tMacTerminalWorkPlanDTO.setInPortStatus("30");
				} else if(minutes >= 60) {
					tMacTerminalWorkPlanDTO.setInPortStatus("20");
				} else {
					tMacTerminalWorkPlanDTO.setInPortStatus("10");
				}
			}
		}
		Collections.sort(resultList, new Comparator<TMacTerminalWorkPlanDTO>() {
	        @Override
	        public int compare(TMacTerminalWorkPlanDTO u1, TMacTerminalWorkPlanDTO u2) {
	        	if(Integer.parseInt(u1.getWorkTimeStatus()) > Integer.parseInt(u2.getWorkTimeStatus())) {
	        		return -1;
	        	} else if(Integer.parseInt(u1.getWorkTimeStatus()) < Integer.parseInt(u2.getWorkTimeStatus())) {
	        		return 1;
	        	} else {
		            return 0;
	        	}
	        }
	    });
		//过滤场站,只展示场站的
//		List<TMacTerminalWorkPlanDTO> workList = Lists.newArrayList();
//		resultList.stream().forEach(e->{
//			TBusCustomerDTO customerDTO = customerMapper.getById(e.getCargoOwnerId());
//			if("1".equals(customerDTO.getIsStations())){
//				workList.add(e);
//			}
//		});
		return resultList;
	}

	@Override
	public List<TCarInHarborForAppDTO> getCarInHarbor(TCarInHarborForAppDTO searchDTO){
		Map<String, Object> paramMap = publicService.getDateAndShift(null);
		if(StringUtils.isEmpty(searchDTO.getIsLeave())){
			throw new BusinessRuntimeException("请先选择在港状态");
		}
		searchDTO.setClassCode(String.valueOf(paramMap.get("classCode")));
		searchDTO.setWorkDate(String.valueOf(paramMap.get("workDate")));
		List<TCarInHarborForAppDTO> dataList = null;
		if ( "1".equals(searchDTO.getIsLeave())) {
			dataList = tMacTerminalMapper.getWorkPlanByConditionCarInHarbor(searchDTO);
//			dataList = dataList.stream().filter(o -> !"1".equals(o.getTytDelFlag())).collect(Collectors.toList());

			dataList.forEach(tMacTerminalWorkPlanDTO->{
				if ("30".equals(tMacTerminalWorkPlanDTO.getWorkTimeStatus())){
					tMacTerminalWorkPlanDTO.setStatusLabel("未配作业线");
				}else if(tMacTerminalWorkPlanDTO.getWorkTimeStart() == null
						&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
					 if(tMacTerminalWorkPlanDTO.getTallyId() !=null){
						 tMacTerminalWorkPlanDTO.setStatusLabel("已理货");
						 tMacTerminalWorkPlanDTO.setTallyName( tMacTerminalWorkPlanDTO.getTallyName() + (StringUtils.isNotBlank(tMacTerminalWorkPlanDTO.getTallRemark())?("("+tMacTerminalWorkPlanDTO.getTallRemark()+")"):""));
					 }else{
						 tMacTerminalWorkPlanDTO.setWorkTimeStatus("10");
						 tMacTerminalWorkPlanDTO.setStatusLabel("未作业");
					 }
				} else if(tMacTerminalWorkPlanDTO.getWorkTimeStart() != null
						&& tMacTerminalWorkPlanDTO.getWorkTimeEnd() == null) {
					tMacTerminalWorkPlanDTO.setWorkTimeStatus("20");
					tMacTerminalWorkPlanDTO.setStatusLabel("作业中");
				}else {
					tMacTerminalWorkPlanDTO.setStatusLabel("已完成理货");
					if(tMacTerminalWorkPlanDTO.getTallyId() !=null){
						tMacTerminalWorkPlanDTO.setStatusLabel("已理货");
						tMacTerminalWorkPlanDTO.setTallyName( tMacTerminalWorkPlanDTO.getTallyName() + (StringUtils.isNotBlank(tMacTerminalWorkPlanDTO.getTallRemark())?("("+tMacTerminalWorkPlanDTO.getTallRemark()+")"):""));
					}
				}

				if(tMacTerminalWorkPlanDTO.getInPortTime() != null) {
					long minutes = checkDateTimePhaseDiffMinutes(tMacTerminalWorkPlanDTO.getInPortTime(), new Date());
					tMacTerminalWorkPlanDTO.setMinutes(String.valueOf(minutes));
					if(minutes >= 30 && minutes < 60){
						tMacTerminalWorkPlanDTO.setInPortStatus("30");
					} else if(minutes >= 60) {
						tMacTerminalWorkPlanDTO.setInPortStatus("20");
					} else {
						tMacTerminalWorkPlanDTO.setInPortStatus("10");
					}
				}
			});
		}
		if ( "0".equals(searchDTO.getIsLeave())){
			dataList = tMacTerminalMapper.getWorkPlanByConditionCarInHarborForLeave(searchDTO);
			dataList.forEach(o->{
				long minutes = checkDateTimePhaseDiffMinutes(o.getInPortTime(),o.getOutPortTime());
				o.setMinutes(String.valueOf(minutes));
				if(minutes >= 30 && minutes < 60){
					o.setInPortStatus("30");
				} else if(minutes >= 60) {
					o.setInPortStatus("20");
				} else {
					o.setInPortStatus("10");
				}
				o.setStatusLabel("离港");
			});
		}

		if(CollectionUtils.isEmpty(dataList)){
			return dataList;
		}
		dataList.forEach(o->{
			if ("_".equals(o.getShipName())){
				o.setShipName("");
			}
			if ("-".equals(o.getMassNamesSource())){
				o.setMassNamesSource("");
			}


			/**
			 *
			 *   {label:'东作业区',value:'01'},
			 *       {label:'中作业区',value:'02'},
			 *       {label:'西作业区',value:'03'},
			 */
			if("01".equals(o.getPortCode())){
				o.setPortName("东作业区");
			}else if("02".equals(o.getPortCode())) {
				o.setPortName("中作业区");

			}else if("03".equals(o.getPortCode())){
				o.setPortName("西作业区");

			}
		});

		return dataList;
	}


	/**
	 * 判断两个时间相差多少分钟
	 * @param start
	 * @param end
	 * @return
	 */
	private long checkDateTimePhaseDiffMinutes(Date start, Date end) {
		if(start == null || end == null) {
			return 0;
		}
		LocalDateTime startDateTime = LocalDateTime
				.ofInstant(start.toInstant(), ZoneId.systemDefault());
		LocalDateTime endDateTime = LocalDateTime
				.ofInstant(end.toInstant(), ZoneId.systemDefault());
		Duration duration = Duration.between(startDateTime, endDateTime);
		return duration.toMinutes();
	}

	@Override
	public List<TMacTerminalStackPositionDTO> getStackPositionList() {
		return tMacTerminalMapper.getStackPositionList();
	}

	@Override
	public List<TMacTerminalStackPositionDTO> getStackPositionByWorkPlanId(Long workPlanId, Long cargoInfoId) {

		// 根据工班计划id查询源货位、目的货位编码
		List<TMacTerminalWorkPlanLocationDTO> dataList = tMacTerminalMapper.getTMacTerminalWorkPlanLocation(workPlanId, cargoInfoId);

		if(CollectionUtils.isNotEmpty(dataList)) {
			List<TMacTerminalWorkPlanLocationDTO> paramList = Lists.newArrayList();
			for (TMacTerminalWorkPlanLocationDTO data : dataList) {
				if(("集港".equals(data.getType()) || "装船".equals(data.getType()) || "拆箱集港".equals(data.getType()))
						&& (data.getCargoInfoId() == null || cargoInfoId.equals(data.getCargoInfoId()))) {
					paramList.add(data);
				}
				if(("疏港".equals(data.getType()) || "卸船".equals(data.getType()) || "陆销".equals(data.getType()))
						&& (cargoInfoId.equals(data.getCargoInfoId()))) {
					paramList.add(data);
				}
			}
			if(CollectionUtils.isNotEmpty(paramList)) {
				// 根据源、目的货位查询垛位点位
				List<TMacTerminalStackPositionDTO> resultList = tMacTerminalMapper.getStackPositionListByStackId(paramList);
				return resultList;
			} else {
				throw new BusinessRuntimeException("配置垛位与车辆计划不符！");
			}
		} else {
			throw new BusinessRuntimeException("当前作业线尚未配置垛位！");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int macWorkStart(TMacWorkTimeDTO tMacWorkTimeDTO) {

		// 验证当前设备是否已经开始作业
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("planType", tMacWorkTimeDTO.getPlanType());
		paramMap.put("reservatCarId", tMacWorkTimeDTO.getMacWorkId());
		paramMap.put("dispatchSecondaryId", tMacWorkTimeDTO.getMacWorkId());
		paramMap.put("weighbridgeId", tMacWorkTimeDTO.getWeighbridgeId());
		paramMap.put("macId", tMacWorkTimeDTO.getMacId());

//		String isElectronFence = "";
//		TMacTerminalDTO macTerminalDTO = null;
//		if(StringUtils.isNoneBlank(tMacWorkTimeDTO.getImei())) {
//			macTerminalDTO = tMacTerminalMapper.getMachineByImei(tMacWorkTimeDTO.getImei());
//			if(macTerminalDTO != null) {
//				isElectronFence = macTerminalDTO.getIsElectronFence();
//			}
//		}
//
//		if("1".equals(isElectronFence)) {
//			List<TMacTerminalWorkPlanLocationDTO> paramList = tMacTerminalMapper.getTMacTerminalWorkPlanLocation(tMacWorkTimeDTO.getWorkPlanId());
//			if(CollectionUtils.isEmpty(paramList)) {
//				throw new BusinessRuntimeException("当前作业线尚未配置垛位！");
//			}
//			int count = 0;
//			for (TMacTerminalWorkPlanLocationDTO param : paramList) {
//				if(param.getStackId() != null) {
//					count++;
//				}
//			}
//
//			if(paramList.size() != count) {
//				throw new BusinessRuntimeException("存在货垛尚未跑垛，请联系库场队！");
//			}
//		}

		List<TMacWorkTimePO> checkList = tMacTerminalMapper.getTMacWorkTimeListByConditionStart(paramMap);
		if(CollectionUtils.isNotEmpty(checkList)) {
			throw new BusinessRuntimeException("当前设备已经开始作业~");
		}

		//获取一车一理开关是否开启
		SysParameterDTO sysParameter = sysParameterMapper.getByKey("UNIQUE_TALLY_VEHICLE");
		boolean uniqueTallyVehicle = ObjectUtil.isEmpty(sysParameter)?false:("Y".equals(sysParameter.getParamVal())?true:false);
		//true是开  false是关
		if(uniqueTallyVehicle) {
			//获取当前设备是否已经开始作业，如果已经开始作业不允许再次开始作业
			Integer StartWorkCount = tMacTerminalMapper.getMacStartWork(paramMap);
			if (StartWorkCount>0){
				throw new BusinessRuntimeException("当前设备已经开始作业,请先结束当前理货车辆~");
			}
		}

		sysParameter = sysParameterMapper.getByKey("CHECK_PORT_STORAGE_VEHICLE");
		boolean oneCarPlan  = ObjectUtil.isEmpty(sysParameter)?false:("Y".equals(sysParameter.getParamVal())?true:false);
		//true是开  false是关
		if(oneCarPlan) {
			Long id = tMacTerminalMapper.getListByTrustCargoId(tMacWorkTimeDTO.getTrustCargoInfoId());
			if (id ==null){
				BigDecimal ton = tMacTerminalMapper.getTonByMassIdAndCargoInfoId(tMacWorkTimeDTO.getMassId(),tMacWorkTimeDTO.getCargoInfoId());
				/*if(ton == null || ton.compareTo(BigDecimal.ZERO) <= 0){
					throw new BusinessRuntimeException("该货物在当前垛位没有场存");
				}*/
				if(ton == null || ton.compareTo(BigDecimal.valueOf(-200)) <= 0){
					throw new BusinessRuntimeException("该货物在当前垛位库存小于-200");
				}
			}
		}

		if(PlanTypeEnum.PLAN_TYPE_2.getCode().equals(tMacWorkTimeDTO.getPlanType()) ||
				PlanTypeEnum.PLAN_TYPE_1.getCode().equals(tMacWorkTimeDTO.getPlanType())) {
			// 判断设备是否已经作业完成
			List<TMacWorkTimePO> isEndList = tMacTerminalMapper.checkMacWorkTimeIsEnd(paramMap);
			if(CollectionUtils.isNotEmpty(isEndList)) {
				throw new BusinessRuntimeException("当前设备已经完成作业~");
			}

			// 根据macWorkId 查询集疏港磅单数据
			//TBusTrustTradeReservatCarPO tBusTrustTradeReservatCarPO = tMacTerminalMapper.getTrustTradeReservatCarById(tMacWorkTimeDTO.getMacWorkId());

			WeightRecordPoundDTO weightRecordPoundDTO = tMacTerminalMapper.getWeightRecordPound(tMacWorkTimeDTO.getWeighbridgeId());


			TMacWorkTimePO tMacWorkTimePO = new TMacWorkTimePO();
			tMacWorkTimePO.setId(snowflake.nextId());
			tMacWorkTimePO.setWorkPlanId(tMacWorkTimeDTO.getWorkPlanId());
			tMacWorkTimePO.setPlanType(tMacWorkTimeDTO.getPlanType());
			tMacWorkTimePO.setWeighbridgeId(tMacWorkTimeDTO.getWeighbridgeId());
			if(weightRecordPoundDTO != null) {
				tMacWorkTimePO.setCarNo(weightRecordPoundDTO.getTruckPlate());
			}
			tMacWorkTimePO.setReservatCarId(tMacWorkTimeDTO.getMacWorkId());
			tMacWorkTimePO.setWorkTimeStart(new Date());
			tMacWorkTimePO.setMassId(tMacWorkTimeDTO.getMassId());
			tMacWorkTimePO.setMassName(tMacWorkTimeDTO.getMassName());
			tMacWorkTimePO.setRegionId(tMacWorkTimeDTO.getRegionId());
			tMacWorkTimePO.setRegionName(tMacWorkTimeDTO.getRegionName());
			tMacWorkTimePO.setStorehouseId(tMacWorkTimeDTO.getStorehouseId());
			tMacWorkTimePO.setStorehouseName(tMacWorkTimeDTO.getStorehouseName());

			return tMacTerminalMapper.insertTMacWorkTime(tMacWorkTimePO);
		} else if(PlanTypeEnum.PLAN_TYPE_3.getCode().equals(tMacWorkTimeDTO.getPlanType())) {
			// 根据macWorkId 查询配工设备
			TPrdDispatchSecondaryPO tPrdDispatchSecondaryPO = tMacTerminalMapper.getPrdDispatchSecondaryByIdAndMacId(tMacWorkTimeDTO.getMacWorkId());

			TMacWorkTimePO tMacWorkTimePO = new TMacWorkTimePO();
			tMacWorkTimePO.setId(snowflake.nextId());
			tMacWorkTimePO.setWorkPlanId(tMacWorkTimeDTO.getWorkPlanId());
			tMacWorkTimePO.setPlanType(tMacWorkTimeDTO.getPlanType());
			if(tPrdDispatchSecondaryPO != null) {
				tMacWorkTimePO.setMacId(tPrdDispatchSecondaryPO.getEquipmentId());
				tMacWorkTimePO.setMacCode(tPrdDispatchSecondaryPO.getEquipmentNo());
			}
			tMacWorkTimePO.setReservatCarId(tMacWorkTimeDTO.getMacWorkId());
			tMacWorkTimePO.setWorkTimeStart(new Date());

			return tMacTerminalMapper.insertTMacWorkTime(tMacWorkTimePO);
		} else if(PlanTypeEnum.PLAN_TYPE_4.getCode().equals(tMacWorkTimeDTO.getPlanType())) {
			// 根据macWorkId 查询配工设备
			TPrdDispatchSecondaryPO tPrdDispatchSecondaryPO = tMacTerminalMapper.getPrdDispatchSecondaryByIdAndMacId(tMacWorkTimeDTO.getMacWorkId());

			TMacWorkTimePO tMacWorkTimePO = new TMacWorkTimePO();
			tMacWorkTimePO.setId(snowflake.nextId());
			tMacWorkTimePO.setWorkPlanId(tMacWorkTimeDTO.getWorkPlanId());
			tMacWorkTimePO.setPlanType(tMacWorkTimeDTO.getPlanType());
			if(tPrdDispatchSecondaryPO != null) {
				tMacWorkTimePO.setMacId(tPrdDispatchSecondaryPO.getEquipmentId());
				tMacWorkTimePO.setMacCode(tPrdDispatchSecondaryPO.getEquipmentNo());
			}
			tMacWorkTimePO.setReservatCarId(tMacWorkTimeDTO.getMacWorkId());
			tMacWorkTimePO.setWorkTimeStart(new Date());

			return tMacTerminalMapper.insertTMacWorkTime(tMacWorkTimePO);
		}

		return 0;
	}

	@Override
	@Transactional
	public int macWorkEnd(TMacWorkTimeDTO tMacWorkTimeDTO) {
		DistributedLock.newBuilder().store(redisTemplate)
				.key(DistributedLockKeyPrefixEnum.TALLY_KEY.getCode() + tMacWorkTimeDTO.getWeighbridgeId())
				.build().run(() -> {
					TMacTerminalDTO macTerminalDTO = null;
					if(StringUtils.isNoneBlank(tMacWorkTimeDTO.getImei())) {
						macTerminalDTO = tMacTerminalMapper.getMachineByImei(tMacWorkTimeDTO.getImei());
					}

					WeightRecordPoundDTO weightRecordPoundDTO = tMacTerminalMapper.getWeightRecordPound(tMacWorkTimeDTO.getWeighbridgeId());
					if(weightRecordPoundDTO == null) {
						throw new BusinessRuntimeException("未获取到磅单信息：" + tMacWorkTimeDTO.getWeighbridgeId());
					}
					// 验证当前设备是否已经开始作业
					Map<String, Object> paramMap = Maps.newHashMap();
					paramMap.put("workPlanId", tMacWorkTimeDTO.getWorkPlanId());
					paramMap.put("planType", tMacWorkTimeDTO.getPlanType());
					paramMap.put("reservatCarId", tMacWorkTimeDTO.getMacWorkId());
					paramMap.put("weighbridgeId", tMacWorkTimeDTO.getWeighbridgeId());
					paramMap.put("dispatchSecondaryId", tMacWorkTimeDTO.getMacWorkId());
					paramMap.put("macId", tMacWorkTimeDTO.getMacId());

					List<TMacWorkTimePO> checkList = tMacTerminalMapper.getTMacWorkTimeListByConditionEnd(paramMap);
					if(CollectionUtils.isEmpty(checkList)) {
						throw new BusinessRuntimeException("当前设备尚未开始作业，无法结束作业~");
					} else if(checkList.size() > 1) {
						throw new BusinessRuntimeException("数据有误，请联系管理员~");
					}

					TMacWorkTimePO tMacWorkTimePO = checkList.get(0);
					tMacWorkTimePO.setWorkTimeEnd(new Date());

					//结束时间应该大于开始时间两分钟
					if (tMacWorkTimePO.getWorkTimeStart() != null && tMacWorkTimePO.getWorkTimeEnd() != null &&
							tMacWorkTimePO.getWorkTimeEnd().getTime() - tMacWorkTimePO.getWorkTimeStart().getTime() < 1 * 60 * 1000) {
						throw new BusinessRuntimeException("理货时间应小于一分钟~");
					}

					// 修改开始结束作业时间
					tMacTerminalMapper.updateTMacWorkTime(tMacWorkTimePO);

					//获取计划线中是否直取标志位
					String isDirection = tMacTerminalMapper.getWorkPlanById(tMacWorkTimeDTO.getWorkPlanId());
					// 修改过磅表数据
					tMacTerminalMapper.updateWeightRecordPound(tMacWorkTimeDTO.getWeighbridgeId(),isDirection);

					// 根据主过程查询第一个子过程
					Map<String, String> processMap = tMacTerminalMapper.getFirstChildProcess(tMacWorkTimeDTO.getWorkPlanId());

					// 理货表中添加数据

					//验重
					TYardTallyPO tYardTallyPO = new TYardTallyPO();
					tYardTallyPO.setWeighbridgeId(tMacWorkTimeDTO.getWeighbridgeId());
					int count = tallyMapper.getByWeighbridgeId(tYardTallyPO);
					if (count > 0) {
						throw new BusinessRuntimeException("该车辆已理货,不能重复理货");
					}

					TYardTallyPO yardTallyPO = new TYardTallyPO();
					Long id = snowflake.nextId();
					yardTallyPO.setId(id);
					yardTallyPO.setPlanId(tMacWorkTimeDTO.getWorkPlanId());
					yardTallyPO.setRelationId(null);
					yardTallyPO.setShipvoyageId(tMacWorkTimeDTO.getShipvoyageId());

					TYardTallyItemPO yardTallyItemPO = new TYardTallyItemPO();
					if(processMap != null) {
						yardTallyPO.setProcessCode(processMap.get("processCode"));
						yardTallyPO.setProcessName(processMap.get("processName"));
						if("10030001".equals(yardTallyPO.getProcessCode())){
							yardTallyItemPO.setSourceOrTargetFlag("2");
						}
						else if("车-场(集港)".equals(yardTallyPO.getProcessName())){
							yardTallyItemPO.setSourceOrTargetFlag("2");
						}
						else if("10130001".equals(yardTallyPO.getProcessCode()) || "10120001".equals(yardTallyPO.getProcessCode())){
							yardTallyItemPO.setSourceOrTargetFlag("1");
						}
						else if("场-车(疏港)".equals(yardTallyPO.getProcessName())){
							yardTallyItemPO.setSourceOrTargetFlag("1");
						}
					} else {
						yardTallyPO.setProcessCode("");
						yardTallyPO.setProcessName("");
					}
					yardTallyPO.setEquipmentId(macTerminalDTO.getId());
					yardTallyPO.setEquipmentNo(macTerminalDTO.getMacCode());
					yardTallyPO.setTransportEquipmentId(weightRecordPoundDTO.getNoteId());
					yardTallyPO.setTransportEquipmentNo(weightRecordPoundDTO.getTruckPlate());
					yardTallyPO.setQuantity(0D);
					yardTallyPO.setTon(0D);
					yardTallyPO.setRemark("车载");
					yardTallyPO.setOperatorsId(securityUtils.getLoginUserId());
					yardTallyPO.setOperatorsName(securityUtils.getLoginUserName());
					yardTallyPO.setTransportOperatorsId(null);
					yardTallyPO.setTransportOperatorsName("无");
					yardTallyPO.setWeighbridgeId(tMacWorkTimeDTO.getWeighbridgeId());
					yardTallyPO.setPlanNo(weightRecordPoundDTO.getPlanNo());
					yardTallyPO.setTsptId(weightRecordPoundDTO.getTsptId());
					yardTallyPO.setWorkErWeiId(weightRecordPoundDTO.getWorkErweiId());
					tMacTerminalMapper.insertYardTally(yardTallyPO);

					yardTallyItemPO.setId(snowflake.nextId());
					yardTallyItemPO.setTallyId(id);
					yardTallyItemPO.setCargoInfoId(tMacWorkTimeDTO.getCargoInfoId());
					yardTallyItemPO.setCargoCode(tMacWorkTimeDTO.getCargoCode());
					yardTallyItemPO.setCargoName(tMacWorkTimeDTO.getCargoName());
					yardTallyItemPO.setQuantity(0);
					yardTallyItemPO.setTon(BigDecimal.ZERO);
					yardTallyItemPO.setRemark("车载");
					yardTallyItemPO.setTrustCargoInfoId(tMacWorkTimeDTO.getTrustCargoInfoId());
					yardTallyItemPO.setStorehouseId(tMacWorkTimePO.getStorehouseId());
					yardTallyItemPO.setStorehouseName(tMacWorkTimePO.getStorehouseName());
					yardTallyItemPO.setLocationId(tMacWorkTimePO.getRegionId());
					yardTallyItemPO.setLocationNo(tMacWorkTimePO.getRegionName());
					yardTallyItemPO.setStackPositionId(tMacWorkTimePO.getMassId());
					yardTallyItemPO.setStackPositionName(tMacWorkTimePO.getMassName());
					tMacTerminalMapper.insertYardTallyItem(yardTallyItemPO);
				});
		return 1;
	}

	@Override
	@Transactional
	public int macWorkPC(TMacWorkTimeDTO tMacWorkTimeDTO) {

		WeightRecordPoundDTO weightRecordPoundDTO = tMacTerminalMapper.getWeightRecordPound(tMacWorkTimeDTO.getWeighbridgeId());
		if(weightRecordPoundDTO == null) {
			throw new BusinessRuntimeException("未获取到磅单信息：" + tMacWorkTimeDTO.getWeighbridgeId());
		}

		// 验证当前设备是否已经开始作业
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("workPlanId", tMacWorkTimeDTO.getWorkPlanId());
		paramMap.put("planType", tMacWorkTimeDTO.getPlanType());
		paramMap.put("reservatCarId", tMacWorkTimeDTO.getMacWorkId());
		paramMap.put("weighbridgeId", tMacWorkTimeDTO.getWeighbridgeId());
		paramMap.put("dispatchSecondaryId", tMacWorkTimeDTO.getMacWorkId());
		paramMap.put("macId", tMacWorkTimeDTO.getMacId());

		List<TMacWorkTimePO> checkList = tMacTerminalMapper.getTMacWorkTimeListByConditionEnd(paramMap);
		if(CollectionUtils.isNotEmpty(checkList)) {
			if(checkList.size() == 1) {
				throw new BusinessRuntimeException("当前车辆已经开始作业，请勿重复操作！");
			} else if(checkList.size() > 1) {
				throw new BusinessRuntimeException("数据有误，请联系管理员~");
			}
		}

		// 查询当前车辆是否已经理货
		Integer count = tMacTerminalMapper.getTallyCountByWeighbridgeId(tMacWorkTimeDTO.getWeighbridgeId());
		if(count >= 1) {
			throw new BusinessRuntimeException("当前车辆已经理货，请刷新列表！");
		}

		TMacWorkTimePO tMacWorkTimePO = new TMacWorkTimePO();
		tMacWorkTimePO.setId(snowflake.nextId());
		tMacWorkTimePO.setWorkPlanId(tMacWorkTimeDTO.getWorkPlanId());
		tMacWorkTimePO.setPlanType(tMacWorkTimeDTO.getPlanType());
		tMacWorkTimePO.setMacId(88L);
		tMacWorkTimePO.setMacCode("PC");
		tMacWorkTimePO.setReservatCarId(tMacWorkTimeDTO.getMacWorkId());
		tMacWorkTimePO.setWorkTimeStart(new Date());
		tMacWorkTimePO.setWorkTimeEnd(new Date());

		tMacTerminalMapper.insertTMacWorkTime(tMacWorkTimePO);

		//获取计划线是否为直取标志位
		String isDirection = tMacTerminalMapper.getWorkPlanById(tMacWorkTimeDTO.getWorkPlanId());

		// 修改过磅表数据
		tMacTerminalMapper.updateWeightRecordPound(tMacWorkTimeDTO.getWeighbridgeId(),isDirection);

		// 根据主过程查询第一个子过程
		Map<String, String> processMap = tMacTerminalMapper.getFirstChildProcess(tMacWorkTimeDTO.getWorkPlanId());

		// 理货表中添加数据
		TYardTallyPO yardTallyPO = new TYardTallyPO();
		Long id = snowflake.nextId();
		yardTallyPO.setId(id);
		yardTallyPO.setPlanId(tMacWorkTimeDTO.getWorkPlanId());
		yardTallyPO.setRelationId(null);
		yardTallyPO.setShipvoyageId(tMacWorkTimeDTO.getShipvoyageId());
		if(processMap != null) {
			yardTallyPO.setProcessCode(processMap.get("processCode"));
			yardTallyPO.setProcessName(processMap.get("processName"));
		} else {
			yardTallyPO.setProcessCode("");
			yardTallyPO.setProcessName("");
		}
		yardTallyPO.setEquipmentId(88L);
		yardTallyPO.setEquipmentNo("PC");
		yardTallyPO.setTransportEquipmentId(weightRecordPoundDTO.getNoteId());
		yardTallyPO.setTransportEquipmentNo(weightRecordPoundDTO.getTruckPlate());
		yardTallyPO.setQuantity(0D);
		yardTallyPO.setTon(0D);
		yardTallyPO.setRemark("PC");
		yardTallyPO.setOperatorsId(securityUtils.getLoginUserId());
		yardTallyPO.setOperatorsName(securityUtils.getLoginUserName());
		yardTallyPO.setTransportOperatorsId(null);
		yardTallyPO.setTransportOperatorsName("无");
		yardTallyPO.setWeighbridgeId(tMacWorkTimeDTO.getWeighbridgeId());
		yardTallyPO.setPlanNo(weightRecordPoundDTO.getPlanNo());
		yardTallyPO.setTsptId(weightRecordPoundDTO.getTsptId());
		yardTallyPO.setWorkErWeiId(weightRecordPoundDTO.getWorkErweiId());
		tMacTerminalMapper.insertYardTally(yardTallyPO);

		TYardTallyItemPO yardTallyItemPO = new TYardTallyItemPO();
		yardTallyItemPO.setId(snowflake.nextId());
		yardTallyItemPO.setTallyId(id);
		yardTallyItemPO.setCargoInfoId(tMacWorkTimeDTO.getCargoInfoId());
		yardTallyItemPO.setCargoCode(tMacWorkTimeDTO.getCargoCode());
		yardTallyItemPO.setCargoName(tMacWorkTimeDTO.getCargoName());
		yardTallyItemPO.setQuantity(0);
		yardTallyItemPO.setTon(BigDecimal.ZERO);
		yardTallyItemPO.setRemark("PC");
		yardTallyItemPO.setStackPositionId(tMacWorkTimeDTO.getMassId());
		yardTallyItemPO.setDirectFetching(tMacWorkTimeDTO.getDirectFetching());
		yardTallyItemPO.setTrustCargoInfoId(tMacWorkTimeDTO.getTrustCargoInfoId());
		tMacTerminalMapper.insertYardTallyItem(yardTallyItemPO);
		return 1;
	}

	@Override
	@Transactional
	public int macWorkApp(TMacWorkTimeDTO tMacWorkTimeDTO) {
		DistributedLock.newBuilder().store(redisTemplate)
				.key(DistributedLockKeyPrefixEnum.TALLY_KEY.getCode() + tMacWorkTimeDTO.getWeighbridgeId())
				.build().run(() -> {
					WeightRecordPoundDTO weightRecordPoundDTO = tMacTerminalMapper.getWeightRecordPound(tMacWorkTimeDTO.getWeighbridgeId());
					if(weightRecordPoundDTO == null) {
						throw new BusinessRuntimeException("未获取到磅单信息：" + tMacWorkTimeDTO.getWeighbridgeId());
					}
					// 验证当前设备是否已经开始作业
					Map<String, Object> paramMap = Maps.newHashMap();
					paramMap.put("workPlanId", tMacWorkTimeDTO.getWorkPlanId());
					paramMap.put("planType", tMacWorkTimeDTO.getPlanType());
					paramMap.put("reservatCarId", tMacWorkTimeDTO.getMacWorkId());
					paramMap.put("weighbridgeId", tMacWorkTimeDTO.getWeighbridgeId());
					paramMap.put("dispatchSecondaryId", tMacWorkTimeDTO.getMacWorkId());
					paramMap.put("macId", tMacWorkTimeDTO.getMacId());
					List<TMacWorkTimePO> checkList = tMacTerminalMapper.getTMacWorkTimeListByConditionEnd(paramMap);
					if(CollectionUtils.isNotEmpty(checkList)) {
						if(checkList.size() == 1) {
							throw new BusinessRuntimeException("当前车辆已经开始作业，请勿重复操作！");
						} else if(checkList.size() > 1) {
							throw new BusinessRuntimeException("数据有误，请联系管理员~");
						}
					}
					// 查询当前车辆是否已经理货
					Integer count = tMacTerminalMapper.getTallyCountByWeighbridgeId(tMacWorkTimeDTO.getWeighbridgeId());
					if(count >= 1) {
						throw new BusinessRuntimeException("当前车辆已经理货，请刷新列表！");
					}
					TMacWorkTimePO tMacWorkTimePO = new TMacWorkTimePO();
					tMacWorkTimePO.setId(snowflake.nextId());
					tMacWorkTimePO.setWorkPlanId(tMacWorkTimeDTO.getWorkPlanId());
					tMacWorkTimePO.setPlanType(tMacWorkTimeDTO.getPlanType());
					tMacWorkTimePO.setMacId(77L);
					tMacWorkTimePO.setMacCode("APP");
					tMacWorkTimePO.setReservatCarId(tMacWorkTimeDTO.getMacWorkId());
					tMacWorkTimePO.setWorkTimeStart(new Date());
					tMacWorkTimePO.setWorkTimeEnd(new Date());
					tMacTerminalMapper.insertTMacWorkTime(tMacWorkTimePO);
					//获取计划线是否为直取标志位
					String isDirection = tMacTerminalMapper.getWorkPlanById(tMacWorkTimeDTO.getWorkPlanId());
					// 修改过磅表数据
					tMacTerminalMapper.updateWeightRecordPound(tMacWorkTimeDTO.getWeighbridgeId(),isDirection);
					// 根据主过程查询第一个子过程
					Map<String, String> processMap = tMacTerminalMapper.getFirstChildProcess(tMacWorkTimeDTO.getWorkPlanId());
					// 理货表中添加数据
					TYardTallyPO yardTallyPO = new TYardTallyPO();
					Long id = snowflake.nextId();
					yardTallyPO.setId(id);
					yardTallyPO.setPlanId(tMacWorkTimeDTO.getWorkPlanId());
					yardTallyPO.setRelationId(null);
					yardTallyPO.setShipvoyageId(tMacWorkTimeDTO.getShipvoyageId());
					if(processMap != null) {
						yardTallyPO.setProcessCode(processMap.get("processCode"));
						yardTallyPO.setProcessName(processMap.get("processName"));
					} else {
						yardTallyPO.setProcessCode("");
						yardTallyPO.setProcessName("");
					}
					yardTallyPO.setEquipmentId(77L);
					yardTallyPO.setEquipmentNo("APP");
					yardTallyPO.setTransportEquipmentId(weightRecordPoundDTO.getNoteId());
					yardTallyPO.setTransportEquipmentNo(weightRecordPoundDTO.getTruckPlate());
					yardTallyPO.setQuantity(0D);
					yardTallyPO.setTon(0D);
					yardTallyPO.setRemark("APP");
					yardTallyPO.setOperatorsId(securityUtils.getLoginUserId());
					yardTallyPO.setOperatorsName(securityUtils.getLoginUserName());
					yardTallyPO.setTransportOperatorsId(null);
					yardTallyPO.setTransportOperatorsName("无");
					yardTallyPO.setWeighbridgeId(tMacWorkTimeDTO.getWeighbridgeId());
					yardTallyPO.setPlanNo(weightRecordPoundDTO.getPlanNo());
					yardTallyPO.setTsptId(weightRecordPoundDTO.getTsptId());
					yardTallyPO.setWorkErWeiId(weightRecordPoundDTO.getWorkErweiId());
					tMacTerminalMapper.insertYardTally(yardTallyPO);

					TYardTallyItemPO yardTallyItemPO = new TYardTallyItemPO();
					yardTallyItemPO.setId(snowflake.nextId());
					yardTallyItemPO.setTallyId(id);
					yardTallyItemPO.setCargoInfoId(tMacWorkTimeDTO.getCargoInfoId());
					yardTallyItemPO.setCargoCode(tMacWorkTimeDTO.getCargoCode());
					yardTallyItemPO.setCargoName(tMacWorkTimeDTO.getCargoName());
					yardTallyItemPO.setQuantity(0);
					yardTallyItemPO.setTon(BigDecimal.ZERO);
					yardTallyItemPO.setRemark("APP");
					yardTallyItemPO.setStackPositionId(tMacWorkTimeDTO.getMassId());
					yardTallyItemPO.setDirectFetching(tMacWorkTimeDTO.getDirectFetching());
					yardTallyItemPO.setTrustCargoInfoId(tMacWorkTimeDTO.getTrustCargoInfoId());
					tMacTerminalMapper.insertYardTallyItem(yardTallyItemPO);
					return 1;
				});
		return 1;
	}

	@Override
	public List<Map<String,Object>> getYardByName(List<String> storageYardNms) {
		if(CollectionUtil.isEmpty(storageYardNms)){
			throw new BusinessRuntimeException("场地不能为空");
		}
		return tMacTerminalMapper.getYardByName(storageYardNms);
	}

	@Override
	public TMacTerminalLoadCarDTO getLoadCarByWeighbridgeId(Long weighbridgeId) {
		TMacTerminalLoadCarDTO tMacTerminalLoadCarDTO= tMacTerminalMapper.getLoadCarByWeighbridgeId(weighbridgeId);
		//给净装吨数赋值
		if(tMacTerminalLoadCarDTO.getWeightSelf() != null) {
			tMacTerminalLoadCarDTO.setNetPackTon(BigDecimal.valueOf(49L).subtract(tMacTerminalLoadCarDTO.getWeightSelf()));
		}

		return tMacTerminalLoadCarDTO;
	}

	@Override
	public TMacWorkNowPO getMacWorkNow(TMacWorkNowDTO searchDTO) {

		Map<String, Object> paramMap = publicService.getDateAndShift(null);

		try {
			searchDTO.setWorkDate(new SimpleDateFormat("yyyy-MM-dd").parse(paramMap.get("workDate").toString()));
			searchDTO.setClassCode(paramMap.get("classCode").toString());
		} catch (ParseException e) {
		}

		return tMacTerminalMapper.getMacWorkNow(searchDTO);
	}

	@Override
	public int insertMacWorkNow(TMacWorkNowDTO paramDTO) {

		paramDTO.setId(snowflake.nextId());

		// 根据系统当前时间获取日期、班次(workDate、classCode)
		Map<String, Object> paramMap = publicService.getDateAndShift(null);

		try {
			paramDTO.setWorkDate(new SimpleDateFormat("yyyy-MM-dd").parse(paramMap.get("workDate").toString()));
			paramDTO.setClassCode(paramMap.get("classCode").toString());
		} catch (ParseException e) {
		}

		return tMacTerminalMapper.insertMacWorkNow(paramDTO);
	}

	@Override
	public boolean doSave(TMacWorKLocationDTO tMacWorKLocationDTO) {
		return tMacTerminalMapper.doSave(tMacWorKLocationDTO)==1;
	}

	@Override
	public TMacWorKLocationDTO getDetailLocation(Long weighbridgeId) {
		return tMacTerminalMapper.getDetailLocation(weighbridgeId);
	}

	/**
	 * 撤销理货
	 * @param weighbridgeId
	 * @return
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public boolean cancelTally(Long weighbridgeId) {
		//删除理货记录
		//不是超级管理员禁止操作
		if(!CommonEnum.YesNoMode.YES.getCode().equals(securityUtils.getUserInfo().getIsSuperadmin())){
			throw new BusinessRuntimeException("权限不足, NOT SUPERADMIN");
		}
		//车辆离港了不能处理
		PoundDTO poundDTO = tMacTerminalMapper.getOutTimeByNoteId(weighbridgeId);
		if(poundDTO.getWeighOutDt()!=null) {
			throw new BusinessRuntimeException("出港过磅时间已存在,车辆已经离港，不能操作");
		}
		if(poundDTO.getIsFinished()!=null && "1".equals(poundDTO.getIsFinished())){
			throw new BusinessRuntimeException("磅单的 IS_FINISHED 字段已经被标记为 1 磅单已结束，不能操作");
		}

		//有港存流水的报错不能处理
		List<TPrdPortStorageDetailPO> storageList = tMacTerminalMapper.getStorageList(weighbridgeId);
		if(!storageList.isEmpty()){
			throw new BusinessRuntimeException("已经产生港存流水，不能操作");
		}

		tMacTerminalMapper.delTallLog(weighbridgeId);

		//恢复磅单的理货状态
		tMacTerminalMapper.changPoundTallyStatusWithZero(weighbridgeId);
		LOGGER.info("在港车辆撤销理货，操作人_操作人ID_操作时间"+securityUtils.getLoginUserName()+"_"+securityUtils.getLoginUserId()+"_"+LocalDateTime.now().toString());
		return true;
	}
}
