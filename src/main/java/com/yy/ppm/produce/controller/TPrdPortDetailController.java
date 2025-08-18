package com.yy.ppm.produce.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.dto.portStorage.*;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.service.TPrdPortDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/portDetail")
@Validated
public class TPrdPortDetailController {

    @Autowired
    private TPrdPortDetailService tPrdPortDetailService;

    /**
     * 详细堆存
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPortStorage")
    public Map<String, Object> listPortStorage(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        Pages<TPrdPortStorageDTO> result = tPrdPortDetailService.listPortStorage(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 件数、吨数（数量）汇总
     *
     * @param query
     * @return
     */
    @GetMapping("/summaryQuantityTon")
    public Map<String, Object> summaryQuantityTon(TPrdPortStorageQueryDTO query) {
        Map<String, Object> result = tPrdPortDetailService.summaryQuantityTon(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 详细堆存导出
     *
     * @param query
     * @param response
     */
    @GetMapping("/exportPortStorage")
    public void exportPortStorage(TPrdPortStorageQueryDTO query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "详细堆存");
        try {
            byte[] bytes = tPrdPortDetailService.exportPortStorage(query);
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
