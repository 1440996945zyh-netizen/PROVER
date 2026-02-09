package com.yy.ppm.equipment.service.impl;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.equipment.bean.dto.InspectionPlanTaskDTO;
import com.yy.ppm.equipment.bean.dto.MaintainTaskDTO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskItemPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;
import com.yy.ppm.equipment.bean.po.MaintainTaskItemPO;
import com.yy.ppm.equipment.bean.po.MaintainTaskPO;
import com.yy.ppm.equipment.mapper.ECheckTaskMapper;
import com.yy.ppm.equipment.mapper.MaintainTaskMapper;
import com.yy.ppm.equipment.service.ECheckTaskService;
import com.yy.ppm.equipment.service.MaintainTaskService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class MaintainTaskServiceImpl implements MaintainTaskService {

    @Autowired
    private MaintainTaskMapper mapper;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private SecurityUtils securityUtils;


    @Override
    public Pages<MaintainTaskPO> getList(MaintainTaskDTO searchDTO, PageParameter parameter) {
        Pages<MaintainTaskPO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public Pages<MaintainTaskPO> getListAPP(MaintainTaskDTO searchDTO, PageParameter parameter) {
        // 权限控制：管理员能查看全部，否则只能查看同部门的数据
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            // 判断是否为超级管理员
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            // 如果不是管理员，强制使用当前用户的部门ID进行过滤
            if (!isAdmin) {
                searchDTO.setInspectorId(userInfo.getId());
            }
        }
        Pages<MaintainTaskPO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getListAPP(searchDTO);
        });
        return pages;
    }

    @Override
    public Pages<MaintainTaskItemPO> getById(MaintainTaskDTO searchDTO, PageParameter parameter) {
        Pages<MaintainTaskItemPO> pages = PageHelperUtils.limit(parameter, () -> {
            return mapper.getById(searchDTO);
        });
        return pages;
    }

    @Override
    public List<Map<String, Object>> getInstitutionById(MaintainTaskDTO searchDTO) {
        List<Map<String, Object>> list = mapper.getInstitutionById(searchDTO);
        return list;
    }

    @Override
    public List<Map<String, Object>> getUnitById(MaintainTaskDTO searchDTO) {
        List<Map<String, Object>> list = mapper.getUnitById(searchDTO);
        return list;
    }

    @Override
    public List<MaintainTaskItemPO> getTaskItemById(MaintainTaskDTO searchDTO) {
        List<MaintainTaskItemPO> list = mapper.getTaskItemById(searchDTO);
        if (!CollectionUtils.isEmpty(list)) {
            for (MaintainTaskItemPO taskItemPO : list) {
                List<SysFileDTO> fileInfo = sysFileService.getBusFiles(taskItemPO.getId(), "EQUIPMENT_TASK");
                taskItemPO.setMattachmentInfoList(fileInfo);

            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void save(List<MaintainTaskItemPO> list) {
        list.stream().forEach(v -> {
            if ("0".equals(v.getIsAbnormal())) {
                throw new BusinessRuntimeException("存在未作业任务");
            }
        });

        mapper.updateTaskItemList(list);

        // 查询是否还存在未检任务
        int count = mapper.getTaskItemCountById(list.get(0).getEquipTaskId());
        Integer status = count > 0 ? 1 : 2;
        mapper.updateTaskById(list.get(0).getEquipTaskId(),status);

        list.stream().forEach(v -> {
            List<Long> fileIds = new ArrayList<>();
            for (SysFileDTO file:v.getMattachmentInfoList()) {
                fileIds.add(file.getId());
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, v.getId());
        });
    }
}
