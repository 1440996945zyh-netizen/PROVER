package com.yy.ppm.produce.service.impl;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.yy.common.enums.CommonEnum;
import com.yy.common.util.DateUtil;
import com.yy.common.util.DateUtils;
import com.yy.ppm.common.mapper.PublicMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.Page;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.produce.bean.dto.CompanyClassResDTO;
import com.yy.ppm.produce.bean.dto.TPrdAttendanceDTO;
import com.yy.ppm.produce.bean.po.TPrdAttendanceUserPO;
import com.yy.ppm.produce.mapper.TPrdAttendanceMapper;
import com.yy.ppm.produce.service.TPrdAttendanceService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;
import com.yy.ppm.system.mapper.SysUserMapper;
import cn.hutool.core.lang.Snowflake;
import org.springframework.util.StringUtils;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 作业计划表(TPrdWorkPlan)ServiceImpl
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@Service
public class TPrdAttendanceServiceImpl implements TPrdAttendanceService {


    @Resource
    private TPrdAttendanceMapper tPrdAttendanceMapper;

    @Resource
    private CommonService commonService;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    public PublicMapper publicMapper;

    /**
     * 获取出勤点名(可编辑列表用）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public List<TPrdAttendanceDTO> getList(TPrdAttendanceDTO searchDTO) {
        searchDTO.setLoginUserId(securityUtils.getLoginUserId());
        return tPrdAttendanceMapper.getList(searchDTO);
    }

    /**
     * 获取出勤点名通过id
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public TPrdAttendanceDTO getAttendanceById(TPrdAttendanceDTO searchDTO) {
        TPrdAttendanceDTO mainObj = tPrdAttendanceMapper.getById(searchDTO.getId());
        List<TPrdAttendanceUserPO> userByAttendanceId = tPrdAttendanceMapper.getUserByAttendanceId(mainObj.getId());
        if (!CollectionUtils.isEmpty(userByAttendanceId)){
            mainObj.setAttendanceUserPOList(userByAttendanceId);
        }
        return mainObj;
    }

    /**
     * 新增出勤点民
     *
     * @param inseretDTO
     * @return 对象列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insert(TPrdAttendanceDTO inseretDTO) {


        List<TPrdAttendanceDTO> dataList = tPrdAttendanceMapper.getAttendaceUserListByTimeAndDept(inseretDTO);
        List<Long> deptIds = dataList.stream().map(TPrdAttendanceDTO::getDeptId).collect(Collectors.toList());
        SysDeptDTO insertDept = tPrdAttendanceMapper.getDeptLevel2ByDeptId(inseretDTO.getDeptId());
        //同一部门在同一班次下不能进行重复点名
        deptIds.forEach(o->{
            SysDeptDTO dataDept = tPrdAttendanceMapper.getDeptLevel2ByDeptId(o);
            if(insertDept.getId().equals(dataDept.getId())){
                throw new BusinessRuntimeException(insertDept.getDeptName()+"已经在"+
                                                    DateUtils.formatDate(inseretDTO.getWorkDate(),
                                                    CommonEnum.DateFormatType.E_1.getCode()) +" "+inseretDTO.getClassName()+"点名，无需重复点名");
            }
        });


        tPrdAttendanceMapper.deleteOldAttendance(inseretDTO);

        List<TPrdAttendanceUserPO> attendanceUserList = inseretDTO.getAttendanceUserPOList();
        if (!CollectionUtils.isEmpty(attendanceUserList)){
            for (int i = 0; i < attendanceUserList.size() - 1; i++) {
                for (int j = attendanceUserList.size() - 1; j > i; j--) {
                    if (attendanceUserList.get(j).getUserName().equals(attendanceUserList.get(i).getUserName())) {
                        throw new BusinessRuntimeException("出勤信息存在重复数据");
                    }
                }
            }
        }

        inseretDTO.setId(snowflake.nextId());
        List<TPrdAttendanceUserPO> attendanceUserPOList = inseretDTO.getAttendanceUserPOList();
        if (!CollectionUtils.isEmpty(attendanceUserPOList)){
            for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUserPOList) {
                tPrdAttendanceUserPO.setId(snowflake.nextId());
                tPrdAttendanceUserPO.setAttendanceId(inseretDTO.getId());
                tPrdAttendanceUserPO.setCreateBy(securityUtils.getLoginUserId());
                tPrdAttendanceUserPO.setCreateByName(securityUtils.getLoginUserName());
                tPrdAttendanceUserPO.setCreateTime(new Date());
            }
            tPrdAttendanceMapper.insertBatch(attendanceUserPOList);
        }
        return tPrdAttendanceMapper.insert(inseretDTO) > 0;
    }

    /**
     * 修改出勤点名  先删后插
     *
     * @param attendanceDTO
     * @return 对象列表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateAttendance(TPrdAttendanceDTO attendanceDTO) {
        if (!CollectionUtils.isEmpty(attendanceDTO.getAttendanceUserPOList())){
            List<TPrdAttendanceUserPO> attendanceUserList = attendanceDTO.getAttendanceUserPOList();
            if (!CollectionUtils.isEmpty(attendanceUserList)){
                for (int i = 0; i < attendanceUserList.size() - 1; i++) {
                    for (int j = attendanceUserList.size() - 1; j > i; j--) {
                        if (attendanceUserList.get(j).getUserName().equals(attendanceUserList.get(i).getUserName())) {
                            throw new BusinessRuntimeException("出勤信息存在重复数据");
                        }
                    }
                }
            }
            List<TPrdAttendanceUserPO> attendanceUserPOList = attendanceDTO.getAttendanceUserPOList();
            tPrdAttendanceMapper.deleteByAttendanceIds(attendanceDTO.getId());
            for (TPrdAttendanceUserPO tPrdAttendanceUserPO : attendanceUserPOList) {
                tPrdAttendanceUserPO.setId(snowflake.nextId());
                tPrdAttendanceUserPO.setAttendanceId(attendanceDTO.getId());
                tPrdAttendanceUserPO.setCreateBy(securityUtils.getLoginUserId());
                tPrdAttendanceUserPO.setCreateByName(securityUtils.getLoginUserName());
                tPrdAttendanceUserPO.setCreateTime(new Date());
                tPrdAttendanceUserPO.setUpdateBy(securityUtils.getLoginUserId());
                tPrdAttendanceUserPO.setUpdateByName(securityUtils.getLoginUserName());
                tPrdAttendanceUserPO.setUpdateTime(new Date());
            }
            tPrdAttendanceMapper.insertBatchWhileUpdate(attendanceDTO.getAttendanceUserPOList());
        }
        return tPrdAttendanceMapper.update(attendanceDTO) > 0;
    }

    /**
     * 根据作业工班查出勤人员
     *
     * @param deptId
     * @return 对象列表
     */
    @Override
    public List<TPrdAttendanceUserPO> getAttendaceUserByDeptId(Long deptId) {
        SysUserSearchDTO sysUserSearchDTO = new SysUserSearchDTO();
        sysUserSearchDTO.setDeptId(deptId);
        sysUserSearchDTO.setIsLabor("0");
        Page<SysUserDTO> list = sysUserMapper.getList(sysUserSearchDTO);
        if (CollectionUtils.isEmpty(list)){
            throw new RuntimeException("该部门下没有人");
        }
        List<Long> userIds = new ArrayList<>();
        for (SysUserDTO sysUserDTO : list) {
            userIds.add(sysUserDTO.getId());
        }
        List<TPrdAttendanceUserPO> attendaceUserByDeptId = tPrdAttendanceMapper.getAttendaceUserByDeptId(userIds);
        //根据人员的id去查询出勤的人员
        return attendaceUserByDeptId;
    }

