package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 设备变更记录DTO
 * @author system
 */
@Data
public class MEquipmentChangeLogDTO implements Serializable {

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
     * 变更类型：BASIC_INFO-设备基本信息，FINANCE_SUPPLY-财务/供货信息，SPECIAL_INFO-特种设备信息
     */
    private String changeType;

    /**
     * 变更类型名称
     */
    private String changeTypeName;

    /**
     * 变更人ID
     */
    private Long changeBy;

    /**
     * 变更人姓名
     */
    private String changeByName;

    /**
     * 变更时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date changeTime;

    /**
     * 变更详情列表
     */
    private List<MEquipmentChangeLogDetailDTO> detailList;
}

