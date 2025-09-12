package com.yy.ppm.master.controller;

import java.util.Map;

import com.yy.ppm.master.bean.dto.*;
import com.yy.ppm.master.service.MPrintService;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.common.service.CommonService;


import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Description 打印操作controller类
 *
 * @author
 * @date
 */
@RestController
@RequestMapping(value = "/api/internal/print")
@Validated
@Tag(name = "基础数据.打印示例")
public class MPrintController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MPrintController.class);

    @Resource
    MPrintService mPrintService;

    @Autowired
    private CommonService commonService;

    /**
     * 新增
     *
     * @param po  实体类
     * @return 响应数据
     * @throws Exception
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('master:hiprint:insert')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> insertDict(@RequestBody MPrintDTO po)
            throws Exception {
        final String methodName = "PrintController:insert";
        LOGGER.enter(methodName, "新增打印示例[start]");

        mPrintService.insert(po);

        LOGGER.exit(methodName, "新增打印示例[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }
    /**
     * 查询列表
     *
     * @param  mPrintSearchDTO
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('master:hiprint:query')")
    @Log(OperateTypeEnum.QUERY)
    public Map<String, Object> getList(MPrintSearchDTO mPrintSearchDTO)
            throws Exception {
        final String methodName = "PrintController:getList";
        LOGGER.enter(methodName, "查询列表[start]");

        Pages<MPrintDTO> list = mPrintService.getList(mPrintSearchDTO);

        LOGGER.exit(methodName, "查询列表[end]");
        return Response.SUCCESS.newBuilder().toResult(list);
    }

}
