package com.yy.ppm.business.bean.dto.reCargoName;


import com.yy.ppm.business.bean.po.TStdShipRecordPO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName 单船测试记录(TStdShipRecord)DTO
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月31日 10:35:00
 */
@Data
public class ReCargoNameDTO extends BasePO implements Serializable {

    private static final long serialVersionUID = 902579493302425681L;

    private Long id;
    /**
     * 原货名
     */
	private String oldCargoName;

    /**
     * 新货名
     */
    private String newCargoName;

    /**
     * 货名code
     */
	private String cargoCode;

    /**
     * 货种code
     */
	private String cargoCategoryCode;

    /**
     * 备注
     */
	private String remark;

}
