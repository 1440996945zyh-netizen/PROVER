package com.yy.ppm.dispatch.bean.dto;

import com.yy.ppm.dispatch.bean.po.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 工班计划DTO
 * @author yangcl
 * @*/
@Getter
@Setter
public class ShiftPlanDTO extends TShiftPlanPO {

     //工班计划-作业工艺-机械配置集合
     List<TShiftPlanMacPO> macConfigList;

     //机械配工信息
     List<TShiftPlanMachinistPO> machineList;

     //工班计划-作业工艺-工人配置集合
     List<TShiftPlanWorkerPO> workerList;

     //工班计划班组人员信息
     List<TShiftPlanWorkteamPO> workTeamList;

     //工属具信息
     List<TShiftPlanWorkwarePO> wareList;
}
