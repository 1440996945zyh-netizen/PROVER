package com.yy.ppm.produce.bean.dto;

import com.yy.ppm.produce.bean.po.TPrdVehicleReservationPO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName (TBusPlanEntrust)DTO
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月23日 14:47:00
 */
@Data
public class TPrdVehicleReservationDTO extends TPrdVehicleReservationPO {

    private static final long serialVersionUID = -57132242286403254L;

    private List<Long> idList;

    private Date endTime;

    private String statusLabel;

    private Integer actInPortNum;

    private String trendStatus;
    private String trendStatusLabel;

    private String shipNameVoyage;

    private String activationStateLabel;

    private String planEntrustStatus;

    private String planEntrustDelFlag;

    private String planTime;

    private Long currCustomerId;

    private String idCard;

    private String isOverDue;

    private String isComeDue;

    private String amount;

    private String dayNightStatus;

    private String signStatus;

    private String packing;

    private String billType;

}
