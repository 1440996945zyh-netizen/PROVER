package com.yy.ppm.businessKH.vo.req;
/**
 * @ClassName SaveOrUpdateHgfxReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 控货管理查询入参VO
 * @createTime 2022-04-24 14:35
 */
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaveOrUpdateHgfxReqVo {
    private Long id;//ID（唯一标识）(*)，使用序列
    private String cargokey;//单票货主键（卸船）
    private String hqcyrid;//货权持有人代码
    private String hqcyr;//货权持有人
    private String dgrq;//抵港日期
    private String note;//备注
    private BigDecimal khjsh;//控货件数
    private BigDecimal khvol;//控货体积
    private String bgdh;//报关单号
    private String tdh;//提单号
    private String createGwid;//创建岗位
    private String lb;//类别

    private String hwdmXl;//集团货物小类代码
    private String hwmchXl;//集团货物小类名称
    private String hwdmZhl;//货物中类代码
    private String hwmchZhl;//货物中类名称

    private BigDecimal fxshl;//放行数量
    private String fxrq;//放行日期
    private String zygsdm;//公司代码(*)CT_ZYGS

    //新增补充
    private String zhwchm;//中文船名
    private BigDecimal zhl;//重量
    private String tgs;//通关数
    private String khfs;//控货方式
    private String imo;//imo

    private String flag;//货权持有人
    private String ywchm;//英文船名(*)
    private String hc;//海关航次
    private String hghc;//海关航次
    private String sbdwmch;//申报单位名称
    private String jydwmch;//经营单位名称
    private String tghwmch;//通关货名
    private Long tgjsh;//通关件数
    private String dlhc;//代理航次
    private String xcdId;//海关id


}
