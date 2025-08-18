package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MOperationTechnologyDTO;
import com.yy.ppm.master.bean.po.MOperationTechnologyPO;
import com.yy.ppm.master.service.MOperationTechnologyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/internal/operationtechnique")
public class MOperationTechnologyController {

    private static final MicroLogger LOGGER = new MicroLogger(MOperationTechnologyController.class);

    @Resource
    MOperationTechnologyService operationTechnologyService;

    /**
     * 作业工艺查询
     */
    @GetMapping("/listtechnique")
    @PreAuthorize("hasAuthority('master:technology:list')")
    public Map<String,Object> listOperationTechnology(String code, PageParameter pageQuery, String name) {
        final String methodName = "MOperationProcessController: listSubProcess";
        LOGGER.enter(methodName + "[start]","code:"+code+",name:"+code);

        Pages<MOperationTechnologyPO> result = operationTechnologyService.selectAllTechnique(code,pageQuery, name);

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增作业工艺
     */
    @PostMapping("/addtechnique")
    @PreAuthorize("hasAuthority('master:technology:add')")
    public Map<String,Object> addOperationTechnology(@RequestBody MOperationTechnologyDTO bo) {
        final String methodName = "MOperationProcessController: addOperationTechnology";
        LOGGER.enter(methodName + "[start]","bo:"+bo);

        operationTechnologyService.insertTechnique(bo);

        LOGGER.exit(methodName+"[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改作业工艺
     */
    @PutMapping("/updatetechnique")
    @PreAuthorize("hasAuthority('master:technology:edit')")
    public Map<String,Object> updateOperationTechnology(@RequestBody MOperationTechnologyDTO bo) {
        final String methodName = "MOperationProcessController: updateOperationTechnology";
        LOGGER.enter(methodName + "[start]","bo:"+bo);

        operationTechnologyService.updateTechnique(bo);

        LOGGER.exit(methodName+"[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 根据id查询某个作业工艺
     */
    @GetMapping("/selecttechniquebyid")
    @PreAuthorize("hasAuthority('master:technology:selectone')")
    public Map<String,Object> selectTechnologyById(Long id) {
        final String methodName = "MOperationProcessController: updateOperationTechnology";
        LOGGER.enter(methodName + "[start]","id:"+id);

        MOperationTechnologyDTO result = operationTechnologyService.selectTechniqueById(id);

        LOGGER.exit(methodName+"[end]","result:" + result);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 删除作业工艺
     */
    @DeleteMapping("/deletetechniquebyid/{ids}")
    @PreAuthorize("hasAuthority('master:technology:delete')")
    public Map<String,Object> deleteTechnologyById(@PathVariable Long id) {
        final String methodName = "MOperationProcessController: updateOperationTechnology";
        LOGGER.enter(methodName + "[start]","id:"+id);

        operationTechnologyService.deleteTechniqueById(id);

        LOGGER.exit(methodName+"[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
