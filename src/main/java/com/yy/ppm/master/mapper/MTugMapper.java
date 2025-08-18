package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MTugDTO;
import com.yy.ppm.master.bean.dto.MTugSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 拖轮资料(MTug)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 14:20:00
 */
@Repository
public interface MTugMapper {

/**
  * 获取拖轮资料列表
  * @param mTugSearchVo
  * @return
  */
 public Page<MTugDTO> getList(MTugSearchDTO mTugSearchVo);

/**
  * 导出拖轮资料列表
  * @param mTugSearchDTO
  * @return
  */
 public List<MTugDTO> exportList(MTugSearchDTO mTugSearchDTO);

 /**
  * 根据id获取拖轮资料
  * @param id 主键
  * @return
  */
 public MTugDTO getById(Long id);

 /**
  * 新增拖轮资料
  * @param mTugDTO
  * @return
  */
 @Edit
 public int insert(MTugDTO mTugDTO);

 /**
  * 修改拖轮资料
  * @param mTugDTO
  * @return
  */
 @Edit
 public int update(MTugDTO mTugDTO);


 /**
  * 根据id删除拖轮资料
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

