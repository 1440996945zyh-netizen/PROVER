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
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskItemPO;
import com.yy.ppm.equipment.bean.po.InspectionPlanTaskPO;
import com.yy.ppm.equipment.mapper.ECheckTaskMapper;
import com.yy.ppm.equipment.service.ECheckTaskService;
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
public class ECheckTaskServiceImpl implements ECheckTaskService {

    @Autowired
    private ECheckTaskMapper eCheckTaskMapper;
    @Resource
    private SysFileService sysFileService;
    @Resource
    private SecurityUtils securityUtils;


    @Override
    public Pages<InspectionPlanTaskPO> getList(InspectionPlanTaskDTO searchDTO, PageParameter parameter) {
        Pages<InspectionPlanTaskPO> pages = PageHelperUtils.limit(parameter, () -> {
            return eCheckTaskMapper.getList(searchDTO);
        });
        return pages;
    }

    @Override
    public Pages<InspectionPlanTaskPO> getListAPP(InspectionPlanTaskDTO searchDTO, PageParameter parameter) {
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
        Pages<InspectionPlanTaskPO> pages = PageHelperUtils.limit(parameter, () -> {
            return eCheckTaskMapper.getListAPP(searchDTO);
        });
        return pages;
    }

    @Override
    public Pages<InspectionPlanTaskItemPO> getById(InspectionPlanTaskDTO searchDTO, PageParameter parameter) {
        Pages<InspectionPlanTaskItemPO> pages = PageHelperUtils.limit(parameter, () -> {
            return eCheckTaskMapper.getById(searchDTO);
        });
        return pages;
    }

    @Override
    public List<Map<String, Object>> getInstitutionById(InspectionPlanTaskDTO searchDTO) {
        List<Map<String, Object>> list = eCheckTaskMapper.getInstitutionById(searchDTO);
        return list;
    }

    @Override
    public List<Map<String, Object>> getUnitById(InspectionPlanTaskDTO searchDTO) {
        List<Map<String, Object>> list = eCheckTaskMapper.getUnitById(searchDTO);
        return list;
    }

    @Override
    public List<InspectionPlanTaskItemPO> getTaskItemById(InspectionPlanTaskDTO searchDTO) {
        List<InspectionPlanTaskItemPO> list = eCheckTaskMapper.getTaskItemById(searchDTO);
        if (!CollectionUtils.isEmpty(list)) {
            for (InspectionPlanTaskItemPO taskItemPO : list) {
                List<SysFileDTO> fileInfo = sysFileService.getBusFiles(taskItemPO.getId(), "EQUIPMENT_TASK");
                taskItemPO.setMattachmentInfoList(fileInfo);

            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void save(List<InspectionPlanTaskItemPO> list) {
        list.stream().forEach(v -> {
            if ("0".equals(v.getIsAbnormal())) {
                throw new BusinessRuntimeException("存在未检任务");
            }
        });

        eCheckTaskMapper.updateTaskItemList(list);

        // 查询是否还存在未检任务
        int count = eCheckTaskMapper.getTaskItemCountById(list.get(0).getEquipTaskId());
        Integer status = count > 0 ? 1 : 2;
        eCheckTaskMapper.updateTaskById(list.get(0).getEquipTaskId(),status);

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
