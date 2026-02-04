package com.yy.ppm.flowable.bean.dto;
import com.yy.common.flowable.utils.DateUtils;
import com.yy.common.page.PageParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程实例分页
 */
@Data
public class BpmProcessInstanceSearchDTO extends PageParameter implements Serializable {
    /**
     * 流程名称
     */
    private String name;
    /**
     *流程定义的标识
     */
    private String processDefinitionKey; // 精准匹配
    /**
     *流程实例的状态
     */
    private Integer status;
    /**
     *流程分类
     */
    private String category;
    /**
     *创建时间
     */
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
    /**
     *结束时间
     */
    private LocalDateTime[] endTime;
    /**
     *发起用户编号
     */
    private Long startUserId; // 注意，只有在【流程实例】菜单，才使用该参数
    /**
     *动态表单字段查询
     */
    private String formFieldsParams; // SpringMVC 在 get 请求下，无法方便的定义 Map 类型的参数，所以通过 String 接收后，逻辑里面转换

}
