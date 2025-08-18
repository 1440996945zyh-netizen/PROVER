package com.yy.ppm.largescreen.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.yy.common.enums.ScreenListType;
import com.yy.common.enums.ScreenPortType;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.largescreen.mapper.TLargeScreenMapper;
import com.yy.ppm.largescreen.service.TLargeScreenService;


@Service
public class TLargeScreenServiceImpl implements TLargeScreenService {

    @Resource
    private TLargeScreenMapper tLargeScreenMapper;

    @Resource
    private PublicService publicService;

	@Override
	public Map<String, Object> getThroughputSInfo(String portCode) {

		Map<String, Object> res = Maps.newHashMap();

		LocalDate currentDate = LocalDate.now();

        DateTimeFormatter yFormatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter y = DateTimeFormatter.ofPattern("yyyy年");
        DateTimeFormatter yMFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter M = DateTimeFormatter.ofPattern("M月份");
        DateTimeFormatter yMdFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter d = DateTimeFormatter.ofPattern("dd日");
        String yyyy = currentDate.format(yFormatter);
        String yyyyMM = currentDate.format(yMFormatter);
        String yyyyMMdd = currentDate.format(yMdFormatter);
        String startDate = yyyy+"-"+"01";

//		if(ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode)) {
//
//	        // 查询年吞吐量
//			Integer yyyyThroughput = tLargeScreenMapper.getYyyyThroughput(yyyy);
//	        res.put("yKey", currentDate.format(y));
//	        res.put("yValue", yyyyThroughput == null?0:yyyyThroughput);
//	        // 查询月吞吐量
//	        Integer yyyyMMThroughput = tLargeScreenMapper.getYyyyMMThroughput(yyyyMM);
//	        res.put("mKey", currentDate.format(M));
//	        res.put("mValue", yyyyMMThroughput == null?0:yyyyMMThroughput);
//	        // 查询当日吞吐量
//	        Integer yyyyMMddThroughput = tLargeScreenMapper.getYyyyMMddThroughput(yyyyMMdd);
//	        res.put("dKey", currentDate.format(d));
//	        res.put("dValue", yyyyMMddThroughput == null?0:yyyyMMddThroughput);
//		}
		if(StringUtils.isEmpty(portCode)){
//			// 查询年吞吐量
//			Integer yyyyThroughput = tLargeScreenMapper.getYyyyThroughput(yyyy);
//			res.put("yKey", currentDate.format(y));
//			Integer yValue = yyyyThroughput == null ? 0 : yyyyThroughput;
//			// 查询月吞吐量
//			Integer yyyyMMThroughput = tLargeScreenMapper.getYyyyMMThroughput(yyyyMM);
//			res.put("mKey", currentDate.format(M));
//			Integer mValue = yyyyMMThroughput == null ? 0 : yyyyMMThroughput;
//			// 查询当日吞吐量
//			Integer yyyyMMddThroughput = tLargeScreenMapper.getYyyyMMddThroughput(yyyyMMdd);
//			res.put("dKey", currentDate.format(d));
//			Integer dValue = yyyyMMddThroughput == null ? 0 : yyyyMMddThroughput;

			// 查询年吞吐量
			Integer yyyyThroughput2 = tLargeScreenMapper.getYyyyMMThroughputS1(startDate,yyyyMM, portCode, "1");
			Integer yValue2 = yyyyThroughput2 == null ? 0 : yyyyThroughput2;
			// 查询月吞吐量
			Integer yyyyMMThroughput2 = tLargeScreenMapper.getYyyyMMThroughputS(yyyyMM, portCode, "1");
			Integer mValue2 = yyyyMMThroughput2 == null ? 0 : yyyyMMThroughput2;
			// 查询当日吞吐量
			Integer yyyyMMddThroughput2 = tLargeScreenMapper.getYyyyMMddThroughputS(yyyyMMdd, portCode, "1");
			Integer dValue2 = yyyyMMddThroughput2 == null ? 0 : yyyyMMddThroughput2;

//			yValue += yValue2;
//			mValue += mValue2;
//			dValue += dValue2;
			// 更新res中的yValue
			res.put("yKey", currentDate.format(y));
			res.put("yValue", yValue2);
			res.put("mKey", currentDate.format(M));
			res.put("mValue", mValue2);
			res.put("dKey", currentDate.format(d));
			res.put("dValue", dValue2);
		}else{
			// 查询年吞吐量
			Integer yyyyThroughput = tLargeScreenMapper.getYyyyMMThroughputS1(startDate, yyyyMM,portCode, "1");
			res.put("yKey", currentDate.format(y));
			res.put("yValue", yyyyThroughput == null?0:yyyyThroughput);
			// 查询月吞吐量
			Integer yyyyMMThroughput = tLargeScreenMapper.getYyyyMMThroughputS(yyyyMM, portCode, "1");
			res.put("mKey", currentDate.format(M));
			res.put("mValue", yyyyMMThroughput == null?0:yyyyMMThroughput);
			// 查询当日吞吐量
			Integer yyyyMMddThroughput = tLargeScreenMapper.getYyyyMMddThroughputS(yyyyMMdd, portCode, "1");
			res.put("dKey", currentDate.format(d));
			res.put("dValue", yyyyMMddThroughput == null?0:yyyyMMddThroughput);
		}
		return res;
	}

