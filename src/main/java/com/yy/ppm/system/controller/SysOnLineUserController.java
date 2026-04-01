package com.yy.ppm.system.controller;


import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysOnLineUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/6 11:37
 */
@RestController
@RequestMapping("/api/internal/sysOnLineUser")
@Validated
@Tag(name = "系统管理.在线用户管理")
public class SysOnLineUserController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysOnLineUserController.class);


    private final SysOnLineUserService sysOnLineUserService;

    public SysOnLineUserController(SysOnLineUserService sysOnLineUserService){
        this.sysOnLineUserService = sysOnLineUserService;
    }

    /**
     * 查询
     * @param userAccount
     * @param userName
     * @return
     */
    @GetMapping("/getlist")
    @Log(OperateTypeEnum.QUERY)
    @PreAuthorize("hasAuthority('system:online:query')")
    public Map<String, Object> getList(@Param("userAccount") String userAccount,
                                       @Param("userName")String userName) {
        final String methodName = "SysOnLineUserController:getList";
        LOGGER.enter(methodName + "[start]");

        List<SysUserDTO> list = sysOnLineUserService.getList(userAccount,userName);

        LOGGER.exit( methodName + "result:" + list);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }


    /**
     * 强退
     * @param userAccount
     * @param id
     * @return
     */
    @GetMapping("/offline")
    @PreAuthorize("hasAuthority('system:online:offline')")
    public Map<String, Object> offLine(@Param("userAccount")String userAccount,
                                       @Param("id")Long id) {
        final String methodName = "SysOnLineUserController:offLine";
        LOGGER.enter(methodName + "[start]");

        sysOnLineUserService.offLine(userAccount,id);

        return Response.SUCCESS.newBuilder().out("强退成功").toResult();
    }


}
