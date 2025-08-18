package com.yy.ppm.produce.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.master.bean.dto.FieldRemark;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据补录表(THqData)PO
 * @Description
 * @createTime 2025年04月24日 17:23:00
 */
@Data
public class THqDataPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -72459335360056972L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 喷漆编码
     */
    @FieldRemark(value = "喷漆编码")
    private String pqNo;
    /*
     * 货名id
     */
    @FieldRemark(value = "货名id")
    private Long hqCargoId;
    /**
     * 货名
     */
    @FieldRemark(value = "货名")
    private String cargoName;
    /**
     * 垛位
     */
    @FieldRemark(value = "垛位id")
    private Long hqYardId;
    /**
     * 垛位
     */
    @FieldRemark(value = "垛位")
    private String yardName;
    /**
     * 件数
     */
    @FieldRemark(value = "件数")
    private Long quantity;
    /**
     * 重量
     */
    @FieldRemark(value = "重量")
    private BigDecimal ton;
    /**
     * 长
     */
    @FieldRemark(value = "长")
    private BigDecimal hqLength;
    /**
     * 宽
     */
    @FieldRemark(value = "宽")
    private BigDecimal width;
    /**
     * 高
     */
    @FieldRemark(value = "高")
    private BigDecimal height;
    /**
     * 体积
     */
    @FieldRemark(value = "体积")
    private BigDecimal volume;
    /**
     * 入库时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FieldRemark(value = "入库时间")
    private Date inPortTime;
    /**
     * 入库人
     */
    @FieldRemark(value = "入库人")
    private String inPortName;
    /**
     * 出库时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @FieldRemark(value = "出库时间")
    private Date outPortTime;
    /**
     * 出库人
     */
    @FieldRemark(value = "出库人")
    private String outPortName;
    /**
     * 状态( 10：入库  20：出库)
     */
    @FieldRemark(value = "出入库状态")
    private String status;
    /**
     * 卸船航次
     */
    @FieldRemark(value = "卸船船名")
    private String inShipName;
    /**
     * 卸船航次
     */
    @FieldRemark(value = "卸船航次")
    private String inVoyage;
    @FieldRemark(value = "卸船，船名航次")
    private String inShipVoyage;
    /**
     * 装船航次
     */
    @FieldRemark(value = "装船船名")
    private String outShipName;
    /**
     * 装船航次
     */
    @FieldRemark(value = "装船航次")
    private String outVoyage;
    @FieldRemark(value = "装船，船名航次")
    private String outShipVoyage;
    /**
     * 删除状态（0：删除，1：未删除）
     */
    @FieldRemark(value = "删除状态")
    private String delFlag;

}

