package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * (MPieceWorkTeam)PO
 *
 * @author linqi
 * @since 2023-08-22 14:22:14
 */
@Setter
@Getter
public class MPieceWorkTeamPO extends BasePO {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    private Long id;

    /**
     * 作业公司ID
     */
    @NotNull(message = "作业公司ID不能为空")
    private Long companyId;

    /**
     * 作业公司NAME
     */
    @NotBlank(message = "作业公司名称不能为空")
    private String companyName;

    /**
     * 部门ID
     */
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 计件工资项目名称代码（字典：PIECE_PROJECT 装载机、现场管理、内燃平衡重式叉车、港口门座起重机）
     */
    @NotBlank(message = "计件工资项目代码不能为空")
    private String pieceProjectCode;

    /**
     * 计件工资项目名称（字典：PIECE_PROJECT 装载机、现场管理、内燃平衡重式叉车、港口门座起重机）
     */
    @NotBlank(message = "计件工资项目名称不能为空")
    private String pieceProjectName;

    /**
     * 是否更新港存（1.是 0否）
     */
    @NotBlank(message = "是否更新港存不能为空")
    private String isUpdateStorage;
}
