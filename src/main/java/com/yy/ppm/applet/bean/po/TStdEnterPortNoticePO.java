package com.yy.ppm.applet.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 入港公告(TStdEnterPortNotice)PO
 * @Description
 * @createTime 2023年12月01日 14:08:00
 */
@Data
public class TStdEnterPortNoticePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 186693667800025539L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 公告标题
     */
    @NotEmpty(message = "公告标题不能为空")
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
    @NotEmpty(message = "内容不能为空")
    private String content;

    /**
     * 生效开始时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;

    /**
     * 生效截至时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;

    private String roleCode;

    private String roleName;

}

