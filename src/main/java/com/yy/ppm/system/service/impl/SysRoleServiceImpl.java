package com.yy.ppm.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.flowable.constants.ErrorCodeConstants;
import com.yy.common.flowable.enums.CommonStatusEnum;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.service.UserCacheService;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import com.yy.ppm.system.bean.dto.SysRoleSearchDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.TreeSelectDTO;
import com.yy.ppm.system.mapper.SysRoleMapper;
import com.yy.ppm.system.service.SysMenuService;
import com.yy.ppm.system.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

import static com.yy.common.flowable.constants.ErrorCodeConstants.ROLE_IS_DISABLE;
import static com.yy.common.flowable.constants.ErrorCodeConstants.ROLE_NOT_EXISTS;
import static com.yy.common.flowable.utils.CollectionUtils.convertMap;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;


/**
 * 角色(SysRole)表服务实现类
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SysRoleServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private CommonMapper baseMapper;

    @Resource
    private CommonService commonService;

    @Resource
    private SysMenuService sysMenuService;

    @Resource
    private UserCacheService userCacheService;

    /**
     * 列表查询
     *
     * @param sysRoleSearchDTO
     * @return
     */
    @Override
    public Pages<SysRoleDTO> getList(SysRoleSearchDTO sysRoleSearchDTO) {
        final String methodName = "SysRoleServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");

        Pages<SysRoleDTO> pages = PageHelperUtils.limit(sysRoleSearchDTO, () -> {
            return sysRoleMapper.getList(sysRoleSearchDTO);
        });

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return pages;
    }

    /**
     * 查询全部角色，不分页
     *
     * @param sysRoleSearchDTO
     * @return
     */
    public List<SysRoleDTO> getListNoPage(SysRoleSearchDTO sysRoleSearchDTO) {
        final String methodName = "SysRoleServiceImpl:getList";
        LOGGER.enter(methodName, "业务执行");
        List<SysRoleDTO> list = sysRoleMapper.getListNoPage(sysRoleSearchDTO);
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return list;
    }

    /**
     * 角色详情
     *
     * @param id 主键
     * @return
     */
    @Override
    public SysRoleDTO getById(Long id) {
        final String methodName = "SysRoleServiceImpl:getById";
        LOGGER.enter(methodName, "业务执行");

        SysRoleDTO sysRoleDTO = sysRoleMapper.getById(id);
        LOGGER.exit(methodName, StringUtils.EMPTY);
        return sysRoleDTO;
    }

    @Override
    @Transactional
    public int save(SysRoleDTO sysRoleDTO) {
        final String methodName = "SysRoleServiceImpl:save";
        LOGGER.enter(methodName, "业务执行");
        // 验证账号重复
        commonService.isRepeate("SYS_ROLE", "ROLE_CODE", sysRoleDTO.getRoleCode(), StringUtil.getString(sysRoleDTO.getId()), "权限字符", null);

        int count = 0;
        // 新增的场合
        if (sysRoleDTO.getId() == null) {
            //id
            sysRoleDTO.setId(snowflake.nextId());

            count = sysRoleMapper.insert(sysRoleDTO);
            //批量新增角色菜单信息
            if (null != sysRoleDTO.getMenuIds() && sysRoleDTO.getMenuIds().size() > 0) {
                count = sysRoleMapper.insertRoleMenu(sysRoleDTO.getMenuIds(), sysRoleDTO.getId());
            }
            // 删除用户角色的部门信息
            count = baseMapper.delete("sys_role_dept", "role_id", sysRoleDTO.getId() + "");
            // 是自定义的数据权限 批量新增角色部门信息
            if ("2".equals(sysRoleDTO.getDataScope()) && null != sysRoleDTO.getDeptIds() && sysRoleDTO.getDeptIds().size() > 0) {
                count = sysRoleMapper.insertRoleDept(sysRoleDTO.getId(), sysRoleDTO.getDeptIds());
            }
            // 修改的场合
        } else {
            //删除该角色的菜单
            count = baseMapper.delete("sys_role_menu", "role_id", sysRoleDTO.getId() + "");
            //批量新增角色菜单信息
            if (null != sysRoleDTO.getMenuIds() && sysRoleDTO.getMenuIds().size() > 0) {
                count = sysRoleMapper.insertRoleMenu(sysRoleDTO.getMenuIds(), sysRoleDTO.getId());
            }

            // 删除该角色的部门信息
            count = baseMapper.delete("sys_role_dept", "role_id", sysRoleDTO.getId() + "");
            // 是自定义的数据权限 批量新增角色部门信息
            if ("2".equals(sysRoleDTO.getDataScope()) && null != sysRoleDTO.getDeptIds() && sysRoleDTO.getDeptIds().size() > 0) {
                count = sysRoleMapper.insertRoleDept(sysRoleDTO.getId(), sysRoleDTO.getDeptIds());
            }
            count = sysRoleMapper.update(sysRoleDTO);
        }

        userCacheService.clearUserInfo();

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return count;
    }

    /**
     * 删除角色
     *
     * @param idList
     * @return
     */
    @Transactional
    public int deleteById(List<Long> idList) {
        final String methodName = "SysRoleServiceImpl:deleteById";
        LOGGER.enter(methodName, "业务执行");

        if (StringUtil.isEmpty(idList)) {
            throw new BusinessRuntimeException("参数不能为空");
        }
        int count = 0;
        if (null != idList && idList.size() > 0) {
            for (Long id : idList) {
                // 删除角色
                count = baseMapper.deleteById("sys_role", id);
                // 删除该角色的菜单
                baseMapper.delete("sys_role_menu", "role_id", id + "");
                // 删除角色人员
                baseMapper.delete("sys_role_user", "role_id", id + "");
            }
        }

        userCacheService.clearUserInfo();

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return count;
    }

    /**
     * 修改角色状态，是否可用
     *
     * @param sysRoleDTO
     * @return
     */
    @Override
    public int changeStatus(SysRoleDTO sysRoleDTO) {
        userCacheService.clearUserInfo();
        return sysRoleMapper.changeStatus(sysRoleDTO);
    }

    /**
     * 获取角色、组织架构关联信息
     *
     * @param id
     * @return
     */
    @Override
    public Map getDeptTree(Long id) {
        Map map = new HashMap();
        List<Long> deptIdByRole = new ArrayList<>();
        if (null != id) {
            deptIdByRole = sysRoleMapper.getDeptIdByRole(id);
        }

        map.put("checkedKeys", deptIdByRole);
        map.put("depts", this.getDept(0L));
        return map;
    }

    /**
     * 递归查询部门树
     *
     * @param parentId
     * @return
     */
    private List<TreeSelectDTO> getDept(Long parentId) {
        // 根据父Id获取
        List<TreeSelectDTO> list = sysRoleMapper.getDept(parentId);
        for (TreeSelectDTO treeSelectDTO : list) {
            // 还有下级
            if (treeSelectDTO.getIsAlwaysShow()) {
                treeSelectDTO.setChildren(this.getDept(treeSelectDTO.getId()));
            }
        }
        return list;
    }


    @Override
    @Transactional
    public int dataScope(SysRoleDTO sysRoleDTO) {
        // 修改角色信息
        sysRoleMapper.update(sysRoleDTO);
        // 删除角色与部门关联
        baseMapper.delete("sys_role_dept", "ROLE_ID", String.valueOf(sysRoleDTO.getId()));
        // 新增角色和部门信息（数据权限）
        int num = 0;
        if (null != sysRoleDTO.getDeptIds() && sysRoleDTO.getDeptIds().size() > 0) {
            num = sysRoleMapper.insertRoleDept(sysRoleDTO.getId(), sysRoleDTO.getDeptIds());
        }
        return num;
    }

    /**
     * 查询已授权和未授权用户列表
     *
     * @param roleId
     * @param flag
     * @param nameOrAccount
     * @return
     */
    @Override
    public List<SysUserDTO> allocatedOrUnallocatedList(Long roleId, String nameOrAccount, String flag) {
        // 查询授权用户
        List<SysUserDTO> list = sysRoleMapper.allocatedOrUnallocatedList(roleId, flag, nameOrAccount);
        return list;
    }

    /**
     * 添加、取消用户授权
     *
     * @param paramMap
     * @return
     */
    @Override
    public int authUserInsertDelete(Map paramMap) {
        int num = 0;
        List<String> userIdStr = (List<String>) paramMap.get("userIds");
        String roleId = (String) paramMap.get("roleId");
        if (null != userIdStr && userIdStr.size() > 0) {
            List<Long> userIdList = userIdStr.stream().map(r -> Long.valueOf(r)).collect(Collectors.toList());
            // 授权
            if ("insert".equals(paramMap.get("insertOrDel"))) {
                num = sysRoleMapper.insertRoleUser(userIdList, Long.valueOf(roleId));
            } else {
                // 取消授权
                num = sysRoleMapper.authUserCancel(userIdList, Long.valueOf(roleId));
            }
        }

        return num;
    }


    /**
     * 根据角色id查询菜单id
     *
     * @param id
     * @return
     */
    @Override
    public Map getMenuTree(Long id) {
        Map map = new HashMap();
        List<Long> menuIdByRole = new ArrayList<>();
        if (null != id) {
            menuIdByRole = sysRoleMapper.getMenuIdByRole(id);
        }
        map.put("checkedKeys", menuIdByRole);
        map.put("menus", sysMenuService.getTreeSelect());
        return map;
    }

    @Override
    public List<Map<String, Object>> getListByRoleClass(String roleClass) {
        List<SysRoleDTO> list = sysRoleMapper.getListByRoleClass(roleClass);
        List<Map<String, Object>> result = Lists.newArrayList();
        list.forEach(e->{
            Map<String, Object> map = Maps.newHashMap();
            map.put("label",e.getDeptName());
            map.put("value",e.getRoleCode());
            result.add(map);
        });
        return result;
    }

    /**
     * 校验角色们是否有效。如下情况，视为无效：
     * 1. 角色编号不存在
     * 2. 角色被禁用
     *
     * @param ids 角色编号数组
     */
    @Override
    public void validRoleList(Set<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        List<SysRoleDTO> roles = sysRoleMapper.selectByIds(ids);
        Map<Long, SysRoleDTO> roleMap = convertMap(roles, SysRoleDTO::getId);
        // 校验
        ids.forEach(id -> {
            SysRoleDTO role = roleMap.get(id);
            if (role == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(Integer.valueOf(role.getStatus()))) {
                throw exception(ROLE_IS_DISABLE, role.getRoleName());
            }
        });
    }

    /**
     * 获得拥有多个角色的用户编号集合
     * @param roleIds
     * @return
     */
    @Override
    public Set<Long> getUserRoleIdListByRoleIds(Set<Long> roleIds) {
        return sysRoleMapper.getUserRoleIdListByRoleId(roleIds);
    }

    /**
     * 根据用户角色ID查询用户信息
     * @param roleIds
     * @return
     */
    @Override
    public Set<Long> getUserIdListByRoleIds(Set<Long> roleIds) {
        return sysRoleMapper.getUserIdListByRoleIds(roleIds);
    }

}
