package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.master.bean.dto.tufFee.MTugFeeDTO;
import com.yy.ppm.master.bean.po.MTugFeePO;
import com.yy.ppm.master.service.TugFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 拖轮费用标准
 * @Date 2023-09-27 10:10
 */
@RestController
@RequestMapping("/api/external/tugFee")
@Validated
public class TugFeeController {

    @Autowired
    private TugFeeService tugFeeService;

    /**
     * 新增拖轮费用标准
     *
     * @param tugFee
     * @return
     */
    @PostMapping("/insertTugFee")
    public Map<String, Object> listTugFee(@RequestBody MTugFeePO tugFee) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(tugFee, true, "id");
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tugFeeService.insertTugFee(tugFee);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 拖轮费用标准列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listTugFee")
    public Map<String, Object> listTugFee(MTugFeePO query, PageParameter parameter) {
        Pages<MTugFeeDTO> result = tugFeeService.listTugFee(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 修改拖轮费用标准
     *
     * @param tugFee
     * @return
     */
    @PutMapping("/updateTugFee")
    public Map<String, Object> updateTugFee(@RequestBody MTugFeePO tugFee) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(tugFee);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tugFeeService.updateTugFee(tugFee);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除拖轮费用标准
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteTugFee")
    public Map<String, Object> deleteTugFee(@NotNull(message = "主键ID不能为空") Long id) {
        tugFeeService.deleteTugFee(id);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
