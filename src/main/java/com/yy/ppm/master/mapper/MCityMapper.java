package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MCityDTO;
import com.yy.ppm.master.bean.dto.MCitySearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName (MCity)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月30日 13:29:00
 */
@Repository
public interface MCityMapper {

/**
  * 获取列表
  * @param mCitySearchVo
  * @return
  */
 public Page<MCityDTO> getList(MCitySearchDTO mCitySearchVo);

/**
  * 导出列表
  * @param mCitySearchDTO
  * @return
  */
 public List<MCityDTO> exportList(MCitySearchDTO mCitySearchDTO);

 /**
  * 根据id获取
  * @param id 主键
  * @return
  */
 public MCityDTO getById(Long id);

 /**
  * 新增
  * @param mCityDTO
  * @return
  */
 @Edit
 public int insert(MCityDTO mCityDTO);

 /**
  * 修改
  * @param mCityDTO
  * @return
  */
 @Edit
 public int update(MCityDTO mCityDTO);


 /**
  * 根据id删除
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

