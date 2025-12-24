package com.yy.ppm.flowable.bean.dto;
import com.yy.common.flowable.utils.DateUtils;
import com.yy.common.page.PageParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BpmTaskSearchDTO extends PageParameter implements Serializable {

    /**
     * 流程任务名
     */
    private String name;
    /**
     * 流程分类
     */
    private String category;
    /**
     * 流程定义的标识
     */
    private String processDefinitionKey; // 精准匹配
    /**
     * 审批状态
     */
    private Integer status; // 仅【已办】使用
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
