package com.yy.ppm.equipment.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MPecialPersonDTO;
import com.yy.ppm.equipment.bean.dto.MPecialPersonSearchDTO;
import com.yy.ppm.equipment.service.MPecialPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 特种作业人员证书Controller
 * @author system
 */
@Validated
@RestController
@RequestMapping("/api/v1/internal/pecialPerson")
public class MPecialPersonController {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(MPecialPersonController.class);

    @Autowired
    private MPecialPersonService service;

    /**
     * 查询特种作业人员证书列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('equipment:pecialPerson:query')")
    public Map<String, Object> getList(MPecialPersonSearchDTO searchDTO) {
        final String methodName = "MPecialPersonController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MPecialPersonDTO> result = service.getList(searchDTO);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据ID查询特种作业人员证书
     */
    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('equipment:pecialPerson:query')")
    public Map<String, Object> getById(@RequestParam("id") Long id) {
        final String methodName = "MPecialPersonController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MPecialPersonDTO result = service.getById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增特种作业人员证书
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('equipment:pecialPerson:add')")
    public Map<String, Object> add(@RequestBody MPecialPersonDTO dto) {
        final String methodName = "MPecialPersonController:add";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改特种作业人员证书
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('equipment:pecialPerson:update')")
    public Map<String, Object> update(@RequestBody MPecialPersonDTO dto) {
        final String methodName = "MPecialPersonController:update";
        LOGGER.enter(methodName + "[start]", "dto:" + dto);

        service.save(dto);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除特种作业人员证书
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('equipment:pecialPerson:delete')")
    public Map<String, Object> delete(@PathVariable("id") Long id) {
        final String methodName = "MPecialPersonController:delete";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        service.deleteById(id);

        LOGGER.exit(methodName + "[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}

