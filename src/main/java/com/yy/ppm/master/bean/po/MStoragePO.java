package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 库场信息(MStorage)PO
 * @Description
 * @createTime 2023年06月05日 17:38:00
 */
@Data
public class MStoragePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 974525289060003020L;

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

}

