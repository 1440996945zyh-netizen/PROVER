package com.yy.ppm.auth.service.impl;

import com.yy.common.util.SecurityUtils;
import com.yy.ppm.auth.bean.dto.RouterDTO;
import com.yy.ppm.auth.enums.LoginTypeEnum;
import com.yy.ppm.auth.mapper.RouterMapper;
import com.yy.ppm.auth.service.RouterService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/4/23 16:31
 */
@Service
public class RouterServiceImpl implements RouterService {
    @Resource
    private RouterMapper routerMapper;

    @Resource
    private SecurityUtils securityUtils;

    /**
     * 获取左侧菜单列表
     * @returns
     * @param loginType
     */
    @Override
    public List<RouterDTO> getRouters(String loginType) {
        return this.getMenuTreeList(0L, loginType);
    }

    /**
     * 菜单树
     * @param menuParentId
     * @return
     */
    private List<RouterDTO> getMenuTreeList(Long menuParentId, String loginType){

        // 根据父Id获取菜单
        final List<RouterDTO> menuDTOS = routerMapper.getRouters(securityUtils.getUserInfo().getIsSuperadmin(), securityUtils.getLoginUserId(), menuParentId, loginType);

        for(RouterDTO menuLeftTreeDTO : menuDTOS){
            // 额外添加meta数据
            HashMap<String, Object> menuMeta = new HashMap<String, Object>();
            menuMeta.put("icon", menuLeftTreeDTO.getIcon());
            menuMeta.put("title", menuLeftTreeDTO.getName());
            menuMeta.put("query", menuLeftTreeDTO.getQuery());
            menuMeta.put("isFrame", menuLeftTreeDTO.getIsFrame());
            menuMeta.put("link", menuLeftTreeDTO.getLink());
            menuMeta.put("menuType", menuLeftTreeDTO.getMenuType());
            menuMeta.put("parentId", menuLeftTreeDTO.getParentId());

            menuLeftTreeDTO.setQuery(null); //解决前端路径上显示query，放到meta使用
            menuLeftTreeDTO.setName(menuLeftTreeDTO.getPath() + "-" + menuLeftTreeDTO.getName()); // 解决前端因name值相同覆盖菜单的bug
            menuLeftTreeDTO.setMeta(menuMeta);

            // 还有下级菜单
            if(menuLeftTreeDTO.isAlwaysShow()){
                menuLeftTreeDTO.setChildren(this.getMenuTreeList(menuLeftTreeDTO.getId(), loginType));
            }
        }
        return menuDTOS;
    }

}
