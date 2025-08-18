package com.yy.ppm.appWork.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class TDisCargoWaterDTO extends BasePO {
    private static final long serialVersionUID = 1L;

    /**主键ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**航次ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal shipvoyageId;
    private BigDecimal shipvoyageItemId;
    /**指令ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal trustId;
    /**公司ID*/
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId; //公司名称
    private String companyName;
    /**贸别，内贸、外贸*/
    private String tradeType;
    /**船名航次*/
    private  String shipvoyageName;
    /**船名航次*/
    private  String shipvoyagesName;
    /**装卸,装.卸*/
    private  String loadUnload;
    /**作业过程代码*/
    private String processName;
    /**作业过程名称*/
    private String processCode;
    /**指令编号*/
    private String trustNo;
    /**泊位编号*/
    private String  berthNo;
    /**靠泊时间*/
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date berthTime;//靠泊时间
    /**作业量*/
    private BigDecimal quantity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date workTime;//作业时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date workTimeEnd;//结束时间
    private String remark;//备注
    private String status;//状态
    private String isVerify;//是否核销 Y是 N否
    /** 附件 */
    private List<SysFileDTO> mattachmentInfoList;
    private List<String> imageList;
    private String fireTruckFilling ;//消防车加水Y是N否
    private String timeRange;

}
