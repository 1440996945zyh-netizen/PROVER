package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MHqStorageStackPO extends BasePO implements Serializable {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 货名
     */
    private String storageYardNm;
    /**
     * 助记码
     */
    private String shortCd;
    /**
     * 面积
     */
    private BigDecimal area;
    /**
     * 工作区域
     */
    private String workAreaCd;
    /**
     * 排序号
     */
    private Integer sortNum;
}
