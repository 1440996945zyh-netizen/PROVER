package com.yy.ppm.master.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.master.bean.dto.FieldRemark;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 海轮资料(MShip)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月27日 15:44:00
 */
@Data
public class MShipPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 738872981208110638L;

    /** id */
    private Long id;
    /** 渤海通id */
    @FieldRemark(value = "渤海通id")
    private Integer boHaiTongId;

    /** 船名 */
    @FieldRemark(value = "船名")
    private String shipName;
    /** 船英文名 */
    @FieldRemark(value = "船英文名")
    private String shipNameEn;
    /** 助记码 */
    @FieldRemark(value = "助记码")
    private String shorthandCode;
    /** 船舶类型 （字典 SHIP_KIND) */
    @FieldRemark(value = "船舶类型code")
    private String shipKindCode;
    /** 国籍，船籍代码 （字典 NATION) */
    @FieldRemark(value = "国籍，船籍代码")
    private String nationCode;
    /** 船型 (字典 SHIP_TYPE) */
    @FieldRemark(value = "船型code")
    private String shipTypeCode;
    /** IMO */
    @FieldRemark(value = "IMO")
    private String imo;
    /** MMSI */
    @FieldRemark(value = "MMSI")
    private String mmsi;
    /** 呼号 */
    @FieldRemark(value = "呼号")
    private String callNo;
    /** 船长 */
    @FieldRemark(value = "船长")
    private BigDecimal shipLength;
    /** 船宽 */
    @FieldRemark(value = "船宽")
    private BigDecimal shipWidth;
    /** 船高 */
    @FieldRemark(value = "船高")
    private BigDecimal shipHeight;
    /** 船舶自重 */
    @FieldRemark(value = "船舶自重")
    private BigDecimal selfWeight;
    /** 净吨 */
    @FieldRemark(value = "净吨")
    private BigDecimal netWeight;
    /** 总吨 */
    @FieldRemark(value = "总吨")
    private BigDecimal totalWeight;
    /** 舱口数 */
    @FieldRemark(value = "舱口数")
    private Integer hatchNum;
    /** 舱层数 （以下为结构信息） */
    @FieldRemark(value = "舱层数")
    private Integer cabinLayerNum;
    /** 载重吨 */
    @FieldRemark(value = "载重吨")
    private BigDecimal dwt;
    /** 仓容 */
    @FieldRemark(value = "仓容")
    private BigDecimal cabinVolume;
    /** 仓口尺寸 */
    @FieldRemark(value = "仓口尺寸")
    private String hatchSize;
    /** 最大船速 */
    @FieldRemark(value = "最大船速")
    private BigDecimal maxSpeed;
    /** 航速 */
    @FieldRemark(value = "航速")
    private BigDecimal speed;
    /** 型深 */
    @FieldRemark(value = "型深")
    private BigDecimal mouldedDepth;
    /** 吊机数量 */
    @FieldRemark(value = "吊机数量")
    private Integer hangerNum;
    /** 舱型 */
    @FieldRemark(value = "舱型")
    private String cabinType;
    /** 机舱位置 */
    @FieldRemark(value = "机舱位置")
    private String hangerLoad;
    /** 舱口盖类型 （字典HATCH__COVER_TYPE) */
    @FieldRemark(value = "舱口盖类型")
    private String hatchCoverTypeCode;
    /** 吊机位置 */
    @FieldRemark(value = "吊机位置")
    private String hangerLocation;
    /** 船长电话 */
    @FieldRemark(value = "船长电话")
    private String captainPhone;
    /** 船长姓名 */
    @FieldRemark(value = "船长姓名")
    private String captainName;
    /** 建造年月 */
    @FieldRemark(value = "建造年月")
    private String builtYm;
    /** 淡水容量 */
    @FieldRemark(value = "淡水容量")
    private BigDecimal waterVolume;
    /** 富余水深 */
    @FieldRemark(value = "富余水深")
    private BigDecimal surplusWaterDepth;
    /** 满载吃水 */
    @FieldRemark(value = "满载吃水")
    private BigDecimal fullLoadWater;
    /** 空载吃水 */
    @FieldRemark(value = "空载吃水")
    private BigDecimal emptyLoadWater;
    /** 头驾距离 */
    @FieldRemark(value = "头驾距离")
    private BigDecimal headerCockpitDistance;
    /** 0：停用；；1：待审核 9：驳回  10：审批通过*/
    @FieldRemark(value = "状态")
    private String status;
    /** 驳回意见 */
    @FieldRemark(value = "驳回意见")
    private String idea;
    /** 审核者-ID */
    @FieldRemark(value = "审核者-ID")
    private Long approvalBy;
    /** 审核者-姓名 */
    @FieldRemark(value = "审核者-姓名")
    private String approvalName;
    /** 审核时间 */
    @FieldRemark(value = "审核时间")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;
    /** 上传国际证书,船舶主要项目等船舶资料 */
    @FieldRemark(value = "国际证书")
    private String certificatePath;
    /** 系统初始化船舶上次来港时间 */
    @FieldRemark(value = "船舶上次来港时间")
    private Date lastTime;
    /** 申报单位 */
    @FieldRemark(value = "申报单位")
    private String applicationUnit;
    /** 申报单位编码 */
    @FieldRemark(value = "申报单位编码")
    private Long applicationUnitCode;

    private String businessType;

    //是否黑名单
    private String isBlackShip;

}

