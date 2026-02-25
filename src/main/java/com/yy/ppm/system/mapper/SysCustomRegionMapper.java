package com.yy.ppm.system.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.system.bean.dto.SysCustomRegionDTO;
import com.yy.ppm.system.bean.dto.SysMenuDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName (SysCustomRegion)Mapper
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月02日 11:14:00
 */
@Repository
public interface SysCustomRegionMapper {

/**
  * 获取列表
  * @param
  * @return
  */
 public List<SysCustomRegionDTO> getList(@Param("loginUserId") Long loginUserId);


 /**
  * 获取列表
  * @param
  * @return
  */
 public List<SysMenuDTO> getListApp(@Param("loginUserId") Long loginUserId);


 /**
  * 根据id获取
  * @param id 主键
  * @return
  */
 public SysCustomRegionDTO getById(Long id);

 /**
  * 新增
  * @param sysCustomRegionDTO
  * @return
  */
 @Edit
 public int insert(SysCustomRegionDTO sysCustomRegionDTO);
 public int getCount(SysCustomRegionDTO sysCustomRegionDTO);



 /**
  * 批量新增
  * @param list
  * @return
  */
 public int batchInsert(@Param("list") List<SysCustomRegionDTO> list);

 /**
  * 修改
  * @param sysCustomRegionDTO
  * @return
  */
 @Edit
 public int update(SysCustomRegionDTO sysCustomRegionDTO);


 /**
  * 根据id删除
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

 /**
  * 取消收藏
  * @param sysCustomRegionDTO
  * @return
  */
 public int deleteSingleData(SysCustomRegionDTO sysCustomRegionDTO);
}

