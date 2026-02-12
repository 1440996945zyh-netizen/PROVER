package com.yy.ppm.applet.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 入港公告(TStdEnterPortNotice)SearchDTO
 * @Description TODO
 * @createTime 2023年12月01日 14:08:00
 */
@Data
public class TStdEnterPortNoticeSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 453124623717253432L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 公告标题
     */
    private String title;
    /**
     * 公告类型
     */
    private String type;
    /**
     * 状态
     */
    private String status;
    /**
     * 内容
     */
    private String content;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

