package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.common.bean.po.SysFilePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 外修单位合同实体
 *
 * @author zhuhao
 * @date 2020/7/22
 * @description 描述
 **/
@Data
public class EMEquipRepairUserDetailPO extends BasePO implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 维修单位id
     */
    private Long repairUserId;

    /**
     * 维修人名
     */
    private String certificateName;

    /**
     * 身份证
     */
    private String certificateType;

    /**
     * phone
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date certificateTime;


}
