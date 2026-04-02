package com.yy.framework.exception;

import com.yy.common.enums.Response;
import com.yy.common.enums.Response.GateWayCode;
import com.yy.common.flowable.common.ServiceException;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;

/**
 * API网关异常处理器
 *
 * @author
 **/
@ControllerAdvice
@RestController
public class ApiGatewayGlobalExceptionHandler {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(ApiGatewayGlobalExceptionHandler.class);


	@Resource
	private SecurityUtils securityUtils;

	/**
	 * 断言数据校验异常
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { AssertException.class })
	public Map<String, Object> assertException(AssertException exception) {
		final String methodName = "assertException";
		LOGGER.error(methodName,
				"账号: " +  loginAccount()  + ", 系统校验断言异常message: " + StringUtil.getErrorText(exception));
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E9999).out("数据异常，请联系管理员！").toResult();
	}

	/**
	 * Security 权限控制异常
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { AccessDeniedException.class })
	public Map<String, Object> accessDeniedException(AccessDeniedException exception) {
		final String methodName = "accessDeniedException";

		LOGGER.error(methodName,
				"账号: " +  loginAccount()  + ", Security权限异常message: " + StringUtil.getErrorText(exception));
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E0101).toResult();
	}

	/**
	 * 数据操作异常
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { DataAccessException.class })
	public Map<String, Object> dataAccessException(DataAccessException exception) {
		final String methodName = "DataAccessException";
		LOGGER.error(methodName,
				"账号: " +  loginAccount()  +  ", 数据操作SQL异常message: " + StringUtil.getErrorText(exception));
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E0300).out("数据异常，请联系管理员！").toResult();
	}

	/**
	 * 数据校验通用异常
	 **/
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public Map<String, Object> constraintViolationException(ConstraintViolationException exception) {
		final String methodName = "constraintViolationException";
		// Fix:处理自定义消息
		StringBuilder message = new StringBuilder();

		message.append(exception.getConstraintViolations().stream().findFirst().map(ConstraintViolation::getMessageTemplate));
		LOGGER.error(methodName,
				"账号: " +  loginAccount()  + ", 业务数据校验异常message: " + StringUtil.getErrorText(exception)); //+ ShiroUtils.getAccNo()
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E0200)
				.out(StringUtils.defaultString(message.toString(), "数据异常，请联系管理员！")).toResult();
	}

	/**
	 * security
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { InternalAuthenticationServiceException.class })
	public Map<String, Object> AuthenticationServiceException(InternalAuthenticationServiceException exception) {
		final String methodName = "authenticationServiceException";
		LOGGER.error(methodName,
				"账号: " +  loginAccount() + ", 业务异常message: " + StringUtil.getErrorText(exception)); ///ShiroUtils.getAccNo() +
		String msg = StringUtils.defaultIfBlank(exception.getMessage(), "数据异常，请联系管理员！");
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E9996).out(msg).toResult();
	}

	/**
	 * 业务异常
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { BusinessRuntimeException.class })
	public Map<String, Object> businessRuntimeException(BusinessRuntimeException exception) {
		final String methodName = "businessRuntimeException";
		LOGGER.error(methodName,
				"账号: " +  loginAccount()  + ", 业务异常message: " + StringUtil.getErrorText(exception)); ///ShiroUtils.getAccNo() +
		String msg = StringUtils.defaultIfBlank(exception.getMessage(), "数据异常，请联系管理员！");
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E9996).out(msg).toResult();
	}

	/**
	 * 运行时异常
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { RuntimeException.class })
	public Map<String, Object> runtimeException(RuntimeException exception) {
		final String methodName = "runtimeException";
		LOGGER.error(methodName,
				"账号: " + loginAccount()  +  ", 系统运行时异常message: " + StringUtil.getErrorText(exception));
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E9997).out("数据异常，请联系管理员！").toResult();
	}

	/**
	 * 运行时异常
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { ConcurrentException.class })
	public Map<String, Object> concurrentException(ConcurrentException exception) {
		final String methodName = "concurrentException";
		LOGGER.error(methodName,
				"账号: "  +  loginAccount() + ", 并发异常message: " + StringUtil.getErrorText(exception));
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E9998).out("并发异常，请联系管理员！").toResult();
	}

	/**
	 * 兜底异常控制
	 *
	 * @author
	 **/
	@ExceptionHandler(value = { Exception.class })
	public Map<String, Object> exception(Exception exception) {
		final String methodName = "exception";
		LOGGER.error(methodName,
				"账号: " +  loginAccount()  + ", 未知异常message: " + StringUtil.getErrorText(exception));
		return Response.FAIL.newBuilder().addGateWayCode(GateWayCode.E9999).out("数据异常，请联系管理员！").toResult();
	}

	/**
	 * 专门处理 ServiceException (带有格式化参数的异常)
	 **/
	@ExceptionHandler(value = { ServiceException.class })
	public Map<String, Object> serviceException(ServiceException exception) {
		final String methodName = "serviceException";

		// 1. 记录日志
		LOGGER.error(methodName,
				"账号: " + loginAccount() + ", 格式化业务异常message: " + exception.getMessage());

		// 2. 构造返回结果
		String msg = StringUtils.defaultIfBlank(exception.getMessage(), "数据异常，请联系管理员！");

		// 注意：这里需要根据 ServiceException 的 getCode() 返回对应的枚举或数值
		return Response.FAIL.newBuilder()
				.addGateWayCode(GateWayCode.E9996)
				.out(msg)
				.toResult();
	}

	private String loginAccount() {
		return securityUtils.getUserInfo() == null ? "未登录" : securityUtils.getUserInfo().getUserAccount();
	}
}
