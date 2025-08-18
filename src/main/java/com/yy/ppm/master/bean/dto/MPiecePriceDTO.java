package com.yy.ppm.master.bean.dto;


import com.yy.ppm.master.bean.po.MPiecePricePO;
import lombok.Data;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 计件单价(MPiecePrice)DTO
 * @Description
 * @createTime 2023年09月15日 11:32:00
 */
@Data
public class MPiecePriceDTO extends MPiecePricePO {

    private static final long serialVersionUID = -73391729437980209L;

    private String processCd;

}
