package com.yy.ppm.business.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
 
/**
 * @ClassName 单船测试记录(TStdShipRecord)PO
 * @author makejava
 * @version 1.0.0
 * @Description 
 * @createTime 2023年12月31日 10:35:00
 */
@Data
public class TStdShipRecordPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 690811560553477901L;
    
        /** 主键ID */
    private Long id;
            /** 工作计划id */
    private Long workPlanId;
            /** 点检计划id */
    private Long checkPlanId;
            /** 标准体系id */
    private Long standardSystemId;
            /** 标准体系名称 */
    private String standardSystemName;
            /** 工艺流程id */
    private Long processId;
            /** 工艺流程名称 */
    private String processName;
            /** 点检计划类  */
    private String checkPlanType;
            /** 业务id */
    private Long businessId;
            /** 业务子表id */
    private Long businessItemId;
            /** 实际值 */
    private Long practicalValue;
            /** 是否达标（0:未达标，1：达标） */
    private Long isQualified;
            /** 备注 */
    private String remark;
                            
}

