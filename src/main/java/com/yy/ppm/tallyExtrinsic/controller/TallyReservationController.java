package com.yy.ppm.tallyExtrinsic.controller;

import com.alibaba.fastjson2.JSONObject;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.tallyExtrinsic.bean.dto.*;
import com.yy.ppm.tallyExtrinsic.bean.po.TBusReservationPoundPO;
import com.yy.ppm.tallyExtrinsic.bean.po.TYardTallyItemPO;
import com.yy.ppm.tallyExtrinsic.bean.po.TYardTallyMacPO;
import com.yy.ppm.tallyExtrinsic.bean.po.TYardTallyPO;
import com.yy.ppm.tallyExtrinsic.service.TBusReservationConfirmService;
import com.yy.ppm.tallyExtrinsic.service.TallyExtrinsicService;
import com.yy.ppm.tallyExtrinsic.service.TallyReservationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;


/**
 * App理货
 *
 * @author chenfs
 * @since 2023年9月14日
 */
@RestController
@RequestMapping("/api/external/tallyReservation")
@Validated
public class TallyReservationController {
    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(TallyReservationController.class);


    @Autowired
    private TallyReservationService reservationService;
    @Autowired
    private TBusReservationConfirmService tBusReservationConfirmService;

    @GetMapping("/listDisShipVoyage")
    public Map<String, Object> listDisShipVoyageApp() {
        List<Map<String,Object>> result = reservationService.listDisShipVoyageApp();
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 查询计划
     *
     * @param
     * @param searchDTO 实例对象
     * @return
     */
    @GetMapping("/getWorkPlan")
    public Map<String, Object> getWorkPlan(TPrdWorkPlanSearchDTO searchDTO) {
        if(StringUtils.isEmpty(searchDTO.getClassCode())){
            throw new BusinessRuntimeException("班次必填");
        }
        if(StringUtils.isEmpty(searchDTO.getWorkDateString())){
            throw new BusinessRuntimeException("日期必填");
        }
        final String methodName = "getWorkPlan";
        LOGGER.enter(methodName, "查询计划, searchDTO: " + searchDTO);
        List<TPrdWorkPlanDTO> list = reservationService.getWorkPlan(searchDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 根据实体类筛选数据列表(查询作业过程)
     *
     * @param processCode 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getProcessInfoList")
    public Map<String, Object> getProcessInfoList(String processCode) {
        final String methodName = "MWorkProcessController: getList";
        LOGGER.enter(methodName + "[start]", "processCode:" + processCode);
        if(StringUtils.isEmpty(processCode)){
            throw new BusinessRuntimeException("作业过程代码不能为空");
        }
        List<Map<String, Object>> mWorkProcessList = reservationService.getProcessInfoList(processCode);
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
    public Map<String, Object> getMechanics(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        final String methodName = "tPrdDispatchSecondarySearchDTO: getMechanics";
        List<Map<String, Object>> mWorkProcessList = reservationService.getMechanics(tPrdDispatchSecondarySearchDTO);
        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询计划下垛位
     * @param tPrdDispatchSecondarySearchDTO
     * @return
     */
    @GetMapping("/getStorageList")
    public Map<String, Object> getStorageList(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        List<Map<String, Object>> list = reservationService.getStorageList(tPrdDispatchSecondarySearchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询转运车信息
     *
     * @param tPrdDispatchSecondarySearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getTransfer")
    public Map<String, Object> getTransfer(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        if(tPrdDispatchSecondarySearchDTO.getPlanId() == null){
            throw new BusinessRuntimeException("计划id不能为空");
        }
        final String methodName = "tPrdDispatchSecondarySearchDTO: getTransfer";
        List<Map<String, Object>> mWorkProcessList = reservationService.getTransfer(tPrdDispatchSecondarySearchDTO);
        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询外部车辆信息
     *
     * @param tPrdDispatchSecondarySearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getTruckNo")
    public Map<String, Object> getTruckNo(TPrdDispatchExtrinsicDTO tPrdDispatchSecondarySearchDTO) {
        final String methodName = "tPrdDispatchSecondarySearchDTO: getTruckNo";
        if(tPrdDispatchSecondarySearchDTO.getTrustId() == null){
            throw new BusinessRuntimeException("指令id不能为空");
        }
        if(StringUtils.isEmpty(tPrdDispatchSecondarySearchDTO.getProcessCode())){
            throw new BusinessRuntimeException("作业过程不能为空");
        }
        List<Map<String, Object>> mWorkProcessList = reservationService.getTruckNo(tPrdDispatchSecondarySearchDTO);
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
        if(StringUtils.isEmpty(trustId)){
            throw new BusinessRuntimeException("指令id不能为空");
        }
        List<Map<String, Object>> mWorkProcessList = reservationService.getCargoInfoId(trustId,planId,processCode);
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
        List<Map<String, Object>> mWorkProcessList = reservationService.getCargoInfoIdTr(planId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 查询库场区域(根据计划ID)
     *
     * @param planId 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getStore")
    public Map<String, Object> getStore(Long planId) {
        if(planId == null){
            throw new BusinessRuntimeException("计划id不能为空");
        }
        List<Map<String, Object>> mWorkProcessList = reservationService.getStore(planId);
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
        List<Map<String, Object>> mWorkProcessList = reservationService.getStackPosition(id);
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
        List<Map<String, Object>> hatchList = reservationService.getHatch(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(hatchList);
    }


    /**
     * 根据指令ID查询待作业

     * @return
     */
    @GetMapping("/getWaitWork")
    public Map<String, Object> getWaitWork(Long trustId,String macName) {
        if(trustId == null){
            throw new BusinessRuntimeException("指令id不能为空");
        }
        final String methodName = "getWaitWork";
        LOGGER.enter(methodName, "根据指令ID查询待作业记录, trustId: " + trustId);
        List<TYardTallyPO> list = reservationService.getWaitWork(trustId,macName);
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
        Pages<TYardTallyItemPO> pages = reservationService.getTallyRecord(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
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
        TYardTallyPO pages = reservationService.getTallyInfoById(tallyRecordSearchDTO, pageParameter);
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
        TYardTallyPO pages = reservationService.getTallyNew(tallyRecordSearchDTO, pageParameter);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询车号关联的票货信息
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
        Map<String, Object> resultMap = reservationService.getTransportEquipmentCargoInfo(trustId, vehicleNo);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    /**
     * 查询车号关联的票货信息(直取)
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
        Map<String, Object> resultMap = reservationService.getTransportEquipmentCargoInfoZ(trustId, vehicleNo);
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
        Map<String, Object> map = reservationService.getTallyRecordSum(tallyRecordSearchDTO);
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
        List<AppTallyLadingDTO> list = reservationService.getCarDetailedList(tYardMeasureSearchDTO);
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
//        Pages<AppTallyCoilNumDTO> list = reservationService.getCoilList(appTallyCoilNumDTO, pageParameter);
//        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
//    }

    @GetMapping("/getCoilList")
    public Map<String, Object> getCoilList(AppTallyCoilNumDTO appTallyCoilNumDTO) {
        List<AppTallyCoilNumDTO> list = reservationService.getCoilList(appTallyCoilNumDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询空/重车数据
     * @param truckPlate
     * @return
     */
    @GetMapping("/getDepartureList")
    public Map<String, Object> getDepartureList(@NotNull(message = "车牌号不能为空") String truckPlate) {
        List<DepartureDTO> list = reservationService.getDepartureList(truckPlate);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询空/重车数据记录
     * @return
     */
    @GetMapping("/getDepartureRecordList")
    public Map<String, Object> getDepartureRecordList(@NotNull(message = "开始时间不能为空") String startDate,@NotNull(message = "结束时间不能为空") String endDate) {
        List<DepartureDTO> list = reservationService.getDepartureRecordList(startDate,endDate);
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
        String flag = reservationService.selectBh(code);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(flag);
    }

    /**
     * 根据计划ID查询装卸班组
     *
     * @param planId
     * @return
     */
    @GetMapping("/getDept")
    public Map<String, Object> getDept() {
        List<Map<String, Object>> map = reservationService.getDept();
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
        reservationService.updateTally(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 车载app吨包理货插入计划状态数据
     *
     * @param result
     * @return
     */
    @Log(title ="车载app吨包理货插入计划状态数据",value = OperateTypeEnum.INSERT)
    @PostMapping("insertMacTally")
    public Map<String, Object> insertMacTally(@RequestBody @Valid TYardTallyMacPO tYardTallyMacPO, BindingResult result) {
        final String methodName = "insertMacTally";
        LOGGER.enter(methodName, "车载app吨包理货插入计划状态数据, tYardTallyPO: " + tYardTallyMacPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        reservationService.insertMacTally(tYardTallyMacPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("提交成功").toResult();
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
        reservationService.departureSub(departureDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("处理成功").toResult();
    }

    /**
     * 空/重车出港撤销
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
        reservationService.departureRevoke(departureDTO);
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
        reservationService.tally(tYardTallyPO);
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
        reservationService.tallyChang(tYardTallyPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("理货成功").toResult();
    }

    /**
     * 吨包理货
     *
     * @param tYardTallyPO
     * @param result
     * @return
     */
    @Log(title ="吨包理货",value = OperateTypeEnum.INSERT)
    @PostMapping("tallyConfirm")
    public Map<String, Object> tallyConfirm(@RequestBody @Valid TYardTallyPO tYardTallyPO, BindingResult result) {
        final String methodName = "tallyConfirm";
        LOGGER.enter(methodName, "吨包理货, tYardTallyPO: " + tYardTallyPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        reservationService.tallyConfirm(tYardTallyPO);
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
        reservationService.tallyChangChuan(tYardTallyPO);
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
        reservationService.tallyCheChuan(tYardTallyPO);
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
        reservationService.tallyChangChe(tYardTallyPO);
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

        reservationService.deleteNotes(tYardTallyPO);
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
        List<TallyCargoDTO> resultMap = reservationService.getCargoStatistics(ids);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    /**
     * 查询作业进度
     * @return
     */
    @GetMapping("/getWorkProgress")
    @Log(title = "件货理货-查询作业进度", value = OperateTypeEnum.QUERY)
    public Map<String, Object> getWorkProgress(Long trustId, String workDate, String classCode, String cargoInfoNo) {
        final String methodName = "TallyController:getWorkProgress";
        LOGGER.enter(methodName + "[start]", "trustId:" + trustId
                + ", workDate:" + workDate + ", classCode:" + classCode + ", cargoInfoNo:" + cargoInfoNo);

        Map<String, Object> resultMap = reservationService.getWorkProgress(trustId, workDate, classCode, cargoInfoNo);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultMap);
    }

    /**
     * 获取门机
     *
     * @param planId 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getNewDoorById")
    public Map<String, Object> getNewDoorById(String planId,String macName) {
        final String methodName = "MWorkProcessController: getNewDoorById";
        LOGGER.enter(methodName + "[start]", "macName:" + macName);
        if(StringUtils.isEmpty(planId)){
            throw new BusinessRuntimeException("计划ID不能为空");
        }
        if(StringUtils.isEmpty(macName)){
            throw new BusinessRuntimeException("车号不能为空");
        }
        TYardTallyMacPO tYardTallyMacPO = reservationService.getNewDoorById(planId,macName);
        LOGGER.exit(methodName + "result:" + tYardTallyMacPO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(tYardTallyMacPO);
    }

    /**
     * 根据门机编号查询
     * @param searchDTO
     * @return
     */
    @GetMapping("/getConfirm")
    //@PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> getConfirm(TBusReservationConfirmSearchDTO searchDTO) {
        final String methodName = "TBusReservationConfirmController:getConfirm";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        TBusReservationConfirmDTO result = tBusReservationConfirmService.getConfirm(searchDTO);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 根据门机编号查询
     * @param id
     * @return
     */
    @DeleteMapping("/deleteConfirm")
    //@PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> deleteConfirm(@RequestParam("id") Long id) {
        final String methodName = "TBusReservationConfirmController:deleteConfirm";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + id);

        int result = tBusReservationConfirmService.deleteConfirm(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 根据航次查询票货
     * @param shipVoyageItemId
     * @return
     */
    @GetMapping("/getPhByShip")
    //@PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> getPhByShip(String shipVoyageItemId) {
        final String methodName = "TBusReservationConfirmController:getPhByShip";
        LOGGER.enter(methodName + "[start]", "shipVoyageItemId:" + shipVoyageItemId);

        List<DbCargoInfoDTO> result = tBusReservationConfirmService.getPhByShip(shipVoyageItemId);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 根据门机编号查询
     * @param searchDTO
     * @return
     */
    @GetMapping("/getConfirmList")
    //@PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> getConfirmList(TBusReservationConfirmSearchDTO searchDTO) {
        final String methodName = "TBusReservationConfirmController:getConfirmList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<TBusReservationConfirmDTO> result = tBusReservationConfirmService.getConfirmList(searchDTO);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 校验吨包理货件数
     * @param params
     * @return
     */
    @PostMapping("/checkTallyQuantity")
    //@PreAuthorize("hasAuthority('business:customer:query')")
    public Map<String, Object> checkTallyQuantity(@RequestBody JSONObject params) {
        final String methodName = "TBusReservationConfirmController:checkTallyQuantity";
        LOGGER.enter(methodName + "[start]", "params:" + params);

        boolean flag = tBusReservationConfirmService.checkTallyQuantity(params);

        LOGGER.exit( methodName + "result:" + flag);

        return Response.SUCCESS.newBuilder().out("").toResult(flag);
    }

    /**
     * 根据票货查询在港车辆
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/getTruckList")
    public Map<String, Object> getTruckList(String cargoInfoId, String truckNo, String shipVoyageItemId) {
        final String methodName = "TBusReservationConfirmController:getTruckList";
        LOGGER.enter(methodName + "[start]", "cargoInfoId:" + cargoInfoId);

        List<DbTruckDTO> result = tBusReservationConfirmService.getTruckList(cargoInfoId, truckNo, shipVoyageItemId);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     *  车载app吨包理货修改计划状态
     *
     * @param result
     * @return
     */
    @Log(title ="车载app吨包理货修改计划状态",value = OperateTypeEnum.INSERT)
    @PostMapping("updateStatus")
    public Map<String, Object> updateStatus(@RequestBody @Valid TBusReservationPoundPO tBusReservationPoundPO, BindingResult result) {
        final String methodName = "insertMacTally";
        LOGGER.enter(methodName, "车载app吨包理货修改计划状态, tBusReservationPoundPO: " + tBusReservationPoundPO);

        if (result.hasErrors()) {
            LOGGER.warn("入参校验失败~");
            String msg = result.getFieldError().getDefaultMessage();
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }
        reservationService.updateStatus(tBusReservationPoundPO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out("提交成功").toResult();
    }

}
