package com.yy.ppm.flowable.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmUserExpressionSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmUserExpressionPO;
import com.yy.ppm.flowable.service.BpmUserExpressionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "管理后台 - BPM 用户常用审批语")
@RestController
@RequestMapping("/api/internal/bpmProcessLanguage")
@Validated
public class BpmUserExpressionController {

    @Resource
    private BpmUserExpressionService userExpressionService;

    /**
     * 获取常用审批语分页列表
     * @param searchDTO 查询参数
     * @return 分页结果
     */
    @GetMapping("/getList")
    @PreAuthorize("@ss.hasPermission('bpm:processLanguage:query')")
    public Map<String, Object> getList(@Valid BpmUserExpressionSearchDTO searchDTO) {
        Pages<BpmUserExpressionPO> pageResult = userExpressionService.getList(searchDTO);
        return Response.SUCCESS.newBuilder().toResult(pageResult);
    }

    /**
     * 创建常用审批语
     * @param expressionPO
     * @return
     */
    @PostMapping("/insert")
    @PreAuthorize("@ss.hasPermission('bpm:processLanguage:insert')")
    public Map<String, Object> createExpression( @RequestBody BpmUserExpressionPO expressionPO) {
        int count = userExpressionService.createExpression(expressionPO);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "创建成功" : "创建失败").toResult();
    }

    /**
     * 更新常用审批语
     * @param expressionPO
     * @return
     */
    @PostMapping("/update")
    @PreAuthorize("@ss.hasPermission('bpm:processLanguage:update')")
    public Map<String, Object> updateExpression(@Valid @RequestBody BpmUserExpressionPO expressionPO) {
        int count = userExpressionService.updateExpression(expressionPO);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除常用审批语
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@ss.hasPermission('bpm:processLanguage:delete')")
    public Map<String, Object> deleteExpression(@PathVariable("id") Long id) {
        int count = userExpressionService.deleteExpression(id);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 获取常用审批语详情
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("@ss.hasPermission('bpm:processLanguage:query')")
    public Map<String, Object> getExpression(@RequestParam("id") Long id) {
        BpmUserExpressionPO expression = userExpressionService.getDetail(id);
        return Response.SUCCESS.newBuilder().toResult(expression);
    }

}
