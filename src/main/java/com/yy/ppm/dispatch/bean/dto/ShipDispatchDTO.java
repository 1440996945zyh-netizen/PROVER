package com.yy.ppm.dispatch.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.framework.annotation.DateFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 船舶调度(TDisShipvoyage)实体类
 *
 * @since 2022-12-12 19:59:12
 */
@Getter
@Setter
@ToString
public class ShipDispatchDTO extends BasePO implements Serializable {
    private static final long serialVersionUID = 246359133371003507L;
    /**
    * 主键
    */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private Long shipvoyageId;
    /**
    * 船舶ID
    */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "船舶id必填~")
    private Long shipId;
    /**
    * 中文船名
    */
    @NotBlank(message = "中文船名必填")
    private String shipName;
    /**
    * 航次
    */
    @NotBlank(message = "航次必填")
    private String voyage;
    /**
    * 公司ID（业务单元）
    */
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "公司id必填")
    private Long companyId;
    /**
    * 公司（业务单元）
    */
    @NotBlank(message = "公司名称必填")
    private String companyName;
    /**
    * 货种代码，多选
    */
    @NotBlank(message = "货种代码必填")
    private String cargoCategoryCode;
    /**
    * 货种名称，多选
    */
    @NotBlank(message = "货种名称必填")
    private String cargoCategoryName;
    /**
    * 贸别，内贸、外贸
    */
    @NotBlank(message = "贸别必填")
    private String tradeType;
    /**
    * 装卸，装、卸
    */
    @NotBlank(message = "装、卸必填")
    private String loadUnload;
    /**
    * 客户代码（船代）
    */
    @NotBlank(message = "客户代码必填")
    private String customerId;
    /**
    * 客户名称（船代）
    */
    @NotBlank(message = "客户名称必填")
    private String customerName;
    /**
    * 起运港代码
    */
    private String startPortCode;
    /**
    * 起运港名称
    */
    private String startPortName;
    /**
    * 目的港代码
    */
    private String endPortCode;
    /**
    * 目的港名称
    */
    private String endPortName;
    /**
     * 起运港名称/目的港名称
     */
    private String groupPortName;
    /**
    * 上一港代码
    */
    private String prePortCode;
    /**
    * 上一港名称
    */
    private String prePortName;
    /**
    * 下一港代码
    */
    private String nextPortCode;
    /**
    * 下一港名称
    */
    private String nextPortName;
    /**
    * 船上电话
    */
    @NotBlank(message = "船上电话必填")
    private String shipPhone;
    /**
    * 抵港艏吃水
    */
    private BigDecimal bowDraft;
    /**
    * 抵港艉吃水
    */
    private BigDecimal sternDraft;
    /**
    * 是否需要拖轮
    */
    @NotBlank(message = "是否需要拖轮必填")
    private String reqTug;
    /**
    * 拖轮个数
    */
    private int tugNumber;
    /**
    * 是否半载
    */
    @NotBlank(message = "是否半载必填")
    private String halfLoad;
    /**
    * 实际载货量
    */
    private BigDecimal actualLoad;
    /**
    * 引航方式,1自引2引航3进自引出引航4出引航进自引
    */
    @NotNull(message = "引航方式必填")
    private int pilotage;
    private String pilotageName;
    /**
    * 预抵日期
    */
    @DateFormat(message = "预抵日期格式错误", value = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private String arrivalTimePlan;
    /**
    * 抵锚时间
    */
    @DateFormat(message = "抵锚时间格式错误", value = "yyyy-MM-dd HH:mm:ss")
    private String arrivalAnchorageTime;
    /**
    * 起锚时间
    */
    @DateFormat(message = "起锚时间格式错误", value = "yyyy-MM-dd HH:mm:ss")
    private String leaveAnchorageTime;
    /**
    * 靠泊时间
    */
    @DateFormat(message = "靠泊时间格式错误", value = "yyyy-MM-dd HH:mm:ss")
    private String berthTime;
    /**
     * 计划/实际靠泊
     */
    private String groupBerthTime;
    /**
    * 靠泊泊位ID
    */
    private Long berthId;
    /**
    * 靠泊泊位编号
    */
    private String berthNo;
    /**
    * 舷靠,左舷、右舷
    */
    private String berthType;
    /**
    * 首榄编号
    */
    private String bollardNoStart;
    /**
    * 尾榄编号
    */
    private String bollardNoEnd;
    /**
    * 开工时间
    */
    @DateFormat(message = "开工时间格式错误", value = "yyyy-MM-dd HH:mm:ss")
    private String workStartTime;
    /**
    * 完工时间
    */
    @DateFormat(message = "完工时间格式错误", value = "yyyy-MM-dd HH:mm:ss")
    private String workEndTime;
    /**
    * 离泊时间
    */
    @DateFormat(message = "离泊时间格式错误", value = "yyyy-MM-dd HH:mm:ss")
    private String leaveBerthTime;
    /**
    * 状态，预报、确报、抵锚..,（字典SHIPSTATUS）
    */
    @NotBlank(message = "状态必填,对外服务默认为预报,，散杂货默认为确报")
    private String status;
    private String statusName;
    /**
    * 确报人-ID
    */
    private String receiveBy;
    /**
    * 确报人-姓名
    */
    private String receiveByName;
    /**
    * 确报时间
    */
    @DateFormat(message = "确报时间格式错误", value = "yyyy-MM-dd HH:mm:ss")
    private String receiveTime;
    /**
    * 结算状态，1未确认2已确认（确认船舶作业报告）3已结算
    */
    private Long settlementStatus;
    private String settlementStatusName;
    /**
    * 确认人-ID
    */
    private String confirmBy;
    /**
    * 确认人-姓名
    */
    private String confirmByName;
    /**
    * 确认时间
    */
    private String confirmTime;
    /**
    * 删除标志（0代表正常 1代表作废）
    */
    private String delFlag;
    /**
    * 作废原因
    */
    private String delRemark;
    /**
    * 备注
    */
    private String remark;
    /**
    * 备注
    */
    /*private List<MAttachmentInfo> attachmentInfoList;*/

    /**
     * 船名航次
     */
    private String shipvoyage;
    private String shipvoyageName;

    /**
     * tabs类型 first|预到  second|锚地  third|在港  fourth|离泊
     */
    private String activeName;

    //载重吨
    private BigDecimal weightTon;
}
