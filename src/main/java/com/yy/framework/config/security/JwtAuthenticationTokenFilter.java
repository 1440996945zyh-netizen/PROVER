package com.yy.framework.config.security;

import com.yy.common.enums.CommonConstants;
import com.yy.common.enums.RedisEnum;
import com.yy.common.enums.Response;
import com.yy.common.jwt.Jwt;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.JSONUtils;
import com.yy.common.util.JwtUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.auth.bean.dto.UserAuthorizeInfo;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.auth.enums.LoginTypeEnum;
import com.yy.ppm.auth.service.AuthService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * JwtFilter
 */
@Component
@Order(1)
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(JwtAuthenticationTokenFilter.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private AuthService authService;

    @Resource
    private WhiteList whiteList;

    /** url白名单 */
    private List<String> whiteListURLs;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    protected void initFilterBean()  {

        whiteListURLs = new ArrayList<String>();

        // 白名单设置
        if (whiteList.getUrls() != null  && whiteList.getUrls().size() > 0) {
            for (int i=0; i< whiteList.getUrls().size();i++) {
                whiteListURLs.add("/" + applicationName + whiteList.getUrls().get(i));
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 白名单拦截
        if (whiteListURLs.indexOf(req.getRequestURI()) >= 0) {
            filterChain.doFilter(request , response);
            return;
        }

        // 令牌获得
        String token = ObjectUtils.defaultIfNull(req.getHeader(CommonConstants.CONTEXT_TOKEN), req.getParameter(CommonConstants.CONTEXT_TOKEN));

        // 令牌不存在
        if (StringUtil.isEmpty(token)) {
            LOGGER.warn("token不存在,鉴权失败!  url: " + req.getRequestURL());
            String responseJson = JSONUtils.NON_NULL
                    .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0001).toResult());
            outFail(resp, responseJson);
            throw new RuntimeException("token鉴权异常");
        }

        try {
            // 本地鉴权
            Jwt.JwtBean bean = JwtUtils.parseToken(token);

            // 系统日期与令牌时间戳比较
            boolean bool = JwtUtils.verifyToken(bean);
            if (!bool) {
                LOGGER.warn("token已过期,鉴权失败!  url: " + req.getRequestURL());
                String responseJson = JSONUtils.NON_NULL
                        .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0002).toResult());
                outFail(resp, responseJson);
                throw new RuntimeException("token已过期,鉴权失败~");
            }

            // 非白名单用户需要踢用户
            if (!whiteList.getAccounts().contains(bean.getAccount()) /*|| !whiteList.getAccounts().contains(bean.getAccount().substring(0, 5))*/) {
                // redis时间戳
                String userMark = StringUtil.getString(
                        redisTemplate.opsForValue().get( applicationName + ":" +
                                (LoginTypeEnum.PC.getCode().equals(bean.getLoginType())
                                        ? RedisEnum.TOKEN_EXPIRES_ACCOUNT_PC.getCode() : RedisEnum.TOKEN_EXPIRES_ACCOUNT_APP.getCode()
                                ) + bean.getAccount()));

                // 令牌时间戳与redis时间戳不一致，踢用户
                if (!userMark.equals(StringUtil.getString(bean.getKv().get(CommonConstants.USER_MARK)))) {
                    LOGGER.warn("账号重复使用,强制退出. 账号: " + bean.getAccount() + ", 旧mark: "
                            + StringUtil.getString(bean.getKv().get(CommonConstants.USER_MARK)) + ", 新mark: " + userMark);
                    String responseJson = JSONUtils.NON_NULL
                            .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0004).toResult());
                    outFail(resp, responseJson);
                    throw new RuntimeException("账号重复使用,强制退出. 账号: " + bean.getAccount());
                }
            }

            // 用户在线状态更新(查询在线用户使用）
            redisTemplate.opsForZSet().add(applicationName + ":" +
                            (LoginTypeEnum.APP.getCode().equals(bean.getLoginType()) ? RedisEnum.ONLINE_ACCOUNTS_PC.getCode() : RedisEnum.ONLINE_ACCOUNTS_APP.getCode()),
                    bean.getAccount(),
                    System.currentTimeMillis());

            // 登录用户信息，从缓存获得，没有查数据库
            UserInfo userInfo = authService.getUserInfoByAccount(bean.getAccount(), bean.getIsSuperadmin());

            // 生成Security权限并放入ThreadLocal
            UserAuthorizeInfo userInfoDTO = new UserAuthorizeInfo(userInfo);
            // 获取权限
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userInfoDTO,
                    null, userInfoDTO.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 创建新令牌 (时间戳是登录时的时间戳）
            String newToken = Jwt.create()
                    .setLoginType(bean.getLoginType())
                    .setAccount(bean.getAccount())
                    .setUserName(bean.getUserName())
                    .setExpires(bean.getExpires())
                    .setIsSuperadmin(bean.getIsSuperadmin())
                    .addAllKv(bean.getKv()).build().sign();

            resp.setHeader(CommonConstants.CONTEXT_TOKEN, newToken);

            LOGGER.exit("JwtAuthenticationTokenFilter.doFilterInternal", "响应token: " + newToken);

            // 放行
            filterChain.doFilter(request , response);

        } catch (Exception ex) {
            String msg = StringUtil.getErrorText(ex);
            String responseJson = JSONUtils.NON_NULL
                    .toJSONString(Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0100).toResult());
            outFail(resp, responseJson);
            LOGGER.error("token鉴权异常, ex: " + msg);
          //  LOGGER.exit(methodName, org.apache.commons.lang3.StringUtils.EMPTY);
            throw new RuntimeException("token鉴权异常");
        }



    }

    /**
     * 输出响应流
     *
     * @author
     * @param resp 响应对象, message 响应消息
     * @return void
     * @throws IOException
     **/
    private void outFail(HttpServletResponse resp, String message) throws IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        resp.setDateHeader("expries", -1);
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        PrintWriter writer = resp.getWriter();
        writer.write(message);
        writer.flush();
        writer.close();
    }

}
