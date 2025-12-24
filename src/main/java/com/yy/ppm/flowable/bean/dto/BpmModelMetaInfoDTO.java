package com.yy.ppm.flowable.bean.dto;

import com.yy.common.flowable.common.KeyValue;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * BPM 流程 MetaInfo Response DTO
 * 主要用于 { Model#setMetaInfo(String)} 的存储
 *
 * 最终，它的字段和
 * {@link BpmProcessDefinitionDTO}
 * 是一致的
 *
 * @author 芋道源码
 */
@Data
@Accessors(chain = true)
public class BpmModelMetaInfoDTO {
    /**
     * 流程图标
     */
    private String icon;

    /**
     * 流程描述
     */
    private String description;

    /**
     * 流程类型，必填（对应BpmModelTypeEnum枚举）
     */
    private Integer type;

    /**
     * 表单类型，必填（对应BpmModelFormTypeEnum枚举）
     */
    private Integer formType;

    /**
     * 表单编号（formType为NORMAL时必须非空）
     */
    private Long formId;

    /**
     * 自定义表单的提交路径，使用Vue的路由地址（表单类型为CUSTOM时必须非空）
     */
    private String formCustomCreatePath;

    /**
     * 自定义表单的查看路径，使用Vue的路由地址（表单类型为CUSTOM时必须非空）
     */
    private String formCustomViewPath;

    /**
     * 是否可见，必填
     */
    private Boolean visible;

    /**
     * 可发起用户编号数组
     */
    private List<Long> startUserIds;

    /**
     * 可发起部门编号数组
     */
    private List<Long> startDeptIds;

    /**
     * 可管理用户编号数组，必填
     */
    private List<Long> managerUserIds;

    /**
     * 排序（创建时后端自动生成）
     */
    private Long sort;

    /**
     * 允许撤销审批中的申请
     */
    private Boolean allowCancelRunningProcess;

    /**
     * 允许审批人撤回任务
     */
    private Boolean allowWithdrawTask;

    /**
     * 流程ID规则
     */
    private ProcessIdRule processIdRule;

    /**
     * 自动去重类型（对应BpmAutoApproveTypeEnum枚举）
     */
    private Integer autoApprovalType;

    /**
     * 标题设置
     */
    private TitleSetting titleSetting;

    /**
     * 摘要设置
     */
    private SummarySetting summarySetting;

    /**
     * 流程前置通知设置
     */
    private HttpRequestSetting processBeforeTriggerSetting;

    /**
     * 流程后置通知设置
     */
    private HttpRequestSetting processAfterTriggerSetting;

    /**
     * 任务前置通知设置
     */
    private HttpRequestSetting taskBeforeTriggerSetting;

    /**
     * 任务后置通知设置
     */
    private HttpRequestSetting taskAfterTriggerSetting;

    /**
     * 自定义打印模板设置
     */
    @Valid
    private PrintTemplateSetting printTemplateSetting;

    /**
     * 流程ID规则
     */
    @Data
    @Valid
    public static class ProcessIdRule {

        /**
         * 是否启用，不能为空
         */
        private Boolean enable;

        /**
         * 前缀
         */
        private String prefix;

        /**
         * 中缀（精确到日、精确到时、精确到分、精确到秒）
         */
        private String infix;

        /**
         * 后缀
         */
        private String postfix;

        /**
         * 序列长度，不能为空
         */
        private Integer length;

    }

    /**
     * 标题设置
     */
    @Data
    @Valid
    public static class TitleSetting {

        /**
         * 是否自定义，不能为空
         */
        private Boolean enable;

        /**
         * 标题
         */
        private String title;

    }

    /**
     * 摘要设置
     */
    @Data
    @Valid
    public static class SummarySetting {

        /**
         * 是否自定义，不能为空
         */
        private Boolean enable;

        /**
         * 摘要字段数组
         */
        private List<String> summary;

    }

    /**
     * http请求通知设置
     */
    @Data
    public static class HttpRequestSetting {

        /**
         * 请求路径，不能为空且格式需为URL
         */
        private String url;

        /**
         * 请求头参数设置
         */
        @Valid
        private List<BpmSimpleModelDTO.HttpRequestParam> header;

        /**
         * 请求体参数设置
         */
        @Valid
        private List<BpmSimpleModelDTO.HttpRequestParam> body;

        /**
         * 请求返回处理设置，用于修改流程表单值
         * <p>
         * key：表示要修改的流程表单字段名(name)
         * value：接口返回的字段名
         */
        private List<KeyValue<String, String>> response;

    }

    /**
     * 自定义打印模板设置
     */
    @Data
    public static class PrintTemplateSetting {

        /**
         * 是否自定义打印模板，不能为空
         */
        private Boolean enable;

        /**
         * 打印模板
         */
        private String template;

    }
}
