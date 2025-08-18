package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 垛位信息(MStorageStack)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 17:39:00
 */
@Data
public class MStorageStackPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -51136538517422258L;

        /** 主键id */
    private Long id;
            /** 库场code */
    private String storageCode;
            /** 垛位code */
    private String stackCode;
            /** 垛位名称 */
    private String stackName;
            /** 状态 1在用 0停用 */
    private Long status;
            /** 创建人 */
    private Long createBy;

}

