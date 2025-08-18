package com.yy.ppm.gis.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import com.yy.ppm.gis.dto.route.TRoutesDTO;
import com.yy.ppm.gis.po.TKeypointsPO;
import com.yy.ppm.gis.po.TRoutesPO;
import com.yy.ppm.gis.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 路线信息Controller
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/external/route")
public class RouteController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(RouteController.class);

    private final RouteService routeService;
    /**
     * 关键点录入
     */
    @PostMapping("/addKeyPoint")
    public Map<String,Object> addKeyPoint(@RequestBody @Validated(AddGroup.class) TKeypointsPO point, BindingResult result) {

        final String methodName = "addKeyPoint";
        LOGGER.enter(methodName, "关键点录入, point: " + point);

        if(result.hasErrors()){
            StringBuffer buffer = new StringBuffer();
            result.getAllErrors().forEach(error->{
                buffer.append(error.getDefaultMessage()).append(";");
            });
            Response.FAIL.newBuilder().out(buffer.toString());
        }

        routeService.addKeyPoint(point);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 关键点查询
     */
    @GetMapping("/listpoint")
    public Map<String,Object> listPoint(Long id) {
        final String methodName = "/listpoint";
        LOGGER.enter(methodName, "关键点查询, id: " + id);
        List<TKeypointsPO> list = routeService.listPoints(id);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 编辑关键点信息
     * */
    @PutMapping("/editKeyPoint")
    public Map<String,Object> editKeyPoint(@RequestBody @Validated(EditGroup.class) TKeypointsPO point) {

        final String methodName = "/editKeyPoint";
        LOGGER.enter(methodName, "编辑关键点信息, point: " + point);
        routeService.editKeyPoint(point);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 删除关键点信息
     * */
    @DeleteMapping("/deleteKeyPoint/{id}")
    public Map<String,Object> deleteKeyPoint(@PathVariable Long id) {

        final String methodName = "/editKeyPoint";
        LOGGER.enter(methodName, "删除关键点信息, id: " + id);
        routeService.deleteKeyPoint(id);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 路线信息录入
     */
    @PostMapping("/addRouteInfo")
    public Map<String,Object> addRouteInfo(@RequestBody @Validated(AddGroup.class) TRoutesPO route) {

        final String methodName = "addRouteInfo";
        LOGGER.enter(methodName, "路线信息录入, route: " + route);

        routeService.addRouteInfo(route);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 路线信息查询
     */
    @GetMapping("/getAllRouteInfo")
    public Map<String,Object> getAllRouteInfo(Long id) {

        final String methodName = "getAllRouteInfo";
        LOGGER.enter(methodName, "路线信息查询, id: " + id);
        List<TRoutesDTO> list = routeService.getAllRouteInfo(id);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 路线信息修改
     */
    @PutMapping("/editRouteInfo")
    public Map<String,Object> editRouteInfo(@RequestBody @Validated(EditGroup.class) TRoutesPO route) {

        final String methodName = "editRouteInfo";
        LOGGER.enter(methodName, "editRouteInfo, route: " + route);
        routeService.editRouteInfo(route);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();

    }

    /**
     * 路线信息删除
     */
    @DeleteMapping("/deleteRouteInfo/{id}")
    public Map<String,Object> deleteRouteInfo(@PathVariable Long id) {

        final String methodName = "deleteRouteInfo";
        LOGGER.enter(methodName, "deleteRouteInfo, id: " + id);
        routeService.deleteRouteInfo(id);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 左右路径生成
     */
    @GetMapping("/generateNavigationRoute")
    public Map<String,Object> generateNavigationRoute() {
        final String methodName = "generateNavigationRoute";
        LOGGER.enter(methodName, "generateNavigationRoute");
        routeService.generateNavigationRoute();
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("生成成功").toResult();

    }
}
