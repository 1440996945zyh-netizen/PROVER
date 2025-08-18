package com.yy.ppm.system.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 登录日志表(SysLoginLog)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 15:51:00
 */
@Data
public class SysLoginLogPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -24028596116615640L;

    /** 主键id */
    private Long id;
    /** 登录人Id */
    private Long userId;
    /** 登录人账号 */
    private String accNo;
    /** 登录人 */
    private String userName;
    /** 岗位 */
    private String post;
    /** 部门 */
    private String dept;
    /** 所属公司 */
    private String companyName;
    /** 登录时间 */
    private Date loginTime;
    /** 登录ip */
    private String loginIp;
    /** 登录渠道,字典:01-PC,02-安卓APP,03-IOS APP */
    private String channelType;
    /** 登录用户唯一标记 */
    private Long uqMark;
    /** 操作系统 */
    private String os;
    /** 浏览器 */
    private String browser;
    /** 登录地点 */
    private String location;
    /** 状态 */
    private String status;
    /** 错误信息 */
    private String errorMsg;

}

