package com.yy.ppm.system.mapper;

import com.yy.common.util.str.StringUtil;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 部门Dao
 *
 * @author daiying
 * @date 2021-02-26 15:40:00
 */
public interface SysDeptMapper {
    /**
     * 查询部门管理数据
     *
     * @return 部门信息集合
     */
    public List<SysDeptDTO> selectDeptList(SysDeptDTO dto);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDeptDTO getById(Long deptId);

    /**
     * 根据部门ID查询信息
     *
     * @param deptNo 部门No
     * @return 部门信息
     */
    public SysDeptDTO getByDeptNo(@Param("deptNo") String deptNo);

    /**
     * 根据ID查询所有子部门
     *
     * @param deptId 部门ID
     * @return 部门列表
     */
    public List<SysDeptDTO> selectChildrenDeptById(String deptId, String status);

    /**
     * 新增部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Edit
    public int insertDept(SysDeptDTO dept);

    /**
     * 修改部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Edit
    public int updateDept(SysDeptDTO dept);

    /**
     * 修改所在部门正常状态
     *
     * @param deptIds 部门ID组
     */
    public void updateDeptStatus(Long[] deptIds, String status);

    /**
     * 修改子元素关系
     *
     * @param depts 子元素
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<SysDeptDTO> depts);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(Long deptId);

    /**
     * 根据部门NO查询信息
     *
     * @param deptNo 部门No
     * @return 部门信息
     */
    public SysDeptDTO getOneByDeptNo(@Param("deptNo") String deptNo);
    /**
     * 根据部门NO查询信息
     *
     * @param deptNos 部门No
     * @return 部门信息
     */
    public List<SysDeptDTO> getByDeptNos(@Param("deptNos") List<String> deptNos);

    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    List<SysDeptDTO> getDeptList(@Param("list") Collection<Long> ids);

    /**
     * 根据部门级别查询部门列表
     *
     * @param deptLevel 部门级别
     * @return 部门信息集合
     */
    public List<SysDeptDTO> selectDeptListByLevel(@Param("deptLevel") String deptLevel);

    /**
     * 根据父部门ID查询部门列表
     *
     * @param parentId 父部门ID
     * @return 部门信息集合
     */
    List<SysDeptDTO> selectDeptListByParentId(@Param("parentId") Long parentId);
}
