package com.yy.ppm.produce.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmDTO;
import com.yy.ppm.produce.bean.dto.TPrdSundryConfirmSearchDTO;
import com.yy.ppm.produce.service.TPrdSundryConfirmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/sundryConfirm")
public class TPrdSundryConfirmController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TPrdSundryConfirmController.class);

    @Autowired
    private TPrdSundryConfirmService tPrdSundryConfirmService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TPrdSundryConfirmSearchDTO searchDTO) {
        final String methodName = "MWeightRulesController:getList";

        Pages<TPrdSundryConfirmDTO> pages = tPrdSundryConfirmService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 杂货确认
     *
     * @param id
     * @return
     */
    @PutMapping("/confirm")
    public Map<String, Object> confirm(@NotNull(message = "磅单id不能为空") Long id) {
        tPrdSundryConfirmService.confirm(id);
        return Response.SUCCESS.newBuilder().out("确认成功").toResult();
    }
    /**
     * 杂货撤销确认
     *
     * @param id
     * @return
     */
    @PutMapping("/revokeConfirm")
    public Map<String, Object> revokeConfirm(@NotNull(message = "磅单id不能为空") Long id) {
        tPrdSundryConfirmService.revokeConfirm(id);
        return Response.SUCCESS.newBuilder().out("撤销确认成功").toResult();
    }
}
