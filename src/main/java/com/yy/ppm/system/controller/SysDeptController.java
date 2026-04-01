package com.yy.ppm.system.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/internal/sysDept")
@Validated
public class SysDeptController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysDeptController.class);

    /**
     * 服务对象
     */

    private final SysDeptService deptService;

    public SysDeptController(SysDeptService deptService){
        this.deptService = deptService;
    }
    /**
     * 获取部门列表
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Map<String, Object> list(SysDeptDTO dto) {
        List<SysDeptDTO> depts = deptService.selectDeptList(dto);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @GetMapping(value = "/getDetail/{id}")
    @PreAuthorize("hasAuthority('system:dept:query')")
    public Map<String, Object> getDetail(@PathVariable Long id) {
        SysDeptDTO result = deptService.getById(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增部门
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:add')")
    public Map<String, Object> add(@Validated @RequestBody SysDeptDTO dept) {
        int count = deptService.insertDept(dept);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 修改部门
     */
    @PutMapping
    @PreAuthorize("hasAuthority('system:dept:update')")
    public Map<String, Object> edit(@Validated @RequestBody SysDeptDTO dept) {

        int count = deptService.updateDept(dept);

        return Response.SUCCESS.newBuilder().out(count > 0 ? "保存成功~" : "保存失败~").toResult();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Map<String, Object> remove(@PathVariable Long id) {
        int count = deptService.deleteDeptById(id);
        return Response.SUCCESS.newBuilder().out(count > 0  ? "删除成功~" : "删除失败~").toResult();
    }
}
