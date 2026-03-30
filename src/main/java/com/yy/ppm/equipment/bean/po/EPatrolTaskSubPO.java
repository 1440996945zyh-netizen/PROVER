package com.yy.ppm.equipment.bean.po;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 巡检任务子表 PO
 *
 * @author system
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EPatrolTaskSubPO extends BasePO {

    private static final long serialVersionUID = 1L;



    /** ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 巡检任务主表ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;

    /** 设备ID */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long equipId;

    /** 设备名称 */
    private String equipName;

    /** 检查内容 */
    private String checkContent;

    /** 合格条件 */
    private String qualifyCondition;

    /** 检查方法 */
    private String checkMethod;

    /** 巡检状态 0未检 1已检 */
    private Integer status;

    /** 是否异常 0待检 1异常 2正常 */
    private Integer isAbnormal;

    /** 是否报修 0否 1是 */
    private Integer isRepair;

    /** 巡检员姓名 */
    private String patrolName;

    /** 附件ID列表 (逗号分隔) */
    private String fileIds;

    /** 附件列表 (非数据库字段，用于展示) */
    private List<SysFileDTO> attachmentList;
}
