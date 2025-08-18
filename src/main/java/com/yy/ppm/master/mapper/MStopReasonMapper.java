package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MStopReasonDTO;
import com.yy.ppm.master.bean.dto.MStopReasonSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 船舶停时原因维护(MStopReason)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月05日 17:21:00
 */
@Repository
public interface MStopReasonMapper {

/**
  * 获取船舶停时原因维护列表
  * @param mStopReasonSearchVo
  * @return
  */
 public Page<MStopReasonDTO> getList(MStopReasonSearchDTO mStopReasonSearchVo);

/**
  * 导出船舶停时原因维护列表
  * @param mStopReasonSearchDTO
  * @return
  */
 public List<MStopReasonDTO> exportList(MStopReasonSearchDTO mStopReasonSearchDTO);

 /**
  * 根据id获取船舶停时原因维护
  * @param id 主键
  * @return
  */
 public MStopReasonDTO getById(Long id);

 /**
  * 新增船舶停时原因维护
  * @param mStopReasonDTO
  * @return
  */
 @Edit
 public int insert(MStopReasonDTO mStopReasonDTO);

 /**
  * 修改船舶停时原因维护
  * @param mStopReasonDTO
  * @return
  */
 @Edit
 public int update(MStopReasonDTO mStopReasonDTO);


 /**
  * 根据id删除船舶停时原因维护
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);
}

