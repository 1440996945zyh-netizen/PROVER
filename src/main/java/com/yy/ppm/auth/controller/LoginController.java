package com.yy.ppm.auth.controller;


import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.common.collect.Maps;
import com.yy.common.util.RSAUtils;
import com.yy.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.CommonConstants;
import com.yy.common.enums.RedisEnum;
import com.yy.common.enums.Response;
import com.yy.common.enums.Response.GateWayCode;
import com.yy.common.jwt.Jwt;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.HttpRequestUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.auth.bean.dto.UserAuthorizeInfo;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.auth.enums.LoginTypeEnum;
import com.yy.ppm.auth.service.AuthService;
import com.yy.ppm.auth.service.LoginService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 系统登录
 **/
@RestController
@RequestMapping(value = "/api/internal")
@Validated
@Tag(name = "用户管理接口",description = "用户信息接口类")
public class LoginController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(LoginController.class);

	/**
	 * token超时时间,单位/分钟
	 **/
	@Value("${yy.token.expires}")
	private short expires;

	@Value("${spring.application.name}")
	private String applicationName;

	private final LoginService loginService;
	private final AuthService authService;
	private final RedisTemplate<String, String> redisTemplate;

	public LoginController(
			LoginService loginService,
			AuthService authService,
			RedisTemplate<String, String> redisTemplate
	) {
		this.loginService = loginService;
		this.authService = authService;
		this.redisTemplate = redisTemplate;
	}
	/**
	 * 系统登录 PC端
	 *
	 * @author
	 * @param account 登录账号信息
	 * @param result  校验绑定对象
	 * @param req     请求对象
	 * @param resp    响应对象
	 * @return 响应结果
	 */
	@PostMapping("/login")
	public Map<String, Object> login(@RequestBody @Validated UserInfo account, BindingResult result, HttpServletRequest req,
                                     HttpServletResponse resp) {

		final String methodName = "LoginController:login";
		LOGGER.enter(methodName, "PC登录请求[start],account: " + account);
		String environmental = "当前环境："+ SpringContextUtils.getActiveProfile();
		RSAUtils rsa = new RSAUtils();
		// 后端获取私钥
		String privateKey = rsa.myPrivateKey;
		// 获得RSA类型的私钥
		RSAPrivateKey rsaPrivateKey = null;
		try {
			rsaPrivateKey = RSAUtils.getPrivateKey(privateKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		// 使用私钥解密经过前端加密用户输入的密文
		String Decrypt_psw = RSAUtils.privateDecrypt(account.getPasswd(), rsaPrivateKey);
		System.out.println("#########"+Decrypt_psw);
		account.setPasswd(Decrypt_psw);

		if (result.hasErrors() && result.getFieldError()!=null) {
			String msg = result.getFieldError().getDefaultMessage();
			LOGGER.warn("登录校验失败,msg: " + msg);
			return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E0200).out(msg).toResult(environmental);
		}

		long currentTime = System.currentTimeMillis();

		// 后台登录处理
		UserInfo sysAcc = loginService.login(account);

		LOGGER.info("登录用户信息--->" + sysAcc);

		// 在线人数集合，account:时间戳
		redisTemplate.opsForZSet().add(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_PC.getCode(), account.getUserAccount(), currentTime);
		// YYY_UQ_ACCOUNT_PC_xxx:时间戳，用于防止一个账户多次登录
		redisTemplate.opsForValue().set(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_PC.getCode() + account.getUserAccount(),
				String.valueOf(currentTime));
		// 生成token
		String token = Jwt.create()
				.setLoginType(LoginTypeEnum.PC.getCode())
				.setAccount(account.getUserAccount())
				.setUserName(sysAcc.getUserName())
				.setExpires(expires)
				.setIsSuperadmin(sysAcc.getIsSuperadmin())
				.addKv(CommonConstants.REQUEST_IP, HttpRequestUtils.getRemoteAddrIp(req))
				.addKv(CommonConstants.USER_MARK, String.valueOf(currentTime)).build().sign();

		resp.setHeader(CommonConstants.CONTEXT_TOKEN, token);
		LOGGER.info("登录Token: " + token);

		LOGGER.exit(methodName, "PC登录请求[end]");
		return Response.SUCCESS.newBuilder().toResult(environmental);
	}

	/**
	 * 系统登录 APP端
	 *
	 * @author
	 * @param account 登录账号信息
	 * @param result  校验绑定对象
	 * @param req     请求对象
	 * @param resp    响应对象
	 * @return 响应结果
	 */
	@PostMapping("/loginApp")
	public Map<String, Object> loginApp(@RequestBody @Validated UserInfo account, BindingResult result, HttpServletRequest req,
									 HttpServletResponse resp) throws InvalidKeySpecException, NoSuchAlgorithmException {

		final String methodName = "LoginController:login";
		LOGGER.enter(methodName, "APP登录请求[start],account: " + account);

		if (result.hasErrors() && result.getFieldError()!=null) {
			String msg = result.getFieldError().getDefaultMessage();
			LOGGER.warn("登录校验失败,msg: " + msg);
			return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E0200).out(msg).toResult();
		}
		if(account.getPasswd().length() > 20){
			RSAUtils rsa = new RSAUtils();
			// 后端获取私钥
			String privateKey = rsa.myPrivateKey;
			// 获得RSA类型的私钥
			RSAPrivateKey rsaPrivateKey = RSAUtils.getPrivateKey(privateKey);
			// 使用私钥解密经过前端加密用户输入的密文
			String Decrypt_psw = RSAUtils.privateDecrypt(account.getPasswd(), rsaPrivateKey);
			System.out.println("#########"+Decrypt_psw);
			account.setPasswd(Decrypt_psw);
		}
		long currentTime = System.currentTimeMillis();

		// 后台登录处理
		UserInfo sysAcc = loginService.login(account);

		LOGGER.info("登录用户信息--->" + sysAcc);
		// 在线人数集合，account:时间戳
		redisTemplate.opsForZSet().add(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_APP.getCode(), account.getUserAccount(), currentTime);
		// YYY_UQ_ACCOUNT_PC_xxx:时间戳，用于防止一个账户多次登录
		redisTemplate.opsForValue().set(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_APP.getCode() + account.getUserAccount(),
				String.valueOf(currentTime));
		// 生成token
		String token = Jwt.create()
				.setLoginType(LoginTypeEnum.APP.getCode())
				.setAccount(account.getUserAccount())
				.setUserName(sysAcc.getUserName())
				.setExpires(expires)
				.setIsSuperadmin(sysAcc.getIsSuperadmin())
				.addKv(CommonConstants.REQUEST_IP, HttpRequestUtils.getRemoteAddrIp(req))
				.addKv(CommonConstants.USER_MARK, String.valueOf(currentTime)).build().sign();

		resp.setHeader(CommonConstants.CONTEXT_TOKEN, token);
		LOGGER.info("登录Token: " + token);

		LOGGER.exit(methodName, "APP登录请求[end]");
		return Response.SUCCESS.newBuilder().toResult();
	}

	/**
	 * 系统登录 小程序端
	 *
	 * @author
	 * @param account 登录账号信息
	 * @param result  校验绑定对象
	 * @param req     请求对象
	 * @param resp    响应对象
	 * @return 响应结果
	 */
	@PostMapping("/loginApplet")
	public Map<String, Object> loginApplet(@RequestBody @Validated UserInfo account, BindingResult result, HttpServletRequest req,
									 HttpServletResponse resp) {

		final String methodName = "LoginController:login";
		LOGGER.enter(methodName, "APP登录请求[start],account: " + account);

		if (result.hasErrors() && result.getFieldError()!=null) {
			String msg = result.getFieldError().getDefaultMessage();
			LOGGER.warn("登录校验失败,msg: " + msg);
			return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E0200).out(msg).toResult();
		}

		long currentTime = System.currentTimeMillis();

		// 后台登录处理
		UserInfo sysAcc = loginService.login(account);

		LOGGER.info("登录用户信息--->" + sysAcc);
		// 在线人数集合，account:时间戳
		redisTemplate.opsForZSet().add(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_APP.getCode(), account.getUserAccount(), currentTime);
		// YYY_UQ_ACCOUNT_PC_xxx:时间戳，用于防止一个账户多次登录
		redisTemplate.opsForValue().set(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_APP.getCode() + account.getUserAccount(),
				String.valueOf(currentTime));
		// 生成token
		String token = Jwt.create()
				.setLoginType(LoginTypeEnum.APP.getCode())
				.setAccount(account.getUserAccount())
				.setUserName(sysAcc.getUserName())
				.setExpires(expires)
				.setIsSuperadmin(sysAcc.getIsSuperadmin())
				.addKv(CommonConstants.REQUEST_IP, HttpRequestUtils.getRemoteAddrIp(req))
				.addKv(CommonConstants.USER_MARK, String.valueOf(currentTime)).build().sign();

		resp.setHeader(CommonConstants.CONTEXT_TOKEN, token);
		LOGGER.info("登录Token: " + token);

		LOGGER.exit(methodName, "APP登录请求[end]");
		Map<String,Object> res = Maps.newHashMap();
		res.put("code",0000);
		res.put("msg","SUCCESS");
		res.put("token",token);
		return res;
	}

	/**
	 * 系统登录 RUNPILE端
	 * @author
	 * @param account 登录账号信息
	 * @param result  校验绑定对象
	 * @param req     请求对象
	 * @param resp    响应对象
	 * @return 响应结果
	 */
	@PostMapping("/loginRunPlie")
	public Map<String, Object> loginRunPlie(@RequestBody @Validated UserInfo account, BindingResult result, HttpServletRequest req,
									 HttpServletResponse resp) {

		final String methodName = "LoginController:loginRunPlie";
		LOGGER.enter(methodName, "跑垛机登录请求[start],account: " + account);

		if (result.hasErrors() && result.getFieldError()!=null) {
			String msg = result.getFieldError().getDefaultMessage();
			LOGGER.warn("登录校验失败,msg: " + msg);
			return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E0200).out(msg).toResult();
		}

		long currentTime = System.currentTimeMillis();

		// 后台登录处理
		UserInfo sysAcc = loginService.login(account);

		LOGGER.info("登录用户信息--->" + sysAcc);
		// 在线人数集合，account:时间戳
		redisTemplate.opsForZSet().add(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_APP.getCode(), account.getUserAccount(), currentTime);
		// YYY_UQ_ACCOUNT_PC_xxx:时间戳，用于防止一个账户多次登录
		redisTemplate.opsForValue().set(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_APP.getCode() + account.getUserAccount(),
				String.valueOf(currentTime));
		// 生成token
		String token = Jwt.create()
				.setLoginType(LoginTypeEnum.APP.getCode())
				.setAccount(account.getUserAccount())
				.setUserName(sysAcc.getUserName())
				.setExpires(expires)
				.setIsSuperadmin(sysAcc.getIsSuperadmin())
				.addKv(CommonConstants.REQUEST_IP, HttpRequestUtils.getRemoteAddrIp(req))
				.addKv(CommonConstants.USER_MARK, String.valueOf(currentTime)).build().sign();

		resp.setHeader(CommonConstants.CONTEXT_TOKEN, token);
		LOGGER.info("登录Token: " + token);

		LOGGER.exit(methodName, "跑垛机登录请求[end]");
		return Response.SUCCESS.newBuilder().toResult();
	}

	/**
	 * 系统退出 PC端
	 * @return
	 */
	@PostMapping("/logout")
	public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response) {
		final String methodName = "LoginController:logout";
		LOGGER.enter(methodName + "[start]");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {

			UserAuthorizeInfo userAuthorizeInfo = (UserAuthorizeInfo) auth.getPrincipal();

			// 移除在线用户
			redisTemplate.opsForZSet().remove(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_PC.getCode(), userAuthorizeInfo.getUserIno().getUserAccount());

			redisTemplate.delete(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_PC.getCode()
					+ userAuthorizeInfo.getUserIno().getId());

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		response.setHeader(CommonConstants.CONTEXT_TOKEN, "");

		LOGGER.exit(methodName);
		return Response.SUCCESS.newBuilder().out("退出").toResult();
	}


	/**
	 * 系统退出 APP端
	 * @return
	 */
	@PostMapping("/logoutApp")
	public Map<String, Object> logoutApp(HttpServletRequest request, HttpServletResponse response) {
		final String methodName = "LoginController:logoutApp";
		LOGGER.enter(methodName + "[start]");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {

			UserAuthorizeInfo userAuthorizeInfo = (UserAuthorizeInfo) auth.getPrincipal();

			// 移除在线用户
			redisTemplate.opsForZSet().remove(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_APP.getCode(), userAuthorizeInfo.getUserIno().getUserAccount());

			redisTemplate.delete(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_APP.getCode()
					+ userAuthorizeInfo.getUserIno().getId());

			new SecurityContextLogoutHandler().logout(request, response, auth);
		}

		response.setHeader(CommonConstants.CONTEXT_TOKEN, "");

		LOGGER.exit(methodName);
		return Response.SUCCESS.newBuilder().out("退出").toResult();
	}

	/**
	 * 获取用户信息相关信息 角色、按钮权限、用户信息
	 * @return
	 */
	@GetMapping("/getPermissions")
	public Map<String, Object> getInfo(){
		final String methodName = "LoginController:getInfo";
		LOGGER.enter(methodName, "登录请求[start]");

		LOGGER.exit(methodName);
		return Response.SUCCESS.newBuilder().toResult(authService.getUserPermissionAndRole());
	}
}
