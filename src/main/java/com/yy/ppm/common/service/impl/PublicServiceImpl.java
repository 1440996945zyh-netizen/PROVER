package com.yy.ppm.common.service.impl;

import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.DateUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.PublicMapper;
import com.yy.ppm.common.service.PublicService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * 公共服务接口实现类
 */
@Service
public class PublicServiceImpl implements PublicService {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(PublicServiceImpl.class);

    @Resource
    public PublicMapper publicMapper;

	@Resource
	private SecurityUtils securityUtils;

    /**
     * 根据字典类型获取字典值
     * */
    @Override
    public Map<String, Object> getDictList(List<String> dictTypeList) {
        final String methodName = "PublicServiceImpl:getDictList";
        LOGGER.enter(methodName, "查询字典值");

        List<Map<String, Object>> dictList = publicMapper.getDictList(dictTypeList);
        Map<String, Object> dictMap = new HashMap<>();
        // 处理多个字典类型情况
        for (String v1 : dictTypeList) {
            dictMap.put(v1, dictList.stream().filter(v2 -> v1.equals(getString(v2.get("dictType"))))
                    .collect(Collectors.toList()));
        }
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dictMap;
    }

    /**
     * 通过单个字典类型获取字典数据
     * @param type
     * @return
     */
    @Override
    public List<Map<String, Object>> getDictListByType(String type) {

        List<Map<String, Object>> resList = publicMapper.getDictListByType(type);

        return resList;
    }

    /**
     * 根据常量类型查询常量
     */
    @Override
    public Map<String, Object> getConstantList(List<String> typeList) {
        final String methodName = "PublicServiceImpl:getConstantList";
        LOGGER.enter(methodName, "查询常量值");

        List<Map<String, Object>> list = publicMapper.getConstantList(typeList);
        Map<String, Object> tempMap = new HashMap<>();
        for (String v1 : typeList) {
            tempMap.put(v1, list.stream().filter(v2 -> v1.toUpperCase().equals(getString(v2.get("typeCd")).toUpperCase()))
                    .collect(Collectors.toList()));
        }

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return tempMap;
    }

    /**
     * 获取系统参数
     */
    @Override
    public SysParameterDTO getSysParamByCode(String code) {

        final String methodName = "PublicServiceImpl:getSysParamByCode";
        LOGGER.enter(methodName, "获取系统参数");

        SysParameterDTO dto = publicMapper.getSysParamByCode(code);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return dto;
    }


    /**** 组织架构 ****/
    /**
     * 获取全部组织部门信息
     * */
    @Override
    public List<SysDeptDTO> getDeptList(SysDeptDTO deptDTO) {
        return publicMapper.getDeptList(deptDTO);
    }

}
