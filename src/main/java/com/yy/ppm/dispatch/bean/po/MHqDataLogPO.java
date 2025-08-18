package com.yy.ppm.dispatch.bean.po;


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
 * @ClassName 海清货物变更日志表(MHqDataLog)PO
 * @Description
 * @createTime 2025年05月27日 18:20:00
 */
@Data
public class MHqDataLogPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -49986371554701641L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 海清货物ID
     */
    private Long hqDataId;
    /**
     * 理货ID
     */
    private String tallyId;
    /**
     * 更新内容详情(JSON格式或其他结构化数据)
     */
    private String updateInfo;

}

