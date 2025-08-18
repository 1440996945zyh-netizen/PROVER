package com.yy.ppm.produce.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondManResultType;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondaryDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划派工表（二次配工）(TPrdDispatchSecondary)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月30日 18:16:00
 */
@Repository
public interface TPrdDispatchSecondaryMapper {

/**
  * 获取作业计划派工表（二次配工）列表
  * @param tPrdDispatchSecondarySearchVo
  * @return
  */
 public List<TPrdDispatchSecondaryDTO> getList(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchVo);
 public List<TPrdDispatchSecondaryDTO> getListLabor(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchVo);

/**
  * 导出作业计划派工表（二次配工）列表
  * @param tPrdDispatchSecondarySearchDTO
  * @return
  */
 public List<TPrdDispatchSecondaryDTO> exportList(TPrdDispatchSecondarySearchDTO tPrdDispatchSecondarySearchDTO);

 /**
  * 根据id获取作业计划派工表（二次配工）
  * @param id 主键
  * @return
  */
 public TPrdDispatchSecondaryDTO getById(Long id);

 /**
  * 新增作业计划派工表（二次配工）
  * @param tPrdDispatchSecondaryDTO
  * @return
  */
 @Edit
 public int insert(TPrdDispatchSecondaryDTO tPrdDispatchSecondaryDTO);

/**
  * 批量新增作业计划派工表（二次配工）
  * @param list
  * @param loginUserId
  * @param loginUserName
  * @param now
  * @return
  */
 public int insertBatch(@Param("list") List<TPrdDispatchSecondaryDTO> list, @Param("loginUserId") Long loginUserId, @Param("loginUserName") String loginUserName, @Param("now") Date now);


 /**
  * 修改作业计划派工表（二次配工）
  * @param tPrdDispatchSecondaryDTO
  * @return
  */
 @Edit
 public int update(TPrdDispatchSecondaryDTO tPrdDispatchSecondaryDTO);



 /**
  * 根据id删除作业计划派工表（二次配工）
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);


 /**
  * 根据id删除作业计划派工表（二次配工）
  * @param ids 主键
  * @return
  */
 public int deleteByIds(@Param("list") List<Long> ids);

 /**
  * 根据条件删除作业计划派工表（二次配工）
  * @param workPlanId
  * @param dispatchType
 * @param deptNo
 * @param canDispatchDept 
  * @return
  */
 public int deleteDispatch(@Param("workPlanId") Long workPlanId, @Param("dispatchType") String dispatchType, @Param("deptNo") String deptNo, @Param("canDispatchDept")  String canDispatchDept);
 public int deleteDispatchLabor(@Param("workPlanId") Long workPlanId, @Param("dispatchType") String dispatchType);

 /**
  * 查询劳务列表
  * @param workPlanId
  * @return
  */
/* List<SysUserDTO> getLaborList(Long workPlanId);*/
 List<TPrdDispatchSecondManResultType> getLaborList(Long workPlanId,@Param("deptNo") String deptNo);
 List<TPrdDispatchSecondManResultType> getByworkplanId(Long workPlanId);


 List<Map<String, String>> getCompanyByDeptId(@Param("deptId") Long deptId);

 /**
  *
  * @param shipVoyageItemIds
  * @return
  */
 List<Map<String,Object>> getWorkTicket(@Param("shipVoyageItemIds")List<Long> shipVoyageItemIds);

 /**
  * 校验是否进行了分配
  * @param o
  * @return
  */
 List<Map<String, Object>> getWorkTicketInfo(@Param("workPlanId") Long o);

 List<TPrdDispatchSecondManResultType> getLaborDeptList();

 List<TPrdDispatchSecondManResultType> getLaborGroupList(String deptParentId);
}

