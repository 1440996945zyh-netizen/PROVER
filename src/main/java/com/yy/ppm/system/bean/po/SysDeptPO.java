package com.yy.ppm.system.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 组织结构PO
 */
@Getter
@Setter
@ToString
public class SysDeptPO extends BasePO implements Serializable {

    /**ID */
    private Long id;
    /**上级组织机构ID */
    private Long parentId;
    /**祖级列表 */
    private String parentIds;
    /**组织机构编号 */
    private String deptCode;
    /**组织机构名称 */
    private String deptName;
    /**排序号 */
    private Long orderNo;
    /**备注 */
    private String remark;
    /**负责人 */
    private String chief;
    /**是否可用（0不可用 1可用） */
    private String status;
    /**描述 */
    private String descr;
    /**级别：0 集团 1.公司 2.部门 3班组 */
    private Long deptLevel;
    /**组织机构类别，0代表从OA同步，1代表本地组织机构 */
    private Long deptType;
    /**组织机构编号，用于懒加载，四位一层，如0001，它的子组织为00010001-00019999，，00010001的子组织为000100010001-000100019999，以此类推 */
    private String deptNo;
    /**父组织机构code */
    private String parentDeptCode;
    /**
     * 是否为项目组 字典:0-否,1-是
     */
    private String isProject;
    /**
     * 是否为劳务队 字典:0-否,1-是
     */
    private String isLabor;
    /**
     * 是否为机械队 字典:0-否,1-是
     */
    private String isMachine;
    /**
     * 内外部 I内部；O:外部
     */
    private String inOutType;
    /** 是否作业公司 （公司） */
    private String isWorkCompany;
    /** 是否作业公司 （公司） */
    private String isTallyCompany;
    /** 1:一级签票  2:二级签票 */
    private String ticketLevel;
}
