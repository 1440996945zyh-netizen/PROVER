package com.yy.ppm.businessKH.vo.resp;
/**
 * @ClassName CargoInfoSearchReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 控货管理查询入参VO
 * @createTime 2022-05-04 14:35
 */
import com.yy.ppm.businessKH.model.CargoInfo;
import com.yy.ppm.businessKH.model.CargoInfoDetail;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class CargoInfoRespVo extends CargoInfo implements Serializable {
//    private String zygs;//
//    private String hth;//合同号
//    private String htgsdm;//
//    private String htgs;//
//    private String hthqcyrid;//
//    private String hthqcyr;//
//    private String zywtr;//
//    private String hwmch;//
//    private String hqcyr;//
//    private String gjfkhflag;//港建费控货状态（已交、未交、不控货）
//    private String gjfflag;//港建费状态（已交、未交）
//    private String gjfkh;//是否港建费控货（是、否）
//    private String xldm;//小类货物代码（用于权限过滤）
//    private String gsdm;//小类货物代码所属作业公司（用于权限过滤）
//    private String departmentid;//部门id（用于权限过滤）
//    private String zhuanyungsmch;//转运公司名称
    private List<CargoInfoDetail> cargoInfoDetailList;//加扣数详情list

    //分货添加的字段
    private String glshgl;//公路疏港量
    private String tlshgl;//铁路疏港量
    private String jhzhchl;//计划装船量
    private String shjzhchl;//实际装船量
    private String qk;//欠款
    private String qkchb;//船舶欠款
    private String qkcus;//客户欠款
    private String kc;//库存

    //分货控数的添加字段
    private BigDecimal fhks;//分货控数
    private Date khrq;//控货时间
    private String jhy;//计划员
    private String note;//备注
}
