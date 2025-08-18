package com.yy.ppm.dispatch.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.common.enums.OperateTypeEnum;

import com.yy.ppm.dispatch.service.MSjsbLogService;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogDTO;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName 数据上报日志表(MSjsbLog)Controller
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2025年05月19日 16:45:00
 */
@RestController
@RequestMapping("/api/v1/internal/mSjsbLog")
public class MSjsbLogController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(MSjsbLogController.class);

    @Autowired
    private MSjsbLogService mSjsbLogService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
	@Log(title = "查询(MSjsbLogController)", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getList(MSjsbLogSearchDTO searchDTO) {
    	final String methodName = "MSjsbLogController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MSjsbLogDTO> pages = mSjsbLogService.getList(searchDTO);
        
        LOGGER.exit( methodName + "result:" + pages);
        
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    
    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
	@Log(title = "详情查询(MSjsbLogController)", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "MSjsbLogController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);
    
        MSjsbLogDTO result = mSjsbLogService.getDetail(id);
        
        LOGGER.exit( methodName + "result:" + result);
        
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param mSjsbLogDTO
     * @return
     */
    @PostMapping("/add")
	@Log(title = "新增(MSjsbLogController)", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody MSjsbLogDTO mSjsbLogDTO) {
        final String methodName = "MSjsbLogController:add";
		LOGGER.enter(methodName + "[start]", "mSjsbLogDTO:" +  mSjsbLogDTO);
		
        boolean flag = mSjsbLogService.doSave(mSjsbLogDTO);
        
        LOGGER.exit(methodName);
        		
        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
  
    }
    
    /**
     * 修改
     * @param mSjsbLogDTO
     * @return
     */
    @PutMapping("/update")
	@Log(title = "修改(MSjsbLogController)", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody MSjsbLogDTO mSjsbLogDTO) {
        final String methodName = "MSjsbLogController:update";
		LOGGER.enter(methodName + "[start]", "mSjsbLogDTO:" +  mSjsbLogDTO);
		
        boolean flag = mSjsbLogService.doSave(mSjsbLogDTO);
        
        LOGGER.exit(methodName);
        
        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
	@Log(title = "删除(MSjsbLogController)", value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "MSjsbLogController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);
    
        boolean flag = mSjsbLogService.deleteById(id);
        
        LOGGER.exit(methodName);
                
        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}
