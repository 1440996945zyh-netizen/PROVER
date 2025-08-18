package com.yy.ppm.dispatch.bean.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.page.PageParameter;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 拖轮服务记录(TDisTugServiceRecord)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月12日 11:45:00
 */
@Data
public class TDisTugServiceRecordSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -53484070173816917L;

    /**主键ID*/
    private Long id;
    /**航次ID*/
    private Long shipvoyageId;
    private String scn;
    /**船舶ID*/
    private Long shipId;
    /**中文船名*/
    private String shipName;
    /**动态id*/
    private Long shipDynamicId;
    /**拖轮id*/
    private Long tugId;
    /**
     * 是否标准使用  1:是；0：否
     */
    private String isStandardUse;
    /**拖轮名称*/
    private String tugName;
    /**服务内容code（字典 TUG_SERVICE _TYPE）*/
    private String tugServiceType;
    /**服务内容name（协助靠泊、协助离泊、协助移泊、演习、海事使用、特殊情况抢险）*/
    private String tugServiceTypeName;
    /**开始时间*/
    private String startTime;
    /**结束时间*/
    private String endTime;
    /**服务时长（小时）*/
    private BigDecimal timeLength;
    /**备注*/
    private String remark;
    /**创建者-ID*/
    private Long createBy;
    /**创建者-姓名*/
    private String createByName;
    /**更新者-姓名*/
    private String updateByName;

}

