package com.yy.ppm.produce.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据理货表(THqTally)PO
 * @Description
 * @createTime 2025年04月24日 19:36:00
 */
@Data
public class THqTallyPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -84809920197363238L;

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

}

