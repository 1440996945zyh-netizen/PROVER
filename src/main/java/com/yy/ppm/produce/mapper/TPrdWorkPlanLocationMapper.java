package com.yy.ppm.produce.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanLocationDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @ClassName 作业计划位置表(TPrdWorkPlanLocation)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:22:00
 */
@Repository
public interface TPrdWorkPlanLocationMapper {


/**
  * 作业计划位置表列表
  * @param workPlanId
  * @Param direction
  * @return
  */
 public List<TPrdWorkPlanLocationDTO> getList(@Param("workPlanId") Long workPlanId, @Param("direction") String direction);


 /**
  *  查询作业计划位置
  * @param workPlanId
  * @return
  */
 public List<Long> getRegionIds(@Param("workPlanId") Long workPlanId, @Param("direction") String dispatchType);

 /**
  * 根据id获取作业计划位置表
  * @param id 主键
  * @return
  */
 public TPrdWorkPlanLocationDTO getById(Long id);

 /**
  * 新增作业计划位置表
  * @param tPrdWorkPlanLocationDTO
  * @return
  */
 @Edit
 public int insert(TPrdWorkPlanLocationDTO tPrdWorkPlanLocationDTO);

 /**
  * 批量新增作业计划位置表
  * @param list
  * @return
  */
 public int insertBatch(@Param("list") List<TPrdWorkPlanLocationDTO> list, @Param("loginUserId") Long loginUserId, @Param("loginUserName") String loginUserName, @Param("now") Date now);


 /**
  * 修改作业计划位置表
  * @param tPrdWorkPlanLocationDTO
  * @return
  */
 @Edit
 public int update(TPrdWorkPlanLocationDTO tPrdWorkPlanLocationDTO);

 /**
  * 根据id删除作业计划一次派工表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

 /**
  * 根据计划id删除作业计划位置表
  * @param ids 主键
  * @return
  */
 public int deleteByWorkPlanIds(@Param("list") List<Long> ids);

 List<TPrdWorkPlanLocationDTO> getWorkPlanLocationList(@Param("ids") List<Long> ids);
}

