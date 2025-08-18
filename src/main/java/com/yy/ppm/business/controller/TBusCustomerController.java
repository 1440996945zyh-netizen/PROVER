package com.yy.ppm.business.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.business.service.TBusCustomerService;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerSearchDTO;

import com.yy.ppm.master.bean.dto.MShipDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 合同表(TBusCustomer)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 13:09:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusCustomer")
public class TBusCustomerController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusCustomerController.class);

    @Autowired
    private TBusCustomerService tBusCustomerService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> getList(TBusCustomerSearchDTO searchDTO) {
    	final String methodName = "TBusCustomerController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusCustomerDTO> pages = tBusCustomerService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "TBusCustomerController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusCustomerDTO result = tBusCustomerService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tBusCustomerDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:customer:add')")
    public Map<String, Object> add(@RequestBody TBusCustomerDTO tBusCustomerDTO) {
        final String methodName = "TBusCustomerController:add";
		LOGGER.enter(methodName + "[start]", "tBusCustomerDTO:" +  tBusCustomerDTO);

        boolean flag = tBusCustomerService.doSave(tBusCustomerDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 授信/变更授信状态接口
     * @param tBusCustomerDTO
     * @return
     */
    @PostMapping("/doCredit")
    @PreAuthorize("hasAuthority('business:customer:doCredit')")
    public Map<String, Object> doCredit(@RequestBody TBusCustomerDTO tBusCustomerDTO) {
        final String methodName = "TBusCustomerController:add";
		LOGGER.enter(methodName + "[start]", "tBusCustomerDTO:" +  tBusCustomerDTO);

        boolean flag = tBusCustomerService.doCredit(tBusCustomerDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "操作成功" : "操作失败").toResult();

    }

    /**
     * 修改
     * @param tBusCustomerDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:customer:update')")
    public Map<String, Object> update(@RequestBody TBusCustomerDTO tBusCustomerDTO) {
        final String methodName = "TBusCustomerController:update";
		LOGGER.enter(methodName + "[start]", "tBusCustomerDTO:" +  tBusCustomerDTO);

        boolean flag = tBusCustomerService.doSave(tBusCustomerDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 修改
     * @param tBusCustomerDTO
     * @return
     */
    @PutMapping("/reject")
    @PreAuthorize("hasAuthority('master:customer:reject')")
    public Map<String, Object> reject(@RequestBody TBusCustomerDTO tBusCustomerDTO) {
        final String methodName = "MShipController:reject";
        LOGGER.enter(methodName + "[start]", "tBusCustomerDTO:" +  tBusCustomerDTO);

        boolean flag = tBusCustomerService.doReject(tBusCustomerDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "驳回成功" : "驳回失败").toResult();
    }


    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('business:customer:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TBusCustomerController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusCustomerService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }



/*
    @PreAuthorize("hasAuthority('business:customer:delete')")
*/
    @GetMapping("publishmisc/{id}")
    public Map<String, Object> approveByIds(@PathVariable("id") Long id) {
        final String methodName = "TBusCustomerController:approveByIds";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusCustomerService.approveById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "审核成功" : "审核失败").toResult();
    }

/*
    @PreAuthorize("hasAuthority('business:customer:delete')")
*/
    @GetMapping("revokeMisc/{id}")
    public Map<String, Object> cancelById(@PathVariable("id") Long id) {
        final String methodName = "TBusCustomerController:cancelById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusCustomerService.cancelById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "消审成功" : "消审失败").toResult();
    }

    /**
     * 获取客户数据（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getData")
    @PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> getData(TBusCustomerSearchDTO searchDTO) {
        final String methodName = "TBusCustomerController:getData";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult();
    }
    /**
     * 批量同步
     * @param list
     * @return
     */
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('business:customer:add')")
    public Map<String, Object> syncData(@RequestBody List<TBusCustomerDTO> list) {
        final String methodName = "TBusCustomerController:update";
        LOGGER.enter(methodName + "[start]", "list:" +  list);

        boolean flag = tBusCustomerService.sync(list);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "同步成功" : "同步失败").toResult();
    }


}

