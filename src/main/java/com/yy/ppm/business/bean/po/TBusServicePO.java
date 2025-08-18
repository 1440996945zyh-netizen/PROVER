package com.yy.ppm.business.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (BusService)PO
 *
 * @author 韩旭
 * @date 2021-03-18 10:50:10
 */
@Getter
@Setter
@ToString
public class TBusServicePO extends BasePO implements Serializable {

    private static final long serialVersionUID = -16363434437183329L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 服务名
     */
    private String serviceNm;
    /**
     * 助记码
     */
    private String shortCd;
    /**
     * 主过程拼串汇总
     */
    private String processNms;
    /**
     * 备注
     */
    private String remark;

    private String inOut;
    private String inteFore; //内外贸

    private Integer inOutStorage;
}
