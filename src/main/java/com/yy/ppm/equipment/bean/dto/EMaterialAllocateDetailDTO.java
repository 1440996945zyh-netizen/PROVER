package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialAllocateDetailPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 物资调拨明细 DTO。
 * 用于调拨明细回显和前后端数据传输。
 *
 * @author system
 */
@Getter
@Setter
@ToString
public class EMaterialAllocateDetailDTO extends EMaterialAllocateDetailPO {

    private static final long serialVersionUID = 1L;
}
