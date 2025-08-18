package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordQueryDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageQueryDTO;
import com.yy.ppm.business.service.TBusCargoMixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/cargoMix")
@Validated
public class TBusCargoMixController {

    @Autowired
    private TBusCargoMixService tBusCargoMixService;

    /**
     * 查询港存
     *
     * @param query
     * @return
     */
    @GetMapping("/portStorages")
    public Map<String, Object> listPortStorage(TPrdPortStorageQueryDTO query) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(query);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        List<TPrdPortStorageDTO> result = tBusCargoMixService.listPortStorage(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询合同
     *
     * @param cargoInfoIds
     * @return
     */
    @GetMapping("/contracts/{cargoInfoIds}")
    public Map<String, Object> contracts(@PathVariable @NotEmpty(message = "票货ID不能为空") List<Long> cargoInfoIds) {
        List<Map<String, Object>> result = tBusCargoMixService.contracts(cargoInfoIds);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 混配新票货 (审核前此时还没有生成新票货)
     *
     * @param dto
     * @return
     */
    @PostMapping("/mix")
    public Map<String, Object> mix(@RequestBody TBusCargoMixRecordDTO dto) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(dto);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(dto.getDetails());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        tBusCargoMixService.mix(dto);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 查询混配记录
     *
     * @param parameter
     * @param query
     * @return
     */
    @GetMapping("/mixes")
    public Map<String, Object> listMix(PageParameter parameter, TBusCargoMixRecordQueryDTO query) {
        Pages<TBusCargoMixRecordDTO> result = tBusCargoMixService.listMix(parameter, query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 回显
     *
     * @return
     */
    @GetMapping("/mix/{id}")
    public Map<String, Object> mix(@PathVariable @NotNull(message = "票货混配记录ID不能为空") Long id) {
        TBusCargoMixRecordDTO result = tBusCargoMixService.getMix(id);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 删除混配
     *
     * @param id
     * @return
     */
    @DeleteMapping("/mix/{id}")
    public Map<String, Object> deleteMix(@PathVariable @NotNull(message = "票货混配记录ID不能为空") Long id) {
        tBusCargoMixService.deleteMix(id);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 审核
     *
     * @param id
     * @return
     */
    @PutMapping("/review/{id}")
    public Map<String, Object> review(@PathVariable @NotNull(message = "票货混配记录ID不能为空") Long id) {
        tBusCargoMixService.review(id);
        return Response.SUCCESS.newBuilder().out("审核成功").toResult();
    }

    /**
     * 销审
     *
     * @param id
     * @return
     */
    @PutMapping("/cancelReview/{id}")
    public Map<String, Object> cancelReview(@PathVariable @NotNull(message = "票货混配记录ID不能为空") Long id) {
        tBusCargoMixService.cancelReview(id);
        return Response.SUCCESS.newBuilder().out("销审成功").toResult();
    }
}
