package com.yy.ppm.middleware.bean.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@ToString
public class WsOfflineMessagePO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 发送者id
     */
    private Long senderId;

    /**
     * 接受者id
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 是否发送
     */
    private String isSent;

    /**
     * 创建时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    private Long createBy;
}
