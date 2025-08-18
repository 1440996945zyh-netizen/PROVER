package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (TWeightPlanItem)SearchDTO
 * @Description TODO
 * @createTime 2023年12月05日 08:39:00
 */
@Data
public class TWeightPlanItemSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 102952334941216871L;

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

