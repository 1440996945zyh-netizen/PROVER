package com.yy.ppm.dispatch.bean.po;

import lombok.Data;

import java.io.Serializable;

/**
 * 工班计划-作业班组信息
 * @author yangcl
 * */
@Data
public class TShiftPlanWorkteamPO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 计划ID
     */
    private Long shiftPlanId;

    /**
     * 作业队code
     */
    private String workTeamCode;

    /**
     * 作业队名称
     */
    private String workTeamName;

    /**
     * 人数
     */
    private Integer num;

    private static final long serialVersionUID = 1L;
}

