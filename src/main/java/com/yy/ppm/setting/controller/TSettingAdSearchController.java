package com.yy.ppm.setting.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.setting.bean.dto.TSettingAdSearchDTO;
import com.yy.ppm.setting.bean.dto.TSettingAdSearchSearchDTO;
import com.yy.ppm.setting.service.TSettingAdSearchService;
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
     * @param menuId,tableId
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("menuId") Long menuId,@RequestParam("tableId") String tableId) {
        final String methodName = "TSettingAdSearchController:getDetail";

        TSettingAdSearchDTO result = tSettingAdSearchService.getDetail(menuId,tableId);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param dto
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TSettingAdSearchDTO dto) {
        final String methodName = "TSettingAdSearchController:add";
		LOGGER.enter(methodName + "[start]", "tSettingAdSearchDTO:" +  dto);

        boolean flag = tSettingAdSearchService.doSave(dto);

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

}

