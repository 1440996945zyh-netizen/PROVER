package com.yy.ppm.produce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDetailDTO;
import com.yy.ppm.produce.service.TPrdPortStorageDetailService;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:38
 */
@RestController
@RequestMapping("/api/external/portStorageDetail")
@Validated
public class TPrdPortStorageDetailController {

    @Autowired
    private TPrdPortStorageDetailService tPrdPortStorageDetailService;

    /**
     * 获取港存流水列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPortStorageDetail")
    @PreAuthorize("hasAuthority('produce:portStorageDetail:list')")
    public Map<String, Object> listPortStorage(TPrdPortStorageDetailDTO query, PageParameter parameter) {
        Pages<TPrdPortStorageDetailDTO> result = tPrdPortStorageDetailService.listPortStorageDetail(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 票货汇总
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPortStorage")
    @PreAuthorize("hasAuthority('produce:portStorageDetail:cargoList')")
    public Map<String, Object> listPortStorage(TPrdPortStorageDTO query, PageParameter parameter) {
        Pages<TPrdPortStorageDTO> result = tPrdPortStorageDetailService.listPortStorage(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    
    /**
     * 查询港存信息
     * @param query
     * @return
     */
    @GetMapping("/getPortStorage")
    //@PreAuthorize("hasAuthority('produce:portStorageDetail:cargoInfo')")
    public Map<String, Object> getPortStorage(TPrdPortStorageDTO query) {
        TPrdPortStorageDTO result = tPrdPortStorageDetailService.getPortStorage(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增场存
     *
     * @param portStorageDetails
     * @return
     */
    @PostMapping("/insertPortStorage")
    @PreAuthorize("hasAuthority('produce:portStorageDetail:insert')")
    public Map<String, Object> insertPortStorage(@RequestBody TPrdPortStorageDetailDTO prdPortStorageDetailDTO) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(prdPortStorageDetailDTO, true,
                "workDate",
                "classCode",
                "className",
                "localStorageChange",
                "fromStorehouseId",
                "fromStorehouseName", 
            	"fromRegionId",
            	"fromRegionName",
            	"fromMassId",
            	"fromMassName", 
            	"fromTrustCargoId", 
            	"companyId", 
            	"companyName"
        );
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tPrdPortStorageDetailService.insertPortStorage(prdPortStorageDetailDTO);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }
}
