package com.yy.ppm.system.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.service.SysParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统参数(SysParameter)表控制层
 *
 * @author 张超
 * @date 2021-03-02 16:28:44
 */
@RestController
@RequestMapping(value = "/api/internal/sysParameter")
@Validated
public class SysParameterController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysParameterController.class);

    /**
     * 服务对象
     */
    @Autowired
    private SysParameterService sysParameterService;

    /**
     * 根据实体类筛选数据列表
     *
     * @return 统一数据封装
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('system:parameter:query')")
    public Map<String, Object> getList(SysParameterDTO sysParameterDTO) {
        final String methodName = "SysParameterController:getList";
        LOGGER.enter( methodName + "[start]");

        List<SysParameterDTO> sysParameterList = sysParameterService.getList(sysParameterDTO);

        LOGGER.exit(methodName + "result:" + sysParameterList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysParameterList);
    }

    /**
     * 查询版本信息
     *
     * @return 统一数据封装
     */
    @GetMapping("/getConfig")
    public Map<String, Object> getConfig(String code) {
        SysParameterDTO po = sysParameterService.getConfig(code);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(po);
    }

    /**
     * 保存
     *
     * @param parameterList
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    @PreAuthorize("hasAuthority('system:parameter:save')")
    public Map<String, Object> save(@RequestBody List<SysParameterDTO> parameterList) {
        final String methodName = "SysParameterController:save";
        LOGGER.enter(methodName + "[start]", "parameterList:" + parameterList);

        sysParameterService.save(parameterList);

        LOGGER.exit(methodName + "result:");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "SysParameterController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sysParameterService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }
    /**
     * 系统参数(用户)保存
     *
     * @param parameterList
     * @return
     */
    @PostMapping("/saveUser")
    @ResponseBody
    @PreAuthorize("hasAuthority('system:parameterUser:save')")
    public Map<String, Object> saveUser(@RequestBody List<SysParameterDTO> parameterList) {
        final String methodName = "SysParameterController:saveUser";
        LOGGER.enter(methodName + "[start]", "parameterList:" + parameterList);

        sysParameterService.saveUser(parameterList);

        LOGGER.exit(methodName + "result:");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 系统参数(用户)根据实体类筛选数据列表
     *
     * @return 统一数据封装
     */
    @GetMapping("/getUserList")
    @PreAuthorize("hasAuthority('system:parameterUser:query')")
    public Map<String, Object> getUserList(SysParameterDTO sysParameterDTO) {
        final String methodName = "SysParameterController:getUserList";
        LOGGER.enter( methodName + "[start]");

        List<SysParameterDTO> sysParameterList = sysParameterService.getUserList(sysParameterDTO);

        LOGGER.exit(methodName + "result:" + sysParameterList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysParameterList);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/deleteUser/{id}")
    public Map<String, Object> deleteUserById(@PathVariable("id") Long id) {
        final String methodName = "SysParameterController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = sysParameterService.deleteUserById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }


}
