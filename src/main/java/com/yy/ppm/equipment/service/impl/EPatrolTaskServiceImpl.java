package com.yy.ppm.equipment.service.impl;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.EPatrolTaskSearchDTO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskPO;
import com.yy.ppm.equipment.bean.po.EPatrolTaskSubPO;
import com.yy.ppm.equipment.mapper.EPatrolTaskMapper;
import com.yy.ppm.equipment.service.EPatrolTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.service.SysFileService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 巡检任务 Service 实现
 *
 * @author system
 */
@Service
public class EPatrolTaskServiceImpl implements EPatrolTaskService {

    @Autowired
    private EPatrolTaskMapper mapper;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private SysFileService sysFileService;

    @Override
    public Pages<EPatrolTaskPO> getTaskList(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> mapper.selectTaskList(searchDTO));
    }

    @Override
    public Pages<EPatrolTaskSubPO> getSubTaskPage(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> mapper.selectSubTaskPage(searchDTO));
    }

    @Override
    public Pages<EPatrolTaskPO> getListAPP(EPatrolTaskSearchDTO searchDTO, PageParameter parameter) {
        UserInfo userInfo = securityUtils.getUserInfo();
        if (userInfo != null) {
            boolean isAdmin = "1".equals(userInfo.getIsSuperadmin());
            if (!isAdmin) {
                searchDTO.setPatrolId(userInfo.getId());
            }
        }
        return PageHelperUtils.limit(parameter, () -> mapper.getListAPP(searchDTO));
    }

    @Override
    public List<Map<String, Object>> getEquipmentById(EPatrolTaskSearchDTO searchDTO) {
        return mapper.selectEquipmentByTaskId(searchDTO);
    }

    @Override
    public List<EPatrolTaskSubPO> getTaskItemById(EPatrolTaskSearchDTO searchDTO) {
        List<EPatrolTaskSubPO> list = mapper.selectTaskItemById(searchDTO);
        if (!CollectionUtils.isEmpty(list)) {
            for (EPatrolTaskSubPO taskItemPO : list) {
                List<SysFileDTO> fileInfo = sysFileService.getBusFiles(taskItemPO.getId(), "EQUIPMENT_PATROL_TASK");
                taskItemPO.setMattachmentInfoList(fileInfo);
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void save(List<EPatrolTaskSubPO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.stream().forEach(v -> {
            if (v.getIsAbnormal() != null && v.getIsAbnormal() == 0) {
                throw new BusinessRuntimeException("存在待检任务");
            }
        });

        mapper.updateTaskSubList(list);

        // 查询是否还存在待检任务
        int count = mapper.getUncheckedSubTaskCount(list.get(0).getParentId());
        Integer status = count > 0 ? 1 : 2;
        mapper.updateTaskStatus(list.get(0).getParentId(), status);

        list.stream().forEach(v -> {
            List<Long> fileIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(v.getMattachmentInfoList())) {
                for (SysFileDTO file : v.getMattachmentInfoList()) {
                    fileIds.add(file.getId());
                }
            }
            // 附件保存
            sysFileService.saveFileBusRelation(fileIds, v.getId());
        });
    }
}
