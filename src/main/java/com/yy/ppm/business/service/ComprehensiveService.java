package com.yy.ppm.business.service;

import com.yy.ppm.business.bean.dto.ComprehensiveDTO;

import java.util.List;
import java.util.Map;

public interface ComprehensiveService {

    ComprehensiveDTO getList(String planDate);

    List<Map<String, String>> getPieList(String planDate);
}
