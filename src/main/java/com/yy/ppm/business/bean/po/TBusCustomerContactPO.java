package com.yy.ppm.business.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class TBusCustomerContactPO extends BasePO implements Serializable {

    /** id*/
    private Long id;
    /** 客户ID */
    private Long customerId;
    /** 联系人姓名 */
    private String contactName;
    //生日
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date birthday;
    /** 手机号 */
    private String mobile;
    /** 是否发送短信（1是0否） */
    private String allowSms;
    //职务code
    private String officeCode;
    //职务name
    private String officeName;
    /** 性别（10男20女） */
    private String sex;
}
