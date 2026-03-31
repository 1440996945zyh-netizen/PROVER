package com.yy.ppm.chat.bean.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 聊天历史响应DTO
 * @author system
 */
public class ChatHistoryResponseDTO implements Serializable {
    private List<ConversationDTO> conversations;
    private List<MessageDTO> messages;

    public List<ConversationDTO> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationDTO> conversations) {
        this.conversations = conversations;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
