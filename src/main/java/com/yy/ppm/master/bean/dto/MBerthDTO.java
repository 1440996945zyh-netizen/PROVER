package com.yy.ppm.master.bean.dto;


import com.yy.ppm.master.bean.po.MBerthPO;
import lombok.Data;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 泊位信息(MBerth)DTO
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
@Data
public class MBerthDTO extends MBerthPO {

    private static final long serialVersionUID = -81745067581462617L;

    /**
     * 揽庄数量
     */
    private String childCount;

    /**主泊位名称*/
    private String parentName;

    /*工作区域名称*/
    private String workAreaCdName;

}
