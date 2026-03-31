package com.yy.ppm.chat.bean.dto;

import java.io.Serializable;

/**
 * 聊天消息响应DTO
 * @author system
 */
public class ChatMessageResponseDTO implements Serializable {

    /**
     * 响应内容
     */
    private String content;

    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 原始响应数据
     */
    private Object data;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
