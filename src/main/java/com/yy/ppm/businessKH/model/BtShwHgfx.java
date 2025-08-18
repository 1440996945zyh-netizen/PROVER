package com.yy.ppm.businessKH.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName BtShwHgfx.java
 * @Description TODO
 * @createTime 2022年05月09日 17:17:00
 */
@Data
@TableName("BT_SHW_HGFX")
@KeySequence("CONFIG_SEQ")
public class BtShwHgfx {
    private Long id;//ID（唯一标识）(*)，使用序列
    private String cargokey;//单票货主键（卸船）
    private Long xh;//序号
    private String ywchm;//英文船名(*)
    private String zygsdm;//公司代码(*)CT_ZYGS
    private String zygsdmch;//
    private String hwdmZhl;//货物中类代码
    private String hwmchZhl;//货物中类名称
    private String hwdmXl;//集团货物小类代码
    private String hwmchXl;//集团货物小类名称
    private String hqcyrid;//货权持有人代码
    private String hqcyr;//货权持有人
    private BigDecimal zhl;//重量
    private BigDecimal fxshl;//放行数量
    private Date fxrq;//放行日期
    private Date dgrq;//抵港日期
    private String tdh;//提单号
    private String bgdh;//报关单号
    private String note;//备注
    private String lb;//类别
    private String hc;//航次(港内)
    private String hghc;//海关航次
    private String bh;//编号
    private String note1;//
    private String note2;//
    private String note3;//NOTE3
    private String note4;//
    private String sbdwmch;//申报单位名称
    private String jydwmch;//经营单位名称
    private String tghwmch;//通关货名
    private String zhwchm;//中文船名
    private Long tgjsh;//通关件数
    private BigDecimal khjsh;//控货件数
    private BigDecimal khvol;//控货体积
    private String dlhc;//代理航次
    private String imo;//
    private String xcdId;//
    private String createby;//创建人
    private String createbyid;//创建人ID
    private Date createon;//创建日期
    private String createGsid;//创建公司
    private String createGsmch;//创建公司
    private String createBmid;//创建部门ID
    private String createBmmch;//创建部门名称
    private String createGwid;//创建岗位
    private String createGwmch;//创建岗位



}
