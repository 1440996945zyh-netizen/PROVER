package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.service.MTugService;
import com.yy.ppm.master.bean.dto.MTugDTO;
import com.yy.ppm.master.bean.dto.MTugSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName 拖轮资料(MTug)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 14:20:00
 */
@RestController
@RequestMapping("/api/v1/internal/mTug")
public class MTugController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(MTugController.class);

    @Autowired
    private MTugService mTugService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('master:tug:query')")
    public Map<String, Object> getList(MTugSearchDTO searchDTO) {
    	final String methodName = "MTugController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MTugDTO> pages = mTugService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('master:tug:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "MTugController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        MTugDTO result = mTugService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param mTugDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('master:tug:add')")
    public Map<String, Object> add(@RequestBody MTugDTO mTugDTO) {
        final String methodName = "MTugController:add";
		LOGGER.enter(methodName + "[start]", "mTugDTO:" +  mTugDTO);

        boolean flag = mTugService.doSave(mTugDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param mTugDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:tug:update')")
    public Map<String, Object> update(@RequestBody MTugDTO mTugDTO) {
        final String methodName = "MTugController:update";
		LOGGER.enter(methodName + "[start]", "mTugDTO:" +  mTugDTO);

        boolean flag = mTugService.doSave(mTugDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('master:tug:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "MTugController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mTugService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

