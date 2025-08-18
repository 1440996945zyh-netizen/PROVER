package com.yy.ppm.produce.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.CompanyClassResDTO;
import com.yy.ppm.produce.bean.dto.TPrdAttendanceDTO;
import com.yy.ppm.produce.bean.po.TPrdAttendanceUserPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;

/**
 * @ClassName 作业计划一次派工表(TPrdDispatch)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:22:00
 */
@Repository
public interface TPrdAttendanceMapper {

/**
  * 出勤点名列表
  * @param attendanceDTO
  * @return
  */
List<TPrdAttendanceDTO> getList(TPrdAttendanceDTO attendanceDTO);

 /**
  * 根据id获取出勤点名
  * @param id 主键
  * @return
  */
 TPrdAttendanceDTO getById(Long id);

 /**
  * 根据id获取出勤点名人员
  * @param attendanceId
  * @return
  */
 List<TPrdAttendanceUserPO> getUserByAttendanceId(Long attendanceId);

 /**
  * 新增出勤点名
  * @param attendanceDTO
  * @return
  */
 @Edit
 int insert(TPrdAttendanceDTO attendanceDTO);

 /**
  * 新增作业计划一次派工表
  * @param list
  * @return
  */
 int insertBatch(@Param("list") List<TPrdAttendanceUserPO> list);

 /**
  * 修改出勤点名
  * @param attendanceDTO
  * @return
  */
 @Edit
 int update(TPrdAttendanceDTO attendanceDTO);

 /**
  * 修改出勤点名
  * @param list
  * @return
  */
 @Edit
 int updateAttendanceUser(List<TPrdAttendanceUserPO> list);


 /**
  * 根据id删除考勤人员信息
  * @param id 主键
  * @return
  */
 public int deleteById(Long id);

 /**
  * 根据出勤点名id删除出勤人员信息
  * @param attendanceId
  * @return
  */
 int deleteByAttendanceIds(@Param("attendanceId") Long attendanceId);

 /**
  * 修改时新增考勤人员信息
  * @param list
  * @return
  */
 int insertBatchWhileUpdate(@Param("list") List<TPrdAttendanceUserPO> list);

 /**
  * 根据作业工班查出勤人员
  *
  * @param userIds
  * @return 对象列表
  */
 List<TPrdAttendanceUserPO> getAttendaceUserByDeptId(@Param("userIds")List<Long> userIds);

 /**
  * 获取用户列表
  * @param sysUserSearchDTO 用户查询DTO
  * @return
  */
 Page<SysUserDTO> getUserList(SysUserSearchDTO sysUserSearchDTO);
 List<SysUserDTO> getUserListNew(@Param("userId") Long userId);

 List<SysDeptDTO> getDeptByCompany(Long companyId);

 /**
  * 出勤点名列表
  * @param attendanceDTO
  * @return
  */
int deleteOldAttendance(TPrdAttendanceDTO attendanceDTO);

 List<SysDeptDTO> getDeptByCompanyLever2(Long companyId);
 List<SysDeptDTO> getDeptByCompanyLever3(Long companyId);
 ArrayList<SysDeptDTO> getDeptByLoginUserId(@Param("userId") Long loginUserId);

 List<CompanyClassResDTO> getCompanyClassByUserId(Long loginUserId);

	int deleteByPId(@Param("id") Long id);


 List<TPrdAttendanceDTO> getAttendaceUserListByTimeAndDept(TPrdAttendanceDTO searchDto);

 SysDeptDTO getDeptLevel2ByDeptId(@Param("deptId") Long deptId);

 List<SysUserDTO> getUserName();
}

