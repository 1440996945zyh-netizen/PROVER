package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 泊位信息(MBerth)SearchDTO
 * @Description TODO
 * @createTime 2023年06月05日 16:06:00
 */
@Data
public class MBerthSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 748043222313250702L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 主泊位code
     */
    private String parentCode;
    /**
     * 泊位代码
     */
    private String berthCode;
    /**
     * 泊位名称
     */
    private String berthName;
    /**
     * 长度
     */
    private BigDecimal berthLong;
    /**
     * 状态  1在用 0停用
     */
    private Long status;
    /**
     * 货物信息 cargo_code 多个,分割
     */
    private String cargoInfo;
    /**
     * 创建人
     */
    private Long createBy;
}

