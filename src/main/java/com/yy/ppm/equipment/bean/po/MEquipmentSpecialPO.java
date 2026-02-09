package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 特种设备PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MEquipmentSpecialPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 设备ID
     */
    private Long equipId;

    /**
     * 特种设备注册码
     */
    private String particularRegistrationCode;

    /**
     * 特种设备检查周期
     */
    private Long specialDiscoverCycle;

    /**
     * 证书类别
     */
    private String certifiType;

    /**
     * 证书类型名称
     */
    private String certifiTypeName;

    /**
     * 证书编号
     */
    private String certifiCode;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    /**
     * 证书所属人
     */
    private String certifiUser;

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
     * 备注
     */
    private String remark;
}

