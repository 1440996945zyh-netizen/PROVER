package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 外修单位合同实体
 *
 * @author zhuhao
 * @date 2020/7/22
 * @description 描述
 **/
@Data
public class EMEquipRepairContractDTO extends BasePO implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 外修单位id
     */
    private Long externalCompanyId;

    /**
     * 外修单位名称
     */
    private String unitName;

    /**
     * 云生态平台social_no号
     */
    private String externalCompanyCode;

    /**
     * 负责人
     */
    private String principal;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 维修范围
     */
    private String repairType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 委外合同开始期限
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date contractDateStart;

    /**
     * 委外合同结束期限
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date contractDateEnd;


    private Long companyId;
    private Long userOrgId;

    private String serviceCompanies;  // 服务公司ids

    //1:企业  2:个人
    private String  entityType;
    //1:内部  2:外部
    private String outType;


    //总单位数
    private int unitNameSum;
    //内部单位数
    private int inSunm;
    //外部单位数
    private int outSum;
}
