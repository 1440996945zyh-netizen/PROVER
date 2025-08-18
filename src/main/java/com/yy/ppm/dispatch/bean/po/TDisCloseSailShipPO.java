package com.yy.ppm.dispatch.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 封航影响航次(TDisCloseSailShip)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:46:00
 */
@Data
public class TDisCloseSailShipPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -39395800696090861L;

    /** 主键ID */
    private Long id;
    /** 封航记录ID */
    private Long closeSailId;
    /** 航次ID */
    private Long shipvoyageId;
    /** 封航时停靠泊位id */
    private Long berthId;
    /** 封航时停靠泊位name */
    private String berthName;

}

