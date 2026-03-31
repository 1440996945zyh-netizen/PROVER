package com.yy.ppm.chat.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatFileDTO implements Serializable {
    /**
     * (uuid) ID
     */
    private String id;
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件大小
     */
    private String size;
    /**
     * mime_type
     */
    private String mime_type;
    /**
     * (uuid) 上传人 ID
     */
    private String created_by;
    /**
     * (timestamp) 上传时间
     */
    private String created_at;

    /**
     * 响应数据
     */
    private String eventData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
}
