package com.yy.ppm.appWork.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.appWork.bean.dto.AppTallyCoilNumDTO;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * App理货(TYardTallyPO)PO
 * @author chenfs
 * @date 2023-09-15
 */


@Getter
@Setter
@ToString
public class THqYardTallyPO extends BasePO {

    private Long id;

    private Long tallyId;

    private Long cargoInfoId;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    private String classCode;

    private String className;

    private String cargoInfoNo;

    private BigDecimal quantity;

    private BigDecimal ton;

    private BigDecimal inQuantity;

    private BigDecimal inTon;

    private BigDecimal outQuantity;

    private BigDecimal outTon;

    private BigDecimal tallyQuantity;

    private BigDecimal tallyTon;

    //1.出库   2入库
    private String inoutType;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date portTime;

    private String portByName;

    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date tallyTime;

    private String tallyName;

    private String equipmentId;
    private String equipmentNo;
    private String transportEquipmentId;
    private String transportEquipmentNo;
    private String deptId;
    private String deptName;
    private String deptParentId;
    private String deptParentName;
    private String sourceMassName;
    private String targetMassName;



}
