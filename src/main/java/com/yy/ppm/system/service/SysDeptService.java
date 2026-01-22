package com.yy.ppm.system.service;


import com.yy.common.flowable.utils.CollectionUtils;
import com.yy.ppm.system.bean.dto.SysDeptDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 部门表服务接口
 *
 * @author daiying
 * @date 2021-02-26 15:40:58
 */
public interface SysDeptService {
    /**
     * 查询部门管理数据
     *
     * @return 部门信息集合
     */
    public List<SysDeptDTO> selectDeptList(SysDeptDTO dto);

    /**
     * 根据部门ID查询信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    public SysDeptDTO getById(Long id);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDeptDTO dept);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDeptDTO dept);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);


    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    List<SysDeptDTO> getDeptList(Collection<Long> ids);

    /**
     * 获得指定编号的部门 Map
     *
     * @param ids 部门编号数组
     * @return 部门 Map
     */
    default Map<Long, SysDeptDTO> getDeptMap(Collection<Long> ids) {
        if (org.springframework.util.CollectionUtils.isEmpty(ids)) {
            return new HashMap<>();
        }
        List<SysDeptDTO> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, SysDeptDTO::getId);
    }

}
