package com.yy.ppm.system.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.system.bean.po.SysUserPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户信息DTO
 * @author 张超
 * @date 2021年2月19日14:13:03*/
@Getter
@Setter
@ToString
public class SysUserDTO extends SysUserPO implements Serializable {
    private static final long serialVersionUID = 3803991192565776724L;

    /** 账号状态Str */
    private String statusLabel;
    /** 管理员状态 */
    private String isSuperadminLabel;
    /** 部门名称 */
    private String deptName;
    /** 角色List */
    private List<Long> roleIds;
    /** 角色名称 */
    private String roleNms;

    /** 最后操作时间 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastRequestTime;

    /**
     * 修改密码
     */
    private String oldPassword;
    private String newPassword;

    /** 用户id */
    private Long operatorsId;
    /** 用户名称 */
    private String operatorsName;

    private Long workPlanId;


}
