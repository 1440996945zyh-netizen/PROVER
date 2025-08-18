package com.yy.ppm.statement.bean.dto.costShip;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther wangxd
 * @Description
 * @Date 2023-12-20 15:11
 */
@Setter
@Getter
public class TCostShipStatusDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 状态
     */
    private String status;
}
