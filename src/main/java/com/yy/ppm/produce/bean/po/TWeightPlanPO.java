package com.yy.ppm.produce.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 杂项过磅计划表(TWeightPlan)PO
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@Data
public class TWeightPlanPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 191451338012553539L;

    /**
     *
     */
    private Long id;
    /**
     * 服务类型(WORK_TYPE 字典)
     */
    private String workTypeCode;
    /**
     *服务类型
     */
    private String workTypeName;
    /**
     * 客户
     */
    private String customerName;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 泊位
     */
    private String berth;
    /**
     * 作业区
     */
    private String portCode;
    /**
     * 作业区
     */
    private String portName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 0开启1结束
     */
    private Long status;
    /**
     * 开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 装卸,装、卸
     */
    private Long loadUnload;

    /** 货物名称 */
    private String cargoName;

    /** 计划号 */
    private String planNo;
    private Long examineStatus;
    private String workAreaName;
    //1:杂货过磅计划 2:商务杂货过磅计划
    private String weightType;
    private List<Long> fileIds;
}

