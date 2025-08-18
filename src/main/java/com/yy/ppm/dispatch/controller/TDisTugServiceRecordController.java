package com.yy.ppm.dispatch.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.dispatch.bean.dto.TDisCloseSailSearchDTO;
import com.yy.ppm.dispatch.service.TDisTugServiceRecordService;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordDTO;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName 拖轮服务记录(TDisTugServiceRecord)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@RestController
@RequestMapping("/api/v1/internal/tDisTugServiceRecord")
public class TDisTugServiceRecordController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TDisTugServiceRecordController.class);

    @Autowired
    private TDisTugServiceRecordService tDisTugServiceRecordService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('dispatch:tugServiceRecord:query')")
    public Map<String, Object> getList(TDisTugServiceRecordSearchDTO searchDTO) {
//    	final String methodName = "TDisTugServiceRecordController:getList";
//		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TDisTugServiceRecordDTO> pages = tDisTugServiceRecordService.getList(searchDTO);
        
//        LOGGER.exit( methodName + "result:" + pages);
        
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    
    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('dispatch:tugServiceRecord:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
//        final String methodName = "TDisTugServiceRecordController:getDetail";
//		LOGGER.enter(methodName + "[start]", "id:" + id);
    
        TDisTugServiceRecordDTO result = tDisTugServiceRecordService.getDetail(id);
        
//        LOGGER.exit( methodName + "result:" + result);
        
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tDisTugServiceRecordDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('dispatch:tugServiceRecord:add')")
    public Map<String, Object> add(@RequestBody TDisTugServiceRecordDTO tDisTugServiceRecordDTO) {
//        final String methodName = "TDisTugServiceRecordController:add";
//		LOGGER.enter(methodName + "[start]", "tDisTugServiceRecordDTO:" +  tDisTugServiceRecordDTO);
		
        boolean flag = tDisTugServiceRecordService.doSave(tDisTugServiceRecordDTO);
        
//        LOGGER.exit(methodName);
        		
        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
  
    }
    
    /**
     * 修改
     * @param tDisTugServiceRecordDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('dispatch:tugServiceRecord:update')")
    public Map<String, Object> update(@RequestBody TDisTugServiceRecordDTO tDisTugServiceRecordDTO) {
//        final String methodName = "TDisTugServiceRecordController:update";
//		LOGGER.enter(methodName + "[start]", "tDisTugServiceRecordDTO:" +  tDisTugServiceRecordDTO);
		
        boolean flag = tDisTugServiceRecordService.doSave(tDisTugServiceRecordDTO);
        
//        LOGGER.exit(methodName);
        
        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('dispatch:tugServiceRecord:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
//        final String methodName = "TDisTugServiceRecordController:deleteById";
//		LOGGER.enter(methodName + "[start]", "id:" + id);
    
        boolean flag = tDisTugServiceRecordService.deleteById(id);
        
//        LOGGER.exit(methodName);
                
        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 导出
     *
     * @param searchDTO
     * @param response
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('dispatch:tugServiceRecord:query')")
    public void export(TDisTugServiceRecordSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "拖轮服务记录");
        try {
            byte[] bytes = tDisTugServiceRecordService.export(searchDTO);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

}

