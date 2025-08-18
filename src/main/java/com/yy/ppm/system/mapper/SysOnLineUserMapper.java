package com.yy.ppm.system.mapper;

import com.yy.ppm.system.bean.dto.SysUserDTO;

import java.util.List;
import java.util.Set;

/**
 * @Description: 在线用户mapper
 * @Author sunqi
 * @Date 2023/5/8 13:54
 */
public interface SysOnLineUserMapper {
    /**
     * 查询
     * @param
     * @return
     */
    List<SysUserDTO> getList(Set<String> accountList, String userAccount, String userName);
}
