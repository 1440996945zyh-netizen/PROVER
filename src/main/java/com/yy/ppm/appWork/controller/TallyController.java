package com.yy.ppm.appWork.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.FieldErrorUtils;
import com.yy.framework.annotation.Log;
import com.yy.ppm.appWork.bean.dto.*;
import com.yy.ppm.appWork.bean.po.THqYardTallyPO;
import com.yy.ppm.appWork.bean.po.TYardTallyItemPO;
import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.appWork.service.TDisWaterElectricityService;
import com.yy.ppm.appWork.service.TallyService;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;


/**
 * App理货
 *
 * @author chenfs
 * @since 2023年9月14日
 */
@RestController
@RequestMapping("/api/external/tally")
@Validated
public class TallyController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(TallyController.class);


    @Autowired
    private TallyService tallyService;

    /**
     * 查询计划
     *
     * @param
     * @param searchDTO 实例对象
     * @return
     */
    @GetMapping("/getWorkPlan")
    public Map<String, Object> getWorkPlan(TPrdWorkPlanSearchDTO searchDTO) {
        final String methodName = "getWorkPlan";
        LOGGER.enter(methodName, "查询计划, searchDTO: " + searchDTO);
        List<TPrdWorkPlanDTO> list = tallyService.getWorkPlan(searchDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 根据实体类筛选数据列表(查询作业过程)
     *
     * @param mWorkProcessSearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getProcessInfoList")
    public Map<String, Object> getProcessInfoList(MWorkProcessSearchDTO mWorkProcessSearchDTO) {
        final String methodName = "MWorkProcessController: getList";
        LOGGER.enter(methodName + "[start]", "sysUserSearchDTO:" + mWorkProcessSearchDTO);
        List<Map<String, Object>> mWorkProcessList = tallyService.getProcessInfoList(mWorkProcessSearchDTO);
        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询机械信息
     *
     * @param tPrdDispatchSecondarySearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getMechanics")
    public Map<String, Object> getMechanics(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        final String methodName = "tPrdDispatchSecondarySearchDTO: getMechanics";
        List<Map<String, Object>> mWorkProcessList = tallyService.getMechanics(tPrdDispatchSecondarySearchDTO);
        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询转运车信息
     *
     * @param tPrdDispatchSecondarySearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getTransfer")
    public Map<String, Object> getTransfer(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        final String methodName = "tPrdDispatchSecondarySearchDTO: getTransfer";
        List<Map<String, Object>> mWorkProcessList = tallyService.getTransfer(tPrdDispatchSecondarySearchDTO);
        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询外部车辆信息
     *
     * @param tPrdDispatchSecondarySearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getCarFer")
    public Map<String, Object> getCarFer(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        final String methodName = "tPrdDispatchSecondarySearchDTO: getCarFer";
        List<Map<String, Object>> mWorkProcessList = tallyService.getCarFer(tPrdDispatchSecondarySearchDTO);
        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }
    /**
     * 查询外部车辆信息
     *
     * @param tPrdDispatchSecondarySearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getCarFerNew")
    public Map<String, Object> getCarFerNew(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO) {
        final String methodName = "tPrdDispatchSecondarySearchDTO: getCarFer";
        List<Map<String, Object>> mWorkProcessList = tallyService.getCarFerNew(tPrdDispatchSecondarySearchDTO);
        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询票货信息(根据指令)
     *
     * @param planId 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getCargoInfoId")
    public Map<String, Object> getCargoInfoId(String trustId,String planId,String processCode) {
        List<Map<String, Object>> mWorkProcessList = tallyService.getCargoInfoId(trustId,planId,processCode);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询票货信息(根据指令)
     *
     * @param planId 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getCargoInfoIdTr")
    public Map<String, Object> getCargoInfoIdTr(String planId) {
        List<Map<String, Object>> mWorkProcessList = tallyService.getCargoInfoIdTr(planId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询库场区域(根据计划ID)
     *
     * @param workPlanId 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getStore")
    public Map<String, Object> getStore(Long workPlanId) {
        List<Map<String, Object>> mWorkProcessList = tallyService.getStore(workPlanId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询垛位(根据区域ID)
     *
     * @param id 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getStackPosition")
    public Map<String, Object> getStackPosition(Long id) {
        List<Map<String, Object>> mWorkProcessList = tallyService.getStackPosition(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     *根据航次ID查询舱口数
     *
     * @param id 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getHatch")
    public Map<String, Object> getHatch(Long id) {
        List<Map<String, Object>> hatchList = tallyService.getHatch(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(hatchList);
    }


    /**
     * 根据指令ID查询待作业
     *
     * @param
     * @return
     */
    @GetMapping("/getWaitWork")
    public Map<String, Object> getWaitWork(Long trustId) {

        final String methodName = "getWaitWork";
        LOGGER.enter(methodName, "根据指令ID查询待作业记录, trustId: " + trustId);
        List<TYardTallyPO> list = tallyService.getWaitWork(trustId);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }


    /**
     * 根据计划id和作业过程查询理货记录
     *
     * @param tallyRecordSearchDTO
     * @return
     */
    @GetMapping("/getTallyRecord")
    public Map<String, Object> getTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getTallyRecord";
        LOGGER.enter(methodName, "根据计划id和作业过程查询理货记录");
        Pages<TYardTallyItemPO> pages = tallyService.getTallyRecord(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 根据计划id和作业过程查询理货记录
     * @param tallyRecordSearchDTO
     * @return
     */
    @GetMapping("/getHqTallyRecord")
    public Map<String, Object> getHqTallyRecord(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getHqTallyRecord";
        LOGGER.enter(methodName, "根据计划id和作业过程查询理货记录");
        Pages<THqYardTallyPO> pages = tallyService.getHqTallyRecord(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }
    /**
     * 根据计划id和作业过程查询理货记录
     * @param tallyRecordSearchDTO
     * @return
     */
    @GetMapping("/getHqTallyData")
    public Map<String, Object> getHqTallyData(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getHqTallyRecord";
        LOGGER.enter(methodName, "根据计划id和作业过程查询理货记录");
        List<THqDataDTO> pages = tallyService.getHqTallyData(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * @return 统一数据封装
     */
    @GetMapping("/getHqCargoName")
    public Map<String, Object> getHqCargoName() {
        List<Map<String, Object>> result = tallyService.getHqCargoName();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * @return 统一数据封装
     */
    @GetMapping("/getHqYard")
    public Map<String, Object> getHqYard() {
        List<Map<String, Object>> result = tallyService.getHqYard();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据理货id查询单条理货记录
     *
     * @param tallyRecordSearchDTO
     * @return
     */
    @GetMapping("/getTallyInfoById")
    public Map<String, Object> getTallyInfoById(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getTallyInfoById";
        LOGGER.enter(methodName, "根据理货id查询单条理货记录");
        TYardTallyPO pages = tallyService.getTallyInfoByIdNew(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    @GetMapping("/getTallyInfoByIdNew")
    public Map<String, Object> getTallyInfoByIdNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getTallyInfoById";
        LOGGER.enter(methodName, "根据理货id查询单条理货记录");
        TYardTallyPO pages = tallyService.getTallyInfoById(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 根据计划id查询单条理货记录(最新的一条)
     *
     * @param tallyRecordSearchDTO
     * @return
     */
    @GetMapping("/getTallyNew")
    public Map<String, Object> getTallyNew(TallyRecordSearchDTO tallyRecordSearchDTO, PageParameter pageParameter) {
        final String methodName = "getTallyInfoById";
        LOGGER.enter(methodName, "根据理货id查询单条理货记录");
        TYardTallyPO pages = tallyService.getTallyNew(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    @GetMapping("/lookCoil")
    public Map<String, Object> lookCoil(Long id) {
        final String methodName = "lookCoil";
        LOGGER.enter(methodName, "根据理货id查询卷钢号理货记录");
        List<AppTallyCoilNumDTO> pages = tallyService.lookCoil(id);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询车号关联的票货信息
     * @param
     * @return
     */
    @GetMapping("/getTransportEquipmentCargoInfo")
    @Log(title = "查询车号关联的票货信息", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getTransportEquipmentCargoInfo(Long trustId, String vehicleNo) {
        final String methodName = "getTransportEquipmentCargoInfo";
        LOGGER.enter(methodName, "查询车号关联的票货信息" + "trustId: " + trustId + ", vehicleNo: " + vehicleNo);
        if(StringUtils.isNoneBlank(vehicleNo)) {
        	vehicleNo = vehicleNo.trim();
        }
        Map<String, Object> resultMap = tallyService.getTransportEquipmentCargoInfo(trustId, vehicleNo);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    /**
     * 查询车号关联的票货信息(直取)
     * @param
     * @return
     */
    @GetMapping("/getTransportEquipmentCargoInfoZ")
    @Log(title = "查询车号关联的票货信息", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getTransportEquipmentCargoInfoZ(Long trustId, String vehicleNo) {
        final String methodName = "getTransportEquipmentCargoInfoZ";
        LOGGER.enter(methodName, "查询车号关联的票货信息" + "trustId: " + trustId + ", vehicleNo: " + vehicleNo);
        if(StringUtils.isNoneBlank(vehicleNo)) {
            vehicleNo = vehicleNo.trim();
        }
        Map<String, Object> resultMap = tallyService.getTransportEquipmentCargoInfoZ(trustId, vehicleNo);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    /**
     * 根据计划id和作业过程理货记录汇总
     *
     * @param tallyRecordSearchDTO
     * @return
     */
    @GetMapping("/getTallyRecordSum")
    public Map<String, Object> getTallyRecordSum(TallyRecordSearchDTO tallyRecordSearchDTO) {

        final String methodName = "getTallyRecordSum";
        LOGGER.enter(methodName, "根据计划id和作业过程理货记录汇总");
        Map<String, Object> map = tallyService.getTallyRecordSumNew(tallyRecordSearchDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(map);
    }

    /**
     * 查询出入库数据(港存)
     *
     * @param tYardMeasureSearchDTO
     * @return
     */
    @GetMapping("/getCarDetailedList")
    public Map<String, Object> getCarDetailedList(TYardMeasureSearchDTO tYardMeasureSearchDTO) {
        List<AppTallyLadingDTO> list = tallyService.getCarDetailedList(tYardMeasureSearchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询出入库数据(港存)
     *
     * @param tYardMeasureSearchDTO
     * @return
     */
    @GetMapping("/getCarDetailedListNew")
    public Map<String, Object> getCarDetailedListNew(TYardMeasureSearchDTO tYardMeasureSearchDTO) {
        List<AppTallyLadingDTO> list = tallyService.getCarDetailedListNew(tYardMeasureSearchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询货物清单
     *
     * @param appTallyCoilNumDTO
     * @return
     */
//    @GetMapping("/getCoilList")
//    public Map<String, Object> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO, PageParameter pageParameter) {
//        Pages<AppTallyCoilNumDTO> list = tallyService.getCoilList(appTallyCoilNumDTO, pageParameter);
//        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
//    }

    @GetMapping("/getCoilList")
    public Map<String, Object> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO) {
        List<AppTallyCoilNumDTO> list = tallyService.getCoilList(appTallyCoilNumDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    @GetMapping("/getBoxList")
    public Map<String, Object> getBoxList(AppTallyCoilNumDTO appTallyCoilNumDTO) {
        List<AppTallyCoilNumDTO> list = tallyService.getBoxList(appTallyCoilNumDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询空/重车数据
     * @param truckPlate
     * @return
     */
    @GetMapping("/getDepartureList")
    public Map<String, Object> getDepartureList(@NotNull(message = "车牌号不能为空") String truckPlate) {
        List<DepartureDTO> list = tallyService.getDepartureList(truckPlate);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询空/重车数据记录
     * @return
     */
    @GetMapping("/getDepartureRecordList")
    public Map<String, Object> getDepartureRecordList(@NotNull(message = "开始时间不能为空") String startDate,@NotNull(message = "结束时间不能为空") String endDate) {
        List<DepartureDTO> list = tallyService.getDepartureRecordList(startDate,endDate);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询货物是否是件号理货(打标号)
     *
     * @param code
     * @return
     */
    @GetMapping("/selectBh")
    public Map<String, Object> selectBh(String code) {
        String flag = tallyService.selectBh(code);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(flag);
    }

    /**
     * 根据计划ID查询装卸班组
     *
     * @param planId
     * @return
     */
    @GetMapping("/getDept")
    public Map<String, Object> getDept(Long planId) {
        List<Map<String, Object>> map = tallyService.getDept(planId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(map);
    }


    /**
     * 修改理货记录
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="修改理货记录",value = OperateTypeEnum.DELETE)
    @PostMapping("updateTallyInfo")
    public Map<String, Object> updateTallyInfo(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "updateTallyInfo";
        LOGGER.enter(methodName, "修改理货记录, tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.updateTallyInfoNew(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }
     /* 修改理货记录
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="修改理货记录",value = OperateTypeEnum.DELETE)
    @PostMapping("updateTallyInfoNew")
    public Map<String, Object> updateTallyInfoNew(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "updateTallyInfo";
        LOGGER.enter(methodName, "修改理货记录, tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.updateTallyInfoNew(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 空/重车出港
     * @param departureDTO
     * @param result
     * @return
     */
    @Log(title ="空/重车出港",value = OperateTypeEnum.UPDATE)
    @PostMapping("departureSub")
    public Map<String, Object> departureSub(@RequestBody @Valid DepartureDTO departureDTO, BindingResult result) {
        final String methodName = "departureSub";
        LOGGER.enter(methodName, "空/重车出港, departureDTO: " + departureDTO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.departureSub(departureDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 空/重车出港撤销
     * @param
     * @param result
     * @return
     */
    @Log(title ="空/重车出港撤销",value = OperateTypeEnum.UPDATE)
    @PostMapping("departureRevoke")
    public Map<String, Object> departureRevoke(@RequestBody @Valid DepartureDTO departureDTO, BindingResult result) {
        final String methodName = "departureRevoke";
        LOGGER.enter(methodName, "空/重车出港撤销, departureDTO: " + departureDTO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.departureRevoke(departureDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 理货(船-场)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(船-场)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyChuAnChan")
    public Map<String, Object> tallyChuAnChan(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyChuAnChan";
        LOGGER.enter(methodName, "理货(船-场), tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tally(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    /**
     * 理货(船-场)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(船-场)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyChuAnChanNew")
    public Map<String, Object> tallyChuAnChanNew(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyChuAnChan";
        LOGGER.enter(methodName, "理货(船-场), tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyNew(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    /**
     * 倒运理货
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="倒运理货",value = OperateTypeEnum.INSERT)
    @PostMapping("zzjTally")
    public Map<String, Object> zzjTally(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "zzjTally";
        LOGGER.enter(methodName, "倒运理货, tYardTallyPO: " + tYardTallyPO);
        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.zzjTally(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    /**
     * 理货(场-场)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(场-场)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyChang")
    public Map<String, Object> tallyChang(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyChang";
        LOGGER.enter(methodName, "理货(场-场), tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyChang(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }


    /**
     * 理货(场-船)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(场-船)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyChangChuan")
    public Map<String, Object> tallyChangChuan(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyChangChuan";
        LOGGER.enter(methodName, "理货(场-船), tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyChangChuan(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    /**
     * 理货(场-船)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(场-船)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyChangChuanNew")
    public Map<String, Object> tallyChangChuanNew(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyChangChuan";
        LOGGER.enter(methodName, "理货(场-船), tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyChangChuanNew(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }


    /**
     * 理货(车-场)/(车-船)/(船-车)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(车-场)/(车-船)/(船-车)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyCheChuan")
    public Map<String, Object> tallyCheChuan(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyCheChuan";
        LOGGER.enter(methodName, "理货集港/直取, tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyCheChuan(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    @Log(title ="理货(车-场)/(车-船)/(船-车)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyCheChuanNew")
    public Map<String, Object> tallyCheChuanNew(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyCheChuan";
        LOGGER.enter(methodName, "理货集港/直取, tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyCheChuanNew(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    /**
     * 理货(场-车)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(场-车)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyChangChe")
    public Map<String, Object> tallyChangChe(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyChangChe";
        LOGGER.enter(methodName, "理货(场-车), tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyChangChe(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    /**
     * 理货(场-车)
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="理货(场-车)",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyChangCheNew")
    public Map<String, Object> tallyChangCheNew(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyChangChe";
        LOGGER.enter(methodName, "理货(场-车), tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyChangCheNew(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }
    /**
     * 删除理货记录
     *
     * @return
     * @paramt tYardTallyPO
     */
    @Log(title ="删除理货记录",value = OperateTypeEnum.DELETE)
    @DeleteMapping("/deleteNotes")
    public Map<String, Object> deleteNotes(TYardTallyPO tYardTallyPO) {
        final String methodName = "deleteNotes";
        LOGGER.enter(methodName, "删除理货记录, tYardTallyPO: " + tYardTallyPO);

        tallyService.deleteNotes(tYardTallyPO);
        return Response.SUCCESS.newBuilder().out("删除理货记录成功").toResult();
    }
    /**
     * 删除理货记录
     *
     * @return
     * @paramt tYardTallyPO
     */
    @Log(title ="删除理货记录",value = OperateTypeEnum.DELETE)
    @DeleteMapping("/deleteNotesNew")
    public Map<String, Object> deleteNotesNew(TYardTallyPO tYardTallyPO) {
        final String methodName = "deleteNotes";
        LOGGER.enter(methodName, "删除理货记录, tYardTallyPO: " + tYardTallyPO);

        tallyService.deleteNotesNew(tYardTallyPO);
        return Response.SUCCESS.newBuilder().out("删除理货记录成功").toResult();
    }

    /**
     * 查询理货作业量
     * @param ids
     * @return
     */
    @GetMapping("/getCargoStatistics")
    @Log(title = "理货-查询理货作业量", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getCargoStatistics(@RequestParam("ids") List<Long> ids) {
    	final String methodName = "TallyController:getCargoStatistics";
        LOGGER.enter(methodName + "[start]", "cargoInfoIds:" + ids );
        if (CollectionUtils.isEmpty(ids)) {
            return Response.SUCCESS.newBuilder().out("查询成功").toResult();
        }
        List<TallyCargoDTO> resultMap = tallyService.getCargoStatistics(ids);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    /**
     * 查询作业进度
     * @param
     * @return
     */
    @GetMapping("/getWorkProgress")
    @Log(title = "件货理货-查询作业进度", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getWorkProgress(Long trustId, String workDate, String classCode, String cargoInfoNo) {
        final String methodName = "TallyController:getWorkProgress";
        LOGGER.enter(methodName + "[start]", "trustId:" + trustId
                + ", workDate:" + workDate + ", classCode:" + classCode + ", cargoInfoNo:" + cargoInfoNo);

        Map<String, Object> resultMap = tallyService.getWorkProgress(trustId, workDate, classCode, cargoInfoNo);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    /**
     * 查询作业进度
     * @param
     * @return
     */
    @GetMapping("/getWorkProgressNew")
    @Log(title = "件货理货-查询作业进度", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getWorkProgressNew(Long trustId, String workDate, String classCode, String cargoInfoNo,Long workPlanId) {

        Map<String, Object> resultMap = tallyService.getWorkProgressNew(trustId, workDate, classCode, cargoInfoNo,workPlanId);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }


    /**
     * 吨包理货
     * @param dto
     * @param result
     * @return
     */
    @Log(title ="吨包理货",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyDunBao")
    public Map<String, Object> tallyDunBao(@RequestBody @Valid DunBaoTallyDTO dto, BindingResult result) {
        final String methodName = "tallyDunBao";
        LOGGER.enter(methodName, "理货(场-车), tYardTallyPO: " + dto);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        tallyService.tallyDunBao(dto);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }


}
