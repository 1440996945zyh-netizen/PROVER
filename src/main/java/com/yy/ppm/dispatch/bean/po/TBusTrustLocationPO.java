package com.yy.ppm.dispatch.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
 
/**
 * @ClassName 集疏港作业通知单位置表，传输渤海通使用(TBusTrustLocation)PO
 * @author makejava
 * @version 1.0.0
 * @Description 
 * @createTime 2023年09月27日 15:41:00
 */
@Data
public class TBusTrustLocationPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 117087443070075669L;
    
        /** 主键ID */
    private Long id;
            /** 通知单ID */
    private Long trustId;
            /** 库场ID */
    private String storehouseId;
            /** 库场名称 */
    private String storehouseName;
            /** 区域ID */
    private String regionId;
            /** 区域名称 */
    private String regionName;
                            
}

