package com.yy.ppm.auth.mapper;

import com.yy.ppm.auth.bean.dto.RouterDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/4/23 16:45
 */

public interface RouterMapper {

    /**
     * 登录后查询左侧菜单路由
     * @param isAdmin
     * @param userId
     * @param parentId
     * @param loginType
     * @return
     */
    List<RouterDTO> getRouters(@Param("isAdmin") String isAdmin, @Param("userId") Long userId, @Param("parentId") Long parentId, @Param("loginType") String loginType);

}