	@Override
	public Map<String, Object> getThroughputJInfo(String portCode) {

		Map<String, Object> res = Maps.newHashMap();

		LocalDate currentDate = LocalDate.now();

        DateTimeFormatter yFormatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter y = DateTimeFormatter.ofPattern("yyyy年");
        DateTimeFormatter yMFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter M = DateTimeFormatter.ofPattern("M月份");
        DateTimeFormatter yMdFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter d = DateTimeFormatter.ofPattern("dd日");
        String yyyy = currentDate.format(yFormatter);
        String yyyyMM = currentDate.format(yMFormatter);
        String yyyyMMdd = currentDate.format(yMdFormatter);
		String startDate = yyyy+"-"+"01";

        // 查询年吞吐量 (startDate, yyyyMM,portCode, "1");
        Integer yyyyThroughput = tLargeScreenMapper.getYyyyMMThroughputS1(startDate, yyyyMM,portCode, "2");
        res.put("yKey", currentDate.format(y));
        res.put("yValue", yyyyThroughput == null?0:yyyyThroughput);
        // 查询月吞吐量
        Integer yyyyMMThroughput = tLargeScreenMapper.getYyyyMMThroughputS(yyyyMM, portCode, "2");
        res.put("mKey", currentDate.format(M));
        res.put("mValue", yyyyMMThroughput == null?0:yyyyMMThroughput);
        // 查询当日吞吐量
        Integer yyyyMMddThroughput = tLargeScreenMapper.getYyyyMMddThroughputS(yyyyMMdd, portCode, "2");
        res.put("dKey", currentDate.format(d));
        res.put("dValue", yyyyMMddThroughput == null?0:yyyyMMddThroughput);
		return res;
	}

