package com.yy.ppm.equipment.controller;


import com.yy.ppm.equipment.bean.dto.EMaintenanceProjectQuotaDTO;
import com.yy.ppm.equipment.service.EMaintenanceProjectQuotaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 维修定额项目 Controller
 *
 * 前端接口入口
 */
@RestController
@RequestMapping("/api/v1/internal/quotaProject")
public class EMaintenanceProjectQuotaController {

    @Autowired
    private EMaintenanceProjectQuotaService service;


    /**
     * 查询维修定额项目列表
     */
    @GetMapping("/list")
    public List<EMaintenanceProjectQuotaDTO> list(){

        return service.list();

    }


    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public EMaintenanceProjectQuotaDTO get(@PathVariable Long id){

        return service.get(id);

    }


    /**
     * 新增维修定额项目
     */
    @PostMapping("/add")
    public int add(@RequestBody EMaintenanceProjectQuotaDTO quota){

        return service.add(quota);

    }


    /**
     * 修改维修定额项目
     */
    @PostMapping("/update")
    public int update(@RequestBody EMaintenanceProjectQuotaDTO quota){

        return service.update(quota);

    }


    /**
     * 删除维修定额项目
     */
    @GetMapping("/delete/{id}")
    public int delete(@PathVariable Long id){

        return service.delete(id);

    }

}