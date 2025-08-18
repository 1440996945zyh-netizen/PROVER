package com.yy.ppm.produce.service;



import java.util.List;

import com.yy.ppm.produce.bean.dto.CompanyClassResDTO;
import com.yy.ppm.produce.bean.dto.TPrdAttendanceDTO;
import com.yy.ppm.produce.bean.po.TPrdAttendanceUserPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;

/**
 * @ClassName 出勤点名Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年08月15日
 */
public interface TPrdAttendanceService {

    /**
     * 获取出勤点名(可编辑列表用）
     *
     * @param searchDTO
     * @return 对象列表
     */
    List<TPrdAttendanceDTO> getList(TPrdAttendanceDTO searchDTO);

    /**
     * 获取出勤点名通过id
     *
     * @param searchDTO
     * @return 对象列表
     */
    TPrdAttendanceDTO getAttendanceById(TPrdAttendanceDTO searchDTO);

    /**
     * 新增出勤点民
     *
     * @param inseretDTO
     * @return 对象列表
     */
    boolean insert(TPrdAttendanceDTO inseretDTO);

    /**
     * 修改出勤点名
     *
     * @param attendanceDTO
     * @return 对象列表
     */
    boolean updateAttendance(TPrdAttendanceDTO attendanceDTO);

    /**
     * 根据作业工班查出勤人员
     *
     * @param deptId
     * @return 对象列表
     */
    List<TPrdAttendanceUserPO> getAttendaceUserByDeptId(Long deptId);

    /**
     * 根据作业工班查人
     *
     * @param deptId
     * @return 对象列表
     */
    List<SysUserDTO> getUserByDeptId(Long deptId);

    /**
     * 根据作业公司查作业工班
     *
     * @param companyId
     * @return 对象列表
     */
    List<SysDeptDTO> getDeptByCompany(Long companyId);

    CompanyClassResDTO getCompanyClass();
    CompanyClassResDTO getCompanyDeptClass();

    /**
     * 删除出勤点名信息
     * @param id
     * @return
     */
	Integer deleteById(Long id);

    List<SysUserDTO> getUserNew();
}

