package com.yy.ppm.produce.bean.dto;


import com.yy.ppm.produce.bean.po.TPrdDispatchSecondaryPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName 作业计划派工表（二次配工）(TPrdDispatchSecondary)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月30日 18:16:00
 */
@Getter
@Setter
@ToString
public class TPrdDispatchSecondaryDTO extends TPrdDispatchSecondaryPO {

    private static final long serialVersionUID = 317324886345336468L;


    //当前班次
    private String classCode;
    private String workDate;
    //上班次
    private String classCodeOne;
    private String workDateOne;
    //下班次
    private String classCodeTwo;
    private String workDateTwo;

    private Long trustId;
    private String trustCargoId;

    private String scn;
    private String planNo;
    private String cargoName;
    private String tradeType;
    private String voyage;
    private String shipName;
    private String shipVoyageId;
    private String cargoInfoId;
    /**
     * 设备类型编号
     */
    private String equipmentTypeCode;

    private Long tmpDeptId;
    private String tmpDeptName;

    /**
     * 装卸队派工用来存部门
     */
    private Long deptParentId;
    /**
     * 装卸队派工用来存部门
     */
    private String deptParentName;

    /**
     * 作业过程中的是否理货过程
     */
    private String isTallyCourse;


}
