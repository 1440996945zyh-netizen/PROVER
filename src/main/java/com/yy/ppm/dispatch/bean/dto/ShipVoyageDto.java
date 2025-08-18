package com.yy.ppm.dispatch.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 船舶航次表(TDisShipvoyage)实体类
 *
 * @author makejava
 * @since 2022-12-12 19:59:12
 */
@Getter
@Setter
@ToString
public class ShipVoyageDto extends BasePO implements Serializable {
    private static final long serialVersionUID = 246359133371003507L;
    /**
    * 主键
    */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shipvoyageId;
    /**
    * 船舶ID
    */
    @NotNull(message = "船舶id必填~")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shipId;
    /**
    * 中文船名
    */
    @NotBlank(message = "中文船名必填")
    private String shipName;
    /**
     * 英文船名
     */
    private String shipNameEn;
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
    * 客户Id（船代）
    */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long customerId;
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
    private BigDecimal tugNumber;
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
    private Integer pilotage;
    /**
    * 预抵日期
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date arrivalTimePlan;
    /**
    * 抵锚时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date arrivalAnchorageTime;
    /**
    * 起锚时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date leaveAnchorageTime;
    /**
    * 靠泊时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date berthTime;
    /**
    * 靠泊泊位ID
    */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long berthId;
    /**
    * 靠泊泊位编号
    */
    private String berthName;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date workStartTime;
    /**
    * 完工时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date workEndTime;
    /**
    * 离泊时间
    */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date leaveBerthTime;
    /**
    * 状态，预报、确报、抵锚..,（字典SHIPSTATUS）
    */
    @NotBlank(message = "状态必填,对外服务默认为预报,，散杂货默认为确报")
    private String status;
    private String statusOld;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date receiveTime;
    /**
    * 结算状态，1未结算2已结算
    */
    private Integer settlementStatus;
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
     * 是否夜航
     */
    private String  isNightFlight;

    /**
    * 辅助作业
    */
    private String assistWork;
    private String assistWorkName;
    /**
    * 船长
    */
    private BigDecimal shipLength;
    /**
     * 船宽
     */
    private BigDecimal shipWidth;
    /**
     * 舱口数
     */
    private Integer hatchNo;
    /**
     * 总吨
     */
    private BigDecimal totalTon;

    /**
     * 净吨
     */
    private BigDecimal netTon;
    /**
     * 载重吨
     */
    private BigDecimal weightTon;
    /**
     * 附件集合
     */
    /*private List<MAttachmentInfo> attachmentInfoList;
    private List<MAttachmentInfo> attachmentInfoListJobNotice;
    private List<MAttachmentInfo> attachmentInfoListStowagePlan;
    private List<MAttachmentInfo> attachmentInfoListPortApplication;
    private List<MAttachmentInfo> attachmentInfoListBill;
    private List<MAttachmentInfo> attachmentInfoListNightApplication;
    private List<MAttachmentInfo> attachmentInfoListCaptainInformation1;
    private List<MAttachmentInfo> attachmentInfoListCaptainInformation2;*/

    /**
     * 提单
     */
    //rivate List<TBusBillPO> billList;

    /**
     * 指令id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long trustId;
    /**
     * 指令id 逗号拼接
     */
    private String trustIdStr;
    /**
     * 指令状态
     */
    private String trustStatus;


    /**货物代码*/
    private String cargoCode;
    /**货物名称*/
    private String cargoName;
    /**开证公司代码*/
    private String issuingCompanyId;
    /**开证公司名称*/
    private String issuingCompanyName;
    /**货主代码*/
    private String cargoOwnerId;
    /**货主名称*/
    private String cargoOwnerName;
    /**货代代码*/
    private String cargoAgentId;
    /**货代名称*/
    private String cargoAgentName;
    /**船名航次*/
    private String shipvoyageName;
    /**起运港/目的港*/
    private String portName;
    /**
     * 票货id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cargoInfoId;

    private BigDecimal  berthDays;//停泊时间（天）
    private BigDecimal  anchorageDays;//在锚天数
    private BigDecimal  shipStopHours;//非生产性停泊时长小时（船方原因）
    private  BigDecimal  shipStopDays;//非生产性停泊天数（船方原因）
    private  BigDecimal  cargoStopHours;//非生产性停泊时长小时（货方原因）
    private Integer  shipReportStatus;//船舶作业报告确认状态，1未确认2已确认

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;
    /**结算人*/
    private String costNmae;
    /**结算时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date costTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date matchTime;

    /**结算id*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long costId;
    /**结算审核状态，1未审核2已审核*/
    private Integer checkStatus;
    private String checkStatusName;



    //费用合计
    private BigDecimal totalAmount;
    //单价
    private BigDecimal rate;



    /**
     * 付款方Id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long payCompanyId;

    /**付费方名称*/
    private String payCompanyName;

    /**收费方ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long chargeCompanyId;
    /**收费方名称*/
    private String chargeCompanyName;

    /**加水标记 Y N*/
    private String isWater;
    /**接电标记 Y N*/
    private String isElectricity;

    /**作业数量 核销数量 熏蒸体积*/
    private BigDecimal checkNumber;
    /**提单体积*/
    private BigDecimal volume;
    /**检尺体积*/
    private BigDecimal measureVolume;
    /**结算体积*/
    private BigDecimal settlementVolume;
    /**未结算检尺体积*/
    private BigDecimal unsettlementVolume;

    /**作业内容*/
    private String measureType;
    private String measureName;

    /**
     * 结算终止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /**是否客户访问 是：Y，否：N*/
    private String isCustomer;

    /**客户所属公司*/
    private String accCompanyId;

    private String trustNo;//指令编号
    private String trustTime;//指令创建时间
    private String trustOffTime;//指令核销时间

    /**计划件数*/
    private Integer planQuantity;
    /**计划重量*/
    private BigDecimal planTon;
    /**计划体积*/
    private BigDecimal planVolume;
    /**计划车数*/
    private Integer planCarNum;

    /**账期*/
    private Integer paymentDays;
    private String paymentType;

    //入库日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date inOutDate;

    //堆存费截至日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endDate;

    //场存量
    private BigDecimal storageVolume;



    /**作业过程代码*/
    private String processCode;
    /**作业过程名称*/
    private String processName;
    /**核销数(核销时填入)*/
    private Integer checkQuantity;
    /**核销重量 CHECK_TON*/
    private BigDecimal checkTon;
    /**核销体积 CHECK_VOLUME*/
    private BigDecimal checkVolume;
    /**提单体积 CHECK_VOLUME*/
    private BigDecimal billVolume;
    /**核销车数 CHECK_CAR_NUM*/
    private Integer checkCarNum;
    /**均方*/
    private BigDecimal averageVolume;
    /**合同id*/
    private Long contractId;
    /**合同编号*/
    private String contactNo;
    /**免堆存天数*/
    private Integer freeStorageDays;
    /**是否熏蒸*/
    private String isFumigation;
    /**是否请场*/
    private String isClear;
}
