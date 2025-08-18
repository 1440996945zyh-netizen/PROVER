package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.PoundbillDTO;
import com.yy.ppm.business.bean.dto.PoundbillSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 单船测试记录(TStdShipRecord)Mapper
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月31日 10:35:00
 */
@Repository
public interface PoundbillMapper {

/**
  * 获取单船测试记录列表
  * @param tStdShipRecordSearchVo
  * @return
  */
 public Page<PoundbillDTO> getPageList(PoundbillSearchDTO tStdShipRecordSearchVo);
 

  /**
  * 导出单船测试记录列表
  * @param tStdShipRecordSearchDTO
  * @return
  */
 public List<PoundbillDTO> getDetailList(PoundbillSearchDTO tStdShipRecordSearchDTO);

// /**
//  * 根据id获取单船测试记录
//  * @param id 主键
//  * @return
//  */
// public PoundbillDTO getById(Long id);
//
//
// /**
//  * 新增单船测试记录
//  * @param tStdShipRecordDTO
//  * @return
//  */
// @Edit
// public int insert(PoundbillDTO tStdShipRecordDTO);
//
//  /**
//  * 批量新增单船测试记录
//  * @param tStdShipRecordDTOS
//  * @return
//  */
// @Edit
// public int insertList(@Param("tStdShipRecordDTOS") List<PoundbillDTO> tStdShipRecordDTOS);
//
//
// /**
//  * 修改单船测试记录
//  * @param tStdShipRecordDTO
//  * @return
//  */
// @Edit
// public int update(PoundbillDTO tStdShipRecordDTO);
//
//  /**
//  * 批量修改
//  * @param tStdShipRecordDTOS
//  * @return
//  */
// @Edit
// public int updateListById(@Param("tStdShipRecordDTOS") List<PoundbillDTO> tStdShipRecordDTOS);
//
//
// /**
//  * 根据id删除单船测试记录
//  * @param id 主键
//  * @return
//  */
// public int deleteById(Long id);
//
//
//  /**
//  * 批量删除
//  * 根据id删除单船测试记录
//  * @param ids 主键
//  * @return
//  */
// public int deleteListByIds(@Param("ids") List<Long> ids);
//
//   /**
//  * 批量删除
//  * 根据id删除单船测试记录
//  * @param tStdShipRecordDTO
//  * @return
//  */
// public int deleteByCondition(PoundbillDTO tStdShipRecordDTO);
 
}

