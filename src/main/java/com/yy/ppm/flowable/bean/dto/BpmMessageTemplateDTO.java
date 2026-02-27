package com.yy.ppm.flowable.bean.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;

/**
 * @Description BPM 消息推送模板 DTO
 */
@Getter
@AllArgsConstructor
@ToString
public class BpmMessageTemplateDTO{

    private static final long serialVersionUID = 1L;

    /**
     * 消息模板编码
     */
    private final String code;

    /**
     * 消息标题模板
     */
    private final String title;

    /**
     * 消息内容模板
     */
    private final String content;

    /**
     * 消息展示类型 (对应 WebSocketUtils中的mesShowType)
     */
    private final String mesShowType;
}
