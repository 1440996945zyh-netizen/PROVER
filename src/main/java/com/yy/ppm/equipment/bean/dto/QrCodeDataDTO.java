package com.yy.ppm.equipment.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 二维码生成数据实体
 * @author system
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeDataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 二维码名称/标题
     */
    private String codeName;

    /**
     * 二维码图片字节
     */
    private byte[] bytes;
}
