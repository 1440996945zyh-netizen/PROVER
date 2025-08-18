package com.yy.ppm.produce.bean.dto.workTicket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketLaborPO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-08-15 11:14
 */
@Setter
@Getter
public class TPrdWorkTiTckInfoDTO   extends BasePO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司名称
     */
    private String companyName;
    private String shipNameVoyage;
    private String loadUnload;

    /**
     * 作业计划ID
     */
    @NotNull(message = "作业计划ID不能为空")
    private Long workPlanId;

    /**
     * 作业票类型
     */
    private String type;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /**
     * 班次code字典
     */
    private String classCode;

    /**
     * 班次名称
     */
    private String className;

    /**
     * 状态:字典待审核、已审核(WORK_TICKET_STATUS)
     */
    private String workTicketStatus;

    /**
     * 状态:字典(WORK_TICKET_STATUS)
     */
    private String workTicketStatusName;

    /**
     * 审核人
     */
    private Long examineBy;

    /**
     * 审核人姓名
     */
    private String examineByName;

    /**
     * 审核时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date examineTime;

    /**
     * 件数
     */
    private Integer quantity;

    /**
     * 吨数(数量)
     */
    private BigDecimal ton;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;


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

    /**
     * 票货ID
     */
    private Long cargoInfoId;

    /**
     * 目标票货ID 如果是拆包、灌包选择指令生成的目标子票货
     */
    private Long targetCargoInfoId;

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
     * 货物CODE
     */
    private String cargoCode;

    /**
     * 货物名称
     */
    private String cargoName;
    private String cargoNameLabel;

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
    private String storehouseName;

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
     * 备注
     */
    private String remark;

    /**
     * 作业工时
     */
    private Integer workHour;

    /**
     * 开始时间
     */

    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;
    private String equipmentTypeName;
    private Long equipmentTypeId;
    private String equipmentId;
    private String equipmentNo;
    private String equipmentTypeCode;
    private Long personNelId;
    private String personNelName;
    private Long deptItemId;
    private String deptItemName;
    private String ticketType;
    private String storehouseNameTargetLabel;
    private String storehouseNameSourceLabel;
    private String trustNo;
    private String shipVoyageIds;
    private String cabinNo;

//    /**
//     * 作业票劳务
//     */
//    private List<TPrdWorkTicketLaborPO> labors;

}
