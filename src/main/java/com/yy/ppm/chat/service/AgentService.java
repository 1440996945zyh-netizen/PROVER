package com.yy.ppm.chat.service;

import com.yy.common.page.Pages;
import com.yy.ppm.chat.bean.dto.AgentDTO;
import com.yy.ppm.chat.bean.dto.AgentSearchDTO;
import com.yy.ppm.chat.bean.dto.SysAgentManageDTO;

import java.util.List;

/**
 * 智能体服务接口
 *
 * @author system
 */
public interface AgentService {

    /**
     * 获取可用智能体列表（状态为可用）
     *
     * @return 智能体列表
     */
    List<AgentDTO> getAgents();

    /**
     * 根据ID获取智能体（含 apiKey，供内部调用）
     *
     * @param id 智能体ID（字符串，与前端一致）
     * @return 智能体，不存在返回 null
     */
    AgentDTO getAgentById(String id);

    /**
     * 分页查询智能体列表（管理端）
     *
     * @param search 查询条件
     * @return 分页结果
     */
    Pages<SysAgentManageDTO> getList(AgentSearchDTO search);

    /**
     * 根据ID获取智能体详情（管理端）
     *
     * @param id 主键
     * @return 智能体详情，不存在返回 null
     */
    SysAgentManageDTO getByIdForManage(Long id);

    /**
     * 新增或修改智能体
     *
     * @param dto 智能体信息
     * @return 影响行数
     */
    int save(SysAgentManageDTO dto);

    /**
     * 根据ID删除智能体
     *
     * @param idList 主键列表
     * @return 影响行数
     */
    int deleteById(List<Long> idList);

    /**
     * 根据标识查询智能体
     * @param bill
     * @return
     */
    AgentDTO getAgentByAgentCode(String dataSource);
}
