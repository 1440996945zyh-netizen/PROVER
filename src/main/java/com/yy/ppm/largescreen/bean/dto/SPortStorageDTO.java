package com.yy.ppm.largescreen.bean.dto;


import com.yy.ppm.largescreen.bean.po.SPortStoragePO;
import lombok.Data;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SPortStorage)DTO
 * @Description
 * @createTime 2024年03月14日 23:13:00
 */
@Data
public class SPortStorageDTO extends SPortStoragePO {

    private static final long serialVersionUID = -31476484067917222L;

    private Integer status;//0代表删除，1代表新增，2代表更新

}