    /**
     * 根据作业工班查人
     *
     * @param deptId
     * @return 对象列表
     */
    @Override
    public List<SysUserDTO> getUserByDeptId(Long deptId) {
        SysUserSearchDTO sysUserSearchDTO = new SysUserSearchDTO();
        sysUserSearchDTO.setDeptId(deptId);
        sysUserSearchDTO.setIsLabor("0");
        sysUserSearchDTO.setStatus("1");
        Page<SysUserDTO> list = tPrdAttendanceMapper.getUserList(sysUserSearchDTO);
        /*if (CollectionUtils.isEmpty(list)){
            throw new RuntimeException("该部门下没有人");
        }*/
        return list;
    }

    @Override
    public List<SysUserDTO> getUserNew() {
        List<SysUserDTO> result = new ArrayList<>();
        result = tPrdAttendanceMapper.getUserListNew(securityUtils.getLoginUserId());
        List<SysUserDTO> userNameList = tPrdAttendanceMapper.getUserName();
        result.addAll(userNameList);
        return result;
    }

    /**
     * 根据作业公司查作业工班
     *
     * @param companyId
     * @return 对象列表
     */
    @Override
    public List<SysDeptDTO> getDeptByCompany(Long companyId) {

        //获取当前用户所在的上级部门信息
        List<CompanyClassResDTO>  tmpList=  tPrdAttendanceMapper.getCompanyClassByUserId(securityUtils.getLoginUserId());
        if (CollectionUtils.isEmpty(tmpList)){
            throw new BusinessRuntimeException("没有查询到用户公司部门信息");
        }
        ArrayList<SysDeptDTO> sysDeptDTOS = new ArrayList<>();
        String deptNo = "";

        // 查询当前用户所在的部门 CODE add by zcc 23/10/20
        List<Map<String, Object>> userInfoAndDeptInfoList = publicMapper.getUserInfoAndDeptInfo(securityUtils.getLoginUserId());
        if(!CollectionUtils.isEmpty(userInfoAndDeptInfoList)) {
            if(userInfoAndDeptInfoList.get(0).get("isSuperadmin") != null
                    && "1".equals(userInfoAndDeptInfoList.get(0).get("isSuperadmin").toString())) {
                List<SysDeptDTO> deptListLever2 = tPrdAttendanceMapper.getDeptByCompanyLever2(companyId);
                List<SysDeptDTO> deptListLever3 = tPrdAttendanceMapper.getDeptByCompanyLever3(companyId);
                sysDeptDTOS.addAll(deptListLever2);
                sysDeptDTOS.addAll(deptListLever3);
            } else {
                CompanyClassResDTO resDTO = new CompanyClassResDTO();
                boolean flag = false;
                for (CompanyClassResDTO item : tmpList) {
                    if("1".equals(item.getDeptLevel())){
                        if(!String.valueOf(companyId).equals(item.getId())){
                            flag = true;
                        }
                    }
                }

                if(flag){
                    return sysDeptDTOS;
                }
                sysDeptDTOS = tPrdAttendanceMapper.getDeptByLoginUserId(securityUtils.getLoginUserId());
            }
        }
        return sysDeptDTOS;
    }

