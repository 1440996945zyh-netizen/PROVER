package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 库场信息(MStorage)SearchDTO
 * @Description TODO
 * @createTime 2023年06月05日 17:38:00
 */
@Data
public class MStorageSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 793716402359496654L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 库场code
     */
    private String storageCode;
    /**
     * 库场名称
     */
    private String storageName;
    /**
     * 库场类型code 字典STORAGE_TYPE
     */
    private String storageTypeCode;
    /**
     * 库场类型名称
     */
    private String storageTypeName;
    /**
     * 行数
     */
    private Long rowCount;
    /**
     * 列数
     */
    private Long columnCount;
    /**
     * 状态 1在用 0停用
     */
    private Long status;
    /**
     * 创建人
     */
    private Long createBy;
}

