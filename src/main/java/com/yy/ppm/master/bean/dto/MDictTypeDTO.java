package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import com.yy.ppm.master.bean.po.MDictTypePO;
import lombok.Data;

import java.io.Serializable;

/**
 *
 */
@Data
public class MDictTypeDTO extends MDictTypePO implements Serializable {
    /**
     * 状态*/
    private String statusLabel;

}
