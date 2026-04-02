package com.yy.framework.aspect;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson2.JSONObject;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.util.AddressUtils;
import com.yy.common.util.HttpRequestUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.annotation.Log;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.system.bean.dto.SysOperLogDTO;
import com.yy.ppm.system.bean.po.SysOperLogPO;
import com.yy.ppm.system.mapper.SysOperLogMapper;

import cn.hutool.core.lang.Snowflake;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/4/13 10:56
 */
@Component
@Aspect
public class LogAspect {

    @Resource
    private SysOperLogMapper sysOperLogMapper;

    @Resource
    private SecurityUtils securityUtils;


    private final HttpServletRequest request;

    private final Snowflake snowflake;

    public LogAspect(Snowflake snowflake,HttpServletRequest request){
        this.snowflake = snowflake;
        this.request = request;
    }

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    /** 计算操作消耗时间 */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new ThreadLocal<>();

//    @Value("${yyy.mqSwitch}")
    private Boolean mqSwitch = false;

    /**
     * 方法调用后触发 , 记录正常操作
     *
     * @param joinPoint
     * @throws ClassNotFoundException
     */
    @AfterReturning(pointcut = "@annotation(com.yy.framework.annotation.Log)", returning = "json_result")
    public void after(JoinPoint joinPoint, Object json_result) throws ClassNotFoundException {

        handleLog(joinPoint, json_result, null);
    }

    /**
     * 异常日志
     * @param joinPoint
     * @param e
     * @throws ClassNotFoundException
     */
    @AfterThrowing(pointcut = "@annotation(com.yy.framework.annotation.Log)", throwing = "e")
    public void AfterThrowing(JoinPoint joinPoint, Throwable e) throws ClassNotFoundException {
        handleLog(joinPoint, "", (Exception) e);///
    }

    /**
     * 整理数据
     * @param joinPoint
     * @param json_result
     * @param e
     * @throws ClassNotFoundException
     */
    protected void handleLog(JoinPoint joinPoint, Object json_result, Exception e) throws ClassNotFoundException {
        // 开始计时
        TIME_THREADLOCAL.set(System.currentTimeMillis());

        SysOperLogDTO oper = new SysOperLogDTO();
        //设置类名+方法名
        oper.setMethod(getMethodDesc(joinPoint).getMethod() + "." + getMethodDesc(joinPoint).getMethodC() + "()");

        // 操作说明
        // delete by zcc 2023/10/19
//        if(null !=  joinPoint.getSignature().getDeclaringType().getAnnotation(Api.class)){
//            oper.setTitle(((Api) joinPoint.getSignature().getDeclaringType().getAnnotation(Api.class)).value());
//        }else{
//            oper.setTitle("未设置业务");
//        }

        oper.setOperId(snowflake.nextId());
        //设置操作人员
        UserInfo userInfo = securityUtils.getUserInfo();
        if(ObjectUtils.isEmpty(userInfo)){
            userInfo = securityUtils.getNewUserInfo();
        }
        oper.setOperUserName(userInfo.getUserName());
        oper.setOperUserId(String.valueOf(userInfo.getId()));

        //设置操作时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oper.setOperTime(dateFormat.format(date));
        //设置业务类型

        Class<?> className = joinPoint.getTarget().getClass();

        String methodName = joinPoint.getSignature().getName();

        Class[] argClass = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();

        Method method = null;
        try {
            method = className.getMethod(methodName, argClass);
            // 判断是否存在@Log注解
            if (method.isAnnotationPresent(Log.class)) {
                oper.setBusinessType(method.getAnnotation(Log.class).value().getComment());
                oper.setTitle(method.getAnnotation(Log.class).title());
            }
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            oper.setBusinessType("未设置操作类型");
            oper.setTitle("未设置业务名称");
        }

        //设置url
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String url ="";
        if(attributes!=null  && attributes.getRequest().getRequestURL()!=null){
            url =  attributes.getRequest().getRequestURL().substring(21);
        }
        oper.setOperUrl(url);
        //设置请求方式
        if(attributes.getRequest()!=null){
            oper.setRequestMethod(attributes.getRequest().getMethod());
        }
        //设置ip地址
        String ip = HttpRequestUtils.getRemoteAddrIp(this.request);
        oper.setOperIp(ip);
        //设置操作类别
        oper.setOperType("PC");

        //设置操作地点
        String address = AddressUtils.getRealAddressByIP(ip);
        oper.setOperLocation(address);

        // 获取参数的信息，传入到数据库中。
        setRequestValue(joinPoint, oper);

        String json = JSONObject.toJSONString(json_result);
        oper.setJsonResult(json);

        if (e == null) {
            //设置状态
            oper.setStatus("正常");
        } else {
            oper.setStatus("异常");
            String msg = String.valueOf(e.getMessage());
            oper.setErrorMsg(msg);
        }
        // 设置消耗时间
        oper.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());
        // 到mq
        if(mqSwitch){

        }else{
            sysOperLogMapper.insert(oper);
        }
        TIME_THREADLOCAL.remove();
    }

    /**
     * 获取 注解中对方法的描述
     *
     * @return
     * @throws ClassNotFoundException
     */
    public static SysOperLogDTO getMethodDesc(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String operteContent = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    // 操作说明
                    operteContent = ((OperateTypeEnum)method.getAnnotation(Log.class).value()).getComment();
                    break;
                }
            }
        }
        SysOperLogDTO oper = new SysOperLogDTO();
        oper.setMethod(targetName);
        oper.setTitle(operteContent);
        oper.setMethodC(methodName);
        return oper;
    }


    /**
     * 获取请求的参数，放到log中
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLogPO oper) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        Map<String, String[]> map = attributes.getRequest().getParameterMap();
        if (map.size() != 0) {
            String params = JSONObject.toJSONString(map);
            oper.setOperParam(JSONObject.toJSONString(map));

        } else {
            Object args = joinPoint.getArgs();
            if (args != null) {
                String params = argsArrayToString(joinPoint.getArgs());
                oper.setOperParam(params);

            }
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (o != null) {
                    try {
                        Object jsonObj = JSONObject.toJSONString(o);
                        params += jsonObj.toString() + " ";
                    } catch (Exception e) {
                    }
                }
            }
        }
        return params.trim();
    }
}
