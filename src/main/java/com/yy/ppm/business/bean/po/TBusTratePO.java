package com.yy.ppm.business.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther linqi
 * @Description 阶梯费率表
 * @Date 2023-11-08 11:33
 */
@Setter
@Getter
public class TBusTratePO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 有效期起
     */
    private Date startTime;

    /**
     * 有效期止
     */
    private Date endTime;
}
