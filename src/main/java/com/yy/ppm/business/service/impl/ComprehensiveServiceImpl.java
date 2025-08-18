package com.yy.ppm.business.service.impl;

import com.yy.ppm.business.bean.dto.CargoTypeDTO;
import com.yy.ppm.business.bean.dto.ComprehensiveDTO;
import com.yy.ppm.business.mapper.ComprehensiveMapper;
import com.yy.ppm.business.service.ComprehensiveService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ComprehensiveServiceImpl implements ComprehensiveService {

    @Resource
    private ComprehensiveMapper comprehensiveMapper;

    @Override
    public ComprehensiveDTO getList(String planDate) {
        ComprehensiveDTO dto = comprehensiveMapper.getList(planDate);
        dto.setInvoiceDayCount(dto.getInvoiceDayCount().setScale(2, RoundingMode.HALF_UP));
        dto.setInvoiceMouthCount(dto.getInvoiceMouthCount().setScale(2, RoundingMode.HALF_UP));
        dto.setInvoiceYearCount(dto.getInvoiceYearCount().setScale(2, RoundingMode.HALF_UP));
        dto.setTradeDayCount(dto.getTradeDayCount().setScale(2, RoundingMode.HALF_UP));
        dto.setTradeMouthCount(dto.getTradeMouthCount().setScale(2, RoundingMode.HALF_UP));
        dto.setTradeYearCount(dto.getTradeYearCount().setScale(2, RoundingMode.HALF_UP));
        return dto;
    }

    @Override
    public List<Map<String, String>> getPieList(String planDate) {

        List<Map<String, String>> resultList = new ArrayList<>();
        List<CargoTypeDTO> cargoTypeList = comprehensiveMapper.getPieList(planDate);
        List<CargoTypeDTO> filteredList = cargoTypeList.stream()
                .filter(dto -> dto != null) // 过滤 null 对象
                .collect(Collectors.toList());
        filteredList.sort(Comparator.comparing(dto -> ((BigDecimal) dto.getAllCount()).intValue())); // 排序时需要将BigDecimal转换为int进行比较

        BigDecimal totalCount = BigDecimal.ZERO;
       if(!CollectionUtils.isEmpty(filteredList)){
           for (CargoTypeDTO cargoType : filteredList) {
               totalCount = totalCount.add(cargoType.getAllCount());
           }
       }

        // 计算前五种货物的百分比，并格式化保留一位小数
        DecimalFormat df = new DecimalFormat("0.0");
        //计算前五数量的和
        BigDecimal topFiveCount = BigDecimal.ZERO;

        double topFivePercentage = 0.0;
        for (int i = Math.max(0, filteredList.size() - 5); i < filteredList.size(); i++) {
            CargoTypeDTO cargoType = filteredList.get(i);
            BigDecimal count = cargoType.getAllCount().setScale(2, RoundingMode.HALF_UP);
            double percentage = count.divide(totalCount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            Map<String, String> cargoMap = new HashMap<>();
            cargoMap.put("name", cargoType.getCargoTypeName());
            cargoMap.put("value", count.toString());
            cargoMap.put("percentage", df.format(percentage));
            resultList.add(cargoMap);
            topFiveCount = topFiveCount.add(count);
            topFivePercentage += percentage;
        }
        BigDecimal otherCount = totalCount.subtract(topFiveCount).setScale(2, RoundingMode.HALF_UP); // 计算其他货物的数量

        double otherPercentage = 100.0 - topFivePercentage;
        Map<String, String> otherMap = new HashMap<>();
        otherMap.put("name", "其他");
        otherMap.put("value", otherCount.toString()); // 其他货物的数量这里设置为0，因为已经在前面统计过总数了
        otherMap.put("percentage", df.format(otherPercentage));
        resultList.add(otherMap);

        return resultList;
    }
}
