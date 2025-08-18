package com.yy.ppm.master.bean.dto;


import com.yy.ppm.master.bean.po.MStoragePO;
import lombok.Data;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 库场信息(MStorage)DTO
 * @Description
 * @createTime 2023年06月05日 17:38:00
 */
@Data
public class MStorageDTO extends MStoragePO {

    private static final long serialVersionUID = 217618129918574504L;

    private String childCount;

}
