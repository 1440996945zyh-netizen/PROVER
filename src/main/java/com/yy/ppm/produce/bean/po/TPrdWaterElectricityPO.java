package com.yy.ppm.produce.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 加水接电记录表(TPrdWaterElectricity)PO
 *
 * @author linqi
 * @since 2023-09-23 14:18:55
 */
@Setter
@Getter
public class TPrdWaterElectricityPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司ID
     */
    private Long companyId;

    /**
     * 作业公司NAME
     */
    private String companyName;

    /**
     * 航次ID
     */
    private Long shipvoyageId;

    /**
     * 指令ID
     */
    private Long trustId;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 高压、低压 1.高压 2.低压
     */
    private String type;

    /**
     * 表底
     */
    private Double startNumber;

    /**
     * 抄表
     */
    private Double endNumber;

    /**
     * 作业量
     */
    private Double quantity;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 备注
     */
    private String remark;
}

