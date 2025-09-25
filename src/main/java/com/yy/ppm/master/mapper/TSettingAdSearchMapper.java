package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.TSettingAdSearchDTO;
import com.yy.ppm.master.bean.dto.TSettingAdSearchSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 高級查詢配置表(TSettingAdSearch)Mapper
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年09月25日 15:35:00
 */
@Repository
public interface TSettingAdSearchMapper {

/**
  * 获取高級查詢配置表列表
  * @param tSettingAdSearchSearchVo
  * @return
  */
 public Page<TSettingAdSearchDTO> getList(TSettingAdSearchSearchDTO tSettingAdSearchSearchVo);

/**
  * 导出高級查詢配置表列表
  * @param tSettingAdSearchSearchDTO
  * @return
  */
 public List<TSettingAdSearchDTO> exportList(TSettingAdSearchSearchDTO tSettingAdSearchSearchDTO);

 /**
  * 根据id获取高級查詢配置表
  * @param id 主键
  * @return
  */
 public TSettingAdSearchDTO getById(Long id);

 /**
  * 新增高級查詢配置表
  * @param tSettingAdSearchDTO
  * @return
  */
 @Edit
 public int insert(TSettingAdSearchDTO tSettingAdSearchDTO);

 /**
  * 修改高級查詢配置表
  * @param tSettingAdSearchDTO
  * @return
  */
 @Edit
 public int update(TSettingAdSearchDTO tSettingAdSearchDTO);


 /**
  * 根据id删除高級查詢配置表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

