package com.yy.ppm.chat.bean.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 智能体维护 DTO（管理端）
 *
 * @author system
 */
@Getter
@Setter
public class SysAgentManageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;
    /** 智能体名称 */
    private String agentName;
    /** 简介 */
    private String introduction;
    /** 头像(存储路径或URL) */
    private String avatar;
    /** 状态(0:不可用，1:可用) */
    private String status;
    /** 智能体API Key(内部使用) */
    private String apiKey;
    /** 显示排序(数值越小越靠前) */
    private Integer agentSort;
    /** 类型 1：对话 2：应用 */
    private String type;
    /** 智能体标识 */
    private String agentCode;
}
