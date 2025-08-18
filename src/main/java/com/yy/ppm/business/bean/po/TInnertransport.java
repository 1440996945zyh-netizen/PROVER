package com.yy.ppm.business.bean.po;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-14 16:50
 */
@Setter
@Getter
public class TInnertransport {

    private Long id;
    private String planCode;
    private String amCode;
    private Integer planType;
    private Integer port;
    private String plan_from;
    private String plan_to;
    private Integer planCompStatus;
    private String planCompOper;
    private Date planCompTime;
    private Integer delete_flag;
    private String creator;
    private Date createTime;
    private String editor;
    private Date editTime;
    private Integer isWeight;
    private Integer isUpload;
    private Integer isFirstOnly;
    private String wbCode;
    private Integer isAll;
    private Integer amId;
    private String contract_item_id;
    private String uploadType;
}
