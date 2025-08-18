package com.yy.ppm.largescreen.bean.dto;


import com.yy.ppm.largescreen.bean.po.SPortThroighputPO;
import lombok.Data;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 港区吞吐量表(SPortThroighput)DTO
 * @Description
 * @createTime 2024年03月15日 09:24:00
 */
@Data
public class SPortThroighputDTO extends SPortThroighputPO {

    private static final long serialVersionUID = 152603811294724867L;

    private Integer status;//0代表删除，1代表新增，2代表更新

}
