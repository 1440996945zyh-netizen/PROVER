package com.yy.ppm.equipment.service;

import com.yy.ppm.equipment.bean.dto.HomeDTO;

import java.util.List;
import java.util.Map;

public interface HomeService {
    Map<String, Object> getList();

    List<HomeDTO> getMainInfo(HomeDTO homeDTO);
}
