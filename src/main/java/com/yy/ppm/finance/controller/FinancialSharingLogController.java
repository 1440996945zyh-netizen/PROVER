package com.yy.ppm.finance.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformResDTO;
import com.yy.ppm.finance.bean.dto.FinacialSharing.FinaceSharePlatformSearchDTO;
import com.yy.ppm.finance.service.FinanceSharePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/FinanceSharePlatform")
public class FinancialSharingLogController {

    @Autowired
    FinanceSharePlatformService service;

    @GetMapping("/getList")
    public Map<String, Object> getList(FinaceSharePlatformSearchDTO searchDTO) {
        Pages<FinaceSharePlatformResDTO> pages = service.getList(searchDTO);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    @GetMapping("/reSend")
    @Log(title ="重发",value = OperateTypeEnum.QUERY)
    public Map<String, Object> reSend(FinaceSharePlatformSearchDTO searchDTO) {
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

}
