package com.yy.ppm.businessKH.vo.resp;
/**
 * @ClassName CargoInfoSearchReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 海关放行查询出参VO
 * @createTime 2022-05-09 14:35
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.businessKH.model.BtShwHgfx;
import com.yy.ppm.businessKH.model.VwShwHgfx;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class HgfxRespVo extends VwShwHgfx implements Serializable {
    private String khfs;//
    private String ejzygsdm;//二级作业公司代码

}
