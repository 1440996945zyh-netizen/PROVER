package com.yy.ppm.flowable.controller;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmCategorySearchDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.service.BpmCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Tag(name = "管理后台 - BPM 流程分类")
@RestController
@RequestMapping("/bpm/category")
@Validated
public class BpmCategoryController {

    @Resource
    private BpmCategoryService categoryService;

    /**
     * 获得流程分类分页
     * @param bpmCategorySearchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(@Valid BpmCategorySearchDTO bpmCategorySearchDTO) {
        Pages<BpmCategoryPO> pageResult = categoryService.getList(bpmCategorySearchDTO);
        return Response.SUCCESS.newBuilder().toResult(pageResult);
    }

    /**
     * 保存
     * @param bpmCategoryPO
     * @return
     */
    @PostMapping("/insert")
    public Map<String, Object> createCategory( @RequestBody BpmCategoryPO bpmCategoryPO) {
        int count = categoryService.createCategory(bpmCategoryPO);
        return Response.SUCCESS.newBuilder().out(count>0?"保存成功":"保存失败").toResult();
    }

    /**
     * 更新流程分类
     * @param updateReqVO
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "")
    @PreAuthorize("@ss.hasPermission('bpm:category:update')")
    public Map<String, Object> updateCategory(@RequestBody BpmCategoryPO updateReqVO) {
        int count = categoryService.updateCategory(updateReqVO);
        return Response.SUCCESS.newBuilder().out(count>0?"修改成功":"修改失败").toResult();
    }


    /**
     * 删除流程分类
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteCategory(@PathVariable("id") Long id) {
        int count = categoryService.deleteCategory(id);
        return Response.SUCCESS.newBuilder().out(count>0?"删除成功":"删除失败").toResult();
    }

    /**
     * 查看详情
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        BpmCategoryPO category = categoryService.getDetail(id);
        return Response.SUCCESS.newBuilder().toResult(category);
    }
}