	@Override
	public List<Map<String, Object>> getThroughputSList(String portCode, String dataType) {

		List<Map<String, Object>> resList = Lists.newArrayList();

//		// 潍坊港
//		if(ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode)) {
//
//			LocalDate currentDate = LocalDate.now();
//			// 月份
//			if(ScreenListType.SCREEN_LIST_10.getCode().equals(dataType)) {
//
//		        String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
//		        String beforeDate = currentDate.minusMonths(12).format(DateTimeFormatter.ofPattern("yyyy-MM"));
//		        List<String> monthList = getMonthBetween(beforeDate, nowDate);
//
//		        resList = tLargeScreenMapper.getThroughputSList_MM(monthList);
//			// 日期
//			} else {
//
//		        String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//		        String beforeDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//		        List<String> daysList = getDaysBetween(beforeDate, nowDate);
//
//		        resList = tLargeScreenMapper.getThroughputSList_DD(daysList);
//			}
//		// 其他港口
//		} else if(!ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode) && !StringUtils.isEmpty(portCode) ){
//
//		}
		//所有港口
		if(StringUtils.isEmpty(portCode)) {
			LocalDate currentDate = LocalDate.now();
			// 月份
//			if(ScreenListType.SCREEN_LIST_10.getCode().equals(dataType)) {
//
//				String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
//				String beforeDate = currentDate.minusMonths(12).format(DateTimeFormatter.ofPattern("yyyy-MM"));
//				List<String> monthList = getMonthBetween(beforeDate, nowDate);
//
//				resList = tLargeScreenMapper.getThroughputSList_MM(monthList);
//				// 日期
//			} else {
//
//				String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//				String beforeDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//				List<String> daysList = getDaysBetween(beforeDate, nowDate);
//
//				resList = tLargeScreenMapper.getThroughputSList_DD(daysList);
//			}

			List<Map<String, Object>> resList2 = Lists.newArrayList();
			// 月份
			if(ScreenListType.SCREEN_LIST_10.getCode().equals(dataType)) {

				String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
				String beforeDate = currentDate.minusMonths(12).format(DateTimeFormatter.ofPattern("yyyy-MM"));
				List<String> monthList = getMonthBetween(beforeDate, nowDate);

				resList2 = tLargeScreenMapper.getThroughputSList_MM_S(monthList, portCode, "1");
				// 日期
			} else {

				String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				String beforeDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				List<String> daysList = getDaysBetween(beforeDate, nowDate);

				resList2 = tLargeScreenMapper.getThroughputSList_DD_S(daysList, portCode, "1");
			}
			resList.addAll(resList2);
		}else{
			LocalDate currentDate = LocalDate.now();
			// 月份
			if(ScreenListType.SCREEN_LIST_10.getCode().equals(dataType)) {

				String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
				String beforeDate = currentDate.minusMonths(12).format(DateTimeFormatter.ofPattern("yyyy-MM"));
				List<String> monthList = getMonthBetween(beforeDate, nowDate);

				resList = tLargeScreenMapper.getThroughputSList_MM_S(monthList, portCode, "1");
				// 日期
			} else {

				String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				String beforeDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				List<String> daysList = getDaysBetween(beforeDate, nowDate);

				resList = tLargeScreenMapper.getThroughputSList_DD_S(daysList, portCode, "1");
			}
		}
		return resList;
	}

	@Override
	public List<Map<String, Object>> getThroughputJList(String portCode, String dataType) {

		List<Map<String, Object>> resList = Lists.newArrayList();

		LocalDate currentDate = LocalDate.now();
		// 月份
		if(ScreenListType.SCREEN_LIST_10.getCode().equals(dataType)) {

	        String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
	        String beforeDate = currentDate.minusMonths(12).format(DateTimeFormatter.ofPattern("yyyy-MM"));
	        List<String> monthList = getMonthBetween(beforeDate, nowDate);

	        resList = tLargeScreenMapper.getThroughputSList_MM_S(monthList, portCode, "2");
		// 日期
		} else {
	        String nowDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        String beforeDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        List<String> daysList = getDaysBetween(beforeDate, nowDate);

	        resList = tLargeScreenMapper.getThroughputSList_DD_S(daysList, portCode, "2");
		}

		return resList;
	}

