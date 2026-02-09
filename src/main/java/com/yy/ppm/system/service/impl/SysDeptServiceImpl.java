package com.yy.ppm.system.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.CommonEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.mapper.SysDeptMapper;
import com.yy.ppm.system.service.SysDeptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 菜单(SysMenu)表服务实现类
 *
 * @author 张超
 * @date 2021-02-26 15:41:02
 */
@Service
public class SysDeptServiceImpl implements SysDeptService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SysDeptServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private SysDeptMapper deptMapper;

    @Resource
    private CommonService commonService;

    @Resource
    private CommonMapper commonMapper;

    /**
     * 查询部门管理数据
     *
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptDTO> selectDeptList(SysDeptDTO dto) {
        return deptMapper.selectDeptList(dto);
    }

    /**
     * 根据部门ID查询信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @Override
    public SysDeptDTO getById(Long id) {
        return deptMapper.getById(id);
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDeptDTO dept) {

        List<CheckDTO> keyValues = null;

        if (dept.getParentId() != null) {
            keyValues = new ArrayList<>();
            keyValues.add(CheckDTO.buildDTO("PARENT_ID", dept.getParentId()));
        }

        // 部门名称重复
        commonService.isRepeate("SYS_DEPT", "DEPT_NAME", dept.getDeptName(), StringUtil.getString(dept.getId()), "部门名称", keyValues);

        // 父类信息
        SysDeptDTO info = deptMapper.getById(dept.getParentId());

        // 如果父节点不为正常状态,则不允许新增子节点
        if (info != null && CommonEnum.YesNoMode.NO.getCode().equals(String.valueOf(info.getStatus()))) {
            throw new BusinessRuntimeException("部门停用，不允许新增");
        }

        // 自动编号
        if (info == null) {
            dept.setDeptNo("0001");
        } else {
            dept.setDeptNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.DEPT, info.getDeptNo()));
        }
        // 父类ids，多个逗号隔开。
        dept.setParentIds(info.getParentIds() + "," + dept.getParentId());
        dept.setId(snowflake.nextId());
        return deptMapper.insertDept(dept);

    }

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDeptDTO dept) {
        // 部门名称重复
        commonService.isRepeate("SYS_DEPT", "DEPT_NAME", dept.getDeptName(), StringUtil.getString(dept.getId()), "部门名称", null);

        // 修改时，当选中的上级部门是自己时
        if (StringUtil.getString(dept.getParentId()).equals(StringUtil.getString(dept.getId()))) {
            throw new BusinessRuntimeException("选中的上级部门不能是自己~");
        }

        // 停用时，如果子级没有停用
        if (CommonEnum.YesNoMode.NO.getCode().equals(StringUtil.getString(dept.getStatus()))
                && deptMapper.selectChildrenDeptById("%" + StringUtil.getString(dept.getId()) + "%", CommonEnum.YesNoMode.YES.getCode()).size() > 0) {
            throw new BusinessRuntimeException("该部门包含未停用的子部门，不能停用！");
        }

        SysDeptDTO leftDept = deptMapper.getById(dept.getId());

        // 父部门
        SysDeptDTO parentDept = deptMapper.getById(dept.getParentId());

        // 当上级部门变化是，所有当前部门的下级部门的parentIds做修改
        if (!StringUtil.getString(leftDept.getParentId()).equals(StringUtil.getString(dept.getParentId()))) {

            if (null != parentDept) {
                String newAncestors = parentDept.getParentIds() + "," + parentDept.getId();
                String oldAncestors = leftDept.getParentIds();
                dept.setParentIds(newAncestors);
                updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
            }
        }

        int result = deptMapper.updateDept(dept);

        // 启用当前部门时，同时启用所有上级部门
        if (CommonEnum.YesNoMode.YES.getCode().equals(StringUtil.getString(dept.getStatus())) && StringUtils.isNotEmpty(dept.getParentIds())
                && !StringUtils.equals(CommonEnum.YesNoMode.YES.getCode(), dept.getParentIds())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            String ancestors = dept.getParentIds();
            Long[] deptIds = Convert.toLongArray(ancestors);
            if (deptIds != null && deptIds.length > 0) {
                deptMapper.updateDeptStatus(deptIds, CommonEnum.YesNoMode.YES.getCode());
            }
        }

        return result;

    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    private void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {

        // 查询当前部门子级列表
        List<SysDeptDTO> children = deptMapper.selectChildrenDeptById("%" + StringUtil.getString(deptId) + "%", null);

        for (SysDeptDTO child : children) {

            child.setParentIds(child.getParentIds().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            deptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId) {

        // 是否有子部门
        int count = commonMapper.getCount("SYS_DEPT", "PARENT_ID", StringUtil.getString(deptId));

        if (count > 0) {
            throw new BusinessRuntimeException("存在下级部门,不允许删除");
        }

        count = commonMapper.getCount("SYS_USER", "DEPT_ID", StringUtil.getString(deptId));

        if (count > 0) {
            throw new BusinessRuntimeException("部门存在用户,不允许删除");
        }

        return deptMapper.deleteDeptById(deptId);
    }


    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    @Override
    public List<SysDeptDTO> getDeptList(Collection<Long> ids) {
        return deptMapper.getDeptList(ids);
    }

    /**
     * 根据部门级别查询部门列表
     *
     * @param deptLevel 部门级别
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptDTO> selectDeptListByLevel(String deptLevel) {
        return deptMapper.selectDeptListByLevel(deptLevel);
    }

}
