package com.yy.ppm.businessKH.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName VwShwHgfx.java
 * @Description TODO
 * @createTime 2023年09月14日 17:05:00
 */
@Data
@TableName("VW_SHW_HGFX")
public class VwShwHgfx {
    private Long id;//ID（唯一标识）(*)，使用序列
    private String cargokey;//单票货主键（卸船）
    private Long xh;//序号
    private String lb;//
    private String zhwchm;//
    private String ywchm;//英文船名(*)
    private String hc;//
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date fxrq;//放行日期
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date dgrq;//抵港日期
    private String tdh;//提单号
    private String bgdh;//报关单号
    private String note;//备注
    private String createby;//创建人
    private String createbyid;//创建人ID
    private Date createon;//创建日期
    private String createGsid;//创建公司
    private String createGsmch;//创建公司
    private String createBmid;//创建部门ID
    private String createBmmch;//创建部门名称
    private String createGwid;//创建岗位
    private String createGwmch;//创建岗位
    private String hghc;//
    private String sbdwmch;//
    private String jydwmch;//
    private String tghwmch;//
    private Long tgjsh;//
    private BigDecimal khjsh;//
    private BigDecimal khvol;//
    private String khfs;//
    private String dlhc;//
    private String imo;//
    private String xcdId;//
    private String ejzygsdm;//二级作业公司代码
}
