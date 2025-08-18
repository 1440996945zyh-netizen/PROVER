package com.yy.ppm.dispatch.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogDTO;
import com.yy.ppm.dispatch.bean.dto.MSjsbLogSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 数据上报日志表(MSjsbLog)Mapper
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2025年05月20日 10:40:00
 */
@Repository
public interface MSjsbLogMapper {

/**
  * 获取数据上报日志表列表
  * @param mSjsbLogSearchVo
  * @return
  */
 public Page<MSjsbLogDTO> getList(MSjsbLogSearchDTO mSjsbLogSearchVo);

/**
  * 导出数据上报日志表列表
  * @param mSjsbLogSearchDTO
  * @return
  */
 public List<MSjsbLogDTO> exportList(MSjsbLogSearchDTO mSjsbLogSearchDTO);

 /**
  * 根据id获取数据上报日志表
  * @param id 主键
  * @return
  */
 public MSjsbLogDTO getById(Long id);

 /**
  * 新增数据上报日志表
  * @param mSjsbLogDTO
  * @return
  */
 @Edit
 public int insert(MSjsbLogDTO mSjsbLogDTO);

 /**
  * 修改数据上报日志表
  * @param mSjsbLogDTO
  * @return
  */
 @Edit
 public int update(MSjsbLogDTO mSjsbLogDTO);

 @Edit
 public int updateSjsbLogStatus();


 /**
  * 根据id删除数据上报日志表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

