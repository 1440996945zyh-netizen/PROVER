package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单子表(TBusDispatchReleaseDetail)SearchDTO
 * @Description TODO
 * @createTime 2024年04月17日 09:28:00
 */
@Data
public class TBusDispatchReleaseDetailSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -39025529375757779L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 放行单id
     */
    private Long dispatchReleaseId;
    /**
     * 票货号
     */
    private String cargoInfoNo;
    /**
     * 货主id
     */
    private String cargoOwnerId;
    /**
     * 货主
     */
    private String cargoOwnerName;
    /**
     * 货名code
     */
    private String cargoCode;
    /**
     * 货名name
     */
    private String cargoName;
    /**
     * 数量
     */
    private Long quantity;
    /**
     * 吨
     */
    private BigDecimal ton;
    /**
     * 包装code
     */
    private String packingCode;
    /**
     * 包装name
     */
    private String packingName;
    /**
     * 规格
     */
    private String specs;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建人
     */
    private String createByName;
    /**
     * 更新人
     */
    private String updateByName;
}

