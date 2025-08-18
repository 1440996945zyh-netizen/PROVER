package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据补录表(THqData)SearchDTO
 * @Description TODO
 * @createTime 2025年04月24日 17:23:00
 */
@Data
public class THqDataSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 334756464150770969L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 喷漆编码
     */
    private String pqNo;
    /**
     * 货名
     */
    private String cargoName;
    /**
     * 垛位
     */
    private String yardName;
    /**
     * 件数
     */
    private Long quantity;
    /**
     * 重量
     */
    private BigDecimal ton;
    /**
     * 入库时间
     */
    private Date inPortTime;
    /**
     * 入库人
     */
    private String inPortName;
    /**
     * 出库时间
     */
    private Date outPortTime;
    /**
     * 出库人
     */
    private String outPortName;
    /**
     * 状态
     */
    private String status;
    /**
     * 卸船航次
     */
    private String inShipName;
    /**
     * 卸船航次
     */
    private String inVoyage;
    /**
     * 装船航次
     */
    private String outShipName;
    /**
     * 装船航次
     */
    private String outVoyage;
    /**
     * 删除状态（0：删除，1：未删除）
     */
    private String delFlag;
    /**
     * 创建人
     */
    private String createByName;
    /**
     * 更新人
     */
    private String updateByName;
}

