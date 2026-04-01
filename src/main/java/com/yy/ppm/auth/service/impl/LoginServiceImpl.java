package com.yy.ppm.auth.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.HttpRequestUtils;
import com.yy.common.util.LocationUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.auth.bean.dto.UserAuthorizeInfo;
import com.yy.ppm.auth.service.LoginService;
import com.yy.ppm.system.bean.dto.SysLoginLogDTO;
import com.yy.ppm.system.mapper.SysLoginLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 登录Service
 */
@Service
public class LoginServiceImpl implements LoginService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(LoginServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final SysLoginLogMapper sysLoginLogMapper;
    private final Snowflake snowflake;

    public LoginServiceImpl(
            AuthenticationManager authenticationManager,
            SysLoginLogMapper sysLoginLogMapper,
            Snowflake snowflake
    ) {
        this.authenticationManager = authenticationManager;
        this.sysLoginLogMapper = sysLoginLogMapper;
        this.snowflake = snowflake;
    }

    @Autowired
    private HttpServletRequest request;

    /**
     * 登录
     * @param accountDTO
     * @return
     */
    public UserInfo login(UserInfo accountDTO) {

        // 创建Authentication对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(accountDTO.getUserAccount() , accountDTO.getPasswd()) ;

        // 调用AuthenticationManager的authenticate方法进行认证
        Authentication authentication = null;

        // 插入登录日志
        SysLoginLogDTO mLoginLog = new SysLoginLogDTO();
        mLoginLog.setId(snowflake.nextId());
        mLoginLog.setUserId(accountDTO.getId());
        mLoginLog.setAccNo(accountDTO.getUserAccount());
        mLoginLog.setUserName(accountDTO.getUserName());
        mLoginLog.setCommonInfo(mLoginLog);

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

        try {
            // security 验证
            authentication = authenticationManager.authenticate(authenticationToken);

        } catch (BadCredentialsException e) {
            mLoginLog.setStatus("失败");
            mLoginLog.setErrorMsg("账号或密码错误");
            sysLoginLogMapper.insert(mLoginLog);
            throw new BusinessRuntimeException("账号或密码错误~");
        }

        // 停用的场合
        if (accountDTO.getStatus()!=null && CommonEnum.IsUsed.UNUSED.getCode().equals(accountDTO.getStatus().toString())) {
            mLoginLog.setStatus("失败");
            mLoginLog.setErrorMsg("账户已停用");
            sysLoginLogMapper.insert(mLoginLog);

            LOGGER.warn("账户已停用~");
            throw new BusinessRuntimeException("账户已停用~");
        }

        // 获取登录用户信息，并返回登录用户基本信息
        UserAuthorizeInfo userAuthorizeInfo = (UserAuthorizeInfo) authentication.getPrincipal();

        // 插入登录日志
        mLoginLog.setStatus("成功");
        sysLoginLogMapper.insert(mLoginLog);

        return userAuthorizeInfo.getUserIno();

    }

}
