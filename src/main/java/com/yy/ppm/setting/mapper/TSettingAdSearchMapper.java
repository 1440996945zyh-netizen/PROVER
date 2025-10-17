package com.yy.ppm.setting.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.setting.bean.dto.TSettingAdSearchDTO;
import com.yy.ppm.setting.bean.dto.TSettingAdSearchSearchDTO;
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
  * @param menuId 页面id
  * @param tableId 前端table的ID
  * @return
  */
 List<TSettingAdSearchDTO> getByMenuIdAndTableId(@Param("menuId") Long menuId, @Param("tableId") String tableId);



 /**
  * 新增高級查詢配置表
  * @param
  * @return
  */
 @Edit
 void insertBatch(List<TSettingAdSearchDTO> tSettingAdSearchDTOList);


 void deleteByMenuIdAndTableId(@Param("menuId") Long menuId, @Param("tableId") String tableId);
}

