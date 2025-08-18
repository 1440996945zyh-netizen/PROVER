package com.yy.ppm.dispatch.bean.dto;

import lombok.Data;

@Data
public class TDisPortDaynightplanSearch2DTO {

    private String planDate;

    /** 航次子表 */
    private Long shipvoyageItemId;
    /** 货主id */
    private Long cargoOwnerId;
    /** 包装 */
    private String packingCode;
    /** 班次*/
    private String classCode;
    /** 计划号*/
    private String businessNo;
    /** 作业区*/
    private String portCode;

    private String shipName;

    private String voyage;
}
