package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.equipment.bean.po.MEquipmentInfoPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 设备台账信息DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class MEquipmentInfoDTO extends MEquipmentInfoPO {

    private static final long serialVersionUID = 1L;

    /**
     * 使用部门名称（关联查询）
     */
    private String useOrgName;

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
     * 特种设备备注
     */
    private String specialRemark;

    /**
     * 设备全景图文件ID列表
     */
    private java.util.List<Long> panoramaImageIds;

    /**
     * 设备方位图文件ID列表
     */
    private java.util.List<Long> orientationImageIds;

    /**
     * 设备主要附属物文件ID列表
     */
    private java.util.List<Long> accessoryImageIds;
}

