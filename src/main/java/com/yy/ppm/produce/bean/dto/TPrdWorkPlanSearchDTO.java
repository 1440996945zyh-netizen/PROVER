package com.yy.ppm.produce.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName 作业计划表(TPrdWorkPlan)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月21日 16:21:00
 */
@Getter
@Setter
@ToString
public class TPrdWorkPlanSearchDTO implements Serializable {

    private static final long serialVersionUID = 587933060352551627L;

    /** 作业公司ID */
    private Long companyId;
    /** 日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;
    private String workDateString;
    /** 班次（字典） */
    private String classCode;
    private String macName;
    private String zfFlag;
    /** 计划类型同作业过程 */
    private String planType;
    /** 状态，10：未审核：20：已审核30作业中40停工50完工 */
    private String status;

    /** 按作业计划ID查询 */
    private List<Long> ids;
    private String loginId;
    private Integer flag;

    /**
     * 包装
     */
    private String packageCode;

    /**
     * 公司编码
     */
    private String deptCode;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 计划号
     */
    private String businessNo;

    /**
     * trustId
     */
    private Long trustId;

    /**
     * 作业过程编码
     */
    private String processCode;
    private String processName;

    private Long workPlanId;
    private String trustNo;
    private String shipvoyageItemId;
    private Long cargoOwnerId;
    private String cargoCode;

    /**
     * 查询类型，以区分零工申请查询、库场派工查询、二次派工查询等
     * oddQuery:零工申请查询
     */
    private String queryType;
    //作业港区，01东港02中港03西港
    private String portCode;
    private String cargoInfoNo;
    //是否完货
    private String isClear;

    /** 船名 */
    private String shipName;
    private String voyage;


    private String trustIds;

    private String trustType;

    private String customerName;
    private String JSGtemp;
    private String classJSGCode;
    private String classJSGName;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date JSGWorkDate;
}
