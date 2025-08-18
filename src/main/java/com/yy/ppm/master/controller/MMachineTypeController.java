package com.yy.ppm.master.controller;


import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MMachineTypeDTO;
import com.yy.ppm.master.bean.po.MMachineTypeModelPO;
import com.yy.ppm.master.bean.po.MMachineTypePO;
import com.yy.ppm.master.service.MMachineTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 机械类型Controller
 * */
@Validated
@RestController
@RequestMapping("/api/internal/bmachinetype")
public class MMachineTypeController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MMachineTypeController.class);

    @Resource
    MMachineTypeService bMachineTypeService;

    /**
     * 机械类型查询
     */
    @GetMapping("/listbmachinetype")
    @PreAuthorize("hasAuthority('master:machineType:query')")
    public Map<String, Object> listBMachineType(PageParameter pageQuery, String name) {
        final String methodName = "MMachineTypeController:listBMachineType";
        LOGGER.enter(methodName, "机械类型查询[start]");

        Pages<MMachineTypeDTO> result = bMachineTypeService.listBMachineType(pageQuery, name);

        LOGGER.exit(methodName, "机械类型查询[end]");
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据id查询机械类型
     */
    @GetMapping("/selectbmachinetypebyid")
    @PreAuthorize("hasAuthority('master:machineType:query')")
    public Map<String, Object> selectBMachineTypeById(String id) {
        final String methodName = "MMachineTypeController:selectBMachineTypeById";
        LOGGER.enter(methodName, "根据id查询机械类型[start]");

        MMachineTypeDTO result = bMachineTypeService.selectBMachineTypeById(id);

        LOGGER.exit(methodName, "根据id查询机械类型[end]");
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增机械类型
     */
    @PostMapping("/insertbmachinetype")
    @PreAuthorize("hasAuthority('master:machineType:add')")
    public  Map<String, Object> insertBMachineType(@RequestBody MMachineTypeDTO bo) {
        final String methodName = "MMachineTypeController:selectBMachineTypeById";
        LOGGER.enter(methodName + "[start]", "bo:" +bo);

        bMachineTypeService.saveBMachineType(bo);

        LOGGER.exit(methodName +"[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改机械类型
     */
    @PutMapping("/updatebmachinetype")
    @PreAuthorize("hasAuthority('master:machineType:update')")
    public Map<String, Object> updateBMachineType(@RequestBody MMachineTypeDTO bo) {
        final String methodName = "MMachineTypeController:updateBMachineType";
        LOGGER.enter(methodName + "[start]", "bo:" +bo);

        bMachineTypeService.saveBMachineType(bo);

        LOGGER.exit(methodName +"[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除机械类型
     */
    @DeleteMapping("/deletemachinetype/{ids}")
    @PreAuthorize("hasAuthority('master:machineType:delete')")
    public  Map<String, Object> deleteMachineType(@PathVariable Long ids) {
        final String methodName = "MMachineTypeController:deleteMachineType";
        LOGGER.enter(methodName + "[start]", "ids:" +ids);

        bMachineTypeService.deleteBMachineType(ids);

        LOGGER.exit(methodName +"[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 机械型号查询
     */
    @GetMapping("/listbmachinetypemodel")
    @PreAuthorize("hasAuthority('master:machineType:query')")
    public Map<String, Object> listBMachineTypeModel() {
        final String methodName = "MMachineTypeController:listBMachineTypeModel";
        LOGGER.enter(methodName + "[start]","");

        List<MMachineTypeModelPO>  result = bMachineTypeService.listBMachineTypeModel();

        LOGGER.exit(methodName +"[end]");
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据机械类型名称查到机械型号名称
     */
    @GetMapping("/getMacModelByTypeCode")
    public Map<String, Object> getMacModelByTypeCode(String id) {
        final String methodName = "MMachineTypeController:selectBMachineTypeById";
        LOGGER.enter(methodName, "根据id查询机械类型[start]");

        List<Map<String, String>> result = bMachineTypeService.getMacModelByTypeCode(id);

        LOGGER.exit(methodName, "根据id查询机械类型[end]");
        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
