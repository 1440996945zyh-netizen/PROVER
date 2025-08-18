package com.yy.ppm.produce.bean.dto.workTicket;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketPO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-15 11:14
 */
@Setter
@Getter
public class TPrdWorkTicketAttenceUserDTO extends BasePO {

    /** 主键ID */
    private Long id;
    private Long deptId;

    /** 出勤点名ID */
    private Long attendanceId;

    /** 出勤人员-ID */
    private Long userId;

    /** 出勤人员-姓名 */
    private String userName;

    /**
     * 分配系数
     */
    private Integer coefficient;

    /**
     * 是否上班
     */
    private String iswork;

}
