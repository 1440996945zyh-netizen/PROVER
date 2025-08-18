package com.yy.ppm.system.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Alias(value = "oper")
public class SysOperLogPO  extends BasePO {

    private static final long serialVersionUID = 400609988311338447L;

    /** 日志主键 */
    @NotNull(message = "主键ID不能为空")
    private Long operId;

    /** 操作模块 */
    private String title;

    /** 操作类型 */
    private String businessType;

    /** 业务类型数组 */
    private Integer[] businessTypes;

    /** 请求方法 */
    private String method;

    private String methodC;
    /** 请求方式 */
    private String requestMethod;

    /** 操作人类别 */
    private String operType;

    /** 操作人员 */
    private String operUserName;

    /** 操作人员id */
    private String operUserId;

    /** 请求url */
    private String operUrl;

    /** 操作地址 */
    private String operIp;

    /** 操作地点 */
    private String operLocation;

    /** 请求参数 */
    private String operParam;

    /** 返回参数 */
    private String jsonResult;

    /** 状态0正常 1异常 */
    private String status;

    /** 错误消息 */
    private String errorMsg;

    /** 操作时间 */
    private String operTime;

    private String statusWz;

    private String businessTypeWz;

    private String startTime;

    private String endTime;
    private Long costTime;

}
