package com.yy.ppm.master.bean.po;


import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.master.bean.dto.FieldRemark;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName 海轮资料(MShip)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月27日 15:44:00
 */
@Data
public class MShipLogPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 738872981208110638L;

    /** id */
    private Long id;
    /** 船舶id */
    @FieldRemark(value = "渤海通id")
    private Long shipId;
    /** IMO */
    @FieldRemark(value = "IMO")
    private String imo;
    /** MMSI */
    private String mmsi;
    /** 修改信息 */
    private String updateInfo;

    private JSONArray updateInfos;

}

