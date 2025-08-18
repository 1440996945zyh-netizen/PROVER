package com.yy.ppm.dispatch.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清货物变更日志表(MHqDataLog)SearchDTO
 * @Description TODO
 * @createTime 2025年05月27日 18:20:00
 */
@Data
public class MHqDataLogSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 623475832865170174L;

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
    /**
     * 创建人姓名
     */
    private String createByName;
}

