package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MWorkwareTypeDTO;
import com.yy.ppm.master.bean.po.MWorkwareTypePO;
import com.yy.ppm.master.service.MWorkwareTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 工属具Controller
 *
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/workwareType")
public class MWorkwareTypeController {

    private static final MicroLogger LOGGER = new MicroLogger(MWorkwareTypeController.class);
    @Resource
    MWorkwareTypeService bWorkwareTypeService;

    /**
     * 工属具类型查询
     */
    @GetMapping("/listworkwaretype")
    @PreAuthorize("hasAuthority('master:Workware:query')")
    public Map<String,Object> listBWorkwareType(PageParameter pageQuery, String name) {
        final String methodName = "MWorkwareTypeController: listBWorkwareType";
        LOGGER.enter(methodName + "[start]","name:"+name);

        Pages<MWorkwareTypeDTO> result = bWorkwareTypeService.listBWorkwareType(pageQuery,name);

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据id查询工属具类型
     */
    @GetMapping("/selectbworkwaretypebyid")
    @PreAuthorize("hasAuthority('master:workware:getone')")
    public Map<String,Object> selectBWorkwareTypeById(Long id) {
        final String methodName = "MWorkwareTypeController: selectBWorkwareTypeById";
        LOGGER.enter(methodName + "[start]","id:"+id);

        MWorkwareTypePO result =  bWorkwareTypeService.selectBWorkwareTypeById(id);

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 新增工属具类型
     */
    @PostMapping("/insertbworkwaretype")
    @PreAuthorize("hasAuthority('master:workware:add')")
    public Map<String,Object>  insertBWorkwareType(@RequestBody MWorkwareTypeDTO bo) {
        final String methodName = "MWorkwareTypeController: insertBWorkwareType";
        LOGGER.enter(methodName + "[start]","bo:"+bo);

        bWorkwareTypeService.insertBWorkwareType(bo);

        LOGGER.exit(methodName + "[end]" );
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改工属具类型
     */
    @PutMapping("/updatebworkwaretype")
    @PreAuthorize("hasAuthority('master:workware:edit')")
    public Map<String,Object> updateBWorkwareType(@RequestBody MWorkwareTypeDTO bo) {
        final String methodName = "MWorkwareTypeController: updateBWorkwareType";
        LOGGER.enter(methodName + "[start]","bo:"+bo);

        bWorkwareTypeService.updateBWorkwareType(bo);

        LOGGER.exit(methodName + "[end]" );
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除工属具类型
     */
    @DeleteMapping("/deleteworkwaretype/{id}")
    @PreAuthorize("hasAuthority('master:workware:delete')")
    public Map<String,Object> deleteWorkwareType(@PathVariable Long id) {
        final String methodName = "MWorkwareTypeController: deleteWorkwareType";
        LOGGER.enter(methodName + "[start]","id:"+id);

        bWorkwareTypeService.deleteBWorkwareType(id);

        LOGGER.exit(methodName + "[end]" );
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
