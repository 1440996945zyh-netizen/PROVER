package com.yy.ppm.master.service;

import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceExcelDTO;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceReq;
import com.yy.ppm.master.bean.dto.WaifuProcessPriceRes;

import java.util.List;
import java.util.Map;

public interface WaifuProcessPriceService {

    List<WaifuProcessPriceRes> getList(WaifuProcessPriceReq reqDto);

    byte[] exportExcel(WaifuProcessPriceReq reqDto);

    List<Map<String, String>> getMainProcessList(WaifuProcessPriceReq reqDto);

    List<Map<String, String>> getPositionList(WaifuProcessPriceReq reqDto);

    List<Map<String, String>> getProcessListNoMain(String processCode);

    List<Map<String, String>> getDeptOut();

    List<Map<String, String>> machinTypeList();

    List<Map<String, String>> deleteFunc(Long id);

    List<Map<String, String>> waifuPackageCodeList();

    void doSave(List<WaifuProcessPriceReq> list);


}
