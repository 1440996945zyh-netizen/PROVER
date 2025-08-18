package com.yy.ppm.produce.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (TWeightPlanItem)PO
 * @Description
 * @createTime 2023年12月05日 08:39:00
 */
@Data
public class TWeightPlanItemPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -66572698859294874L;

    /**
     * id
     */
    private Long id;
    /**
     * 父id
     */
    private Long parentId;
    /**
     * 身份证号
     */
    private String idNumber;
    /**
     * 车牌号
     */
    private String truckNo;
    /**
     * 司机
     */
    private String driver;
    /**
     * 电话
     */
    private String tel;
    /**
     * 0正常1停止
     */
    private Long status;
    /**
     * 是否过磅
     * */
    private Long isPounds;

}

