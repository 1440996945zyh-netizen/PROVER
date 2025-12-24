package com.yy.ppm.flowable.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户精简信息
 */
@Data
@Accessors(chain = true)
public class UserSimpleBaseDTO {
    /**
     * 用户主键
     */
    private Long id;
    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 部门编号
     */
    private Long deptId;
    /**
     * 部门名称
     */
    private String deptName;
}
