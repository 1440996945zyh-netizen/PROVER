package com.yy.ppm.dispatch.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.excel.export.bean.SheetMapping;
import com.yy.ppm.dispatch.bean.po.TDayNightPlanPO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 昼夜计划DTO
 * @author yangcl
 * */
@Getter
@Setter
public class ShipWorkReportDTO extends SheetMapping {

    private String shipVoyage;
    private String scn;
    private String tradeType;
    private String loadUnload;
    private String hatchNum;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date arrivalAnchorageTime;
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;
    private String lbsj;
    private String kgsj;
    private String wgsj;
    private String sumTime;
    private String tingGongTime;
    private String jingTime;
    private String shipEfficiency;
    private String doorEfficiency;
    private List<HandoverList> handoverList;
    private List<Dynamic> dynamicList;
    private List<WorkTickets> workTickets;

    @Getter
    @Setter
    public class HandoverList{
        private String cargoName;
        private String ton;
        private String hatchNum;
        private String sumTime;
        private String tingGongTime;
        private String jingTime;
        private String shipEfficiency;
        private String doorEfficiency;
    }

    @Getter
    @Setter
    public class Dynamic{
        private String tingGongReason;
        private String tingGongStartTime;
        private String tingGongEndTime;
        private String tingGongTime;
        private String value;
    }

    @Getter
    @Setter
    public class WorkTickets{
        private String className;
        private String processDetailName;
        private String personnelName;
        private String equipmentNo;
        //门机
        private String menjiNo;
        //下仓设备号
        private String xiaCangNo;
        private String deptItemName;
        private String ton;
        private String value;
    }




}
