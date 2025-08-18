package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (MWorkProcess)SearchDTO
 *
 * @author 张超
 * @date 2021-03-10 13:55:43
 */
@Getter
@Setter
@ToString
public class MWorkProcessSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -15815584633259578L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 父ID
     */
    private Long parentId;
    /**
     * 作业过程编号，主4位，子过程8位
     */
    private String processCd;
    /**
     * 作业过程名称
     */
    private String processNm;
    /**
     * 速记码
     */
    private String shortCd;
    /**
     * 作业过程种类  （字典PROCESS_TYPE）
     */
    private String processTypeCd;
    /**
     * 进出港配置  (字典IN_OUT_PORT_TYPE)
     */
    private String inOutPortTypeCd;
    /**
     * 是否选择子票货  1:是；0：否
     */
    private String isSelectSubticket;
    /**
     * 是否合并票货  1:是；0：否
     */
    private String isMergeTicket;
    /**
     * 是否新增目标票货  1:是；0：否
     */
    private String isCreateTicket;
    /**
     * 是否直取  1:是；0：否
     */
    private String isDirectAccess;
    /**
     * 是理货员还是调度员  1:调度员；2：理货员
     */
    private String isDispatchTally;
    /**
     * 是否可提  1:是；0：否
     */
    private String isTake;
    /**
     * 源     (字典 SOURCE_TARGET_TYPE)
     */
    private String sourceCd;
    /**
     * 目的     (字典 SOURCE_TARGET_TYPE)
     */
    private String targetCd;
    /**
     * 是否作业理货量
     */
    private String isTallyTon;
    /**
     * 是否为操作量
     */
    private String isOperationTon;
    /**
     * 是否吞吐量
     */
    private String isThroughput;
    /**
     * 是否自然吨
     */
    private String isNaturalTon;
    /**
     * 是否指定核销
     */
    private String isSettle;
    /**
     * 是否有前置环节
     */
    private String isPreProcess;
    /**
     * 更新场存节点
     */
    private String updatePoint;
    /**
     * 入库标识 (字典 IN_OUT_STORAGE_TYPE）
     */
    private String inOutStorageTypeCd;
    /**
     * 排序号
     */
    private Integer sortNum;
    /**
     * 备注
     */
    private String remark;
/**
     * 理货数据统计
     */
    private String tallyDataStat;

}
