package com.yy.ppm.storage.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.produce.service.TPrdWorkPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划表(TPrdWorkPlan)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@RestController
@RequestMapping("/api/v1/internal/houseYardDispatch")
public class HouseYardDispatchController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(HouseYardDispatchController.class);

    @Autowired
    private TPrdWorkPlanService tPrdWorkPlanService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('storage:houseYardDispatch:query')")
    public Map<String, Object> getList(TPrdWorkPlanSearchDTO searchDTO) {
    	final String methodName = "HouseYardDispatchController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<TPrdWorkPlanDTO> pages = tPrdWorkPlanService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 场地安排
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PutMapping("/updateMass")
    @PreAuthorize("hasAuthority('storage:houseYardDispatch:yardDispatch')")
    public Map<String, Object> updateMass(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "HouseYardDispatchController:updateMass";
        LOGGER.enter(methodName + "[start]", "tPrdWorkPlanDTO:" +  tPrdWorkPlanDTO);

        boolean flag = tPrdWorkPlanService.updateDispatch(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "场地安排成功" : "场地安排失败").toResult();
    }

    /**
     * 理货员派工
     * @param tPrdWorkPlanDTO
     * @return
     */
    @PutMapping("/updateTally")
    @PreAuthorize("hasAuthority('storage:houseYardDispatch:userDispatch')")
    public Map<String, Object> updateTally(@RequestBody TPrdWorkPlanDTO tPrdWorkPlanDTO) {
        final String methodName = "HouseYardDispatchController:updateTally";
        LOGGER.enter(methodName + "[start]", "list:" +  tPrdWorkPlanDTO);

        boolean flag = tPrdWorkPlanService.updateDispatch(tPrdWorkPlanDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }
}

