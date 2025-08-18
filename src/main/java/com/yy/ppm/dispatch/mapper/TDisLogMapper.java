package com.yy.ppm.dispatch.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.TDisLogDTO;
import com.yy.ppm.dispatch.bean.dto.TDisLogSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 调度日志(TDisLog)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@Repository
public interface TDisLogMapper {

/**
  * 获取调度日志列表
  * @param tDisLogSearchVo
  * @return
  */
 public Page<TDisLogDTO> getList(TDisLogSearchDTO tDisLogSearchVo);

/**
  * 导出调度日志列表
  * @param tDisLogSearchDTO
  * @return
  */
 public List<TDisLogDTO> exportList(TDisLogSearchDTO tDisLogSearchDTO);

 /**
  * 根据id获取调度日志
  * @param id 主键
  * @return
  */
 public TDisLogDTO getById(Long id);

 /**
  * 新增调度日志
  * @param tDisLogDTO
  * @return
  */
 @Edit
 public int insert(TDisLogDTO tDisLogDTO);

 /**
  * 修改调度日志
  * @param tDisLogDTO
  * @return
  */
 @Edit
 public int update(TDisLogDTO tDisLogDTO);


 /**
  * 根据id删除调度日志
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

}

