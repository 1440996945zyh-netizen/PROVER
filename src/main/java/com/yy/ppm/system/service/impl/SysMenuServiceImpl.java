package com.yy.ppm.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.service.UserCacheService;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.system.bean.dto.SysMenuDTO;
import com.yy.ppm.system.bean.dto.SysMenuSearchDTO;
import com.yy.ppm.system.bean.dto.TreeSelectDTO;
import com.yy.ppm.system.mapper.SysMenuMapper;
import com.yy.ppm.system.service.SysMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 菜单(SysMenu)表服务实现类
 *
 * @author yy
 * @date 2021-02-26 15:41:02
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(SysMenuServiceImpl.class);

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private CommonMapper baseMapper;


    private final UserCacheService userCacheService;
    private final Snowflake snowflake;

    public SysMenuServiceImpl(UserCacheService userCacheService,Snowflake snowflake){
        this.userCacheService = userCacheService;
        this.snowflake = snowflake;
    }

    @Override
    public SysMenuDTO getDetailById(Long id) {
        final String methodName = "SysMenuServiceImpl:getById";
        LOGGER.enter(methodName, "业务执行");

        SysMenuDTO sysMenuDTO = sysMenuMapper.getDetailById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return sysMenuDTO;
    }

    /**
     * 查询菜单TreeTable
     * @param parentGid 父gid
     * @return
     */
    @Override
    public List<SysMenuDTO> getByParentGid(long parentGid) {
        final String methodName = "SysMenuServiceImpl:getByParentGid";
        LOGGER.enter(methodName, "业务执行");

        SysMenuSearchDTO menu = new SysMenuSearchDTO();
        menu.setUserId(securityUtils.getLoginUserId());
        menu.setIsAdmin(securityUtils.getUserInfo().getIsSuperadmin());
        menu.setParentGid(parentGid);
        List<SysMenuDTO> resultList = sysMenuMapper.getByParentId(menu);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return resultList;
    }

    /**
     * 保存菜单
     * @param sysMenuDTO
     * @return
     */
    @Override
    @Transactional
    public Long save(SysMenuDTO sysMenuDTO) {
        final String methodName = "SysMenuServiceImpl:save";
        LOGGER.enter(methodName, "业务执行");

        // 新增的场合
        if (sysMenuDTO.getId() == null) {
            //id
            sysMenuDTO.setId(snowflake.nextId());
            // 如果是目录，设置样式为Layout
            if("M".equals(sysMenuDTO.getMenuType()) && sysMenuDTO.getParentId() == 0){
                sysMenuDTO.setComponent("Layout");
            }
            // 如果是按钮 判断当前菜单下的按钮权限标识不能重复
            if("F".equals(sysMenuDTO.getMenuType())) {
                List<SysMenuDTO> sysMenuDTOList =  sysMenuMapper.getButtonPermsListByParentId(sysMenuDTO.getParentId());
                if(CollectionUtil.isNotEmpty(sysMenuDTOList)) {
                    boolean anyMatch = sysMenuDTOList.stream().anyMatch(dto -> sysMenuDTO.getPerms().equals(dto.getPerms()));
                    if(anyMatch) {
                        throw new BusinessRuntimeException("当前菜单下已有相同权限标识，请重新填写");
                    }
                }
            }

            sysMenuMapper.insert(sysMenuDTO);

            // 修改的场合
        } else {

            sysMenuMapper.update(sysMenuDTO);
        }

        userCacheService.clearUserInfo();

        LOGGER.exit(methodName, StringUtils.EMPTY);

        return sysMenuDTO.getId();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Override
    @Transactional
    public int deleteById(Long id) {
        final String methodName = "SysMenuServiceImpl:deleteById";
        LOGGER.enter(methodName, "业务执行");

        if (baseMapper.getCount("SYS_MENU", "PARENT_ID", id.toString()) > 0) {
            throw new BusinessRuntimeException("该数据有子级，不能删除~");
        }

        userCacheService.clearUserInfo();

        LOGGER.exit(methodName, StringUtils.EMPTY);

        return baseMapper.deleteById("SYS_MENU", id);

    }

    /**
     * 获取全部菜单
     * @param menu
     * @return
     */
    @Override
    public List<SysMenuDTO> selectMenuList(SysMenuSearchDTO menu) {

        menu.setUserId(securityUtils.getLoginUserId());
        menu.setIsAdmin(securityUtils.getUserInfo().getIsSuperadmin());

        List<SysMenuDTO> menuList = sysMenuMapper.selectMenuList(menu);

        return menuList;
    }

    @Override
    public List<SysMenuDTO> listApp(SysMenuSearchDTO menu) {

        menu.setUserId(securityUtils.getLoginUserId());
        menu.setIsAdmin(securityUtils.getUserInfo().getIsSuperadmin());

        List<SysMenuDTO> menuList = sysMenuMapper.listApp(menu);

        return menuList;
    }

    @Override
    public List<SysMenuDTO> listApplet(SysMenuSearchDTO menu) {

        menu.setUserId(securityUtils.getLoginUserId());
        menu.setIsAdmin(securityUtils.getUserInfo().getIsSuperadmin());

        List<SysMenuDTO> menuList = sysMenuMapper.listApplet(menu);

        return menuList;
    }

    /**
     * 获取菜单树 下拉数据源
     */
    @Override
    public List<TreeSelectDTO> getTreeSelect() {
        return this.getTreeSelect(0L);
    }
    /**
     * 查询所有的目录、菜单
     * @return
     */
    @Override
    public List<SysMenuDTO> getContentsMenu() {
        return sysMenuMapper.getContentsMenu();
    }
    /**
     * 菜单树
     * @param menuParentId
     * @return
     */
    private List<TreeSelectDTO> getTreeSelect(Long menuParentId){

        // 根据父Id获取菜单
        final List<TreeSelectDTO> menuDTOS = sysMenuMapper.getTreeSelect(menuParentId);

        for(TreeSelectDTO treeSelectDTO : menuDTOS){
            // 还有下级菜单
            if(treeSelectDTO.getIsAlwaysShow()){
                treeSelectDTO.setChildren(this.getTreeSelect(treeSelectDTO.getId()));
            }
        }
        return menuDTOS;
    }


}
