package com.yy.ppm.businessKH.model;
/**
 * @title
 * @description 控货信息视图
 * @author lihuijie
 * @updateTime 2022-04-24 11:53
 * @throws
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("VW_SHW_CARGOINFO")
public class CargoInfoVW implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 货键（唯一标识）(*)
     */
    private String cargokey;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 父货键（转货等非集港/卸船产生的货键的上一级货键）
     */
    private String fCargokey;

    /**
     * 原始货键（货键对应的最初集港/卸船货键）
     */
    private String oCargokey;

    /**
     * 货键来源（集港/卸船/转货/转运/筛选）
     */
    private String cargokeyType;

    /**
     * 货物最初集港运输方式（船舶/铁路/公路/管道/皮带）
     */
    private String cargoOri;

    /**
     * 最初集港计划日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date dgrqS;

    /**
     * 最终集港计划日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date dgrqE;

    /**
     * 作业公司代码
     */
    private String zygsdm;

    /**
     * 合同ID
     */
    private String htid;

    /**
     * 作业委托人代码
     */
    private String zywtrid;

    /**
     * 作业委托人
     */
    private String zywtr;

    /**
     * 通知单ID
     */
    private String tzhdid;

    /**
     * 航次
     */
    private String hc;

    /**
     * 船名
     */
    private String zhwchm;

    /**
     * 舱别
     */
    private String cb;

    /**
     * 集团小类货物代码
     */
    private String hwdm;

    /**
     * 集团小类货物名称
     */
    private String hwmch;

    /**
     * 货物标识（筛后块/筛后粉/地脚货等）
     */
    private String hwFlag;

    /**
     * 货权持有人代码
     */
    private String hqcyrid;

    /**
     * 货权持有人
     */
    private String hqcyr;

    /**
     * 控货方式（重量、件数、体积）
     */
    private String khfs;

    /**
     * 数量
     */
    @TableField("shl")
    private BigDecimal cargokeyshl;

    /**
     * 通关数
     */
    private BigDecimal tgs;

    /**
     * 加数合计（正数）
     */
    private BigDecimal sumJs;

    /**
     * 扣数合计（负数）
     */
    private BigDecimal sumKs;

    /**
     * 可发数（通关数+加数合计+扣数合计）
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
     * 控货日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date khrq;

    /**
     * 标志位（是否归档）
     */
    private String flag;

    /**
     * 备注
     */
    private String note;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
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
     * 修改人
     */
    private String modifyby;

    /**
     * 修改人ID
     */
    private String modifybyid;

    /**
     * 修改日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date modifyon;

    /**
     *
     */
    private String hthLsh;

    /**
     * 是否作废标志位（是/否）
     */
    private String flagZf;

    /**
     * 发站
     */
    private String fzh;

    /**
     * 货主ID
     */
    private String hzid;

    /**
     * 货主
     */
    private String hz;

    /**
     * 二级作业公司（作业区域）
     */
    private String ejzygsdm;

    /**
     * 船舶计划号（二公司）
     */
    private String chbjhh;

    /**
     * 单船执行合同ID
     */
    private String zxhtid;

    /**
     * 大类代码
     */
    private String dldm;

    /**
     * 大类名称
     */
    private String dlmch;

    /**
     * 中类代码
     */
    private String zhldm;

    /**
     * 中类名称
     */
    private String zhlmch;
}
