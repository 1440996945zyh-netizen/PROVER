package com.yy.ppm.auth.bean.dto;

import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 用户信息DTO
 * @author 成龙
 * @date 2021年2月19日14:13:03*/
@Getter
@Setter
@ToString
public class UserInfo extends BasePO implements Serializable {
    private static final long serialVersionUID = 3803991192565776724L;
    private Long id;
    private String userAccount;
    private String passwd;
    private String userName;
    private String userType;
    private Long deptId;
    private String deptName;
    private String email;
    private Long status;
    private Long sortNum;
    private String remark;
    private Date psdUpdDate;
    private Long sex;
    private String tel;
    private String mobile;
    private String address;
    private Long deleted;
    private String isSuperadmin;
    private Long userSource;
    private String posts;
    private String postName;
    private List<HashMap<String, Object>> roleList;

    /**组织机构编号，用于懒加载，四位一层，如0001，它的子组织为00010001-00019999，，00010001的子组织为000100010001-000100019999，以此类推 */
    private String deptNo;

    /** 角色编号， 资源（菜单）编号 */
    private List<String> roles;
    private List<String> permissions;

}
