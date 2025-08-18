package com.yy.ppm.runpile.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.machine.bean.dto.TMacTerminalStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.TRunPilePortStorageDetailDTO;
import com.yy.ppm.runpile.bean.dto.TStorageYardDTO;
import com.yy.ppm.runpile.service.TRunPileService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 跑垛程序
 * @author zcc
 * @Date 2023/09/26
 */
@RestController
@RequestMapping("/api/v1/external/tRunPile")
@Validated
@Tag(name = "跑垛.用户管理")
public class TRunPileController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TRunPileController.class);

    @Autowired
    private TRunPileService tRunPileService;

    /**
     * 查询库场信息列表
     * @param storageYardLevel
     * @param parentId
     * @return
     */
    @GetMapping("/getStorageYardList")
    @Validated
    public Map<String, Object> getStorageYardList(@Valid @NotBlank(message = "storageYardLevel不能为空！") String storageYardLevel,
    		String parentId) {
        final String methodName = "TRunPileController:getStorageYardList";
        LOGGER.enter(methodName + "[start]", "storageYardLevel:" + storageYardLevel + ", parentId:" + parentId);

        List<TStorageYardDTO> resultList = tRunPileService.getStorageYardList(storageYardLevel, parentId);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }


    /**
     * 查询货垛点位列表
     * @return
     */
    @GetMapping("/getStackPositionList")
    @Validated
    public Map<String, Object> getStackPositionList() {
        final String methodName = "TRunPileController:getStackPositionList";
        LOGGER.enter(methodName + "[start]");

        List<TMacTerminalStackPositionDTO> resultList = tRunPileService.getStackPositionList();

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 保存跑垛信息
     * @param storageStackPositionDTO
     * @return
     */
    @PostMapping("/saveStackPosition")
    @Validated
    public Map<String, Object> saveStackPosition(@RequestBody MStorageStackPositionDTO storageStackPositionDTO) {
        final String methodName = "TRunPileController:saveStackPosition";
        LOGGER.enter(methodName + "[start]", "storageStackPositionDTO:" + storageStackPositionDTO);

        int count = tRunPileService.saveStackPosition(storageStackPositionDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult(count);
    }

    /**
     * 删除跑垛信息
     * @param storageStackPositionDTO
     * @return
     */
    @PostMapping("/deleteStackPosition")
    @Validated
    public Map<String, Object> deleteStackPosition(@RequestBody MStorageStackPositionDTO storageStackPositionDTO) {
        final String methodName = "TRunPileController:deleteStackPosition";
        LOGGER.enter(methodName + "[start]", "storageStackPositionDTO:" + storageStackPositionDTO);

        int count = tRunPileService.deleteStackPosition(storageStackPositionDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }

    /**
     * 查询待跑垛信息列表
     * @return
     */
    @GetMapping("/getRunPileNeedList")
    @Validated
    public Map<String, Object> getRunPileNeedList(Long massId, String runPileState) {

        List<TRunPilePortStorageDetailDTO> resultList = tRunPileService.getRunPileNeedList(massId, runPileState);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }


}