	/**获取两个时间节点之间的月份列表**/
    private List<String> getMonthBetween(String minDate, String maxDate){
        List<String> result = Lists.newArrayList();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<String> getDaysBetween(String minDate, String maxDate){
        List<String> result = Lists.newArrayList();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//格式化为年月

            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            min.setTime(sdf.parse(minDate));
            //min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), min.get(Calendar.DAY_OF_YEAR));

            max.setTime(sdf.parse(maxDate));
            //max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), max.get(Calendar.DAY_OF_YEAR));

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

	@Override
	public Map<String, Object> getInPortCarNumAndDuration(String portCode) {

		Map<String, Object> res = Maps.newHashMap();

		if(ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode)) {

			/**
			 * 在港车辆数
			 */
			Integer inPortCarNum = tLargeScreenMapper.getInPortCarNum();
			res.put("inPortCarNum", inPortCarNum);

			/**
			 * 在港停时
			 */
			try {
				Integer duration = tLargeScreenMapper.getDuration();
				res.put("duration", duration);
			} catch (Exception e) {
				res.put("duration", 0);
			}

			/**
			 * 在港车辆列表
			 */
			List<Map<String, Object>> inPortCarList = tLargeScreenMapper.getInPortCarList();
			res.put("inPortCarList", inPortCarList);
		}  else if(!ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode) && !StringUtils.isEmpty(portCode) ){

			/**
			 * 在港车辆数
			 */
			Integer inPortCarNum = tLargeScreenMapper.getInPortCarNum_S(portCode);
			res.put("inPortCarNum", inPortCarNum);

			/**
			 * 在港停时
			 */
			Integer duration = tLargeScreenMapper.getDuration_S(portCode);
			res.put("duration", inPortCarNum == 0?0:(int)(duration/inPortCarNum));

			/**
			 * 在港车辆列表
			 */
			List<Map<String, Object>> inPortCarList = tLargeScreenMapper.getInPortCarList_S(portCode);
			res.put("inPortCarList", inPortCarList);
		} else if(StringUtils.isEmpty(portCode)){
			//在港车辆数潍坊港
			Integer inPortCarNum = tLargeScreenMapper.getInPortCarNum();
			//在港停时所有港
			try {
				Integer duration = tLargeScreenMapper.getDurationAll();
				res.put("duration", duration);
			} catch (Exception e) {
				res.put("duration", 0);
			}
			//在港车辆列表潍坊港
			List<Map<String, Object>> inPortCarList = tLargeScreenMapper.getInPortCarList();


			//在港车辆数其他港
			Integer inPortCarNum2 = tLargeScreenMapper.getInPortCarNum_S(portCode);
			//在港车辆列表其他港
			List<Map<String, Object>> inPortCarList2 = tLargeScreenMapper.getInPortCarList_S(portCode);

			inPortCarNum +=inPortCarNum2;
			inPortCarList.addAll(inPortCarList2);
			res.put("inPortCarNum", inPortCarNum);
			res.put("inPortCarList", inPortCarList);

		}
		return res;
	}

