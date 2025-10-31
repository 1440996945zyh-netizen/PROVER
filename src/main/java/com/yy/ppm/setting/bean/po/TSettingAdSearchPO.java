package com.yy.ppm.setting.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 高級查詢配置表(TSettingAdSearch)PO
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年09月25日 15:35:00
 */
@Data
public class TSettingAdSearchPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -26156084003186798L;

    /** 主键 */
    private Long id;
    /** 前端table的ID */
    private String tableId;
    /** 前端TABLE的列对应的唯一标识 */
    private String colKey;
    /** 前端TABLE的列对应的label */
    private String colLabel;
    /** 列类型 常量AD_SEARCH_COL_TYPE */
    private String colType;
    /** 状态：1：启用；0：停用 */
    private String status;
    /** 备注 */
    private String remark;
    /** 当列为下拉框时，对应的接口KEY */
    private String colSelectKey;
    /**页面id*/
    private Long menuId;
    /** 排序号*/
    private Integer sortNum;

    /** 下拉框数据源 */
    private String colSelectSource;

    /** 日期类型所对应的格式 */
    private String dateFormat;

}

