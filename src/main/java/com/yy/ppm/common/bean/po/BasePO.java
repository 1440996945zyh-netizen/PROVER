package com.yy.ppm.common.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yy.ppm.master.bean.dto.FieldRemark;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 基础bean
 *
 * @author
 **/
@Setter
@Getter
@EqualsAndHashCode
public class BasePO implements Serializable {

    private static final long serialVersionUID = -6734686200034099011L;

    /**
     * 录入人
     */
    @FieldRemark(value = "创建人")
    private Long createBy;

    /**
     * 录入人姓名
     */
    @FieldRemark(value = "创建人姓名")
    private String createByName;

    /**
     * 录入时间
     */
    @FieldRemark(value = "创建时间")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @FieldRemark(value = "更新者")
    private Long updateBy;

    /**
     * 更新者姓名
     */
    @FieldRemark(value = "更新者姓名")
    private String updateByName;

    /**
     * 更新时间
     */
    @FieldRemark(value = "更新时间")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 登录用户名
     */
    @FieldRemark(value = "用户id")
    @JsonIgnore
    private Long loginUserId;

    /**
     * 登录用户名
     */
    @FieldRemark(value = "用户名")
    @JsonIgnore
    private String loginUserName;

    /**
     * 登录用户类型
     */
    @FieldRemark(value = "用户类型")
    @JsonIgnore
    private String loginUserType;

    /**
     * 登录时间
     */
    @FieldRemark(value = "操作时间")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonIgnore
    private Date now;

}