    @Override
    public CompanyClassResDTO getCompanyClass() {
        List<CompanyClassResDTO>  tmpList=  tPrdAttendanceMapper.getCompanyClassByUserId(securityUtils.getLoginUserId());
        if (CollectionUtils.isEmpty(tmpList)){
            throw new BusinessRuntimeException("没有查询到用户公司部门信息");
        }
        CompanyClassResDTO resDTO = new CompanyClassResDTO();
        tmpList.forEach(item->{
            if("1".equals(item.getDeptLevel())){
                resDTO.setCompanyName(item.getDeptName());
                resDTO.setCompanyId(item.getId());
            }
            if("3".equals(item.getDeptLevel())){
                resDTO.setClassName(item.getDeptName());
                resDTO.setClassId(item.getId());
            }
            if("4".equals(item.getDeptLevel())){
                resDTO.setClassName(item.getDeptName());
                resDTO.setClassId(item.getId());
            }
        });
        return resDTO;
    }

    @Override
    public CompanyClassResDTO getCompanyDeptClass() {
        List<CompanyClassResDTO>  tmpList=  tPrdAttendanceMapper.getCompanyClassByUserId(securityUtils.getLoginUserId());
        if (CollectionUtils.isEmpty(tmpList)){
            throw new BusinessRuntimeException("没有查询到用户公司部门信息");
        }
        CompanyClassResDTO resDTO = new CompanyClassResDTO();
        tmpList.forEach(item->{
            if("1".equals(item.getDeptLevel())){
                resDTO.setCompanyName(item.getDeptName());
                resDTO.setCompanyId(item.getId());
            }
            if("2".equals(item.getDeptLevel())){
                if (StringUtils.isEmpty(resDTO.getClassId())) {
                    resDTO.setClassName(item.getDeptName());
                    resDTO.setClassId(item.getId());
                    resDTO.setLevel("2");
                }
                resDTO.setCompanyDeptId(item.getId());
                resDTO.setCompanyDeptName(item.getDeptName());
            }
            if("4".equals(item.getDeptLevel())){
                resDTO.setClassName(item.getDeptName());
                resDTO.setClassId(item.getId());
                resDTO.setLevel("4");
            }

        });
        return resDTO;
    }

	@Override
	@Transactional
	public Integer deleteById(Long id) {
		
		tPrdAttendanceMapper.deleteByAttendanceIds(id);
		
		return tPrdAttendanceMapper.deleteByPId(id);
	}
}

