package com.yy.ppm.common.controller;

import cn.hutool.core.util.ObjectUtil;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.annotation.Log;
import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.common.bean.dto.SelecSearchDTO;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.common.service.SelectService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RestController
@RequestMapping(value = "/api/internal/public")
@Validated
public class PublicController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(PublicController.class);

    @Resource
    PublicService publicService;

    @Resource
    SelectService selectService;
    @Resource
    private SysParameterMapper sysParameterMapper;

    /**
     * 根据字典类型获取字典值
     *
     * @param types 字典类型  多个用,分割
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getDictList")
    public Map<String, Object> getDictList(String types)
            throws Exception {
        final String methodName = "PublicController:getDictList";
        LOGGER.enter(methodName, "根据字典类型列表获取字典值[start], types: " + types);

        List<String> dictTypeList = new ArrayList<>();
        if (!StringUtil.isEmpty(types)) {
            dictTypeList = Arrays.asList(types.split(","));
        }
        dictTypeList = dictTypeList.stream().filter(v1 -> !StringUtil.isEmpty(v1)).collect(Collectors.toList());
        Map<String, Object> dataMap = new HashMap<>();
        if (dictTypeList.size() > 0) {
            dataMap = publicService.getDictList(dictTypeList);
        }

        LOGGER.exit(methodName, "根据字典类型列表获取字典值[end], result: " + dataMap);
        return Response.SUCCESS.newBuilder().toResult(dataMap);
    }

    /**
     * 根据字典type获取字典数据
     *
     * @param type
     * @return
     * @throws Exception
     */
    @GetMapping("/getDictListByType/{type}")
    public Map<String, Object> getDictListByType(@PathVariable("type") String type) throws Exception {
        final String methodName = "PublicController:getDictListByType";
        LOGGER.enter(methodName, "根据字典类型列表获取字典值[start], type: " + type);
        List<Map<String, Object>> resList = publicService.getDictListByType(type);
        LOGGER.exit(methodName, "根据字典类型列表获取字典值[end], result: " + resList);
        return Response.SUCCESS.newBuilder().toResult(resList);
    }

    /**
     * 根据常量类型查询常量值
     *
     * @param types 常量类型  多个用,分割
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getconstantlist")
    public Map<String, Object> getConstantList(String types)
            throws Exception {
        final String methodName = "PublicController.getConstantList";
        LOGGER.enter(methodName, "根据常量类型列表获取常量值[start], types: " + types);

        List<String> typeList = new ArrayList<>();
        if (isNotBlank(types)) {
            typeList = Arrays.asList(types.split(","));
        }
        typeList = typeList.stream().filter(v1 -> isNotBlank(v1)).collect(Collectors.toList());
        Map<String, Object> dataMap = new HashMap<>();
        if (typeList.size() > 0) {
            dataMap = publicService.getConstantList(typeList);
        }

        LOGGER.exit(methodName, "根据常量类型列表获取常量值[end], result: " + dataMap);
        return Response.SUCCESS.newBuilder().toResult(dataMap);
    }

    /**
     * 获取单个系统参数
     *
     * @param  code
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getSysParam")
    public Map<String, Object> getSysParamByCode(String code)
            throws Exception {
        final String methodName = "PublicController.getSysParam";
        LOGGER.enter(methodName, "获取单个系统桉树[start], code: " + code);

        SysParameterDTO dto = publicService.getSysParamByCode(code);

        LOGGER.exit(methodName, "获取单个系统参数[end], result: " + dto);
        return Response.SUCCESS.newBuilder().toResult(dto);
    }

    /**
     * 获取远程下拉框数据源
     *
     * @param selectCommonSearch
     * @return
     */
    @PostMapping("/getRemoteSelect")
    public Map<String, Object> getSelectDataSource(@RequestBody SelecSearchDTO selectCommonSearch) {
        final String methodName = "PublicController:selectCommonSearch";
        LOGGER.enter(methodName + "[start]", "param:" + selectCommonSearch.toString());

        List<Map<String, Object>> res = selectService.getRemoteSelect(selectCommonSearch);

        LOGGER.exit(methodName + "result:" + res);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(res);
    }

    /**
     * 获取本地下拉框数据源
     *
     * @return
     */
    @GetMapping("/getLocalSelect")
    public Map<String, Object> getLocalSelect(@RequestParam Map<String, Object> params) {
        final String methodName = "PublicController:selectCommonSearch";
        LOGGER.enter(methodName + "[start]", "params:" + params);
        List<Map<String, Object>> res = selectService.getLocalSelect(params);

        LOGGER.exit(methodName + "result:" + res);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(res);
    }

    /**
     * 获取本地下拉框数据源
     *
     * @return
     */
    @GetMapping("/getLocalSelects")
    public Map<String, Object> getLocalSelects(String types) {
        final String methodName = "PublicController:getLocalSelects";
        LOGGER.enter(methodName + "[start]", "param:" + types);

        Map<String, List<Map<String, Object>>> map = selectService.getLocalSelects(types);

        LOGGER.exit(methodName + "result:" + map);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(map);
    }

    /**
     * 获取部门列表
     */
    @GetMapping("/getDeptList")
    public Map<String, Object> list(SysDeptDTO deptDTO) {
        List<SysDeptDTO> depts = publicService.getDeptList(deptDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(depts);
    }

    /**
     * 传入计划日期，返回计划的执行开始结束时间
     *
     * @author yy
     * @param planDte
     */
    @GetMapping(value = "/getShiftClassInfoByPlanDate")
    public Map<String, Object> getShiftClassInfoByPlanDate(String planDte)  {
        final String methodName = "getShiftClassInfoByPlanDate";
        LOGGER.enter(methodName, "传入计划日期，返回计划的执行开始结束时间[start], planDte: " + planDte);

        String res = publicService.getShiftClassInfoByPlanDate(planDte);

        LOGGER.exit(methodName, "传入时间戳返回班次[end], result: " + res);
        return Response.SUCCESS.newBuilder().toResult(res);
    }

    /**
     * 获取当前时间的班次
     *
     * @author yy
     * @param
     */
    @GetMapping(value = "/getCurrentShiftClassInfo")
    public Map<String, Object> getCurrentShiftClassInfo(String time)  {
        final String methodName = "getCurrentShiftClassInfo";
        LOGGER.enter(methodName, "获取当前时间的班次[start]");

        Map<String, Object> res = publicService.getCurrentShiftClassInfo(time);

        LOGGER.exit(methodName, "获取当前时间的班次[end], result: " + res);
        return Response.SUCCESS.newBuilder().toResult(res);
    }

    /**
     * 通用指令popup
     *
     * @author yy
     * @param
     */
    @GetMapping(value = "/getPopupTrust")
    @Log(value=OperateTypeEnum.QUERY, title="通用指令获取")
    public Map<String, Object> getPopupTrust(@RequestParam Map<String, Object> params)  {
        final String methodName = "getScheduleType";
        LOGGER.enter(methodName, "通用指令popup[start], params: " + params);

        List<ResponsePopupTrustDTO> list  = selectService.getPopupTrust(params);

        LOGGER.exit(methodName, "通用指令popup[end], result: " + list);
        return Response.SUCCESS.newBuilder().toResult(list);
    }


    /**
     * 查询机械列表
     * @param map
     * @return
     */
    @GetMapping("/getMachineList")
    public Map<String, Object> getMachineList(@RequestParam Map<String, Object> map) {
        final String methodName = "CommonController:getMachineList";
        LOGGER.enter(methodName + "按条件查询通用设备接口 [start]", "map:" + map);

        List<Map<String, Object>> list = publicService.getMachineList(map);;

        LOGGER.exit(methodName + " 按条件查询通用设备接口 [end] result:" + list);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(list);
    }

    /**
     * 查询机械列表(不控制权限)
     * @param map
     * @return
     */
    @GetMapping("/getMachineList2")
    public Map<String, Object> getMachineList2(@RequestParam Map<String, Object> map) {
        final String methodName = "CommonController:getMachineList";
        LOGGER.enter(methodName + "按条件查询通用设备接口 [start]", "map:" + map);

        List<Map<String, Object>> list = publicService.getMachineList2(map);;

        LOGGER.exit(methodName + " 按条件查询通用设备接口 [end] result:" + list);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }
    /**
     * 根据区域ID查垛位
     *
     * @return
     */
    @GetMapping("/listMass")
    public Map<String, Object> listMass(@NotNull(message = "区域ID不能为空") Long regionId) {
        final String methodName = "PublicController:listMass";
        LOGGER.enter(methodName + "根据区域ID查垛位[start]", "regionId:" + regionId);

        List<Map<String, Object>> list = publicService.listMass(regionId);

        LOGGER.exit(methodName + " 根据区域ID查垛位[end] result:" + list);
        return Response.SUCCESS.newBuilder().toResult(list);
    }

    @GetMapping("/getDateAndShift")
    public Map<String, Object> getDateAndShift(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateTime) {
        Map<String, Object> result = publicService.getDateAndShift(dateTime);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    /**
     * 查询系统参数，是否为新流程
     * @param
     * @param
     * @return
     */
    @GetMapping("/getSystemParams")
    public Map<String, Object> getSystemParams(String key) {
        SysParameterDTO sysParameter = sysParameterMapper.getByKey(key);
//        boolean switchIsFrontier = ObjectUtil.isEmpty(sysParameter) ? false : ("Y".equals(sysParameter.getParamVal()) ? true : false);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysParameter);
    }

}
