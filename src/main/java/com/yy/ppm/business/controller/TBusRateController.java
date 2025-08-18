package com.yy.ppm.business.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.service.TBusRateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 费率(TBusRate)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 16:48:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusRate")
public class TBusRateController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusRateController.class);

    @Autowired
    private TBusRateService tBusRateService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('business:rate:query')")
    public Map<String, Object> getList(TBusRateSearchDTO searchDTO) {
    	final String methodName = "TBusRateController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusRateDTO> pages = tBusRateService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取货物包干费列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getListCargo")
    @PreAuthorize("hasAuthority('business:lumpSum:query')")
    public Map<String, Object> getListCargo(TBusRateSearchDTO searchDTO) {
        final String methodName = "TBusRateController:getListCargo";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusRateDTO> pages = tBusRateService.getListCargo(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询服务内容
     *
     * @param busServiceSearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getListService")
    @PreAuthorize("hasAuthority('master:serviceContent:query')")
    public Map<String, Object> getListService(BusServiceSearchDTO busServiceSearchDTO) {
        final String methodName = "getListService";
        LOGGER.enter("BusServiceController:" + methodName + "[start]", "sysUserSearchDTO:" + busServiceSearchDTO);
        List<TBusServiceDTO> busServiceList = tBusRateService.getListService(busServiceSearchDTO);
        LOGGER.exit("BusServiceController:" + methodName + "result:" + busServiceList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(busServiceList);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('business:rate:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TBusRateController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusRateDTO result = tBusRateService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询单条记录(货物包干费)
     * @param tBusRateSearchDTO
     * @return
     */
    @PostMapping("/getDetailCargo")
    @PreAuthorize("hasAuthority('business:lumpSum:query')")
    public Map<String, Object> getDetailCargo(@RequestBody TBusRateSearchDTO tBusRateSearchDTO) {
        final String methodName = "TBusRateController:getDetailCargo";
        LOGGER.enter(methodName + "[start]", "tBusRateSearchDTO:" + tBusRateSearchDTO);
        List<TBusRateDTO> result = tBusRateService.getDetailCargo(tBusRateSearchDTO);
        LOGGER.exit( methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tBusRateDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:rate:add')")
    public Map<String, Object> add(@RequestBody TBusRateDTO tBusRateDTO) {
        final String methodName = "TBusRateController:add";
		LOGGER.enter(methodName + "[start]", "tBusRateDTO:" +  tBusRateDTO);

        boolean flag = tBusRateService.doSave(tBusRateDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 新建(货物包干费)
     * @param list
     * @return
     */
    @PostMapping("/addCargo")
    @PreAuthorize("hasAuthority('business:lumpSum:add')")
    public Map<String, Object> addCargoFei(@RequestBody List<TBusRateDTO> list) {
        final String methodName = "TBusRateController:addCargoFei";
        LOGGER.enter(methodName + "[start]", "tBusRateDTO:" +  list);
        tBusRateService.doSaveCargo(list);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();

    }


    /**
     * 修改(货物包干费)
     * @param dto
     * @return
     */
    @PutMapping("/updateCargo")
    @PreAuthorize("hasAuthority('business:lumpSum:update')")
    public Map<String, Object> updateCargo(@RequestBody TBusRateUpdateDTO dto) {
        final String methodName = "TBusRateController:addCargoFei";
        LOGGER.enter(methodName + "[start]", "tBusRateDTO:" +  dto);
        tBusRateService.updateCargo(dto);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();

    }

    /**
     * 修改
     * @param tBusRateDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:rate:update')")
    public Map<String, Object> update(@RequestBody TBusRateDTO tBusRateDTO) {
        final String methodName = "TBusRateController:update";
		LOGGER.enter(methodName + "[start]", "tBusRateDTO:" +  tBusRateDTO);

        boolean flag = tBusRateService.doSave(tBusRateDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('business:rate:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TBusRateController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusRateService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 审核
     * @param id
     * @return
     */
    @GetMapping("/busRatePass/{id}")
    @PreAuthorize("hasAuthority('business:rate:pass')")
    public Map<String, Object> busRatePass(@PathVariable("id") Long id) {
        final String methodName = "TBusRateController:busRatePass";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean result = tBusRateService.busRatePass(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out(result ? "删除成功" : "删除失败").toResult();
    }
    /**
     * 消审
     * @param id
     * @return
     */
    @GetMapping("/busRateCancle/{id}")
    @PreAuthorize("hasAuthority('business:rate:pass')")
    public Map<String, Object> busRateCancle(@PathVariable("id") Long id) {
        final String methodName = "TBusRateController:busRateCancle";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean result = tBusRateService.busRateCancle(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out(result ? "删除成功" : "删除失败").toResult();
    }



    /**
     * 审核(货物包干费)
     * @param tBusRateDTO
     * @return
     */
    @PostMapping("/busRatePassCargo")
    @PreAuthorize("hasAuthority('business:lumpSum:pass')")
    public Map<String, Object> busRatePassCargo(@RequestBody TBusRateDTO tBusRateDTO) {
        final String methodName = "TBusRateController:busRatePass";
        LOGGER.enter(methodName + "[start]", "tBusRateDTO:" + tBusRateDTO);
        tBusRateService.busRatePassCargo(tBusRateDTO);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 撤销审核(货物包干费)
     * @param tBusRateDTO
     * @return
     */
    @PostMapping("/busRateRevokeCargo")
    @PreAuthorize("hasAuthority('business:lumpSum:revoke')")
    public Map<String, Object> busRateRevokeCargo(@RequestBody TBusRateDTO tBusRateDTO) {
        final String methodName = "TBusRateController:busRatePass";
        LOGGER.enter(methodName + "[start]", "tBusRateDTO:" + tBusRateDTO);
        tBusRateService.busRateRevokeCargo(tBusRateDTO);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 删除(货物包干费)
     * @param tBusRateDTO
     * @return
     */
    @PostMapping("/delRateCargo")
    @PreAuthorize("hasAuthority('business:lumpSum:delete')")
    public Map<String, Object> delRateCargo(@RequestBody TBusRateDTO tBusRateDTO) {
        final String methodName = "TBusRateController:busRatePass";
        LOGGER.enter(methodName + "[start]", "tBusRateDTO:" + tBusRateDTO);
        tBusRateService.delRateCargo(tBusRateDTO);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

}

