package com.yy.ppm.dispatch.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 调度日志(TDisLog)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月12日 11:45:00
 */
@Data
public class TDisLogSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 750610733947223702L;

            /**主键ID*/
    private Long id;
            /**交班日期*/
    private String shiftDate;
            /**交班班次ID*/
    private String shiftClassCode;
            /**交班班次name*/
    private String shiftClassName;
            /**交班人id*/
    private Long shiftBy;
            /**交班人name*/
    private String shiftByName;
            /**接班人id*/
    private Long acceptBy;
            /**接班人name*/
    private String acceptByName;
            /**水文信息*/
    private String hydrologic;
            /**潮汐信息*/
    private String tide;
            /**注意事项*/
    private String remark;
            /**创建者-ID*/
    private Long createBy;
            /**创建者-姓名*/
    private String createByName;
                            /**更新者-姓名*/
    private String updateByName;
            }

