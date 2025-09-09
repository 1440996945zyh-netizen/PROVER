package com.yy.ppm.midCore.bean.po;

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
     * 发送者账户
     */
    private String senderAccount;

    /**
     * 接受者账户
     */
    private String receiverAccount;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String mesType;

    /**
     * 消息类型消息划分
     */
    private String contentType;

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
     * 业务ID
     */
    private Long businessId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 组织单位id
     */
    private Long deptId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 消息展示类型
     */
    private String mesShowType;
}
