package com.yy.ppm.produce.bean;

import lombok.Data;

@Data
public class SyncDTO {
    private Long id;
    private String bizType;
    private Long bizId;
    private String isDelete;
}
