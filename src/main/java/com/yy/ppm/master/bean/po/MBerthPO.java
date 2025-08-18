package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 泊位信息(MBerth)PO
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
@Data
public class MBerthPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 327907994416627221L;

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
     * 工作区域
     */
    private String workAreaCd;

}

