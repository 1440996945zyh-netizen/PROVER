package com.yy.ppm.chat.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 智能体查询 DTO
 *
 * @author system
 */
@Getter
@Setter
public class AgentSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 智能体名称（模糊） */
    private String agentName;
    /** 状态(0:不可用，1:可用) */
    private String status;
}
