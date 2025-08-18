package com.yy.ppm.largescreen.service;

import java.util.List;
import java.util.Map;

public interface TLargeScreenService {

	Map<String, Object> getThroughputSInfo(String portCode);
	
	Map<String, Object> getThroughputJInfo(String portCode);

	List<Map<String, Object>> getThroughputSList(String portCode, String dataType);

	List<Map<String, Object>> getThroughputJList(String portCode, String dataType);

	Map<String, Object> getInPortCarNumAndDuration(String portCode);

	Map<String, Object> getPortStorage(String portCode);

	List<Map<String, Object>> getShipPlan(String portCode);

	List<Map<String, Object>> getShipDynamics(String portCode);
}
