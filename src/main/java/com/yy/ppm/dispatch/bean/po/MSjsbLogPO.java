package com.yy.ppm.dispatch.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 数据上报日志表(MSjsbLog)PO
 * @Description
 * @createTime 2025年05月20日 09:20:00
 */
@Data
public class MSjsbLogPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -40397073577276850L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 接口信息
     */
    private String jkInfo;
    /**
     * 新增，修改，删除
     */
    private String sjsbType;
    /**
     * 成功，失败
     */
    private String sjsbStatus;
    /**
     * 业务id
     */
    private Long serviceId;
    /**
     * 业务字段1
     */
    private String serviceFieldI;
    /**
     * 业务字段2
     */
    private String serviceFieldIi;
    /**
     * 业务字段3
     */
    private String serviceFieldIii;
    /**
     * 请求参数(JSON格式或其他结构化数据)
     */
    private String requestParameters;
    /**
     * 返回结果(JSON格式或其他结构化数据)
     */
    private String returnResult;
    /**
     * 失败原因
     */
    private String failReason;
    /**
     * 备注
     */
    private String remark;

}

