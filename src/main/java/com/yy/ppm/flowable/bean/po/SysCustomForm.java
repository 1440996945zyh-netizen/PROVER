package com.yy.ppm.flowable.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 系统自定义表单表
 * @Author: hukang
 * @Date: 2025-11-10
 * @Version: V1.0
 */
@Getter
@Setter
@ToString
public class SysCustomForm implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private java.lang.String id;
    /**
     * 业务表单名称
     */
    private java.lang.String businessName;
    /**
     * 业务服务名称
     */
    private java.lang.String businessService;
    /**
     * 流程名称
     */
    private java.lang.String flowName;
    /**
     * 关联流程发布主键
     */
    private java.lang.String deployId;
    /**
     * 前端路由地址
     */
    private java.lang.String routeName;
    /**
     * 组件注入方法
     */
    private java.lang.String component;
    /**
     * 创建人
     */
    private java.lang.String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    private java.lang.String sysOrgCode;
}
