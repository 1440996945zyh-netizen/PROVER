package com.yy.ppm.chat.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.chat.bean.dto.AgentDTO;
import com.yy.ppm.chat.bean.dto.AgentSearchDTO;
import com.yy.ppm.chat.bean.po.SysAgentPO;

import java.util.List;

/**
 * 智能体信息表(SYS_AGENT) Mapper
 *
 * @author system
 */
public interface SysAgentMapper {

    /**
     * 按状态查询智能体列表
     *
     * @param status 状态(1:可用)
     * @return 智能体列表
     */
    List<SysAgentPO> listByStatus(String status);

    /**
     * 分页查询智能体列表（管理端）
     *
     * @param search 查询条件
     * @return 分页列表
     */
    Page<SysAgentPO> getList(AgentSearchDTO search);

    /**
     * 根据ID查询智能体
     *
     * @param id 主键
     * @return 智能体
     */
    SysAgentPO getById(Long id);

    /**
     * 获取下一个ID（用于新增，不依赖序列）
     *
     * @return 下一个ID
     */
    Long getNextId();

    /**
     * 新增智能体
     *
     * @param po 智能体
     * @return 影响行数
     */
    @Edit
    int insert(SysAgentPO po);

    /**
     * 修改智能体
     *
     * @param po 智能体
     * @return 影响行数
     */
    @Edit
    int update(SysAgentPO po);

    /**
     * 根据ID删除智能体
     *
     * @param id 主键
     * @return 影响行数
     */
    @Edit
    int deleteById(Long id);

    AgentDTO getAgentByAgentCode(String dataSource);
}
