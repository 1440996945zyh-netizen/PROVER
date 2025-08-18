package com.yy.ppm.dispatch.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 封航记录表(TDisCloseSail)SearchDTO
 * @Description TODO
 * @createTime 2023年07月12日 11:54:00
 */
@Data
public class TDisCloseSailSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 383731049615603997L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 封航开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;
    /**
     * 封航结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    /**
     * 封航时长（小时）
     */
    private BigDecimal timeLength;
    /**
     * 注意事项
     */
    private String remark;
    /**
     * 创建者-ID
     */
    private Long createBy;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
    /**
     * 查询条件开始时间
     */
    private String beginTimes;
    /**
     * 查询条件结束时间
     */
    private String endTimes;
    /**
     * 查询条件航次
     */
    private String shipvoyageId;
    /**
     * 状态
     */
    private String status;
}

