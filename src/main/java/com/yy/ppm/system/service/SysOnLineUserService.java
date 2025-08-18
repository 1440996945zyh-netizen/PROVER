package com.yy.ppm.system.service;

import com.yy.ppm.system.bean.dto.SysUserDTO;

import java.util.List;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/6 11:38
 */
public interface SysOnLineUserService {

    /**
     * 查询
     * @return
     */
    List<SysUserDTO> getList(String userAccount, String userName);


    /**
     * 强退
     */
    void offLine(String userAccount,Long id);
}
