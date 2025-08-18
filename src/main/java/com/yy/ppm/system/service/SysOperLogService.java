package com.yy.ppm.system.service;

import com.yy.common.page.Pages;
import com.yy.ppm.system.bean.dto.SysOperLogDTO;
import com.yy.ppm.system.bean.dto.SysOperLogSearchDTO;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/6 11:25
 */
public interface SysOperLogService {

    /**
     * 查询
     * @param sysOperLogSearchDTO
     * @return
     */
    public Pages<SysOperLogDTO> getList(SysOperLogSearchDTO sysOperLogSearchDTO);

    /**
     * 新增
     * @param sysOperLogDTO
     * @return
     */
    public int insert(SysOperLogDTO sysOperLogDTO);

    /**
     * 根据Id查询
     * @param operId
     * @return
     */
    SysOperLogDTO getById(Long operId);
}
