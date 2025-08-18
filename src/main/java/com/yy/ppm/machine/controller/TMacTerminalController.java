package com.yy.ppm.machine.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.ppm.machine.bean.dto.TCarInHarborForAppDTO;
import com.yy.ppm.machine.bean.dto.TMacTerminalDTO;
import com.yy.ppm.machine.bean.dto.TMacTerminalLoadCarDTO;
import com.yy.ppm.machine.bean.dto.TMacTerminalStackPositionDTO;
import com.yy.ppm.machine.bean.dto.TMacTerminalWorkPlanDTO;
import com.yy.ppm.machine.bean.dto.TMacWorKLocationDTO;
import com.yy.ppm.machine.bean.dto.TMacWorkNowDTO;
import com.yy.ppm.machine.bean.dto.TMacWorkTimeDTO;
import com.yy.ppm.machine.bean.dto.TPortVehicleNumDTO;
import com.yy.ppm.machine.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.machine.bean.po.TMacWorkNowPO;
import com.yy.ppm.machine.service.TMacTerminalService;

/**
 * 车载终端基础信息
 * @author zcc
 * @Date 2023/09/06
 */
@RestController
@RequestMapping("/api/v1/external/tMacTerminal")
@Validated
public class TMacTerminalController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TMacTerminalController.class);

    @Autowired
    private TMacTerminalService tMacTerminalService;

    /**
     * 查询电子围栏距离
     * @return
     */
    @GetMapping("/getMacDistance")
    @Validated
    public Map<String, Object> getMacDistance() {
        final String methodName = "TMacTerminalController:getMacDistance";
        LOGGER.enter(methodName + "[start]");

        Integer result = tMacTerminalService.getMacDistance();

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    
    /**
     * 根据imei号查询设备信息
     * @param imei
     * @return
     */
    @GetMapping("/getMachineByImei")
    @Validated
    public Map<String, Object> getMachineByImei(@Valid @NotBlank(message = "imei不能为空！") String imei) {
        final String methodName = "TMacTerminalController:getMachineByImei";
        LOGGER.enter(methodName + "[start]", "imei:" + imei);

        TMacTerminalDTO result = tMacTerminalService.getMachineByImei(imei);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    
    /**
     * 根据设备信息查询作业指令
     * @param imei
     * @param macCode 作业设备（当前登录设备）
     * @param workPlanId
     * @param carNo 车牌号
     * @return
     */
    @GetMapping("/getWorkPlanByCondition")
    @Validated
    @Log(title ="车载计划查询",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getWorkPlanByCondition(String imei,
    		@Valid @NotBlank(message = "macCode不能为空！") String macCode, 
    		String workPlanId, 
    		String carNo, 
    		String portCode) {
        final String methodName = "TMacTerminalController:getWorkPlanByCondition";
        LOGGER.enter(methodName + "[start]", "imei:" + imei + ", macCode:" + macCode 
        		 + ", workPlanId:" + workPlanId
        		 + ", carNo:" + carNo
        		 + ", portCode:" + portCode);

        List<TMacTerminalWorkPlanDTO> resultList = tMacTerminalService.getWorkPlanByCondition(imei, macCode, workPlanId, carNo, portCode);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @param imei
     * @param macCode 作业设备（当前登录设备）
     * @param workPlanId
     * @param carNo 车牌号
     * @return
     */
    @Log(title ="根据设备信息查询作业指令",value = OperateTypeEnum.QUERY)
    @GetMapping("/getDyWorkPlanByCondition")
    @Validated
    public Map<String, Object> getDyWorkPlanByCondition(String imei,
    		@Valid @NotBlank(message = "macCode不能为空！") String macCode,
    		String workPlanId,
    		String carNo,
    		String portCode) {
        final String methodName = "TMacTerminalController:getWorkPlanByCondition";
        LOGGER.enter(methodName + "[start]", "imei:" + imei + ", macCode:" + macCode
        		 + ", workPlanId:" + workPlanId
        		 + ", carNo:" + carNo
        		 + ", portCode:" + portCode);

        List<TMacTerminalWorkPlanDTO> resultList = tMacTerminalService.getDyWorkPlanByCondition(imei, macCode, workPlanId, carNo, portCode);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @param carNo 车牌号
     * @return
     */
    @GetMapping("/getWorkPlanByConditionForAppPC")
    @Validated
    @Log(title = "AppPC查询在港车辆信息", value = OperateTypeEnum.QUERY)
    @PreAuthorize("hasAuthority('machine:tMacTerminal:query')")
    public Map<String, Object> getWorkPlanByConditionForAppPC(String carNo, String workAreaCd, String portCode) {
        final String methodName = "TMacTerminalController:getWorkPlanByConditionForAppPC";
        LOGGER.enter(methodName + "[start]", "carNo:" + carNo + ", workAreaCd:" + workAreaCd + ", portCode:" + portCode);

        List<TMacTerminalWorkPlanDTO> resultList = tMacTerminalService.getWorkPlanByConditionForAppPC(carNo, workAreaCd, portCode);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }
    
    /**
     * 根据设备信息查询作业指令
     * @param imei
     * @param macCode
     * @return
     */
    @GetMapping("/getWorkPlan")
    @Validated
    @Log(title ="根据设备信息查询作业指令-getWorkPlan",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getWorkPlan(String imei,
    		@Valid @NotBlank(message = "macCode不能为空！") String macCode, String portCode) {
        final String methodName = "TMacTerminalController:getWorkPlan";
        LOGGER.enter(methodName + "[start]", "imei:" + imei + ", macCode:" + macCode + ", portCode:" + portCode);

        List<TPrdWorkPlanDTO> resultList = tMacTerminalService.getWorkPlan(imei, macCode, portCode);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据设备信息查询作业指令
     * @param imei
     * @param macCode
     * @return
     */
    @GetMapping("/getDyWorkPlan")
    @Validated
    @Log(title ="根据设备信息查询作业指令-getDyWorkPlan",value = OperateTypeEnum.QUERY)
    public Map<String, Object> getDyWorkPlan(String imei,
    		@Valid @NotBlank(message = "macCode不能为空！") String macCode, String portCode) {
        final String methodName = "TMacTerminalController:getWorkPlan";
        LOGGER.enter(methodName + "[start]", "imei:" + imei + ", macCode:" + macCode + ", portCode:" + portCode);

        List<TPrdWorkPlanDTO> resultList = tMacTerminalService.getDaoYunWorkPlan(imei, macCode, portCode);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 查询货垛点位列表
     * @return
     */
    @GetMapping("/getStackPositionList")
    @Validated
    public Map<String, Object> getStackPositionList() {
        final String methodName = "TMacTerminalController:getStackPositionList";
        LOGGER.enter(methodName + "[start]");

        List<TMacTerminalStackPositionDTO> resultList = tMacTerminalService.getStackPositionList();

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 根据工班计划id查询起始、目的货位坐标点
     * @param workPlanId
     * @return
     */
    @GetMapping("/getStackPositionByWorkPlanId")
    @Validated
    public Map<String, Object> getStackPositionByWorkPlanId(@Valid @NotNull(message = "workPlanId不能为空！") Long workPlanId, 
    		Long cargoInfoId) {
        final String methodName = "TMacTerminalController:getStackPositionByWorkPlanId";
        LOGGER.enter(methodName + "[start]");

        List<TMacTerminalStackPositionDTO> resultList = tMacTerminalService.getStackPositionByWorkPlanId(workPlanId, cargoInfoId);

        LOGGER.exit(methodName + "result:" + resultList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }
    
    /**
     * 设备开始作业
     * @param tMacWorkTimeDTO
     * @return
     */
    @PostMapping("/macWorkStart")
    @Validated
    @Log(title ="设备开始作业-macWorkStart",value = OperateTypeEnum.QUERY)
    public Map<String, Object> macWorkStart(@RequestBody TMacWorkTimeDTO tMacWorkTimeDTO) {
        final String methodName = "TMacTerminalController:macWorkStart";
        LOGGER.enter(methodName + "[start]", "tMacWorkTimeDTO:" + tMacWorkTimeDTO);

        int count = tMacTerminalService.macWorkStart(tMacWorkTimeDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("设备开始作业成功").toResult(count);
    }
    
    /**
     * 设备结束作业
     * @param tMacWorkTimeDTO
     * @return
     */
    @PostMapping("/macWorkEnd")
    @Validated
    @Log(title ="设备结束作业-macWorkEnd",value = OperateTypeEnum.QUERY)
    public Map<String, Object> macWorkEnd(@RequestBody TMacWorkTimeDTO tMacWorkTimeDTO) {
        final String methodName = "TMacTerminalController:macWorkEnd";
        LOGGER.enter(methodName + "[start]", "tMacWorkTimeDTO:" + tMacWorkTimeDTO);

        int count = tMacTerminalService.macWorkEnd(tMacWorkTimeDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("设备结束作业成功").toResult(count);
    }
    
    /**
     * 散货理货接口PC端
     * @param tMacWorkTimeDTO
     * @return
     */
    @PostMapping("/macWorkPC")
    @Validated
    public Map<String, Object> macWorkPC(@RequestBody TMacWorkTimeDTO tMacWorkTimeDTO) {
        final String methodName = "TMacTerminalController:macWorkPC";
        LOGGER.enter(methodName + "[start]", "tMacWorkTimeDTO:" + tMacWorkTimeDTO);

        int count = tMacTerminalService.macWorkPC(tMacWorkTimeDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("理货作业成功").toResult(count);
    }
    
    /**
     * 散货理货接口APP端
     * @param tMacWorkTimeDTO
     * @return
     */
    @PostMapping("/macWorkApp")
    @Validated
    public Map<String, Object> macWorkApp(@RequestBody TMacWorkTimeDTO tMacWorkTimeDTO) {
        final String methodName = "TMacTerminalController:macWorkApp";
        LOGGER.enter(methodName + "[start]", "tMacWorkTimeDTO:" + tMacWorkTimeDTO);

        int count = tMacTerminalService.macWorkApp(tMacWorkTimeDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("理货作业成功").toResult(count);
    }
    /**
     * @param storageYardNms
     * @return
     */
    @PostMapping("/getYardByName")
    @Validated
    public Map<String, Object> getYardByName(@RequestBody List<String> storageYardNms) {
        final String methodName = "TMacTerminalController:getYardByName";
        LOGGER.enter(methodName + "[start]", "storageYardNms:" + storageYardNms);

        List<Map<String,Object>> count = tMacTerminalService.getYardByName(storageYardNms);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().toResult(count);
    }





    /**
     * 根据磅单id查询装车信息
     * @param weighbridgeId
     * @return
     */
    @GetMapping("/getLoadCarByWeighbridgeId")
    @Validated
    public Map<String, Object> getLoadCarByWeighbridgeId(@Valid @NotNull(message = "weighbridgeId不能为空！") Long weighbridgeId) {
        final String methodName = "TMacTerminalController:getWorkPlanByCondition";
        LOGGER.enter(methodName + "[start]", "weighbridgeId:" + weighbridgeId);

        TMacTerminalLoadCarDTO dto = tMacTerminalService.getLoadCarByWeighbridgeId(weighbridgeId);

        LOGGER.exit(methodName + "result:" + dto);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }

       /**
     * 根据磅单id查询装车信息
     * @param searchDTO
     * @return
     */
    @GetMapping("/getCarInHarborForApp")
    @Validated
    public Map<String, Object> getCarInHarbor(TCarInHarborForAppDTO  searchDTO) {
        final String methodName = "TMacTerminalController:getCarInHarbor";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<TCarInHarborForAppDTO> result =  tMacTerminalService.getCarInHarbor(searchDTO);
        if(!CollectionUtils.isEmpty(result)){
            for (int i = 0; i < result.size(); i++) {
                result.get(i).setSortNum(i+1);
            }
        }


        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询设备当前作业货垛
     * @param searchDTO
     * @return
     */
	 @GetMapping("/getMacWorkNow")
	 @Validated
	 public Map<String, Object> getMacWorkNow(TMacWorkNowDTO searchDTO) {
	     final String methodName = "TMacTerminalController:getMacWorkNow";
	     LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);
	
	     TMacWorkNowPO result = tMacTerminalService.getMacWorkNow(searchDTO);
	     
	     LOGGER.exit(methodName + "result:" + result);
	     return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
	 }

    /**
     * 查询在港车辆
     * @param
     * @return
     */
    @GetMapping("/queryPortVehicleNum")
    @Validated
    public Map<String, Object> queryPortVehicleNum() {
        final String methodName = "TMacTerminalController:queryPortVehicleNum";
        LOGGER.enter(methodName + "[start]", "searchDTO:");

        List<TPortVehicleNumDTO> result = tMacTerminalService.queryPortVehicleNum();

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
	    
	/**
	 * 查询设备当前作业货垛
	 * @param paramDTO
	 * @return
	 */
	@PostMapping("/insertMacWorkNow")
	@Validated
	public Map<String, Object> insertMacWorkNow(@RequestBody TMacWorkNowDTO paramDTO) {
	    final String methodName = "TMacTerminalController:insertMacWorkNow";
	    LOGGER.enter(methodName + "[start]", "paramDTO:" + paramDTO);
	
	    int count = tMacTerminalService.insertMacWorkNow(paramDTO);
	
	    LOGGER.exit(methodName + "result:" + count);
	    return Response.SUCCESS.newBuilder().out("添加成功").toResult(count);
	}

    /**
     * 修改
     * @param tMacWorKLocationDTO
     * @return
     */
    @PutMapping("/updateLocation")
    public Map<String, Object> update(@RequestBody TMacWorKLocationDTO tMacWorKLocationDTO) {
        boolean flag = tMacTerminalService.doSave(tMacWorKLocationDTO);
        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 查询单条记录
     *
     * @param weighbridgeId
     * @return
     */
    @GetMapping("/getDetailLocation")
    public Map<String, Object> getDetailLocation(@RequestParam("weighbridgeId") Long weighbridgeId) {
        TMacWorKLocationDTO result = tMacTerminalService.getDetailLocation(weighbridgeId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    /**
     * 撤销理货
     *
     * @param weighbridgeId
     * @return
     */
    @GetMapping("/cancelTally/{weighbridgeId}")
    public Map<String, Object> cancelTally(@PathVariable Long weighbridgeId) {
        boolean flag= tMacTerminalService.cancelTally(weighbridgeId);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult(flag);
    }
}
