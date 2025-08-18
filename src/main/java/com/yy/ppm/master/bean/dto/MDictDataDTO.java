package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MDictDataPO;
import com.yy.ppm.master.bean.po.MDictTypePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 字典数据
 */
@Data
public class MDictDataDTO extends MDictDataPO implements Serializable {
    /**
     * 状态*/
    private String statusLabel;

}
