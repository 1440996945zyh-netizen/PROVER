package com.yy.ppm.chat.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.ppm.chat.bean.dto.AgentDTO;
import com.yy.ppm.chat.bean.dto.AgentSearchDTO;
import com.yy.ppm.chat.bean.dto.SysAgentManageDTO;
import com.yy.ppm.chat.bean.po.SysAgentPO;
import com.yy.ppm.chat.mapper.SysAgentMapper;
import com.yy.ppm.chat.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能体服务实现（从 SYS_AGENT 表读取）
 *
 * @author system
 */
@Service
public class AgentServiceImpl implements AgentService {

    private static final String STATUS_ENABLED = "1";

    @Autowired
    private SysAgentMapper sysAgentMapper;

    @Override
    public List<AgentDTO> getAgents() {
        List<SysAgentPO> list = sysAgentMapper.listByStatus(STATUS_ENABLED);
        return list.stream().map(this::toAgentDTO).collect(Collectors.toList());
    }

    @Override
    public AgentDTO getAgentById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        Long pk;
        try {
            pk = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;
        }
        SysAgentPO po = sysAgentMapper.getById(pk);
        return po == null ? null : toAgentDTO(po);
    }

    @Override
    public Pages<SysAgentManageDTO> getList(AgentSearchDTO search) {
        Pages<SysAgentPO> poPages = PageHelperUtils.limit(search, () -> sysAgentMapper.getList(search));
        Pages<SysAgentManageDTO> result = new Pages<>();
        result.setTotalNum(poPages.getTotalNum());
        result.setTotalPageNum(poPages.getTotalPageNum());
        result.setPageSize(poPages.getPageSize());
        result.setPageNum(poPages.getPageNum());
        if (poPages.getPages() != null) {
            result.setPages(poPages.getPages().stream().map(this::toManageDTO).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public SysAgentManageDTO getByIdForManage(Long id) {
        if (id == null) return null;
        SysAgentPO po = sysAgentMapper.getById(id);
        return po == null ? null : toManageDTO(po);
    }


    @Autowired
    private Snowflake snowflake;


    @Override
    public int save(SysAgentManageDTO dto) {
        if (dto == null) return 0;
        if (dto.getId() == null) {
            SysAgentPO po = new SysAgentPO();
            po.setAgentName(dto.getAgentName());
            po.setIntroduction(dto.getIntroduction());
            po.setAvatar(dto.getAvatar());
            po.setStatus(dto.getStatus() != null ? dto.getStatus() : STATUS_ENABLED);
            po.setApiKey(dto.getApiKey());
            po.setAgentSort(dto.getAgentSort() != null ? dto.getAgentSort() : 0);
            po.setType(dto.getType());
            po.setAgentCode(dto.getAgentCode());
            po.setId(snowflake.nextId());
            return sysAgentMapper.insert(po);
        } else {
            SysAgentPO po = sysAgentMapper.getById(dto.getId());
            if (po == null) return 0;
            po.setAgentName(dto.getAgentName());
            po.setIntroduction(dto.getIntroduction());
            po.setAvatar(dto.getAvatar());
            po.setStatus(dto.getStatus());
            po.setApiKey(dto.getApiKey());
            po.setAgentSort(dto.getAgentSort() != null ? dto.getAgentSort() : 0);
            po.setType(dto.getType());
            po.setAgentCode(dto.getAgentCode());
            return sysAgentMapper.update(po);
        }
    }

    @Override
    public int deleteById(List<Long> idList) {
        if (idList == null || idList.isEmpty()) return 0;
        int count = 0;
        for (Long id : idList) {
            count += sysAgentMapper.deleteById(id);
        }
        return count;
    }

    @Override
    public AgentDTO getAgentByAgentCode(String dataSource) {
        AgentDTO agentDTO = sysAgentMapper.getAgentByAgentCode(dataSource);
        return agentDTO;
    }

    private AgentDTO toAgentDTO(SysAgentPO po) {
        AgentDTO dto = new AgentDTO();
        dto.setId(po.getId() != null ? String.valueOf(po.getId()) : null);
        dto.setAvatar(po.getAvatar());
        dto.setName(po.getAgentName());
        dto.setDescription(po.getIntroduction());
        dto.setApiKey(po.getApiKey());
        return dto;
    }

    private SysAgentManageDTO toManageDTO(SysAgentPO po) {
        SysAgentManageDTO dto = new SysAgentManageDTO();
        dto.setId(po.getId());
        dto.setAgentName(po.getAgentName());
        dto.setIntroduction(po.getIntroduction());
        dto.setAvatar(po.getAvatar());
        dto.setStatus(po.getStatus());
        dto.setApiKey(po.getApiKey());
        dto.setAgentSort(po.getAgentSort());
        dto.setType(po.getType());
        dto.setAgentCode(po.getAgentCode());
        return dto;
    }
}
