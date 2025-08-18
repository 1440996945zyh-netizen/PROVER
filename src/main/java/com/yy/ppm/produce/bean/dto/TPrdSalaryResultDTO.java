package com.yy.ppm.produce.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yy.common.page.PageParameter;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Auther wangxd
 * @Description
 * @Date 2023-11-17 20:28
 */
@Setter
@Getter
public class TPrdSalaryResultDTO extends TPrdSalaryPO {


    /**
     * 签票时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ticketTime;

    /**
     * 签票人
     */
    private String ticketByName;

    private String deptNm;

    //零工内容
    private String workContent;

    //零工编号
    private String oddPlanNo;

    //作业过程code
    private String processCode;

    //作业过程name
    private String processName;




}
