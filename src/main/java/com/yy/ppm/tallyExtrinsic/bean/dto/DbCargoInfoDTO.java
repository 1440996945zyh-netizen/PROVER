package com.yy.ppm.tallyExtrinsic.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class DbCargoInfoDTO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 票货id
     */
    private Long cargoInfoId;
    /**
     * 票货号
     */
    private String cargoInfoNo;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 重量
     */
    private Integer ton;

    /**
     * 货主
     */
    private String cargoOwnerName;

}
