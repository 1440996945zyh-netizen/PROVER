package com.yy.ppm.produce.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipDaynigttplanPO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanLocationDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;
import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划表(TPrdWorkPlan)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@Repository
public interface TPrdWorkPlanMapper {

 /**
  * 获取作业计划表列表
  * @param tPrdWorkPlanSearchVo
  * @return
  */
 public List<TPrdWorkPlanDTO> getWorkPlanList(TPrdWorkPlanSearchDTO tPrdWorkPlanSearchVo);
 public List<TPrdWorkPlanDTO> getLgWorkPlanList(TPrdWorkPlanSearchDTO tPrdWorkPlanSearchVo);
 public List<Map<String,String>> getLgMacNum(List<Long> ids);

/**
  * 获取作业计划表列表
  * @param tPrdWorkPlanSearchVo
  * @return
  */
 public List<TPrdWorkPlanDTO> getList(TPrdWorkPlanSearchDTO tPrdWorkPlanSearchVo);

 /**
  * 获取配机配工信息
  * @param tPrdWorkPlanSearchVo
  * @return
  */
 public List<TPrdDispatchDTO> getPrdDispatch(TPrdWorkPlanSearchDTO tPrdWorkPlanSearchVo);

 /**
  * 获取垛位信息
  * @param tPrdWorkPlanSearchVo
  * @return
  */
 List<TPrdWorkPlanLocationDTO> getWorkPlanLocationList(TPrdWorkPlanSearchDTO tPrdWorkPlanSearchVo);

 /**
  * 获取货主货代信息
  * @param tPrdWorkPlanSearchVo
  * @return
  */
 List<TBusTrustCargoDTO> getTrustCargoList(TPrdWorkPlanSearchDTO tPrdWorkPlanSearchVo);


 List<TBusCargoInfoDTO> getCargoInfoByIds(@Param("cargoInfoIds") List<Long> cargoInfoIds);


 /**
  * 导出作业计划表列表
  * @param tPrdWorkPlanSearchDTO
  * @return
  */
 public List<TPrdWorkPlanDTO> exportList(TPrdWorkPlanSearchDTO tPrdWorkPlanSearchDTO);

 /**
  * 根据id获取作业计划表
  * @param id 主键
  * @return
  */
 public TPrdWorkPlanDTO getById(Long id);

 /**
  * 新增作业计划表
  * @param tPrdWorkPlanDTO
  * @return
  */
 @Edit
 public int insert(TPrdWorkPlanDTO tPrdWorkPlanDTO);

 /**
  * 批量新增作业计划表
  * @param list
  * @return
  */
 public int insertBatch(@Param("list") List<TPrdWorkPlanDTO> list, @Param("loginUserId") Long loginUserId, @Param("loginUserName") String loginUserName, @Param("now") Date now);

 /**
  * 修改作业计划表
  * @param tPrdWorkPlanDTO
  * @return
  */
 @Edit
 public int update(TPrdWorkPlanDTO tPrdWorkPlanDTO);

 /**
  * 修改作业计划表
  * @param tPrdWorkPlanDTO
  * @return
  */
 @Edit
 public int updateDispatch(TPrdWorkPlanDTO tPrdWorkPlanDTO);

 @Edit
 public int updateDispatchPeople(@Param("list") List<Long> ids,@Param("dto") TPrdWorkPlanDTO tPrdWorkPlanDTO);

 /**
  * 修改作业计划表
  * @param tPrdWorkPlanDTO
  * @return
  */
 @Edit
 public int updateNotNull(TPrdWorkPlanDTO tPrdWorkPlanDTO);

 /**
  * 批量修改作业计划表
  * @param list
  * @return
  */
 public int updateShipPlanBatch(@Param("list") List<TPrdWorkPlanDTO> list, @Param("loginUserId") Long loginUserId, @Param("loginUserName") String loginUserName, @Param("now") Date now);

 /**
  * 批量修改作业计划表
  * @param list
  * @return
  */
 public int updateOtherPlanBatch(@Param("list") List<TPrdWorkPlanDTO> list, @Param("loginUserId") Long loginUserId, @Param("loginUserName") String loginUserName, @Param("now") Date now);

 /**
  * 根据id删除作业计划表
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);


 /**
  * 根据id删除作业计划一次派工表
  * @param ids 主键
  * @return
  */
 public int deleteByIds(@Param("ids") List<Long> ids);

 /**
  * 审批、删除时的验证
  * @param ids
  * @return
  */
 public int getCheckCount(@Param("list") List<Long> ids, @Param("checkType") String checkType);
 public int getCheckCountById(@Param("id") Long id, @Param("checkType") String checkType);

 public int getTallyCountByPlanId(@Param("planId") Long planId);

 /**
  * 批量审批
  * @param ids
  * @return
  */
 public int updateStatusByIds(@Param("list") List<Long> ids, @Param("status") String status, @Param("updateType") String updateType,  @Param("loginUserId") Long loginUserId, @Param("loginUserName") String loginUserName, @Param("now") Date now);

 List<TPrdWorkPlanPO> getProcessName();

 List<Long> getByWorkProcess(@Param("ids") List<Long> trustIds,@Param("workDate") String workDate,@Param("classCode") String classCode );

	public List<TDisShipDaynigttplanPO> getDayNigttPlan(@Param("trustId") Long trustId, @Param("planDate") String planDate);

    List<Map<String, String>> getNormalWorkProcess();

 List<TPrdWorkPlanDTO> getByIds(List<Long> ids);

   List<TPrdWorkPlanDTO> getStatusCountById(@Param("id") Long id,@Param("status") String status);
   @Edit
   void updateTrustStatus(@Param("list") List<TBusTrustDTO> tmpWorkIds);

 List<TPrdWorkPlanDTO> getNewIdsByOldsIds(@Param("list") List<Long> ids);

 List<Map<String, String>> workProcessType(@Param("planType") Long planType);

 List<Map<String, String>> workProcessType2(@Param("planType") Long planType,@Param("dictValue")  String dictValue);

public List<Map<String, Object>> getFlowStatus(@Param("idList") List<Long> idList);

public List<Map<String, Object>> getFixedStatus(@Param("idList") List<Long> idList);

public List<Map<String, Object>> getLaborStatus(@Param("idList") List<Long> idList);

public List<Map<String, Object>> getWorkCompanyCode(@Param("idList") List<Long> idList);

 List<TPrdWorkPlanDTO> getLastWorkPlanList(TPrdWorkPlanSearchDTO searchDTO);

 Integer getYardTallyInfo(@Param("id") Long id);

 Integer getTicketInfo(@Param("id") Long id);

 TPrdWorkPlanDTO getWorkPlanById(@Param("id") Long id);

 List<ResponsePopupTrustDTO> getJSGDayNightWorkPlanList(TPrdWorkPlanSearchDTO searchDTO);

 List<TPrdWorkPlanDTO> getJSGWorkPlanList(TPrdWorkPlanSearchDTO searchDTO);

 List<Map<String, Object>> getMassIdsWithPlanId(Long planId);

 Map<String, Object> getPlanTypeById(Long planId);
}

