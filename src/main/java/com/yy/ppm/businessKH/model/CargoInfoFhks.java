package com.yy.ppm.businessKH.model;
/**
 * @title
 * @description 分货控数表
 * @author lihuijie
 * @updateTime 2022-04-24 11:53
 * @throws
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("BT_SHW_CARGOINFO_FHKS")
public class CargoInfoFhks implements Serializable {

    /**
     * 货键
     */
    @TableId(type = IdType.INPUT)
    private String cargokey;

    /**
     * 作业公司ID
     */
    private String zygsdm;

    /**
     * 分货控数
     */
    private BigDecimal fhks;

    /**
     * 控货时间
     */
    private Date khrq;

    /**
     * 计划员
     */
    private String jhy;

    /**
     * 备注
     */
    private String note;

    /**
     * 备注1
     */
    private String note1;

    /**
     * 备注2
     */
    private String note2;

    /**
     * 备注3
     */
    private String note3;

    /**
     * 备注4
     */
    private String note4;

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
    private Date modifyon;
}
