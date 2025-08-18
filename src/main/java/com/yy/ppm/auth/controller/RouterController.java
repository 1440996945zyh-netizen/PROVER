package com.yy.ppm.auth.controller;

import com.yy.common.enums.CommonConstants;
import com.yy.common.enums.Response;
import com.yy.common.jwt.Jwt;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.JwtUtils;
import com.yy.ppm.auth.bean.dto.RouterDTO;
import com.yy.ppm.auth.service.RouterService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 登录后路由跳转需要的数据
 * @author FanQi
 * @version 1.0
 * @date 2023/4/23 9:35
 */
@RestController
@RequestMapping(value = "/api/internal/router")
public class RouterController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(RouterController.class);

    @Autowired
    private RouterService routerService;

    /**
     * 获取首页左侧菜单列表
     * @return
     */
    @GetMapping("/getRouters")
    //@RequiresPermissions("system:routers")
    public Map<String, Object> getRouters(ServletRequest request) {
        final String methodName = "RouterController:getRouters";
        LOGGER.enter(methodName + "[start]");
        // 获取令牌
        HttpServletRequest req = (HttpServletRequest) request;
        String token = ObjectUtils.defaultIfNull(req.getHeader(CommonConstants.CONTEXT_TOKEN), req.getParameter(CommonConstants.CONTEXT_TOKEN));
        Jwt.JwtBean bean = JwtUtils.parseToken(token);
        List<RouterDTO> resultList = routerService.getRouters(bean.getLoginType());
        LOGGER.exit(methodName + "result:" + resultList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

}
