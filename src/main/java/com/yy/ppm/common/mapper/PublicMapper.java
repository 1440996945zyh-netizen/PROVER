package com.yy.ppm.common.mapper;

import com.yy.ppm.master.bean.po.MDictDataPO;
import com.yy.ppm.master.bean.po.MWorkSchedulePO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysParameterDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 公共服务DAO
 *
 * @author yangcl
 * @date 2021-3-3 09:44:33
 */
public interface PublicMapper {
    /**
     * 根据字典类型获取字典值
     * */
    public List<Map<String, Object>> getDictList(@Param("dictTypeList") List<String> dictTypeList);

    /**
     * 通过单个字典类型获取字典数据
     * @param type
     * @return
     */
    List<Map<String, Object>> getDictListByType(@Param("type") String type);

    /**
     * 通用部门树构建
     * @param deptDTO
     * @return
     */
    List<SysDeptDTO> getDeptList(SysDeptDTO deptDTO);

    /**
     * 根据常量类型获取常量值
     */
    public List<Map<String, Object>> getConstantList(@Param("typeList") List<String> typeList);

    /**
     * 根据常量类型获取常量值
     */
    public SysParameterDTO getSysParamByCode(@Param("code") String code);

    /**
     * 获取班次信息列表
     * @return
     */
    List<Map<String, Object>> getScheduleTypeList();

    /**
     * 获取机械列表
     * @param map
     * @return
     */
    List<Map<String, Object>> getMachineList(Map<String, Object> map);

    List<Map<String, Object>> listMass(Long regionId);

	public List<Map<String, Object>> getUserInfoAndDeptInfo(@Param("userId") Long userId);

    List<MDictDataPO> listDictData();

    List<MWorkSchedulePO> listWorkSchedule();

    List<Map<String, Object>> getMachineList2(Map<String, Object> map);
}
