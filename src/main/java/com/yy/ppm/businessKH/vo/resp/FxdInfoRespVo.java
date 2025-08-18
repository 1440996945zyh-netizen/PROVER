package com.yy.ppm.businessKH.vo.resp;
/**
 * @ClassName CargoInfoSearchReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 海关放行查询出参VO
 * @createTime 2022-05-09 14:35
 */
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class FxdInfoRespVo implements Serializable {

    private String bgdh;//报关单号
    private String flag;//货权持有人
    private String ywchm;//英文船名(*)
    private String tdh;//提单号
    private BigDecimal fxshl;//放行数量
    @DateTimeFormat(pattern = "yyyy-MM-dd ")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date fxrq;//放行日期
    private String hghc;//海关航次
    private String sbdwmch;//申报单位名称
    private String jydwmch;//经营单位名称
    private String tghwmch;//通关货名
    private Long tgjsh;//通关件数
    private String dlhc;//代理航次
    private String imo;//imo
    private String xcdId;//海关id

    private String fxdvalue;//用于前端筛选

}
