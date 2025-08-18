package com.yy.ppm.business.service.impl;

import com.yy.ppm.business.mapper.TBusDayNightVehiclesMapper;
import com.yy.ppm.business.service.TBusDayNightVehiclesService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class TBusDayNightVehiclesServiceImpl implements TBusDayNightVehiclesService {
    @Resource
    private TBusDayNightVehiclesMapper tBusDayNightVehiclesMapper;

}
