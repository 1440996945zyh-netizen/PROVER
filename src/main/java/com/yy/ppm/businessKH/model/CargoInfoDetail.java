package com.yy.ppm.businessKH.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengjiaqi
 * @description 控货管理从表——创建人：郭立燕
 * @date 2021-04-25
 */
@Data
@TableName("BT_SHW_CARGOINFO_DETAIL")
@KeySequence("CONFIG_SEQ")
public class CargoInfoDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 货键（唯一标识）(*)
     */
    private String cargokey;

    /**
     * 类别（加数扣数/通知单/计划申报）
     */
    private String lb;

    /**
     * 变更类型(加数扣数类型)
     */
    private String type;

    /**
     * 数量
     */
    @TableField("shl")
    private BigDecimal cdshl;

    /**
     * 集港计划日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd hh24:mi:ss")
    private Date dgrq;

    /**
     * 通知单ID
     */
    private String tzhdid;

    /**
     * 计划数
     */
    private BigDecimal jhs;

    /**
     * 交接数
     */
    private BigDecimal jjs;

    /**
     * 备注
     */
    private String note;

    /**
     * 集疏港计划申报人ID
     */
    private String jhsbrid;

    /**
     * 可发数
     */
    private BigDecimal kfs;

    /**
     * 下达数
     */
    private BigDecimal xds;

    /**
     * 剩余数
     */
    private BigDecimal sys;

    /**
     * 创建人
     */
    private String createby;

    /**
     * 创建人ID
     */
    private String createbyid;

    /**
     * 创建日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd hh24:mi:ss")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createon;

    /**
     * 创建公司
     */
    private String createGsid;

    /**
     * 创建公司名称
     */
    private String createGsmch;

    /**
     * 创建部门ID
     */
    private String createBmid;

    /**
     * 创建部门名称
     */
    private String createBmmch;

    /**
     * 创建岗位ID
     */
    private String createGwid;

    /**
     * 创建岗位名称
     */
    private String createGwmch;

    /**
     * 序号
     */
    private Integer xh;

    /**
     * 通知单类别
     */
    private String tzhdlb;

    /**
     * 计划号
     */
    private String hthLsh;

    private String status;//流程状态

}
