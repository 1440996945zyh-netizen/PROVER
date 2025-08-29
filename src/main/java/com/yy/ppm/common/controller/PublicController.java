package com.yy.ppm.common.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.bean.dto.SelecSearchDTO;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

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
     * 获取部门列表
     */
    @GetMapping("/getDeptList")
    public Map<String, Object> list(SysDeptDTO deptDTO) {
        List<SysDeptDTO> depts = publicService.getDeptList(deptDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(depts);
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
