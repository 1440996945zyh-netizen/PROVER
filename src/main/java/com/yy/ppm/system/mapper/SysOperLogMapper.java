package com.yy.ppm.system.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.system.bean.dto.SysOperLogDTO;
import com.yy.ppm.system.bean.dto.SysOperLogSearchDTO;

/**
 * 部门Dao
 *
 * @author daiying
 * @date 2021-02-26 15:40:00
 */
public interface SysOperLogMapper {

    /**
     * 查询
     *
     * @return 部门信息集合
     */
    public Page<SysOperLogDTO> getList(SysOperLogSearchDTO sysUserSearchDTO);

    /**
     * 新增
     *
     * @param dto
     * @return 结果
     */
    public int insert(SysOperLogDTO dto);

    /**
     * 根据Id查询
     * @param operId
     * @return
     */
    SysOperLogDTO getById(Long operId);
}
