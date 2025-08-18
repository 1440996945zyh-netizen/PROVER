package com.yy.ppm.finance.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDetailDTO;
import com.yy.ppm.finance.service.TFdDebtorpaymentService;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentSearchDTO;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpayment)Controller
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdDebtorpayment")
@Tag(name = "计费相关.付款收据")
public class TFdDebtorpaymentController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdDebtorpaymentController.class);

    @Autowired
    private TFdDebtorpaymentService tFdDebtorpaymentService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdDebtorpaymentSearchDTO searchDTO) {

        Pages<TFdDebtorpaymentDTO> pages = tFdDebtorpaymentService.getList(searchDTO);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {

        TFdDebtorpaymentDTO result = tFdDebtorpaymentService.getDetail(id);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param dto
     * @return
     */
    @PostMapping("/add")
    @Log(title = "付款收据新增", value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody TFdDebtorpaymentDTO dto) {
        final String methodName = "TFdDebtorpaymentController:add";
        LOGGER.enter(methodName + "[start]", "tFdDebtorpaymentDTO:" + dto);

        boolean flag = tFdDebtorpaymentService.doSave(dto);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdDebtorpaymentDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdDebtorpaymentDTO tFdDebtorpaymentDTO) {
        final String methodName = "TFdDebtorpaymentController:update";
        LOGGER.enter(methodName + "[start]", "tFdDebtorpaymentDTO:" + tFdDebtorpaymentDTO);

        boolean flag = tFdDebtorpaymentService.update(tFdDebtorpaymentDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TFdDebtorpaymentController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdDebtorpaymentService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }
    /**
     * 作废
     * @param id
     * @return
     */
    @GetMapping("/voidDebtorpay/{id}")
    @Log(title = "付款收据作废", value = OperateTypeEnum.UPDATE)
    public Map<String, Object> voidDebtorpay(@PathVariable("id") Long id) {
        final String methodName = "TFdDebtorpaymentController:voidDebtorpay";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdDebtorpaymentService.voidDebtorpayById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "作废成功" : "作废失败").toResult();
    }
    /**
     * 获取发票和预付款的信息
     * @param searchDTO
     * @return
     */
    @GetMapping("/searchList")
    @Log(title = "获取发票和预付款的信息", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getSearchList(TFdDebtorpaymentSearchDTO searchDTO) {
        final String methodName = "TFdDebtorpaymentController:addSearchList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        if(searchDTO==null){
            throw new BusinessRuntimeException("请选择查询条件");
        }
        if(searchDTO.getPrepaymentTypeCode()==null){
            throw new BusinessRuntimeException("请选择对账类型");
        }

        List<TFdDebtorpaymentDetailDTO> result = tFdDebtorpaymentService.getSearchList(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

