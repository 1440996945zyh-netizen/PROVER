package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据理货表(THqTally)SearchDTO
 * @Description TODO
 * @createTime 2025年04月24日 19:36:00
 */
@Data
public class THqTallySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -80255544607466455L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 理货id
     */
    private Long tallyId;
    /**
     * 海清货物id
     */
    private Long hqDataId;
    /**
     * 创建人
     */
    private String createByName;
    /**
     * 更新人
     */
    private String updateByName;
}

