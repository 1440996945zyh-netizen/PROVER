package com.yy.ppm.system.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 登录日志表(SysLoginLog)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年06月29日 15:51:00
 */
@Data
public class SysLoginLogSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -80096028896347040L;

    /** 开始时间 */
    private String beginTimes;
    /** 结束时间 */
    private String endTimes;
    /**登录人账号*/
    private String accNo;
    /**登录人*/
    private String userName;
    /**岗位*/
    private String post;
    /**部门*/
    private String dept;
    /**所属公司*/
    private String companyName;
    /**登录ip*/
    private String loginIp;
    /**登录渠道,字典:01-PC,02-安卓APP,03-IOS APP*/
    private String channelType;
    /**登录用户唯一标记*/
    private Long uqMark;
    /**操作系统*/
    private String os;
    /**浏览器*/
    private String browser;
    /**登录地点*/
    private String location;
    /**状态*/
    private String status;
    }

