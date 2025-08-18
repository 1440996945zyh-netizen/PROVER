package com.yy.ppm.largescreen.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TLargeScreenMapper {

	Integer getYyyyThroughput(String yyyy);

	Integer getYyyyMMThroughput(String yyyyMM);

	Integer getYyyyMMddThroughput(String yyyyMMdd);

	List<Map<String, Object>> getThroughputSList_MM(List<String> monthList);

	List<Map<String, Object>> getThroughputSList_DD(List<String> daysList);

	Integer getInPortCarNum();

	Integer getDuration();
	
	List<Map<String, Object>> getInPortCarList();

	Integer getYyyyThroughputS(@Param("dateTime") String dateTime, @Param("portCode")String portCode, @Param("cargoType")String cargoType);

	Integer getYyyyMMThroughputS1(@Param("startTime") String startTime,@Param("endTime") String endTime, @Param("portCode")String portCode, @Param("cargoType")String cargoType);

	Integer getYyyyMMThroughputS(@Param("dateTime")String dateTime, @Param("portCode")String portCode, @Param("cargoType")String cargoType);

	Integer getYyyyMMddThroughputS(@Param("dateTime")String dateTime, @Param("portCode")String portCode, @Param("cargoType")String cargoType);

	List<Map<String, Object>> getThroughputSList_MM_S(@Param("monthList")List<String> monthList, @Param("portCode")String portCode, @Param("cargoType")String cargoType);

	List<Map<String, Object>> getThroughputSList_DD_S(@Param("daysList")List<String> daysList, @Param("portCode")String portCode, @Param("cargoType")String cargoType);

	Integer getInPortCarNum_S(@Param("portCode") String portCode);

	Integer getDuration_S(@Param("portCode") String portCode);

	List<Map<String, Object>> getInPortCarList_S(@Param("portCode") String portCode);

	List<Map<String, Object>> getLeftPieChartList();

	List<Map<String, Object>> getRightPieChartList();

	List<Map<String, Object>> getShipPlan(@Param("searchDate") String searchDate);

	List<Map<String, Object>> getShipPlan_S(@Param("portCode") String portCode);

	List<Map<String, Object>> getShipDynamics(@Param("searchDate") String searchDate);

	List<Map<String, Object>> getShipDynamics_S(@Param("portCode") String portCode);

	String getShipLeaveTime(String id);

	String getShipberthTime(String id);

	BigDecimal getWorkTon(@Param("shipvoyageItemId") String id, @Param("type") String type);


	Integer getDurationAll();

	List<Map<String, Object>> getLeftPieChartList_S(String portCode);

	List<Map<String, Object>> getRightPieChartList_S(String portCode);
}