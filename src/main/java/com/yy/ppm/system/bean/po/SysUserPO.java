package com.yy.ppm.system.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息PO
 * @author yy
 * @date 2021年2月19日14:13:03*/
@Getter
@Setter
@ToString
public class SysUserPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1355478822322095866L;
    private Long id;
    private String userAccount;
    private String passwd;
    private String userName;
    private String userType;
    private Long deptId;
    private String email;
    private Long status;
    private Long sortNum;
    private String remark;
    private Date psdUpdDate;
    private Long sex;
    private String tel;
    private String mobile;
    private String isSuperadmin;
    private Long userSource;
    private String posts;
    /** 是否劳务 */
    private String isLabor;
    /** 岗位*/
    private String postCode;
    private String postName;
    /** 单位类型*/
    private String unitTypeCode;
    private String unitTypeName;

}
