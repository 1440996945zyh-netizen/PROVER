package com.yy.ppm.chat.bean.dto;

import java.util.List;
import java.util.Map;

/**
 * 聊天消息请求DTO
 * @author system
 */
public class ChatMessageRequestDTO {
    
    /**
     * 输入参数
     */
    private Map<String, Object> inputs;
    
    /**
     * 查询内容
     */
    private String query;
    
    /**
     * 响应模式：streaming 或 blocking
     */
    private String responseMode;
    
    /**
     * 会话ID
     */
    private String conversationId;
    
    /**
     * 用户标识
     */
    private String user;
    
    /**
     * 文件列表
     */
    private List<FileInfo> files;

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    /**
     * 文件信息
     */
    public static class FileInfo {
        /**
         * 文件类型：image, document等
         */
        private String type;
        
        /**
         * 传输方式：remote_url, local_file等
         */
        private String transferMethod;
        
        /**
         * 文件URL
         */
        private String url;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTransferMethod() {
            return transferMethod;
        }

        public void setTransferMethod(String transferMethod) {
            this.transferMethod = transferMethod;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
