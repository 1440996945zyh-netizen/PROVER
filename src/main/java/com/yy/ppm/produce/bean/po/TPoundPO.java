package com.yy.ppm.produce.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
public class TPoundPO extends BasePO {

    /**
     * ID
     */
    private Long noteId;

    /**
     * 磅单号
     */
    private String unionNo;


    /**
     * 进港时间
     */
    private String weighInDt;


    /**
     * 进港司磅员
     */
    private String checkerInName;

    /**
     * 出港时间
     */
    private String weighOutDt;

    /**
     * 出港司磅员
     */
    private String checkerOutName;

    /**
     * 计划单号
     */
    private String planNo;

    /**
     * 货物名称
     */
    private String goodsName;

    /**
     * 目的地
     */
    private String goodsDes;

    /**
     * 车牌号
     */
    private String truckPlate;

    /**
     * 进港类型
     */
    private String weighNumberype;

    /**
     * 出港类型
     */
    private String weighOuttype;

    /**
     * 皮重
     */
    private String weighSelf;

    /**
     * 毛重
     */
    private String weighAll;

    /**
     * 净重
     */
    private String weighGoods;

    /**
     * 船名
     */
    private String comName;

    /**
     * 船号
     */
    private String comNo;

    /**
     * 是否删除
     */
    private String isDelete;

    /**
     * 是否完成
     */
    private String isFinished;

    /**
     * 备注
     */
    private String invRem;

    /**
     * 发货单位
     */
    private String conUnit;

    /**
     * 收货单位
     */
    private String recUnit;

    /**
     * 委托人
     */
    private String agentName;

    /**
     * 任务ID
     */
    private String tsptId;

    /**
     * 作业ID
     */
    private String workErweiId;

    /**
     * 一次磅号
     */
    private String inBangNo;

    /**
     * 二次磅号
     */
    private String outBangNo;

    /**
     * 港区代码
     */
    private String portCode;

    /**
     * 到港编号
     */
    private String scn;

    /**
     * 贸别
     */
    private String tradeType;
}

