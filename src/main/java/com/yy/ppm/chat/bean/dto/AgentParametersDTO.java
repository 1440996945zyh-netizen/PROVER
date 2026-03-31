package com.yy.ppm.chat.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 智能体参数DTO
 * @author system
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentParametersDTO implements Serializable {
    private String openingStatement; // 开场白
    private List<String> suggestedQuestions; // 开场推荐问题列表

    /**
     * 反序列化时从 opening_statement 读取（从 Dify API）
     */
    @JsonProperty("opening_statement")
    public void setOpeningStatement(String openingStatement) {
        this.openingStatement = openingStatement;
    }

    /**
     * 序列化时返回 openingStatement（给前端）
     */
    @JsonProperty("openingStatement")
    public String getOpeningStatement() {
        return openingStatement;
    }

    /**
     * 反序列化时从 suggested_questions 读取（从 Dify API）
     */
    @JsonProperty("suggested_questions")
    public void setSuggestedQuestions(List<String> suggestedQuestions) {
        this.suggestedQuestions = suggestedQuestions;
    }

    /**
     * 序列化时返回 suggestedQuestions（给前端）
     */
    @JsonProperty("suggestedQuestions")
    public List<String> getSuggestedQuestions() {
        return suggestedQuestions;
    }
}
