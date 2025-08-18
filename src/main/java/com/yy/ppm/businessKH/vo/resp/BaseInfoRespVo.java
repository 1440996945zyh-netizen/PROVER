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
public class BaseInfoRespVo implements Serializable {

    private String hthqcyrid;//货权持有人代码(合同的，前端用这个)
    private String hthqcyr;//货权持有人(合同的，前端用这个)
    private String hqcyrid;//货权持有人代码
    private String hqcyr;//货权持有人

    private String zygsdm;//作业公司代码
    private String zygsmch;//作业公司名称

    private String zhwchm;//中文船名
    private String ywchm;//英文船名
    private String hwdmXl;//货物名称
    private String hwmchXl;//货物代码
    private BigDecimal zhl;//重量
    @DateTimeFormat(pattern = "yyyy-MM-dd ")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date dgrq;//到港日期
    private String cargokey;//控货主键
    private String tgs;//通关数
    private String khfs;//控货方式
    private String imo;//imo
    private String hc;//航次(港内)

    private String value;//组合内容用于前端筛选
}
