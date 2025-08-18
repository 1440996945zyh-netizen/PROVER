package com.yy.ppm.dispatch.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class TDisLowerCabinPO  extends BasePO {
    private Long id;
    private Long shipvoyageId; //船舶ID
    private List<Long> shipVoyageItemIds;
    private Long shipvoyageItemId; //船舶ID
    private String workType; //作业类型，1机械、2劳务
    private String cabinNo; //舱口
    private String equipmentId; //设备ID
    private String equipmentNo; //设备编号
    private Long equipmentTypeId; //设备类型编号
    private String equipmentTypeName; //设备类型编号
    private Long deptId; //部门id
    private String deptName; //部门name
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime; //下舱时间
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime; //出舱时间
    private BigDecimal workload; //作业量
    private String remark; //备注
    private String cnsc; //舱内时长

    private String cargoName; //货名

    private String loadUnload; //装卸

}
