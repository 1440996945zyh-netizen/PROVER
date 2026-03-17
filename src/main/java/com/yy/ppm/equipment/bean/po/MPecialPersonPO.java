package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 特种作业人员证书PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MPecialPersonPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目代号
     */
    private String certifiType;

    /**
     * 档案编号
     */
    private String certifiCode;

    /**
     * 作业项目
     */
    private String certificateName;

    /**
     * 证书编号
     */
    private String certifiNumber;

    /**
     * 证书所属人
     */
    private String certifiUser;
    private String certifiUserName;

    /**
     * 到期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expireDate;

    /**
     * 有效期
     */
    private Long validDate;

    /**
     * 证书状态
     */
    private String certifiState;

    /**
     * 批准时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date firstDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0-未删除，1-已删除）
     */
    private Long delFlag;

    /**
     * 所属部门
     */
    private Long useOrgId;

    /**
     * 所属部门名称
     */
    private String useOrgName;

    /**
     * 认证单位
     */
    private String certificationAuthority;

    /**
     * 考试机构
     */
    private String examOrg;

    /**
     * 是否聘用
     */
    private String isEmploy;

    /**
     * 删除人ID
     */
    private Long deleteBy;

    /**
     * 删除人姓名
     */
    private String deleteByName;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
}

