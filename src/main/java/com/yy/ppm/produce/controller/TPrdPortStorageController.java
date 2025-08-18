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
import com.yy.ppm.produce.service.TPrdPortStorageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:38
 */
@RestController
@RequestMapping("/api/external/portStorage")
@Validated
public class TPrdPortStorageController {

    @Autowired
    private TPrdPortStorageService tPrdPortStorageService;

    /**
     * 详细堆存
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPortStorage")
    public Map<String, Object> listPortStorage(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        Pages<TPrdPortStorageDTO> result = tPrdPortStorageService.listPortStorage(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 票货汇总
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPortStorageGbCargoInfo")
    public Map<String, Object> listPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        Pages<TPrdPortStorageGbCargoInfoDTO> result = tPrdPortStorageService.listPortStorageGbCargoInfo(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 货主汇总
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPortStorageGbCargoOwner")
    public Map<String, Object> listPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        Pages<TPrdPortStorageGbCargoOwnerDTO> result = tPrdPortStorageService.listPortStorageGbCargoOwner(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 货名汇总
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPortStorageGbCargo")
    public Map<String, Object> listPortStorageGbCargo(TPrdPortStorageQueryDTO query, PageParameter parameter) {
        Pages<TPrdPortStorageGbCargoDTO> result = tPrdPortStorageService.listPortStorageGbCargo(query, parameter);
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
        Map<String, Object> result = tPrdPortStorageService.summaryQuantityTon(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增场存
     *
     * @param portStorageDetails
     * @return
     */
    @PostMapping("/insertPortStorage")
    public Map<String, Object> insertPortStorage(@RequestBody List<TPrdPortStorageDetailPO> portStorageDetails) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(portStorageDetails, true,
                "workDate",
                "classCode",
                "className",
                "inoutStorageCode",
                "inoutStorageName",
                "inoutDate"
        );
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tPrdPortStorageService.insertPortStorage(portStorageDetails);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 清场
     *
     * @param cleanPortStorage
     * @return
     */
    @PutMapping("/cleanPortStorage")
    public Map<String, Object> cleanPortStorage(@RequestBody CleanPortStorageDTO cleanPortStorage) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(cleanPortStorage);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tPrdPortStorageService.cleanPortStorage(cleanPortStorage);
        return Response.SUCCESS.newBuilder().out("清场成功").toResult();
    }

    /**
     * 撤销清场
     *
     * @param cancelCleanPortStorage
     * @return
     */
    @PutMapping("/cancelCleanPortStorage")
    public Map<String, Object> cancelCleanPortStorage(@RequestBody CancelCleanPortStorageDTO cancelCleanPortStorage) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(cancelCleanPortStorage);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tPrdPortStorageService.cancelCleanPortStorage(cancelCleanPortStorage);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 查询进出明细
     *
     * @param query
     * @return
     */
    @GetMapping("/getInoutDetail")
    public Map<String, Object> getInoutDetail(InoutDetailQueryDTO query) {
        if (StringUtils.isNotBlank(query.getBeginClassCode())) {
            if (query.getBeginWorkDate() == null) {
                throw new BusinessRuntimeException("起始作业日期不能为空");
            }
        }
        if (StringUtils.isNotBlank(query.getEndClassCode())) {
            if (query.getEndWorkDate() == null) {
                throw new BusinessRuntimeException("结束作业日期不能为空");
            }
        }

        Map<String, Object> result = tPrdPortStorageService.getInoutDetail(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询票货进出明细
     *
     * @param query
     * @return
     */
    @GetMapping("/getCargoInoutDetail")
    public Map<String, Object> getCargoInoutDetail(InoutDetailQueryDTO query) {
        if (StringUtils.isNotBlank(query.getBeginClassCode())) {
            if (query.getBeginWorkDate() == null) {
                throw new BusinessRuntimeException("起始作业日期不能为空");
            }
        }
        if (StringUtils.isNotBlank(query.getEndClassCode())) {
            if (query.getEndWorkDate() == null) {
                throw new BusinessRuntimeException("结束作业日期不能为空");
            }
        }

        Map<String, Object> result = tPrdPortStorageService.getCargoInoutDetail(query);
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
            byte[] bytes = tPrdPortStorageService.exportPortStorage(query);
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

    /**
     * 票货汇总导出
     *
     * @param query
     * @param response
     */
    @GetMapping("/exportPortStorageGbCargoInfo")
    public void exportPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "票货汇总");
        try {
            byte[] bytes = tPrdPortStorageService.exportPortStorageGbCargoInfo(query);
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

    /**
     * 货主汇总导出
     *
     * @param query
     * @param response
     */
    @GetMapping("/exportPortStorageGbCargoOwner")
    public void exportPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "货主汇总");
        try {
            byte[] bytes = tPrdPortStorageService.exportPortStorageGbCargoOwner(query);
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

    /**
     * 货名汇总导出
     *
     * @param query
     * @param response
     */
    @GetMapping("/exportPortStorageGbCargo")
    public void exportPortStorageGbCargo(TPrdPortStorageQueryDTO query, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "货名汇总");
        try {
            byte[] bytes = tPrdPortStorageService.exportPortStorageGbCargo(query);
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
    /**
     * 电子货垛牌导出
     *
     * @param reqList
     * @param response
     */
    @PostMapping("/stackSigns")
    public void stackSigns(@RequestBody List<StackSignReq> reqList, HttpServletResponse response) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(reqList)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        //ResponseUtils.compliantWithExcel(response, "电子货垛牌");
        try {
            byte[] bytes = tPrdPortStorageService.stackSigns(reqList, response);
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
