package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 航次主表(TDisShipvoyage)PO
 *
 * @author linqi
 * @since 2023-07-04 11:07:28
 */
@Setter
@Getter
@ToString
public class TDisShipvoyagePO extends BasePO {

    /**
     * 主键ID
     */
    @NotNull(message = "主键id不能为空")
    private Long id;
    /** 渤海通id */
    private Long boHaiTongId;
    private String bhtAcceptFlag;//渤海通受理状态
    /** 渤海通船舶资料id */
    private Long boHaiTongShipId;
    /**
     * SCN（系统自动生成规则：如23年6月份第3个航次 则号码为23060003）
     */
    private String scn;

    /**
     * 船舶类型名称
     */
    private String shipKindName;

    /**
     * 船舶类型名称
     */
    private String shipKindCode;

    /**
     * 船舶ID
     */
    @NotNull(message = "船舶id不能为空")
    private Long shipId;

    private List<Long> shipIds;

    /**
     * 中文船名
     */
    @NotBlank(message = "中文船名不能为空")
    private String shipName;

    /**
     * 船舶国籍
     */
    private String nationCode;

    /**
     * 英文船名
     */
    private String shipNameEn;

    /**
     * 进出口(进口  出口 进出口)
     */
    @NotBlank(message = "进出口不能为空")
    private String impExp;

    /**
     * 贸别，内贸、外贸
     */
    @NotBlank(message = "贸别不能为空")
    private String tradeType;

    /**
     * 装卸,装、卸
     */
    @NotBlank(message = "装卸不能为空")
    private String loadUnload;

    /**
     * 是否加油加水（1.是 0.否）
     */
    private String isWater;

    /**
     * 是否接岸电（1.是 0.否）
     */
//    @NotBlank(message = "是否接岸电不能为空")
    private String isShorePower;

    /**
     * 预计加水量
     */
    private BigDecimal reqWater;

    /**
     * 预计加油量
     */
    private BigDecimal reqElectricity;

    /**
     * 抵港艏吃水
     */
    private BigDecimal bowDraft;

    /**
     * 抵港艉吃水
     */
    private BigDecimal sternDraft;

    /**
     * 是否需要拖轮(1.是 0.否)
     */
//    @NotBlank(message = "是否需要拖轮不能为空")
    private String isTug;

    /**
     * 拖轮个数
     */
    private Integer tugNumber;

    /**
     * 引航方式,1自引2引航3进自引出引航4出引航进自引
     */
    private String pilotage;

    /**
     * 是否扣除预缴费用 1是 0否
     */
    private String isPayment;

    /**
     * 预缴金额
     */
    private BigDecimal paymentAmount;

    /**
     * 是否危险物品 1是 0否
     */
    @NotBlank(message = "是否危险物品不能为空")
    private String isDangerous;

    /**
     * 预抵日期
     */
    @NotNull(message = "预抵日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date arrivalTimePlan;

    /**
     * 预计离港时间
     */
    @NotNull(message = "预计离港时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date leavePortTimePlan;

    /**
     * 抵锚时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date arrivalAnchorageTime;

    /**
     * 起锚时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveAnchorageTime;

    /**
     * 靠泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date berthTime;

    /**
     * 靠泊泊位ID
     */
    private Long berthId;

    /**
     * 靠泊泊位名称
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
     * 离泊时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaveBerthTime;

    /**
     * 离港时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leavePortTime;


    /**
     * 接收人-ID
     */
    private Long receiveBy;

    /**
     * 接收人-姓名
     */
    private String receiveByName;

    /**
     * 接收时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date receiveTime;

    /**
     * 删除标志（0代表正常 1代表作废）
     */
    private String delFlag;

    /**
     * 作废原因
     */
    private String delRemark;

    /**
     * 作废人ID
     */
    private Long delBy;

    /**
     * 作废人-姓名
     */
    private String delByName;

    /**
     * 作废时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date delTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 起运港代码
     */
    @NotNull(message = "起运港代码不能为空")
    private Long startPortId;

    /**
     * 起运港名称
     */
    @NotBlank(message = "起运港名称不能为空")
    private String startPortName;

    /**
     * 目的港代码
     */
    @NotNull(message = "目的港代码不能为空")
    private Long endPortId;

    /**
     * 目的港名称
     */
    @NotBlank(message = "目的港名称不能为空")
    private String endPortName;

    /**
     * 上一港代码
     */
    @NotNull(message = "上一港代码不能为空")
    private Long prePortId;

    /**
     * 上一港名称
     */
    @NotBlank(message = "上一港名称不能为空")
    private String prePortName;

    /**
     * 下一港代码
     */
//    @NotNull(message = "下一港代码不能为空")
    private Long nextPortId;

    /**
     * 下一港名称
     */
//    @NotBlank(message = "下一港名称不能为空")
    private String nextPortName;

    /**
     * 状态CODE，预报、接收、抵锚..,（字典SHIPSTATUS）
     */
    private String shipStatusCode;

    /**
     * 状态名称 ，预报、接收、抵锚..,（字典SHIPSTATUS）
     */
    private String shipStatusName;

    private String captainPhone;

    private String voyage;

    private String cargoCategoryName;

    /** 附件 */
    private String certificatePath;

    private String isStartWork;

    /**
     * 是否首次到港
     */
    private String isLastArrivalType;

    /**
     * 是否收取停泊费,是：1，否：0
     */
    private String berthingCharge;

    /**
     * 接收原因
     */
    private String jieShouRemark;

    /**
     * 码头
     */
    private String wharf;

    /**
     * 确报时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    /**
     * 是否点检测试，0：不点检，1：点检
     */
    private String isCheck;

    /**
     * 数量2 计算船舶停泊费的时候用
     */
    private Integer number2;
}
