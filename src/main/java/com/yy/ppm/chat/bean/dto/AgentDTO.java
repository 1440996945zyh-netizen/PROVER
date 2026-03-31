package com.yy.ppm.chat.bean.dto;

import java.io.Serializable;

/**
 * 智能体DTO
 * @author system
 */
public class AgentDTO implements Serializable {

    /**
     * 唯一标识ID
     */
    private String id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 名称
     */
    private String name;

    /**
     * 介绍
     */
    private String description;

    /**
     * API Key（内部使用，不返回给前端）
     */
    private String apiKey;

    public AgentDTO() {
    }

    public AgentDTO(String id, String avatar, String name, String description, String apiKey) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
        this.description = description;
        this.apiKey = apiKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
