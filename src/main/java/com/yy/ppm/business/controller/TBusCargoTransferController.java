package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.service.TBusCargoInfoService;
import com.yy.ppm.business.service.TBusCargoTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 票货信息表(TBusCargoInfo)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 18:47:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusCargoTransfer")
public class TBusCargoTransferController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusCargoTransferController.class);

    @Autowired
    private TBusCargoTransferService tBusCargoTransferService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('business:cargoTransfer:query')")
    public Map<String, Object> getList(TBusCargoTransferSearchDTO searchDTO) {
    	final String methodName = "TBusCargoTransferController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusCargoTransferDTO> pages = tBusCargoTransferService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getStorageList")
    public Map<String, Object> getStorageList(TBusCargoTransferSearchDTO searchDTO) {
        final String methodName = "TBusCargoTransferController:getStorageList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<TBusCargoStorageDTO> list = tBusCargoTransferService.getStorageList(searchDTO.getCargoInfoIdSource());

        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('business:cargoTransfer:query')")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "TBusCargoTransferController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusCargoTransferDTO result = tBusCargoTransferService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建货转
     * @param tBusCargoTransferDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:cargoTransfer:add')")
    public Map<String, Object> addTransfer(@RequestBody TBusCargoTransferDTO tBusCargoTransferDTO) {
        final String methodName = "TBusCargoTransferController:add";
        LOGGER.enter(methodName + "[start]", "tBusCargoTransferDTO:" +  tBusCargoTransferDTO);

        boolean flag = tBusCargoTransferService.doSave(tBusCargoTransferDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "货转成功" : "货转失败").toResult();

    }

    /**
     * 修改
     * @param tBusCargoTransferDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:cargoTransfer:update')")
    public Map<String, Object> updateTransfer(@RequestBody TBusCargoTransferDTO tBusCargoTransferDTO) {
        final String methodName = "TBusCargoTransferController:update";
        LOGGER.enter(methodName + "[start]", "tBusCargoTransferDTO:" +  tBusCargoTransferDTO);

        boolean flag = tBusCargoTransferService.doSave(tBusCargoTransferDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "货转修改成功" : "货转修改失败").toResult();
    }

    /**
     * 库场审批
     * @param transferDTO
     * @return
     */
    @PutMapping("/yardApprove")
    public Map<String, Object> yardApprove(@RequestBody TBusCargoStorageTransferDTO transferDTO) {
        final String methodName = "TBusCargoTransferController:yardApprove";
        LOGGER.enter(methodName + "[start]", "transferDTO:" +  transferDTO);

        boolean flag = tBusCargoTransferService.yardApprove(transferDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "审批成功" : "审批失败").toResult();
    }

    /**
     * 库场撤销审批
     * @param transferDTO
     * @return
     */
    @PutMapping("/yardCancelApprove")
    public Map<String, Object> yardCancelApprove(@RequestBody TBusCargoTransferDTO transferDTO) {
        final String methodName = "TBusCargoTransferController:yardApprove";
        LOGGER.enter(methodName + "[start]", "transferDTO:" +  transferDTO);

        boolean flag = tBusCargoTransferService.yardCancelApprove(transferDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "货转修改成功" : "货转修改失败").toResult();
    }
    /**
     * 商务撤销审批
     * @param transferDTO
     * @return
     */
    @PutMapping("/cancelApprove")
    public Map<String, Object> cancelApprove(@RequestBody TBusCargoTransferDTO transferDTO) {
        final String methodName = "TBusCargoTransferController:cancelApprove";
        LOGGER.enter(methodName + "[start]", "transferDTO:" +  transferDTO);

        boolean flag = tBusCargoTransferService.cancelApprove(transferDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "撤销成功" : "撤销失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('business:cargoTransfer:delete')")
    public Map<String, Object> deleteTransferById(@PathVariable("id") Long id) {
        final String methodName = "TBusCargoTransferController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusCargoTransferService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 审核
     * @param dto
     * @return
     */
    @PutMapping("/approve")
    @PreAuthorize("hasAuthority('business:cargoTransfer:approve')")
    public Map<String, Object> approve(@RequestBody TBusCargoTransferDTO dto) {
        final String methodName = "TBusCargoTransferController:approve";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);
        boolean flag = tBusCargoTransferService.doApprove(dto);
        return Response.SUCCESS.newBuilder().out(flag ? "success" : "fail").toResult();
    }

}

