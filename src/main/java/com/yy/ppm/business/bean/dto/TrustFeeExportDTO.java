package com.yy.ppm.business.bean.dto;

import com.yy.common.excel.export.bean.SheetMapping;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrustFeeExportDTO extends SheetMapping {

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /** 货主 */
    private  String cargoOwnerName;
    /** 货名 */
    private String cargoName;
    /** 重量 */
    private BigDecimal planTon;
    /** 费率 **/
    private BigDecimal rate;
    /** 金额 **/
    private BigDecimal estAmount;
    /**
     * 船名
     */
    private String shipName;

    private String costInfo;

}