	@Override
	public Map<String, Object> getPortStorage(String portCode) {
		Map<String, Object> res = Maps.newHashMap();
		//潍坊港
		if(ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode)) {
			List<Map<String, Object>> leftPieChartList = tLargeScreenMapper.getLeftPieChartList();

			res.put("leftPieChart", leftPieChartList);

			List<Map<String, Object>> rightPieChartList = tLargeScreenMapper.getRightPieChartList();
			res.put("rightPieChart", rightPieChartList);
		}
		//其他港
		else if(!ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode) && !StringUtils.isEmpty(portCode) ){
			List<Map<String, Object>> leftPieChartList = tLargeScreenMapper.getLeftPieChartList_S(portCode);

			res.put("leftPieChart", leftPieChartList);

			List<Map<String, Object>> rightPieChartList = tLargeScreenMapper.getRightPieChartList_S(portCode);
			res.put("rightPieChart", rightPieChartList);
		}
		//所有港
		else if(StringUtils.isEmpty(portCode)){
			List<Map<String, Object>> leftPieChartList = tLargeScreenMapper.getLeftPieChartList();
			List<Map<String, Object>> rightPieChartList = tLargeScreenMapper.getRightPieChartList();
			List<Map<String, Object>> leftPieChartList_s = tLargeScreenMapper.getLeftPieChartList_S(portCode);
			List<Map<String, Object>> rightPieChartList_s = tLargeScreenMapper.getRightPieChartList_S(portCode);
			leftPieChartList.addAll(leftPieChartList_s);
			rightPieChartList.addAll(rightPieChartList_s);

			// 使用 Map 来按照 packingName 合并相同的项并累加 ton 值
			Map<String, BigDecimal> mergedData = new HashMap<>();
			for (Map<String, Object> item : leftPieChartList) {
				String packingName = (String) item.get("packingName");
				BigDecimal ton = (BigDecimal) item.get("ton");

				if (mergedData.containsKey(packingName)) {
					mergedData.put(packingName, mergedData.get(packingName).add(ton));
				} else {
					mergedData.put(packingName, ton);
				}
			}

			// 将合并后的数据转换回 List<Map<String, Object>>
			List<Map<String, Object>> mergedList = new ArrayList<>();
			for (Map.Entry<String, BigDecimal> entry : mergedData.entrySet()) {
				Map<String, Object> newItem = new HashMap<>();
				newItem.put("packingName", entry.getKey());
				newItem.put("ton", entry.getValue());
				mergedList.add(newItem);
			}

			res.put("leftPieChart", mergedList);

			Map<String, BigDecimal> mergedData2 = new HashMap<>();
			for (Map<String, Object> item : rightPieChartList) {
				String cargoName = (String) item.get("cargoName");
				BigDecimal ton = (BigDecimal) item.get("ton");

				if (mergedData2.containsKey(cargoName)) {
					mergedData2.put(cargoName, mergedData2.get(cargoName).add(ton));
				} else {
					mergedData2.put(cargoName, ton);
				}
			}

			// 将合并后的数据转换回 List<Map<String, Object>>
			List<Map<String, Object>> mergedList2 = new ArrayList<>();
			for (Map.Entry<String, BigDecimal> entry : mergedData2.entrySet()) {
				Map<String, Object> newItem = new HashMap<>();
				newItem.put("cargoName", entry.getKey());
				newItem.put("ton", entry.getValue());
				mergedList2.add(newItem);
			}

			res.put("rightPieChart", mergedList2);

		}
		return res;
	}

	@Override
	public List<Map<String, Object>> getShipPlan(String portCode) {

        List<Map<String, Object>> res = Lists.newArrayList();

		if(ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode)) {
			LocalDate currentDate = LocalDate.now();
	        String searchDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			res = tLargeScreenMapper.getShipPlan(searchDate);

			for (Map<String, Object> map : res) {
				// 查询作业进度（判断是装还是卸）
				BigDecimal workTon = BigDecimal.ZERO;
				if("装".equals(map.get("loadOrUnload"))) {
					workTon = tLargeScreenMapper.getWorkTon(map.get("id").toString(), "装船");
				} else if("卸".equals(map.get("loadOrUnload"))) {
					workTon = tLargeScreenMapper.getWorkTon(map.get("id").toString(), "卸船");
				}

				if(map.get("ton") != null && workTon != null && workTon.compareTo(BigDecimal.ZERO) > 0) {
					map.put("schedule",
							workTon.divide(new BigDecimal(map.get("ton").toString()),2, RoundingMode.HALF_UP));
				} else {
					map.put("schedule", 0);
				}
			}
		} else if(!ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode) && !StringUtils.isEmpty(portCode)){
			//
			res = tLargeScreenMapper.getShipPlan_S(portCode);
			for (Map<String, Object> map : res) {
				if(map.get("shipStatus") != null) {
					//船舶状态（1：靠泊 2：到港 3：离港 4:开工 5:完工 6：停工 9:预报）
					if("1".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "靠泊");
					} else if("2".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "到港");
					} else if("3".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "离港");
					} else if("4".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "开工");
					} else if("5".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "完工");
					} else if("6".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "停工");
					} else if("9".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "预报");
					}
				}
			}
		}else if(StringUtils.isEmpty(portCode)){
			LocalDate currentDate = LocalDate.now();
			String searchDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			res = tLargeScreenMapper.getShipPlan(searchDate);

			for (Map<String, Object> map : res) {
				// 查询作业进度（判断是装还是卸）
				BigDecimal workTon = BigDecimal.ZERO;
				if("装".equals(map.get("loadOrUnload"))) {
					workTon = tLargeScreenMapper.getWorkTon(map.get("id").toString(), "装船");
				} else if("卸".equals(map.get("loadOrUnload"))) {
					workTon = tLargeScreenMapper.getWorkTon(map.get("id").toString(), "卸船");
				}

				if(map.get("ton") != null && workTon != null && workTon.compareTo(BigDecimal.ZERO) > 0) {
					map.put("schedule",
							workTon.divide(new BigDecimal(map.get("ton").toString()),2, RoundingMode.HALF_UP));
				} else {
					map.put("schedule", 0);
				}
			}
			List<Map<String, Object>> res2 = Lists.newArrayList();
			res2 = tLargeScreenMapper.getShipPlan_S(portCode);
			for (Map<String, Object> map : res2) {
				if(map.get("shipStatus") != null) {
					//船舶状态（1：靠泊 2：到港 3：离港 4:开工 5:完工 6：停工 9:预报）
					if("1".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "靠泊");
					} else if("2".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "到港");
					} else if("3".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "离港");
					} else if("4".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "开工");
					} else if("5".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "完工");
					} else if("6".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "停工");
					} else if("9".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "预报");
					}
				}
			}
			res.addAll(res2);
		}

		return res;
	}

	@Override
	public List<Map<String, Object>> getShipDynamics(String portCode) {

        List<Map<String, Object>> res = Lists.newArrayList();

		if(ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode)) {
			LocalDate currentDate = LocalDate.now();
	        String searchDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        List<Map<String, Object>> shipPlanList = tLargeScreenMapper.getShipDynamics(searchDate);
			for (Map<String, Object> shipPlan : shipPlanList) {
				if(shipPlan.get("shipStatusCode") != null
						&& "120".equals(shipPlan.get("shipStatusCode").toString())) {
					// 查询离港时间
					String leaveTime = tLargeScreenMapper.getShipLeaveTime(shipPlan.get("id").toString());
					shipPlan.put("leaveTime", leaveTime);
				}

				// 查询靠泊时间
				String berthTime = tLargeScreenMapper.getShipberthTime(shipPlan.get("id").toString());
				if(StringUtils.isNoneBlank(berthTime)) {
					shipPlan.put("berthTime", berthTime);
				}
			}
			return shipPlanList;
		} else if (!ScreenPortType.SCREEN_PORT_10.getCode().equals(portCode) && !StringUtils.isEmpty(portCode)){
			res = tLargeScreenMapper.getShipDynamics_S(portCode);
			for (Map<String, Object> map : res) {
				if(map.get("shipStatus") != null) {
					//船舶状态（1：靠泊 2：到港 3：离港 4:开工 5:完工 6：停工 9:预报）
					if("1".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "靠泊");
					} else if("2".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "到港");
					} else if("3".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "离港");
					} else if("4".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "开工");
					} else if("5".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "完工");
					} else if("6".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "停工");
					} else if("9".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "预报");
					}
				}
			}
		}else if(StringUtils.isEmpty(portCode)){
			LocalDate currentDate = LocalDate.now();
			String searchDate = currentDate.minusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			List<Map<String, Object>> shipPlanList = tLargeScreenMapper.getShipDynamics(searchDate);
			for (Map<String, Object> shipPlan : shipPlanList) {
				if(shipPlan.get("shipStatusCode") != null
						&& "120".equals(shipPlan.get("shipStatusCode").toString())) {
					// 查询离港时间
					String leaveTime = tLargeScreenMapper.getShipLeaveTime(shipPlan.get("id").toString());
					shipPlan.put("leaveTime", leaveTime);
				}

				// 查询靠泊时间
				String berthTime = tLargeScreenMapper.getShipberthTime(shipPlan.get("id").toString());
				if(StringUtils.isNoneBlank(berthTime)) {
					shipPlan.put("berthTime", berthTime);
				}
			}
			res = tLargeScreenMapper.getShipDynamics_S(portCode);
			for (Map<String, Object> map : res) {
				if(map.get("shipStatus") != null) {
					//船舶状态（1：靠泊 2：到港 3：离港 4:开工 5:完工 6：停工 9:预报）
					if("1".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "靠泊");
					} else if("2".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "到港");
					} else if("3".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "离港");
					} else if("4".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "开工");
					} else if("5".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "完工");
					} else if("6".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "停工");
					} else if("9".equals(map.get("shipStatus").toString())) {
						map.put("shipStatusName", "预报");
					}
				}
			}
			res.addAll(shipPlanList);
		}
		return res;
	}
}
