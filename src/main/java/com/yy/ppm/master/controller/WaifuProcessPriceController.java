package com.yy.ppm.master.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;

import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceReq;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceRes;
import com.yy.ppm.master.service.WaifuProcessPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/waifuProcess")
@Validated
public class WaifuProcessPriceController {

    @Autowired
    WaifuProcessPriceService service;


    @GetMapping("/getList")
    public Map<String, Object> getList(WaifuProcessPriceReq reqDto) {
        List<WaifuProcessPriceRes> result = service.getList(reqDto);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/getMainProcessList")
    public Map<String, Object> getMainProcessList(WaifuProcessPriceReq reqDto) {
        List<Map<String,String>> result= service.getMainProcessList(reqDto);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/getPositionList")
    public Map<String, Object> getPositionList(WaifuProcessPriceReq reqDto) {
        List<Map<String,String>> result= service.getPositionList(reqDto);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/getProcessListNoMain")
    public Map<String, Object> getProcessListNoMain(String processCode) {
        List<Map<String,String>> result= service.getProcessListNoMain(processCode);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/getDeptOut")
    public Map<String, Object> getDeptOut() {
        List<Map<String,String>> result= service.getDeptOut();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/machinTypeList")
    public Map<String, Object> machinTypeList() {
        List<Map<String,String>> result= service.machinTypeList();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/waifuPackageCodeList")
    public Map<String, Object> waifuPackageCodeList() {
        List<Map<String,String>> result= service.waifuPackageCodeList();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @GetMapping("/delete/{id}")
    public Map<String, Object> deleteFunc(@NotNull(message = "id必传") @PathVariable Long id) {
        List<Map<String,String>> result= service.deleteFunc(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    @PostMapping("/doSave")
    public Map<String, Object> doSave(@RequestBody List<WaifuProcessPriceReq> list) {
         service.doSave(list);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    @GetMapping("/downReport")
    public void downReport(WaifuProcessPriceReq reqDto, HttpServletResponse response) throws IOException {
        ResponseUtils.compliantWithExcel(response, "外付配工规则信息");
        try {
            byte[] bytes = service.exportExcel(reqDto);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

}
