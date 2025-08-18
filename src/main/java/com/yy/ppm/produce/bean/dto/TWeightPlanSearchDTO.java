package com.yy.ppm.produce.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 杂项过磅计划表(TWeightPlan)SearchDTO
 * @Description TODO
 * @createTime 2023年12月05日 08:39:00
 */
@Data
public class TWeightPlanSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 546333523606600951L;

    /***/
    private Long id;
    /**
     * 服务类型(WORK_TYPE 字典)
     */
    private String workTypeCode;
    /**
     * 服务类型(WORK_TYPE 字典)
     * */
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
    /***/
    private String createByName;
    /***/
    private String updateByName;
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

    //1:杂货过磅计划 2:商务杂货过磅计划
    private String weightType;
}

