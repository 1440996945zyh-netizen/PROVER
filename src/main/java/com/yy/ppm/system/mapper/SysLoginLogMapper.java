package com.yy.ppm.system.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysLoginLogDTO;
import com.yy.ppm.system.bean.dto.SysLoginLogSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 登录日志表(SysLoginLog)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 15:51:00
 */
@Repository
public interface SysLoginLogMapper {

/**
  * 获取登录日志表列表
  * @param sysLoginLogSearchVo
  * @return
  */
 public Page<SysLoginLogDTO> getList(SysLoginLogSearchDTO sysLoginLogSearchVo);

/**
  * 导出登录日志表列表
  * @param sysLoginLogSearchDTO
  * @return
  */
 public List<SysLoginLogDTO> exportList(SysLoginLogSearchDTO sysLoginLogSearchDTO);

 /**
  * 根据id获取登录日志表
  * @param id 主键
  * @return
  */
 public SysLoginLogDTO getById(Long id);

 /**
  * 新增登录日志表
  * @param sysLoginLogDTO
  * @return
  */
 @Edit
 public int insert(SysLoginLogDTO sysLoginLogDTO);

 /**
  * 修改登录日志表
  * @param sysLoginLogDTO
  * @return
  */
 @Edit
 public int update(SysLoginLogDTO sysLoginLogDTO);


 /**
  * 根据id删除登录日志表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

