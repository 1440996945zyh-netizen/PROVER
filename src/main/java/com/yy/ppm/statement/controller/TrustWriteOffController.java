package com.yy.ppm.statement.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.dto.TBusTrustSearchDTO;
import com.yy.ppm.business.controller.TBusTrustController;
import com.yy.ppm.statement.service.TrustWriteOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 通知单核销
 * @Date 2023-10-11 13:40
 */
@RestController
@RequestMapping("/api/external/trustWriteOff")
@Validated
public class TrustWriteOffController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TBusTrustController.class);

    @Autowired
    private TrustWriteOffService trustWriteOffService;

    /**
     * 获取指令核销列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getWriteOffList")
    public Map<String, Object> getWriteOffList(TBusTrustSearchDTO searchDTO) {
        final String methodName = "TBusTrustController:getWriteOffList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusTrustDTO> pages = trustWriteOffService.getWriteOffList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取核销详情
     *
     * @param trustId
     * @return
     */
    @GetMapping("/getWriteOffById")
    @PreAuthorize("hasAuthority('business:order:verification')")
    public Map<String, Object> getWriteOffById(@NotNull(message = "指令ID不能为空") Long trustId) {
        final String methodName = "TBusTrustController:trustId";
        LOGGER.enter(methodName + "[start]", "trustId:" + trustId);
        Map<String, Object> result = trustWriteOffService.getWriteOffById(trustId);
        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 指令核销
     *
     * @return
     */
    @PostMapping("/writeOff")
    public Map<String, Object> writeOff(
            @NotNull(message = "指令ID不能为空") Long trustId,
            Integer checkNumber,
            @NotNull(message = "核销重量不能为空") BigDecimal checkTon
    ) {
        final String methodName = "TBusTrustController:writeOff";
        LOGGER.enter(methodName + "[start]", "id:" + trustId);
        trustWriteOffService.writeOff(trustId, checkNumber, checkTon);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("核销成功").toResult();
    }

    /**
     * 指令核销
     *
     * @return
     */
    @PostMapping("/cancelWriteOff")
    public Map<String, Object> cancelWriteOff(
            @NotNull(message = "指令ID不能为空") Long trustId
    ) {
        final String methodName = "TBusTrustController:cancelWriteOff";
        LOGGER.enter(methodName + "[start]", "id:" + trustId);
        trustWriteOffService.cancelWriteOff(trustId);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("取消核销成功").toResult();
    }
}
