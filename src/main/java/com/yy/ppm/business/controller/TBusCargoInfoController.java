package com.yy.ppm.business.controller;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.enums.Response;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.cargoInfo.CleanAllPortStorageDTO;
import com.yy.ppm.business.service.TBusCargoInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
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
@RequestMapping("/api/v1/internal/tBusCargoInfo")
public class TBusCargoInfoController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusCargoInfoController.class);

    @Autowired
    private TBusCargoInfoService tBusCargoInfoService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('business:cargoInfo:query')")
    public Map<String, Object> getList(TBusCargoInfoSearchDTO searchDTO) {
    	final String methodName = "TBusCargoInfoController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusCargoInfoDTO> pages = tBusCargoInfoService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 导出
     *
     * @param searchDTO
     * @param response
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('business:cargoInfo:query')")
    public void export(TBusCargoInfoSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "票货信息");
        try {
            byte[] bytes = tBusCargoInfoService.export(searchDTO);
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

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getPoundbillList")
    @PreAuthorize("hasAuthority('business:cargoInfo:query')")
    public Map<String, Object> getPoundbillList(PoundbillSearchDTO searchDTO) {
    	final String methodName = "TBusCargoInfoController:getPoundbillList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);
		if(searchDTO.getDateTime()!=null && searchDTO.getDateTime().size()>0){
            searchDTO.setStartTime(DateUtils.formatDate(searchDTO.getDateTime().get(0),"yyyy-MM-dd HH:mm:ss"));
            searchDTO.setEndTime(DateUtils.formatDate(searchDTO.getDateTime().get(1),"yyyy-MM-dd HH:mm:ss"));
        }
        List<Map<String,Object>> list = tBusCargoInfoService.getPoundbillList(searchDTO);

        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 汇总
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/summary")
    public Map<String, Object> summary(TBusCargoInfoSearchDTO searchDTO) {
        final String methodName = "TBusCargoInfoController:summary";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Map<String, Object> result = tBusCargoInfoService.summary(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('business:cargoInfo:query')")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "TBusCargoInfoController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusCargoInfoDTO result = tBusCargoInfoService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tBusCargoInfoDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:cargoInfo:add')")
    public Map<String, Object> add(@RequestBody TBusCargoInfoDTO tBusCargoInfoDTO) {
        final String methodName = "TBusCargoInfoController:add";
		LOGGER.enter(methodName + "[start]", "tBusCargoInfoDTO:" +  tBusCargoInfoDTO);

        boolean flag = tBusCargoInfoService.doSave(tBusCargoInfoDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param tBusCargoInfoDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:cargoInfo:update')")
    public Map<String, Object> update(@RequestBody TBusCargoInfoDTO tBusCargoInfoDTO) {
        final String methodName = "TBusCargoInfoController:update";
		LOGGER.enter(methodName + "[start]", "tBusCargoInfoDTO:" +  tBusCargoInfoDTO);

        boolean flag = tBusCargoInfoService.doSave(tBusCargoInfoDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('business:cargoInfo:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TBusCargoInfoController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusCargoInfoService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 货权转移列表
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/getTransferList")
    @PreAuthorize("hasAuthority('business:cargoTransfer:query')")
    public Map<String, Object> getList(Long cargoInfoId) {
        final String methodName = "TBusCargoInfoController:getTransferList";
        LOGGER.enter(methodName + "[start]", "cargoInfoId:" + cargoInfoId);

        List<TBusCargoTransferDTO> list = tBusCargoInfoService.getTransferList(cargoInfoId);

        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 完货
     *
     * @param cleanAllPortStorage
     * @return
     */
    @PutMapping("/cleanAllPortStorage")
    @PreAuthorize("hasAuthority('business:cargoTransfer:clean')")
    public Map<String, Object> cleanAllPortStorage(@RequestBody CleanAllPortStorageDTO cleanAllPortStorage) {
        final String methodName = "TBusCargoInfoController:cleanAllPortStorage";
        LOGGER.enter(methodName + "[start]", "cleanAllPortStorage:" + cleanAllPortStorage);

        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(cleanAllPortStorage);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tBusCargoInfoService.cleanAllPortStorage(cleanAllPortStorage);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("完货操作成功").toResult();
    }

    /**
     * 海清物流
     *
     * @param tBusCargoInfoDTO
     * @return
     */
    @PutMapping("/updateIsHq")
    public Map<String, Object> updateIsHq(@RequestBody TBusCargoInfoDTO tBusCargoInfoDTO) {

        boolean flag = tBusCargoInfoService.updateIsHq(tBusCargoInfoDTO);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();

    }

    /**
     * 撤销完货
     *
     * @param id
     * @return
     */
    @PutMapping("/cancelCleanAllPortStorage")
    @PreAuthorize("hasAuthority('business:cargoTransfer:cancel')")
    public Map<String, Object> cancelCleanAllPortStorage(@NotNull(message = "票货ID不能为空") Long id) {
        final String methodName = "TBusCargoInfoController:cancelCleanAllPortStorage";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        tBusCargoInfoService.cancelCleanAllPortStorage(id);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 批量导入
     * @param file
     * @return
     */
    @PostMapping("/importCargoList/{id}")
    @PreAuthorize("hasAuthority('business:cargoTransfer:import')")
    public Map<String, Object> importCargoList(@PathVariable("id") Long id, MultipartFile file) {

        final String methodName = "TBusCargoInfoController:importCargoList";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        boolean flag = tBusCargoInfoService.importCargoList(id,file);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "批量添加成功" : "批量添加失败").toResult(flag);

    }

    /**
     * 批量导入
     * @param file
     * @return
     */
    @PostMapping("/importCargoBoxList/{id}")
    public Map<String, Object> importCargoBoxList(@PathVariable("id") Long id, MultipartFile file) {

        final String methodName = "TBusCargoInfoController:importCargoBoxList";
        LOGGER.enter(methodName + "[start]", "id:" +  id);

        boolean flag = tBusCargoInfoService.importCargoBoxList(id,file);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "批量添加成功" : "批量添加失败").toResult(flag);

    }



    /**
     * 查询是否存在货物清单
     * @param id
     * @return
     */
    @GetMapping("/getCargoListInfo/{id}")
    public Map<String, Object> getCargoListInfo(@PathVariable("id") Long id,@RequestParam("businessType") String businessType) {
        final String methodName = "TBusCargoInfoController:getCargoListInfo";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        if(id == null){
            throw new BusinessRuntimeException("请选中票货信息");
        }

        Map<String, Object> result= tBusCargoInfoService.getCargoListByCargoCode(id,businessType);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
//        return null;
    }




    /**
     * 查询货物清单详情
     * @param id
     * @return
     */
    @GetMapping("/getCargoListInfoByCargoId/{id}")
    public Map<String, Object> getCargoListInfoByCargoId(@PathVariable("id") Long id ,@RequestParam("businessType") String businessType) {
        final String methodName = "TBusCargoInfoController:getCargoListInfo";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        List<CargoListInfoDTO> result = new ArrayList<>();
        if(id == null){
           return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
        }

        result = tBusCargoInfoService.getCargoListInfoByCargoId(id,businessType);


        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    @GetMapping("/downTemplate")
    public Map<String, Object> downTemplate( HttpServletResponse response) {
        tBusCargoInfoService.exportTemplate(response);
        return Response.SUCCESS.newBuilder().out("导出成功").toResult();

    }

    @GetMapping("/downBoxTemplate")
    public Map<String, Object> downBoxTemplate( HttpServletResponse response) {
        tBusCargoInfoService.exportBoxTemplate(response);
        return Response.SUCCESS.newBuilder().out("导出成功").toResult();
    }

    /**
     * 根据客户查询票货
     * @param customerId
     * @return
     */
    @GetMapping("/getCargoListByCustomerId")
    public Map<String, Object> getCargoListByCustomerId(@RequestParam(value = "customerId") Long customerId,
                                                        @RequestParam(value = "shipvoyageItemId",required = false) Long shipvoyageItemId) {
        final String methodName = "TBusCargoInfoController:getCargoListByCustomerId";
        LOGGER.enter(methodName + "[start]", "customerId:" + customerId);

        List<TBusCargoInfoDTO> result = tBusCargoInfoService.getCargoListByCustomerId(customerId,shipvoyageItemId);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 修改开启停用状态
     */
    @PutMapping("/isLogoutStatus")
    public Map<String, Object> isLogoutStatus(@RequestBody TBusCargoInfoDTO tBusCargoInfoDTO) {
        boolean flag = tBusCargoInfoService.isLogoutStatus(tBusCargoInfoDTO);
        return Response.SUCCESS.newBuilder().out(flag ? "成功" : "失败").toResult();
    }

}

