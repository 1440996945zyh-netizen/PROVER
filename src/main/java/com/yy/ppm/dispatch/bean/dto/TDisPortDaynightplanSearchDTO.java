package com.yy.ppm.dispatch.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 集疏港昼夜计划(TDisPortDaynightplan)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年11月14日 10:31:00
 */
@Data
public class TDisPortDaynightplanSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -22425403383420890L;

            /**主键ID*/
    private Long id;
            /**指令ID*/
    private Long trustId;
            /**计划日期*/
    private Date planDate;
            /**航次ID*/
    private Long shipvoyageId;
            /**航次子表*/
    private Long shipvoyageItemId;
}

