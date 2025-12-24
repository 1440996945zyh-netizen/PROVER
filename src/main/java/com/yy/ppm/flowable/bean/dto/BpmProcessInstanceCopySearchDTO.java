package com.yy.ppm.flowable.bean.dto;

import com.yy.common.page.PageParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.yy.common.flowable.utils.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 流程实例抄送
 */
@Schema(description = "管理后台 - 的分页 Request VO")
@Data
public class BpmProcessInstanceCopySearchDTO extends PageParameter implements Serializable {

    /**
     * 流程名称
     */
    private String processInstanceName;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    /**
     * 登陆人
     */
    private Long userId;

}
