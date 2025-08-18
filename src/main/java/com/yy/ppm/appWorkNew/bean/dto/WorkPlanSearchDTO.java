package com.yy.ppm.appWorkNew.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName 件货理货作业计划查询参数
 * @author cuizk
 * @version 1.0.0
 * @createTime 2024年07月04日 16:21:00
 */
@Getter
@Setter
@ToString
public class WorkPlanSearchDTO implements Serializable {

    private static final long serialVersionUID = 587933060352551627L;

    /**
     * 日期
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    private String workDate;

    /**
     * 班次（字典）
     */
    private String classCode;

    /**
     * 计划号
     */
    private String businessNo;

    /**
     * 票货号
     */
    private String cargoInfoNo;


    private String shipNameVoyage;

    /**
     * 船名
     */
    private String shipName;

    /**
     * 航次
     */
    private String voyage;

    private String loginId;

    private Integer flag;

    private String processName;

    private String packingCode;

}
