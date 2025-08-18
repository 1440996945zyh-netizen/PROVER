package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 昼夜计划业务对象 t_day_night_plan
 *
 */

@Data
public class TDayNightPlanPO extends BasePO implements Serializable {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long ID;

    /**
     * 日期
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @NotNull(message = "日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date workDate;

    /**
     * 计划类型 1船舶 2场地
     */
    @NotBlank(message = "计划类型 1船舶 2场地不能为空", groups = { AddGroup.class, EditGroup.class })
    private String planType;

    /**
     * 委托主键ID
     */
    @NotBlank(message = "委托主键ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String billId;

    /**
     * 委托人name
     */
    @NotBlank(message = "委托人name不能为空", groups = { AddGroup.class, EditGroup.class })
    private String clientName;

    /**
     * 委托人code
     */
    @NotBlank(message = "委托人code不能为空", groups = { AddGroup.class, EditGroup.class })
    private String clientCode;

    /**
     * 委托信息描述
     */
    @NotBlank(message = "委托信息描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String billDescription;

    /**
     * 主受理单号
     */
    @NotBlank(message = "主受理单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String mvbillNo;

    /**
     * 受理单号
     */
    @NotBlank(message = "受理单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String vbillNo;

    /**
     * 来源运受单号
     */
    @NotBlank(message = "来源运受单号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String fromVbillNo;

    /**
     * 单据类型(装船NC 卸船NJ 集港JG 疏港SG 过户GH)
     */
    @NotBlank(message = "单据类型(装船NC 卸船NJ 集港JG 疏港SG 过户GH)不能为空", groups = { AddGroup.class, EditGroup.class })
    private String billType;

    /**
     * 委托日期
     */
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @NotNull(message = "委托日期不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date billDate;

    /**
     * 航次ID
     */
    @NotBlank(message = "航次ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String vesselVisitId;

    /**
     * 船名
     */
    @NotBlank(message = "船名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String vesselName;

    /**
     * 船舶code
     */
    @NotBlank(message = "船舶code不能为空", groups = { AddGroup.class, EditGroup.class })
    private String vesselCode;

    /**
     * 船长
     */
    @NotNull(message = "船长不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal vesselLongNum;

    /**
     * 船宽
     */
    @NotNull(message = "船宽不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal vesselWidthNum;

    /**
     * 泊位
     */
    @NotBlank(message = "泊位不能为空", groups = { AddGroup.class, EditGroup.class })
    private String berthName;

    /**
     * 货名 多个,分割
     */
    @NotBlank(message = "货名 多个,分割不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cargoNames;

    /**
     * 包装
     */
    @NotBlank(message = "包装不能为空", groups = { AddGroup.class, EditGroup.class })
    private String pkgCode;

    /**
     * 字典LOAD_CODE 船舶为装卸
     */
    @NotBlank(message = "字典LOAD_CODE 船舶为装卸 不能为空", groups = { AddGroup.class, EditGroup.class })
    private String loadCode;

    /**
     * 配载/集疏重量 配载录入后回写
     */
    @NotNull(message = "配载/集疏重量 配载录入后回写不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal stowage;

    /**
     * 作业过程CODE
     */
    @NotBlank(message = "作业过程CODE不能为空", groups = { AddGroup.class, EditGroup.class })
    private String processCode;

    /**
     * 船代code
     */
    @NotBlank(message = "船代code不能为空", groups = { AddGroup.class, EditGroup.class })
    private String shipAgentCode;

    /**
     * 船代名称
     */
    @NotBlank(message = "船代名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String shipAgentName;

    /**
     * 货代/货主code
     */
    @NotBlank(message = "货代/货主code不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cargoAgentCode;

    /**
     * 货代名称
     */
    @NotBlank(message = "货代名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String cargoAgentName;

    /**
     * 进出口 字典IN_OUT 1进口 2出口
     */
    @NotBlank(message = "进出口 字典IN_OUT 1进口 2出口不能为空", groups = { AddGroup.class, EditGroup.class })
    private String inOut;

    /**
     * 内外贸 字典TRADE_TYPE 1内贸 2外贸
     */
    @NotBlank(message = "内外贸 字典TRADE_TYPE 1内贸 2外贸不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tradeType;

    /**
     * 作业工艺ID
     */
    @NotNull(message = "作业工艺ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long craftId;

    /**
     * 白班开舱量
     */
    @NotNull(message = "白班开舱量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long hatchDay;

    /**
     * 夜班开舱量
     */
    @NotNull(message = "夜班开舱量不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long hatchNight;

    /**
     * 集疏方式 字典 transport_type
     */
    @NotBlank(message = "集疏方式 字典 transport_type不能为空", groups = { AddGroup.class, EditGroup.class })
    private String transportType;

    /**
     * 车数
     */
    @NotNull(message = "车数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long carNum;

    /**
     * 件数
     */
    @NotNull(message = "件数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long pcs;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;

    /**
     * 装卸港口
     */
    @NotBlank(message = "装卸港口 不能为空", groups = { AddGroup.class, EditGroup.class })
    private String loadPort;

    /**
     * 机械配置
     * */
    private List<TDaynightPlanMacPO> listMachine;
    /**
     * 工人配置
     * */
    private List<TDaynightPlanWorkerPO> listWorker;
    /**
     * 作业队
     * */
    private List<TDaynightPlanWorkteamPO> listWorkteam;
    /**
     * 工属具
     * */
    private List<TDaynightPlanWorkwarePO> listWorkware;

    /**
     * 集疏
     * */
    private Long collecteFlag;

}
