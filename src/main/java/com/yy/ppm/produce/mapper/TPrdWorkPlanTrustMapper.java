package com.yy.ppm.produce.mapper;

import com.yy.ppm.produce.bean.dto.TPrdWorkPlanTrustDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @ClassName 工班计划指令Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年08月09日 12:19:00
 */
@Repository
public interface TPrdWorkPlanTrustMapper {

/**
  * 获取工班计划指令列表
  * @param workPlanId
  * @return
  */
 public List<TPrdWorkPlanTrustDTO> getList(@Param("workPlanId") Long workPlanId);

/**
  * 批量新增工班计划指令
  * @param list
  * @param loginUserId
  * @param loginUserName
  * @param now
  * @return
  */
 public int insertBatch(@Param("list") List<TPrdWorkPlanTrustDTO> list, @Param("loginUserId") Long loginUserId, @Param("loginUserName") String loginUserName, @Param("now") Date now);

 /**
  * 根据id删除工班计划指令
  * @param ids 主键
  * @return
  */
 public int deleteByWorkPlanIds(@Param("list") List<Long> ids);

}

