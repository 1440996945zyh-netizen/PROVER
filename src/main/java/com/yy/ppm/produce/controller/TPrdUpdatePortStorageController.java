package com.yy.ppm.produce.controller;

import com.google.common.collect.Maps;
import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TPrdUpdatePortStorageReqDTO;
import com.yy.ppm.produce.bean.dto.workTicket.PoundToPortStorageDTO;
import com.yy.ppm.produce.mapper.TPoundMapper;
import com.yy.ppm.produce.service.TPoundService;
import com.yy.ppm.produce.service.TPrdUpdatePortStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/updatePortStorage")
public class TPrdUpdatePortStorageController {
    @Resource
    TPrdUpdatePortStorageService service;
    @Resource
    TPoundService poundService;
    @Resource
    private TPoundMapper tPoundMapper;
    @Autowired
    private TPoundService tPoundService;

    @GetMapping("/getList")
    public Map<String,Object> getList(TPrdUpdatePortStorageReqDTO query) {
        Pages<PoundToPortStorageDTO> result = service.getList(query);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

//    @GetMapping("/getList")
//    public Map<String,Object> getList(TPrdUpdatePortStorageReqDTO query) {
//        Pages<Map<String,Object>> result = service.getUpdatePortStoragePage(query);
//        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
//    }


    @PutMapping("/update")
    public Map<String,Object> update(@RequestBody List<PoundToPortStorageDTO> reqList) {
//        Map<String,Object> params = Maps.newHashMap();
////            params.put("startDate",startDate);
////            params.put("endDate",endDate);
//        params.put("noteId","1114596");
//        tPoundService.updatePortStage(tPoundMapper.getTallyByParams(params));
        poundService.updatePortStage( reqList);
        return Response.SUCCESS.newBuilder().out("更新成功").toResult();
    }
}
