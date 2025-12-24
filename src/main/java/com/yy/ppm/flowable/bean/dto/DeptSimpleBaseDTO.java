package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门精简信息
 */
@Data
public class DeptSimpleBaseDTO {

    /**
     * 部门编号
     */
    private String deptCode;
    /**
     * 部门名称
     */
    private String deptName;

}
