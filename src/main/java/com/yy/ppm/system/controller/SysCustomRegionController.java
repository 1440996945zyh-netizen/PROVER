package com.yy.ppm.system.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.system.bean.dto.SysCustomRegionDTO;
import com.yy.ppm.system.bean.dto.SysMenuDTO;
import com.yy.ppm.system.service.SysCustomRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName (SysCustomRegion)Controller
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月02日 11:14:00
 */
@RestController
@RequestMapping("/api/v1/internal/sysCustomRegion")
public class SysCustomRegionController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(SysCustomRegionController.class);


    private final SysCustomRegionService sysCustomRegionService;

    public SysCustomRegionController(SysCustomRegionService sysCustomRegionService){
        this.sysCustomRegionService = sysCustomRegionService;
    }

    /**
     * 获取列表（翻页）
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList() {
    	final String methodName = "SysCustomRegionController:getList";
		LOGGER.enter(methodName + "[start]", "");

        List<SysCustomRegionDTO> pages = sysCustomRegionService.getList();

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取列表（翻页）
     * @return
     */
    @GetMapping("/getListApp")
    public Map<String, Object> getListApp() {
        List<SysMenuDTO> pages = sysCustomRegionService.getListApp();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "SysCustomRegionController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        SysCustomRegionDTO result = sysCustomRegionService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param sysCustomRegionDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody SysCustomRegionDTO sysCustomRegionDTO) {
        final String methodName = "SysCustomRegionController:add";
		LOGGER.enter(methodName + "[start]", "sysCustomRegionDTO:" +  sysCustomRegionDTO);

        boolean flag = sysCustomRegionService.doSave(sysCustomRegionDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "收藏成功~" : "收藏失败").toResult();

    }
    /**
     * 新建App快捷菜单
     * @param sysCustomRegionDTO
     * @return
     */
    @PostMapping("/saveRegion")
    public Map<String, Object> addApp(@RequestBody SysCustomRegionDTO sysCustomRegionDTO) {

        boolean flag = sysCustomRegionService.doAppSave(sysCustomRegionDTO);

        return Response.SUCCESS.newBuilder().out(flag ? "收藏成功~" : "收藏失败").toResult();

    }

    /**
     * 删除app快捷菜单
     * @param
     * @return
     */
    @PostMapping("/delRegion")
    public Map<String, Object> delRegion(@RequestBody SysCustomRegionDTO sysCustomRegionDTO) {

        boolean flag = sysCustomRegionService.delAppRegion(sysCustomRegionDTO);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }
    /**
     * 新建
     * @param list
     * @return
     */
    @PostMapping("/batchAdd")
    public Map<String, Object> batchAdd(@RequestBody List<SysCustomRegionDTO> list) {
        final String methodName = "SysCustomRegionController:doBatchInsert";
        LOGGER.enter(methodName + "[start]", "list:" +  list);

        boolean flag = sysCustomRegionService.doBatchInsert(list);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }



    /**
     * 修改
     * @param sysCustomRegionDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody SysCustomRegionDTO sysCustomRegionDTO) {
        final String methodName = "SysCustomRegionController:update";
		LOGGER.enter(methodName + "[start]", "sysCustomRegionDTO:" +  sysCustomRegionDTO);

        boolean flag = sysCustomRegionService.doSave(sysCustomRegionDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "SysCustomRegionController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sysCustomRegionService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

