package com.yy.ppm.flowable.controller;
import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmFormSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.service.BpmFormService;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
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


@Tag(name = "管理后台 - 动态表单")
@RestController
@RequestMapping("/bpm/form")
public class BpmFormController {

    @Resource
    private BpmFormService formService;

    /**
     * 获得动态表单分页
     * @param bpmFormSearchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(@Valid BpmFormSearchDTO bpmFormSearchDTO) {
        Pages<BpmFormPO> list = formService.getList(bpmFormSearchDTO);
        return Response.SUCCESS.newBuilder().toResult(list);
    }

    /**
     * 创建动态表单
     * @param bpmFormPO
     * @return
     */
    @PostMapping("/insert")
    public Map<String, Object> createForm(@RequestBody BpmFormPO bpmFormPO) {
        int count = formService.createForm(bpmFormPO);
        return Response.SUCCESS.newBuilder().out(count>0?"保存成功":"保存失败").toResult();
    }

    /**
     * 更新动态表单
     * @param updateReqVO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> updateForm(@RequestBody BpmFormPO updateReqVO) {
        int count = formService.updateForm(updateReqVO);
        return Response.SUCCESS.newBuilder().out(count>0?"修改成功":"修改失败").toResult();
    }

    /**
     * 删除动态表单
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteForm(@PathVariable("id") Long id) {
        int count = formService.deleteForm(id);
        return Response.SUCCESS.newBuilder().out(count>0?"删除成功":"删除失败").toResult();
    }

    /**
     * 获得动态表单详情
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        BpmFormPO form = formService.getDetail(id);
        return Response.SUCCESS.newBuilder().toResult(form);
    }
}
