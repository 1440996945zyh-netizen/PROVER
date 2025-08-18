package com.yy.ppm.businessKH.vo.resp;
/**
 * @ClassName CargoInfoSearchReqVo.java
 * @author lihuijie
 * @version 1.0.0
 * @Description 查询某个从表id下的主表和从表的关联数据
 * @createTime 2022-05-04 14:35
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.yy.ppm.businessKH.model.CargoInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CargoInfoAndDetailRespVo extends CargoInfo implements Serializable {

    /**
     * id
     */
    private String id;


    /**
     * 类别（加数扣数/通知单/计划申报/尾货合并）
     */
    private String lb;

    /**
     * 变更类型(加数扣数类型)
     */
    private String type;

    /**
     * 数量
     */
    @TableField("shl")
    private BigDecimal cdshl;

    /**
     * 备注
     */
    private String note;


}
