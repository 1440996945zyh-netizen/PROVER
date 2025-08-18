package com.yy.ppm.runpile.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionSearchDTO;
import com.yy.ppm.runpile.service.TRunPileHistoryService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 跑垛历史
 * @author zcc
 * @Date 2023/12/28
 */
@RestController
@RequestMapping("/api/v1/external/tRunPileHistory")
@Validated
@Tag(name = "跑垛历史")
public class TRunPileHistoryController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TRunPileHistoryController.class);

    @Autowired
    private TRunPileHistoryService tRunPileHistoryService;

    /**
     * 查询跑垛历史
     * @param stackId
     * @return
     */
    @GetMapping("/getRunPileHistoryList")
    @Validated
    public Map<String, Object> getRunPileHistoryList(MStorageStackPositionSearchDTO mStorageStackPositionSearchDTO) {
        final String methodName = "TRunPileHistoryController:getRunPileHistoryList";
        LOGGER.enter(methodName + "[start]", "mStorageStackPositionSearchDTO:" + mStorageStackPositionSearchDTO);

        Pages<MStorageStackPositionDTO> list = tRunPileHistoryService.getRunPileHistoryList(mStorageStackPositionSearchDTO);
        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询跑垛历史
     * @param stackId
     * @return
     */
    @GetMapping("/getList")
    @Validated
    public Map<String, Object> getList(@Valid @NotBlank(message = "stackId不能为空！") String stackId) {
        final String methodName = "TRunPileHistoryController:getList";
        LOGGER.enter(methodName + "[start]", "stackId:" + stackId);

        List<MStorageStackPositionDTO> list = tRunPileHistoryService.getList(stackId);
        LOGGER.exit( methodName + "result:" + list);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }
}
