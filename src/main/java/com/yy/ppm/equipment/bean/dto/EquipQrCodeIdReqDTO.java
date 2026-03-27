package com.yy.ppm.equipment.bean.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 批量导出二维码请求DTO
 * @author system
 */
@Data
@ToString
public class EquipQrCodeIdReqDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 打印机id
     */
    private Long printerId;
    
    /**
     * 批量导出二维码设备ids集合
     */
    private List<Long> equipIdList;
}
