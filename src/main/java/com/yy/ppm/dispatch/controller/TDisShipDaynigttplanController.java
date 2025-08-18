package com.yy.ppm.dispatch.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.dispatch.bean.dto.TDisShipDaynigttplanDTO;
import com.yy.ppm.dispatch.service.TDisShipDaynigttplanService;

/**
 * @ClassName 船舶昼夜计划(TDisShipDaynigttplan)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月17日 10:31:00
 */
@RestController
@RequestMapping("/api/v1/internal/tDisShipDaynigttplan")
public class TDisShipDaynigttplanController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TDisShipDaynigttplanController.class);

    @Autowired
    private TDisShipDaynigttplanService tDisShipDaynigttplanService;

    /**
     * 按日期查询昼夜计划详情
     * @param planDate
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('dispatch:dayNightPlan:query')")
    public Map<String, Object> getList(String planDate) {
//        final String methodName = "TDisShipDaynigttplanController:getList";
//		LOGGER.enter(methodName + "调度昼夜计划查询 [start]", "date:" + planDate);

        List<TDisShipDaynigttplanDTO> result = tDisShipDaynigttplanService.getList(planDate);

//        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 保存
     * @param list 计划内容
     * @param planDate 计划日期
     * @return
     */
    @PostMapping("/doSave")
    @PreAuthorize("hasAuthority('dispatch:dayNightPlan:save')")
    public Map<String, Object> doSave(@RequestBody List<TDisShipDaynigttplanDTO> list, String planDate) {
//        final String methodName = "TDisShipDaynigttplanController:doSave";
//		LOGGER.enter(methodName + " 调度昼夜计划保存[start]", "planDate:" +  planDate);

        boolean flag = tDisShipDaynigttplanService.doSave(list, planDate);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "保存成功" : "保存失败").toResult();

    }

    /**
     * 删除
     * @param planDate
     * @return
     */
    @DeleteMapping("/delete/{planDate}")
    @PreAuthorize("hasAuthority('dispatch:dayNightPlan:delete')")
    public Map<String, Object> deleteByPlanDate(@PathVariable("planDate") String planDate) {
//        final String methodName = "TDisShipDaynigttplanController:deleteByPlanDate";
//		LOGGER.enter(methodName + " 调度昼夜计划删除 [start]", "id:" + planDate);

        boolean flag = tDisShipDaynigttplanService.deleteByPlanDate(planDate);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 通过子表id查询到作业量
     * @param shipvoyageItemId
     * @return
     */
    @GetMapping("/getWorkNum")
    @PreAuthorize("hasAuthority('dispatch:dayNightPlan:query')")
    public Map<String, Object> getWorkNum(@RequestParam Long shipvoyageItemId,@RequestParam String planDate){
//        final String methodName = "TDisShipDaynigttplanController:getWorkNum";

        Long workNum = tDisShipDaynigttplanService.getWorkNum(shipvoyageItemId,planDate);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(workNum);
    }
    /**
     * 按日期查询昼夜计划详情
     * @param planDate
     * @return
     */
    @GetMapping("/getList2")
    @PreAuthorize("hasAuthority('dispatch:dayNightPlan:query')")
    public Map<String, Object> getList2(String planDate) {
//        final String methodName = "TDisShipDaynigttplanController:getList";
//        LOGGER.enter(methodName + "调度昼夜计划查询 [start]", "date:" + planDate);

        List<TDisShipDaynigttplanDTO> result = tDisShipDaynigttplanService.getList2(planDate);

//        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 查询从预报到离泊的船
     * @return
     */
    @GetMapping("/getShipVoyage")
    public Map<String, Object> getShipVoyage() {
//        final String methodName = "TDisShipDaynigttplanController:getShipVoyage";
//        LOGGER.enter(methodName + "[start]");

        List<Map<String, String>> result = tDisShipDaynigttplanService.getShipVoyage();

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().toResult(result);
    }

}

