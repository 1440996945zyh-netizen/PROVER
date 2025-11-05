package com.yy.ppm.setting.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 高級查詢配置表(TSettingAdSearch)SearchDTO
 * @author zws
 * @version 1.0.0
 * @Description TODO
 * @createTime 2025年09月25日 15:35:00
 */
@Data
public class TSettingAdSearchSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -59603691673240179L;

/**主键*/
    private Long id;
/**前端table的ID*/
    private String tableId;
/**序号*/
    private Integer sortNum;
/**前端TABLE的列对应的唯一标识*/
    private String colKey;
/**前端TABLE的列对应的label*/
    private String colLabel;
/**列类型 常量AD_SEARCH_COL_TYPE*/
    private String colType;
/**当列为下拉框是，对应接口的数据来源 字典还是常量 常量AD_SEARCH_COL_SELECT_SOURCE*/
    private String colSelectSource ;
/**当列为日期类型时，对应的日期格式*/
    private String dateFormat;
/**菜单名称*/
    private String menuName;
 /**菜单ID*/
    private Long menuId;
/**状态：1：启用；0：停用*/
    private String status;
/**备注*/
    private String remark;
/**创建者姓名*/
    private String createByName;
/**修改者姓名*/
    private String updateByName;
/**当列为下拉框时，对应的接口KEY*/
    private String colSelectKey;

    /**业务是否多选 1是 0否*/
    private String isBusinessMultiSelect;
}

