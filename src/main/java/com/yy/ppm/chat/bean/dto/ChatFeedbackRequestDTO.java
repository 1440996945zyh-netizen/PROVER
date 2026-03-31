package com.yy.ppm.chat.bean.dto;

/**
 * 消息反馈请求DTO
 */
public class ChatFeedbackRequestDTO {

    /**
     * 点赞 like, 点踩 dislike, 撤销点赞 null
     */
    private String rating;

    /**
     * 反馈的具体内容
     */
    private String content;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

