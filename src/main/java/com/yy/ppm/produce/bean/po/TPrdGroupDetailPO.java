package com.yy.ppm.produce.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

/**
 * (TPrdGroupDetailPO)PO
 *
 * @author chenfs
 * @since 2023-10-12 09:54:41
 */
@Setter
@Getter
public class TPrdGroupDetailPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 主表ID
     */
    private Long groupId;

    /**
     * 子作业过程代码
     */
    @NotNull(message = "子作业过程不能为空")
    private String processDetailCode;

    /**
     * 子作业过程名称
     */
    private String processDetailName;


    /**
     * 班组ID
     */
    private String deptId;

    /**
     * 班组名称
     */
    private String deptName;

}

