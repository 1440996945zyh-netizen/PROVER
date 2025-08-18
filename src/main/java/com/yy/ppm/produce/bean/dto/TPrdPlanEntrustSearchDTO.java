package com.yy.ppm.produce.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName (TBusPlanEntrust)DTO
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月22日 14:47:00
 */
@Data
public class TPrdPlanEntrustSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -57272243886403254L;
    /**
     * 集疏港类型
     */
    private String trustType;
    private String billType;
    /**
     * 类型（1：自提；2：委托计划；3：被委托计划）
     */
    private String type;
    /**
     * 计划号（模糊查询）
     */
    private String planNo;
    /**
     * 计划号（精确查询）
     */
    private String planNoAct;
    /**
     * 船名
     */
    private String shipName;
    /**
     * 航次
     */
    private String voyage;
    /**
     * 货主、货代
     */
    private Long cargoOwnerId;
    private String cargoOwnerName;
    /**
     * 货名
     */
    private String cargoName;
    /**
     * 检索条件
     */
    private String searchContent;
    /**
     * 当前用户所属企业
     */
    private String currCustomerCode;
    /**
     * 子计划号精确查询
     */
    private String subPlanNoAct;
    /**
     * 是否管理员
     */
    private String isAdmin;
    /**
     * 船名航次
     */
    private String shipNameVoyage;
	
}
