package com.yy.ppm.flowable.bean.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
/**
 * @Description: 流程实例关联表单
 * @Author: hukang
 * @Date:   2025-11-07
 * @Version: V1.0
 */
@Getter
@Setter
@ToString
@TableName("sys_deploy_form")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysDeployForm implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    private String id;
    /**表单主键*/
    private String formId;
    /**流程实例主键*/
    private String deployId;
    /**流程实例节点主键*/
    private String nodeKey;
    /**流程实例节点名称*/
    private String nodeName;
    /**流程实例节点名称*/

    private String formFlag;
    /**创建人*/

    private String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")

    private Date createTime;
    /**更新人*/

    private String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")

    private Date updateTime;
    /**所属部门*/

    private String sysOrgCode;
}
