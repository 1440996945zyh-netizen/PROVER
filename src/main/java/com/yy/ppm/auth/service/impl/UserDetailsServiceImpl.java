package com.yy.ppm.auth.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.HttpRequestUtils;
import com.yy.common.util.LocationUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.auth.bean.dto.UserAuthorizeInfo;
import com.yy.ppm.auth.mapper.AuthMapper;
import com.yy.ppm.auth.service.UserCacheService;
import com.yy.ppm.system.bean.dto.SysLoginLogDTO;
import com.yy.ppm.system.mapper.SysLoginLogMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(UserDetailsServiceImpl.class);

    @Resource
    AuthMapper authMapper;
    @Resource
    SysLoginLogMapper sysLoginLogMapper;


    private final HttpServletRequest request;
    private final UserCacheService userCacheService;
    private final Snowflake snowflake;
    public UserDetailsServiceImpl(
            Snowflake snowflake,
            HttpServletRequest request,
            UserCacheService userCacheService
    ) {
        this.userCacheService = userCacheService;
        this.request = request;
        this.snowflake = snowflake;
    }

    @Override
    public UserAuthorizeInfo loadUserByUsername(String accNo) throws UsernameNotFoundException {

        final String methodName = "AuthServiceImpl:verifyAcc";
        LOGGER.enter(methodName, "登录用户验证");

        UserInfo account = authMapper.getUserInfoByAccount(accNo);

        if (account == null) {
            // 保存登录日志
            saveLog(accNo);
            throw new BusinessRuntimeException("账号或密码错误~");
        }

        // 重置缓存
        userCacheService.cleanCacheByAccNo(account.getUserAccount());

        LOGGER.exit(methodName, StringUtils.EMPTY);

        return new UserAuthorizeInfo(account);

    }

    private void saveLog(String accNo) {

        // 插入登录日志
        SysLoginLogDTO mLoginLog = new SysLoginLogDTO();

        mLoginLog.setId(snowflake.nextId());
        mLoginLog.setAccNo(accNo);
        mLoginLog.setCommonInfo(mLoginLog);
        mLoginLog.setStatus("失败");
        mLoginLog.setErrorMsg("账号或密码错误");

        mLoginLog.setLoginTime(new Date());
        String loginIp = HttpRequestUtils.getRemoteAddrIp(request);
        mLoginLog.setLoginIp(loginIp);
//        mLoginLog.setChannelType();
        mLoginLog.setUqMark(snowflake.nextId());
        String uaStr = request.getHeader("User-Agent");
        UserAgent ua = UserAgentUtil.parse(uaStr);
        mLoginLog.setOs(ua.getOs().getName());
        mLoginLog.setBrowser(ua.getBrowser().getName());
        mLoginLog.setLocation(LocationUtils.getLocation(loginIp));

        sysLoginLogMapper.insert(mLoginLog);
    }

}
