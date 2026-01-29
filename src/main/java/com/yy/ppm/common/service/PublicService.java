package com.yy.ppm.common.service;

import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 公共服务接口
 */
public interface PublicService {

    /**
     * 根据字典类型获取字典值
     * */
    Map<String, Object> getDictList(List<String> dictTypeList);

    /**
     * 通过单个字典类型获取字典数据
     * @param type
     * @return
     */
    List<Map<String, Object>> getDictListByType(String type);

    /**
     * 常量表
     */
    Map<String, Object> getConstantList(List<String> typeList);

    /**
     * 获取系统参数
     */
    SysParameterDTO getSysParamByCode(String code);


    /** 组织架构 */

    /**
     * 部门树，前端构建
     * @param deptDTO
     * @return
     */
    List<SysDeptDTO> getDeptList(SysDeptDTO deptDTO);
}
