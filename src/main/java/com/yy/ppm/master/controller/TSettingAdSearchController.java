package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.service.TSettingAdSearchService;
import com.yy.ppm.master.bean.dto.TSettingAdSearchDTO;
import com.yy.ppm.master.bean.dto.TSettingAdSearchSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName 高級查詢配置表(TSettingAdSearch)Controller
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年09月25日 15:35:00
 */
@RestController
@RequestMapping("/api/v1/internal/tSettingAdSearch")
public class TSettingAdSearchController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TSettingAdSearchController.class);

    @Autowired
    private TSettingAdSearchService tSettingAdSearchService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TSettingAdSearchSearchDTO searchDTO) {
    	final String methodName = "TSettingAdSearchController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TSettingAdSearchDTO> pages = tSettingAdSearchService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "TSettingAdSearchController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TSettingAdSearchDTO result = tSettingAdSearchService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tSettingAdSearchDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TSettingAdSearchDTO tSettingAdSearchDTO) {
        final String methodName = "TSettingAdSearchController:add";
		LOGGER.enter(methodName + "[start]", "tSettingAdSearchDTO:" +  tSettingAdSearchDTO);

        boolean flag = tSettingAdSearchService.doSave(tSettingAdSearchDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param tSettingAdSearchDTO
     * @return
     */
    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody TSettingAdSearchDTO tSettingAdSearchDTO) {
        final String methodName = "TSettingAdSearchController:update";
		LOGGER.enter(methodName + "[start]", "tSettingAdSearchDTO:" +  tSettingAdSearchDTO);

        boolean flag = tSettingAdSearchService.doSave(tSettingAdSearchDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteById(@RequestParam("id") Long id) {
        final String methodName = "TSettingAdSearchController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tSettingAdSearchService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

