package com.yy.ppm.system.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 个人中心 响应PO
 * @author FanQi
 * @version 1.0
 * @date 2023/5/5 16:15
 */

@Data
public class ProfileDTO implements Serializable {

    private Boolean admin;
    private String avatar;
    private String createBy;
    private String createTime;
    private String delFlag;
    private SysDeptDTO dept;
    private Long deptId;
    private String email;
    private String loginDate;
    private String loginIp;
    private String nickName;
    private String password;
    private String phonenumber;
    private String postIds;
    private String remark;
    private List<SysRoleDTO> roles;
    private String sex;
    private String status;
    private String updateBy;
    private String updateTime;
    private Long userId;
    private String userName;
}
