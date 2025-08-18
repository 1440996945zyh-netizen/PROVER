package com.yy.ppm.master.bean.dto;

import com.yy.ppm.master.bean.po.MWorkProcessPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (MWorkProcess)DTO
 *
 * @author 张超
 * @date 2021-03-10 13:55:43
 */
@Getter
@Setter
@ToString
public class MWorkProcessDTO extends MWorkProcessPO implements Serializable {

    private static final long serialVersionUID = 762614740800065640L;

    /**
     * 父过程编号
     */
    private String parentProcessCd;

    private String cd;
    private String nm;

    /**
     * 过程类型
     */
    private String processTypeNm;
    /**
     * 进出港
     */
    private String inOutPortTypeNm;
    /**
     * 是否作业理货量
     */
    private String isTallyTonLabel;
    /**
     * 是否为操作量
     */
    private String isOperationTonLabel;
    /**
     * 是否吞吐量
     */
    private String isThroughputLabel;
    /**
     * 是否自然吨
     */
    private String isNaturalTonLabel;
    /**
     * 是否有前置环节
     */
    private String isPreProcessLabel;
    /**
     * 更新场存节点
     */
    private String updatePoint;
    /**
     * 入库标识
     */
    private String inOutStorageTypeNm;
    /**
     * 源
     */
    private String sourceNm;
    /**
     * 目的
     */
    private String targetNm;
    /**
     * 是否指定核销
     */
    private String isSettleLabel;
    /**
     * 是否理货
     */
    private String isTallyCourseNm;
    /**
     * 是否理货过程  0否1是
     */
    private String isTallyCourse;
    /*是否选择子票货nm*/
    private String isSelectSubticketNm;
    /*是否合并票货nm*/
    private String isMergeTicketNm;
    /*是否新增目标票货nm*/
    private String isCreateTicketNm;
    /*是否直取nm*/
    private String isDirectAccessNm;
    /**
     * 是否直取
     */
    private String isDirectAccess;
    /*是理货员或者调度员nm*/
    private String isDispatchTallyNm;
    /**
     * 是否改变货物包装
     */
    private String isCargoPackageChangeLabel;
    /**
     * 理货数据统计
     */
    private String tallyDataStat;
    /**
     * 理货数据统计nm
     */
    private String tallyDataStatNm;
    private String isFrontier;
    private String isFrontierNm;
    private String isMeanwhile;
    private String isMeanwhileNm;
    private String isDispatchTallyShNm;
    private String isDispatchTallySh;

    /**
     * 新流程，1：新流程，2：老流程
     */
    private String newProcess;
    /*是否二次确认*/
    private String isConfirm;


}
