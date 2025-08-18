package com.yy.ppm.produce.bean.dto;
import com.yy.ppm.master.bean.dto.FieldRemark;
import com.yy.ppm.produce.bean.po.THqDataPO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据补录表(THqData)DTO
 * @Description
 * @createTime 2025年04月24日 17:23:00
 */
@Data
public class THqDataDTO extends THqDataPO {

    private static final long serialVersionUID = 156021371054542204L;

    private String source;

    private String target;

    private Long tallyId;

    @FieldRemark(value = "理货件数")
    private Integer tallyQuantity;

    @FieldRemark(value = "理货吨数")
    private BigDecimal tallyTon;

    private String inoutType;

    private Boolean isDisabled;

    private Long hqTallyId;

}
