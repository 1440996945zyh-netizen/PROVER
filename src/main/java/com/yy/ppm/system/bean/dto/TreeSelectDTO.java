package com.yy.ppm.system.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/4/24 15:03
 */

@Data
public class TreeSelectDTO {

    /** 节点ID */
    private Long id;

    /** 节点名称 */
    private String label;

    /** 是否有子节点 */
    private Boolean isAlwaysShow;

    /** 子节点 */
    private List<TreeSelectDTO> children;

}
