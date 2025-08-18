package com.yy.ppm.system.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户信息查询DTO
 * @author 张超
 * @date 2021年2月19日14:13:03
 */
@Getter
@Setter
@ToString
public class SysUserSearchDTO extends PageParameter implements Serializable {
    private static final long serialVersionUID = 3803991192565776724L;
    /** 主键ID */
    private Long id;
    /** 账号 */
    private String userAccount;
    /** 账号或姓名 */
    private String accountOrUserNm;
    /** 用户姓名 */
    private String userName;
    /** 是否是管理员 */
    private String isSuperadmin;
    /** 账号状态 */
    private String status;
    /** 所属部门ID */
    private Long deptId;
    /** 所属部门名称 */
    private String deptName;
    /** 手机号 */
    private String mobile;
    /**是否劳保*/
    private String isLabor;

}
