package com.yy.ppm.produce.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class TPrdWorkTicketDetailPO extends BasePO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业票ID
     */
    private Long workTicketId;

    /**
     * 分组ID
     */
    private Long groupId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 指令ID
     */
    private Long trustId;

    /**
     * 指令票货ID
     */
    private Long trustCargoInfoId;

    private Long personNelId;

    private String personNelName;

    /**
     * 票货ID
     */
    private Long cargoInfoId;

    /**
     * 目标票货ID 如果是拆包、灌包选择指令生成的目标子票货
     */
    private Long targetCargoInfoId;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 子作业过程代码
     */
    @NotBlank(message = "子作业过程代码不能为空")
    private String processDetailCode;

    /**
     * 子作业过程名称
     */
    @NotBlank(message = "子作业过程名称不能为空")
    private String processDetailName;

    /**
     * 计件工班ID
     */
    private Long pieceWorkTeamId;
    private String pieceWorkTeamName;

    /**
     * 部门ID
     */
//    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    private Long workPlanId;

    /**
     * 部门名称
     */
//    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;
    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDateCheck;

    /**
     * 班次code字典
     */
    private String classCode;

    /**
     * 班次名称
     */
    private String className;

    /**
     * 货物CODE
     */
    private String cargoCode;

    /**
     * 货物名称
     */
    private String cargoName;

    /**
     * 航次GID
     */
    private String shipvoyageId;

    /**
     * 航次子表GID
     */
    private String shipvoyageItemId;

    /**
     * 舱口(多个逗号隔开)
     */
    private String hatch;

    /**
     * 源库场ID
     */
    private Long storehouseIdSource;

    /**
     * 源库场名称
     */
    private String storehouseNameSource;

    /**
     * 源区域ID
     */
    private Long regionIdSource;

    /**
     * 源区域名称
     */
    private String regionNameSource;

    /**
     * 源垛位ID
     */
    private Long massIdSource;

    /**
     * 源垛位名称
     */
    private String massNameSource;

    /**
     * 目标库场ID
     */
    private Long storehouseIdTarget;

    /**
     * 目标库场名称
     */
    private String storehouseNameTarget;

    /**
     * 目标区域ID
     */
    private Long regionIdTarget;

    /**
     * 目标区域名称
     */
    private String regionNameTarget;

    /**
     * 目标垛位ID
     */
    private Long massIdTarget;

    /**
     * 目标垛位名称
     */
    private String massNameTarget;

    /**
     * 车数
     */
    private Integer carCount;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 吨数(数量)
     */
    private BigDecimal ton;

    /**
     * 备注
     */
    private String remark;

    /**
     * 作业工时
     */
    private BigDecimal workHour;

    /**
     * 开始时间
     */

    private String startTime;

    /**
     * 作业量
     */
    private BigDecimal workTon;

    /**
     * 结束时间
     */
    private String endTime;
    private String equipmentTypeName;
    private Long equipmentTypeId;
    private String equipmentId;
    private String equipmentNo;
    private String equipmentTypeCode;
    private List<String> cabinNoList; //舱口
    private String cabinNo; //舱口
    //源：1，目标：2
    private String sourceOrTargetFlag;


}
