package com.yy.ppm.system.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/6 11:30
 */
@Data
public class SysOperLogSearchDTO extends PageParameter implements Serializable {

    /** 开始时间 */
    private String beginTimes;
    /** 结束时间 */
    private String endTimes;

    /** 操作人员 */
    private String operUserName;

    /** 操作模块 */
    private String title;

    /** 方法名 */
    private String method;

    /** 操作类型 */
    private String businessType;

    /** pc */
    private String operType;

    /** 状态正常 异常 */
    private String status;


}
