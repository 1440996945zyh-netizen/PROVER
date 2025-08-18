package com.yy.ppm.dispatch.mapper;


import cn.hutool.core.lang.Dict;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationDTO;
import com.yy.ppm.dispatch.bean.dto.TBusTrustLocationSearchDTO;
import com.yy.ppm.dispatch.bean.po.TBusTrustLocationPO;
import com.yy.ppm.master.bean.po.MDictDataPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 集疏港作业通知单位置表，传输渤海通使用(TBusTrustLocation)Mapper
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年09月27日 14:34:00
 */
@Repository
public interface TBusTrustLocationMapper {

/**
  * 获取集疏港作业通知单位置表，传输渤海通使用列表
  * @param tBusTrustLocationSearchVo
  * @return
  */
 public Page<TBusTrustLocationDTO> getPageList(TBusTrustLocationSearchDTO tBusTrustLocationSearchVo);
 

  /**
  * 导出集疏港作业通知单位置表，传输渤海通使用列表
  * @param tBusTrustLocationSearchDTO
  * @return
  */
 public List<TBusTrustLocationDTO> exportList(TBusTrustLocationSearchDTO tBusTrustLocationSearchDTO);

 /**
  * 根据id获取集疏港作业通知单位置表，传输渤海通使用
  * @param id 主键
  * @return
  */
 public TBusTrustLocationDTO getById(Long id);
 

 /**
  * 新增集疏港作业通知单位置表，传输渤海通使用
  * @param tBusTrustLocationDTO
  * @return
  */
 @Edit
 public int insert(TBusTrustLocationDTO tBusTrustLocationDTO);

 /**
  * 获取指派位置的所属区域
  * @param tBusTrustLocationPOS
  * @return
  */
 public List<MDictDataPO> getWorkArea(@Param("tBusTrustLocationPOS") List<TBusTrustLocationPO> tBusTrustLocationPOS);

 /**
  * 批量新增集疏港作业通知单位置表，传输渤海通使用
  * @param tBusTrustLocationPOS
  * @return
  */
 @Edit
 public int insertList(@Param("tBusTrustLocationPOS") List<TBusTrustLocationPO> tBusTrustLocationPOS);

 
 /**
  * 修改集疏港作业通知单位置表，传输渤海通使用
  * @param tBusTrustLocationDTO
  * @return
  */
 @Edit
 public int update(TBusTrustLocationDTO tBusTrustLocationDTO);
 
  /**
  * 批量修改
  * @param tBusTrustLocationDTOS
  * @return
  */
 @Edit
 public int updateListById(@Param("tBusTrustLocationDTOS") List<TBusTrustLocationDTO> tBusTrustLocationDTOS);


 /**
  * 根据id删除集疏港作业通知单位置表，传输渤海通使用
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
 
 
  /**
  * 批量删除
  * 根据id删除集疏港作业通知单位置表，传输渤海通使用
  * @param ids 主键
  * @return
  */
 public int deleteListByIds(@Param("ids") List<Long> ids);
 
   /**
  * 批量删除
  * 根据id删除集疏港作业通知单位置表，传输渤海通使用
  * @param tBusTrustLocationDTO 
  * @return
  */
 public int deleteByCondition(TBusTrustLocationDTO tBusTrustLocationDTO);

 //根据指令ID获取计划编号
 public List<String> getBusinessNo(@Param("trustId") Long trustId);

 //将作业港区写入动脉计划表
 @DS("simeauto")
 public int writeSimeautoLocation(@Param("businessNoList") List<String> businessNoList,@Param("workArea") MDictDataPO workArea);

 /**
  * 根据trustId批量查询场地位置
  * @param trustIds
  * @return
  */
 public List<TBusTrustLocationDTO> getListByTrustIds(@Param("trustIds") List<Long> trustIds);

 List<Map<String, Object>> getMassIdsWithTrustId(TBusTrustLocationDTO dto);
}

