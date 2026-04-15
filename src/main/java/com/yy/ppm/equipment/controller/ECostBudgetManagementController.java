package com.yy.ppm.equipment.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.equipment.bean.dto.ECostBudgetManagementDTO;
import com.yy.ppm.equipment.service.ECostBudgetManagementService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 预算管理 Controller
 *
 * 接口前缀：/api/internal/ECostBudgetManagement
 *
 * 提供功能：
 * 1. 分页查询
 * 2. 详情查询
 * 3. 新增
 * 4. 修改
 * 5. 删除
 */
@RestController
@RequestMapping("/api/internal/ECostBudgetManagement")
public class ECostBudgetManagementController {

    /**
     * 控制器日志对象
     */
    private static final MicroLogger LOGGER = new MicroLogger(ECostBudgetManagementController.class);

    @Resource
    private ECostBudgetManagementService service;

    /**
     * 分页查询预算管理列表
     *
     * @param searchDTO 查询条件
     * @param parameter 分页参数
     * @return 分页数据
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('equipment:ecostbudgetmanagement:query')")
    public Map<String, Object> getList(ECostBudgetManagementDTO searchDTO, PageParameter parameter) {
        final String methodName = "ECostBudgetManagementController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO + ", parameter:" + parameter);

        Pages<ECostBudgetManagementDTO> result = service.list(searchDTO, parameter);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据主键ID查询详情
     *
     * @param id 主键ID
     * @return 详情数据
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:ecostbudgetmanagement:getById')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "ECostBudgetManagementController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        ECostBudgetManagementDTO result = service.get(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增预算管理
     *
     * @param dto 请求参数
     * @return 操作结果
     */
    @PostMapping("/add")
    @Log(title = "新增预算管理", value = OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('equipment:ecostbudgetmanagement:add')")
    public Map<String, Object> add(@RequestBody ECostBudgetManagementDTO dto) {
        final String methodName = "ECostBudgetManagementController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.add(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改预算管理
     *
     * @param dto 请求参数
     * @return 操作结果
     */
    @PutMapping("/update")
    @Log(title = "修改预算管理", value = OperateTypeEnum.UPDATE)
    @PreAuthorize("hasAuthority('equipment:ecostbudgetmanagement:update')")
    public Map<String, Object> update(@RequestBody ECostBudgetManagementDTO dto) {
        final String methodName = "ECostBudgetManagementController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.update(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除预算管理
     *
     * @param id 主键ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    @Log(title = "删除预算管理", value = OperateTypeEnum.DELETE)
    @PreAuthorize("hasAuthority('equipment:ecostbudgetmanagement:delete')")
    public Map<String, Object> delete(@RequestParam("id") Long id) {
        final String methodName = "ECostBudgetManagementController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.delete(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }



    /**
     * 根据业务id与角色获取用户
     *
     * @param id 主键ID
     * @return 详情数据
     */
    @GetMapping("/getWarningUser")
    @PreAuthorize("hasAuthority('equipment:ecostbudgetmanagement:getWarningUser')")
    public Map<String, Object> getWarningUser(ECostBudgetManagementDTO dto) {
        final String methodName = "ECostBudgetManagementController:getById";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        List<ECostBudgetManagementDTO > result = service.getWarningUser(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
}

