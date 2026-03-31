package com.yy.ppm.chat.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * 消息DTO
 * @author system
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageDTO implements Serializable {
    private String id;
    private String conversation_id;
    private Object inputs;
    private String query;
    private String answer;
    private List<Object> message_files;
    private Object feedback;
    private List<Object> retriever_resources;
    private Long created_at;
    private List<Object> agent_thoughts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public Object getInputs() {
        return inputs;
    }

    public void setInputs(Object inputs) {
        this.inputs = inputs;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Object> getMessage_files() {
        return message_files;
    }

    public void setMessage_files(List<Object> message_files) {
        this.message_files = message_files;
    }

    public Object getFeedback() {
        return feedback;
    }

    public void setFeedback(Object feedback) {
        this.feedback = feedback;
    }

    public List<Object> getRetriever_resources() {
        return retriever_resources;
    }

    public void setRetriever_resources(List<Object> retriever_resources) {
        this.retriever_resources = retriever_resources;
    }

    public Long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public List<Object> getAgent_thoughts() {
        return agent_thoughts;
    }

    public void setAgent_thoughts(List<Object> agent_thoughts) {
        this.agent_thoughts = agent_thoughts;
    }
}
