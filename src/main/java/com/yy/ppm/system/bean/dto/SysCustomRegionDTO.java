package com.yy.ppm.system.bean.dto;


import com.yy.ppm.system.bean.po.SysCustomRegionPO;
import lombok.Data;

/**
 * @ClassName (SysCustomRegion)DTO
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月02日 11:14:00
 */
@Data
public class SysCustomRegionDTO extends SysCustomRegionPO {

    private static final long serialVersionUID = 803351056487855181L;

    private String isQuickEnter;

}
