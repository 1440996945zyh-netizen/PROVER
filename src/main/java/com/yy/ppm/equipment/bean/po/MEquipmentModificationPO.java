package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 设备改造记录PO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MEquipmentModificationPO extends BasePO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属设备ID
     */
    private Long equipId;

    /**
     * 改造类型编码
     */
    private String modifyTypeCode;

    /**
     * 改造类型名称
     */
    private String modifyTypeName;

    /**
     * 改造厂家
     */
    private String modifyManufacturer;

    /**
     * 改造时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date modifyTime;

    /**
     * 改造内容
     */
    private String modifyContent;

    /**
     * 改造备注
     */
    private String modifyRemark;

    /**
     * 删除人ID
     */
    private Long deleteBy;

    /**
     * 删除人姓名
     */
    private String deleteByName;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
}

