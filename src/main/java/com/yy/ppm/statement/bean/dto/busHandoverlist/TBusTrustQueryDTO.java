package com.yy.ppm.statement.bean.dto.busHandoverlist;

import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-08 10:30
 */
@Setter
@Getter
public class TBusTrustQueryDTO {

    /**
     * 指令编号
     */
    private String trustNo;

    private String cargoName;

    private String cargoInfoNo;

    private Long cargoOwnerId;
}
