package com.yy.ppm.dispatch.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordDTO;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordExcelDTO;
import com.yy.ppm.dispatch.bean.dto.TDisTugServiceRecordSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;

/**
 * @ClassName 拖轮服务记录(TDisTugServiceRecord)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@Repository
public interface TDisTugServiceRecordMapper {

/**
  * 获取拖轮服务记录列表
  * @param tDisTugServiceRecordSearchVo
  * @return
  */
 public Page<TDisTugServiceRecordDTO> getList(TDisTugServiceRecordSearchDTO tDisTugServiceRecordSearchVo);

/**
  * 导出拖轮服务记录列表
  * @param tDisTugServiceRecordSearchDTO
  * @return
  */
 public Cursor<TDisTugServiceRecordExcelDTO> exportList(TDisTugServiceRecordSearchDTO tDisTugServiceRecordSearchDTO);


 public Page<TDisTugServiceRecordDTO> getTugList(TDisTugServiceRecordSearchDTO tDisTugServiceRecordSearchDTO);

 /**
  * 根据id获取拖轮服务记录
  * @param id 主键
  * @return
  */
 public TDisTugServiceRecordDTO getById(Long id);

 /**
  * 新增拖轮服务记录
  * @param tDisTugServiceRecordDTO
  * @return
  */
 @Edit
 public int insert(TDisTugServiceRecordDTO tDisTugServiceRecordDTO);

 /**
  * 修改拖轮服务记录
  * @param tDisTugServiceRecordDTO
  * @return
  */
 @Edit
 public int update(TDisTugServiceRecordDTO tDisTugServiceRecordDTO);


 /**
  * 根据id删除拖轮服务记录
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

