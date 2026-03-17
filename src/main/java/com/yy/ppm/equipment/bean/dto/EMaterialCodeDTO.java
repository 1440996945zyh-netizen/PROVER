package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaterialCodePO;
import lombok.Data;

import java.util.List;

/**
 * 物资代码DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class EMaterialCodeDTO extends EMaterialCodePO {

    private static final long serialVersionUID = 1L;

    /**
     * 类别名称（用于显示）
     */
    private String categoryName;

    /**
     * 类别路径（用于显示，如：劳保用品/劳保类用品/劳保手套）
     */
    private String categoryPath;

    /**
     * 附件文件ID数组
     */
    private List<Long> fileIds;
}

