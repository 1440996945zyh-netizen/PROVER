package com.yy.ppm.largescreen.bean.dto;


import com.yy.ppm.largescreen.bean.po.SShipTrendsPO;
import lombok.Data;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SShipTrends)DTO
 * @Description
 * @createTime 2024年03月15日 09:35:00
 */
@Data
public class SShipTrendsDTO extends SShipTrendsPO {

    private static final long serialVersionUID = -62453412454370754L;

    private Integer status;//0代表删除，1代表新增，2代表更新

}
