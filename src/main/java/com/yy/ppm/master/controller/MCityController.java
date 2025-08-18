package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.service.MCityService;
import com.yy.ppm.master.bean.dto.MCityDTO;
import com.yy.ppm.master.bean.dto.MCitySearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName (MCity)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月30日 13:29:00
 */
@RestController
@RequestMapping("/api/v1/internal/mCity")
public class MCityController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(MCityController.class);

    @Autowired
    private MCityService mCityService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MCitySearchDTO searchDTO) {
    	final String methodName = "MCityController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MCityDTO> pages = mCityService.getList(searchDTO);

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
        final String methodName = "MCityController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        MCityDTO result = mCityService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param mCityDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MCityDTO mCityDTO) {
        final String methodName = "MCityController:add";
		LOGGER.enter(methodName + "[start]", "mCityDTO:" +  mCityDTO);

        boolean flag = mCityService.doSave(mCityDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param mCityDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MCityDTO mCityDTO) {
        final String methodName = "MCityController:update";
		LOGGER.enter(methodName + "[start]", "mCityDTO:" +  mCityDTO);

        boolean flag = mCityService.doSave(mCityDTO);

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
        final String methodName = "MCityController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mCityService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

