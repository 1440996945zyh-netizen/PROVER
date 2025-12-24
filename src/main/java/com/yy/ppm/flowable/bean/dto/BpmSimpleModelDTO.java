package com.yy.ppm.flowable.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yy.common.flowable.common.KeyValue;
import com.yy.common.flowable.enums.BpmTaskCandidateStrategyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.flowable.bpmn.model.IOParameter;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 仿钉钉流程设计模型节点
 */
@Data
@Accessors(chain = true)
public class BpmSimpleModelDTO {
    /**
     * 模型节点编号
     */
    private String id;

    /**
     * 模型节点类型
     */
    private Integer type;

    /**
     * 模型节点名称
     */
    private String name;

    /**
     * 节点展示内容
     */
    private String showText;

    /**
     * 子节点
     * 补充说明：在该模型下，子节点有且仅有一个，不会有多个
     */
    private BpmSimpleModelDTO childNode;

    /**
     * 候选人策略
     */
    private Integer candidateStrategy; // 用于审批，抄送节点

    /**
     * 候选人参数
     */
    private String candidateParam; // 用于审批，抄送节点

    /**
     * 审批节点类型
     */
    private Integer approveType; // 用于审批节点

    /**
     * 多人审批方式
     */
    private Integer approveMethod; // 用于审批节点

    /**
     * 通过比例
     */
    private Integer approveRatio; // 通过比例，当多人审批方式为：多人会签(按通过比例) 需要设置

    /**
     * 表单权限
     */
    private List<Map<String, String>> fieldsPermission;

    /**
     * 操作按钮设置
     */
    private List<OperationButtonSetting> buttonsSetting;  // 用于审批节点

    /**
     * 是否需要签名
     */
    private Boolean signEnable;

    /**
     * 是否填写审批意见
     */
    private Boolean reasonRequire;

    /**
     * 跳过表达式
     */
    private String skipExpression;  // 用于审批节点

    /**
     * 审批节点拒绝处理
     */
    private RejectHandler rejectHandler;

    /**
     * 审批节点超时处理
     */
    private TimeoutHandler timeoutHandler;

    /**
     * 审批节点的审批人与发起人相同时，对应的处理类型
     */
    private Integer assignStartUserHandlerType;

    /**
     * 空处理策略
     */
    private AssignEmptyHandler assignEmptyHandler;

    /**
     * 创建任务监听器
     */
    private ListenerHandler taskCreateListener;
    /**
     * 指派任务监听器
     */
    private ListenerHandler taskAssignListener;
    /**
     * 完成任务监听器
     */
    private ListenerHandler taskCompleteListener;

    /**
     * 延迟器设置
     */
    private DelaySetting delaySetting;

    /**
     * 条件节点
     * 补充说明：有且仅有条件、并行、包容分支会使用
     */
    private List<BpmSimpleModelDTO> conditionNodes;

    /**
     * 条件节点设置
     * 仅用于条件节点 BpmSimpleModelNodeTypeEnum.CONDITION_NODE
     */
    private ConditionSetting conditionSetting;

    /**
     * 路由分支组
     */
    private List<RouterSetting> routerGroups;

    /**
     * 路由分支默认分支 ID
     * 由后端生成（不从前端传递）
     */
    @JsonIgnore
    private String routerDefaultFlowId; // 仅用于路由分支节点 BpmSimpleModelNodeType.ROUTER_BRANCH_NODE

    /**
     * 触发器节点设置
     */
    private TriggerSetting triggerSetting;

    /**
     * 附加节点 Id
     * 由后端生成（不从前端传递）
     */
    @JsonIgnore
    private String attachNodeId; // 目前用于触发器节点（HTTP 回调）。需要 UserTask 和 ReceiveTask（附加节点) 来完成

    /**
     * 子流程设置
     */
    private ChildProcessSetting childProcessSetting;

    /**
     * 任务监听器
     */
    @Valid
    @Data
    public static class ListenerHandler {

        /**
         * 是否开启任务监听器
         */
        @NotNull(message = "是否开启任务监听器不能为空")
        private Boolean enable;

        /**
         * 请求路径
         */
        private String path;

        /**
         * 请求头
         */
        private List<HttpRequestParam> header;

        /**
         * 请求体
         */
        private List<HttpRequestParam> body;

    }

    /**
     * HTTP 请求参数设置
     */
    @Data
    public static class HttpRequestParam {

        /**
         * 值类型
         */
        private Integer type;

        /**
         * 键
         */
        @NotEmpty(message = "键不能为空")
        private String key;

        /**
         * 值
         */
        @NotEmpty(message = "值不能为空")
        private String value;

    }

    /**
     * 审批节点拒绝处理策略
     */
    @Data
    public static class RejectHandler {

        /**
         * 拒绝处理类型
         */
        private Integer type;

        /**
         * 任务拒绝后驳回的节点 Id
         */
        private String returnNodeId;
    }

    /**
     * 审批节点超时处理策略
     */
    @Valid
    @Data
    public static class TimeoutHandler {

        /**
         * 是否开启超时处理
         */
        @NotNull(message = "是否开启超时处理不能为空")
        private Boolean enable;

        /**
         * 任务超时未处理的行为
         */
        private Integer type;

        /**
         * 超时时间
         */
        @NotEmpty(message = "超时时间不能为空")
        private String timeDuration;

        /**
         * 最大提醒次数
         */
        private Integer maxRemindCount;
    }

    /**
     * 空处理策略
     */
    @Data
    @Valid
    public static class AssignEmptyHandler {

        /**
         * 空处理类型
         */

        private Integer type;

        /**
         * 指定人员审批的用户编号数组
         */
        private List<Long> userIds;
    }

    /**
     * 操作按钮设置
     */
    @Data
    @Valid
    public static class OperationButtonSetting {

        // TODO @jason：是不是按钮的标识？id 会和数据库的 id 自增有点模糊，key 标识会更合理一点点哈。
        /**
         * 按钮 Id
         */
        private Integer id;

        /**
         * 显示名称
         */
        private String displayName;

        /**
         * 是否启用
         */
        private Boolean enable;
    }

    /**
     * 条件设置
     * 仅用于条件节点 BpmSimpleModelNodeTypeEnum.CONDITION_NODE
     */
    @Data
    @Valid
    public static class ConditionSetting {

        /**
         * 条件类型
         */

        private Integer conditionType;

        /**
         * 条件表达式
         */
        private String conditionExpression;

        /**
         * 是否默认条件
         */
        private Boolean defaultFlow;

        /**
         * 条件组
         */
        private ConditionGroups conditionGroups;
    }

    /**
     * 条件组
     */
    @Data
    @Valid
    public static class ConditionGroups {

        /**
         * 条件组下的条件关系是否为与关系
         */
        @NotNull(message = "条件关系不能为空")
        private Boolean and;

        /**
         * 条件组下的条件
         */
        @NotEmpty(message = "条件不能为空")
        private List<Condition> conditions;
    }

    /**
     * 条件
     */
    @Data
    @Valid
    public static class Condition {

        /**
         * 条件下的规则关系是否为与关系
         */
        @NotNull(message = "规则关系不能为空")
        private Boolean and;

        /**
         * 条件下的规则
         */
        @NotEmpty(message = "规则不能为空")
        private List<ConditionRule> rules;
    }

    /**
     * 条件规则
     */
    @Data
    @Valid
    public static class ConditionRule {

        /**
         * 运行符号
         */
        @NotEmpty(message = "运行符号不能为空")
        private String opCode;

        /**
         * 运算符左边的值,例如某个流程变量
         */
        @NotEmpty(message = "运算符左边的值不能为空")
        private String leftSide;

        /**
         * 运算符右边的值
         */
        @NotEmpty(message = "运算符右边的值不能为空")
        private String rightSide;
    }

    /**
     * 延迟器
     */
    @Data
    @Valid
    public static class DelaySetting {

        /**
         * 延迟时间类型
         */

        private Integer delayType;

        /**
         * 延迟时间表达式
         */
        @NotEmpty(message = "延迟时间表达式不能为空")
        private String delayTime;
    }

    /**
     * 路由分支
     */
    @Data
    @Valid
    public static class RouterSetting {

        /**
         * 节点 Id
         * 跳转到该节点
         */
        @NotEmpty(message = "节点 Id 不能为空")
        private String nodeId;

        /**
         * 条件类型
         */

        private Integer conditionType;

        /**
         * 条件表达式
         */
        private String conditionExpression;

        /**
         * 条件组
         */
        private ConditionGroups conditionGroups;
    }

    /**
     * 触发器节点配置
     */
    @Data
    @Valid
    public static class TriggerSetting {

        /**
         * 触发器类型
         */

        @NotNull(message = "触发器类型不能为空")
        private Integer type;

        /**
         * http 请求触发器设置
         */
        @Valid
        private HttpRequestTriggerSetting httpRequestSetting;

        /**
         * 流程表单触发器设置
         */
        private List<FormTriggerSetting> formSettings;

        /**
         * http 请求触发器设置
         */
        @Data
        public static class HttpRequestTriggerSetting {

            /**
             * 请求路径
             */
            @NotEmpty(message = "请求 URL 不能为空")
            @URL(message = "请求 URL 格式不正确")
            private String url;

            /**
             * 请求头参数设置
             */
            @Valid
            private List<HttpRequestParam> header;

            /**
             * 请求头参数设置
             */
            @Valid
            private List<HttpRequestParam> body;

            /**
             * 请求返回处理设置，用于修改流程表单值
             * <p>
             * key：表示要修改的流程表单字段名(name)
             * value：接口返回的字段名
             */
            private List<KeyValue<String, String>> response;

            /**
             * Http 回调请求，需要指定回调任务 Key，用于回调执行
             */
            private String callbackTaskDefineKey;

        }

        /**
         * 流程表单触发器设置
         */
        @Data
        public static class FormTriggerSetting {

            /**
             * 条件类型
             */

            private Integer conditionType;

            /**
             * 条件表达式
             */
            private String conditionExpression;

            /**
             * 条件组
             */
            private ConditionGroups conditionGroups;

            /**
             * 修改的表单字段
             */
            private Map<String, Object> updateFormFields;

            /**
             * 删除表单字段
             */
            private Set<String> deleteFields;
        }
    }

    /**
     * 子流程节点配置
     */
    @Data
    @Valid
    public static class ChildProcessSetting {

        /**
         * 被调用流程
         */
        @NotEmpty(message = "被调用流程不能为空")
        private String calledProcessDefinitionKey;

        /**
         * 被调用流程名称
         */
        @NotEmpty(message = "被调用流程名称不能为空")
        private String calledProcessDefinitionName;

        /**
         * 是否异步
         */
        @NotNull(message = "是否异步不能为空")
        private Boolean async;

        /**
         * 输入参数(主->子)
         */
        private List<IOParameter> inVariables;

        /**
         * 输出参数(子->主)
         */
        private List<IOParameter> outVariables;

        /**
         * 是否自动跳过子流程发起节点
         */
        @NotNull(message = "是否自动跳过子流程发起节点不能为空")
        private Boolean skipStartUserNode;

        /**
         * 子流程发起人配置
         */
        @NotNull(message = "子流程发起人配置不能为空")
        private StartUserSetting startUserSetting;

        /**
         * 超时设置
         */
        private TimeoutSetting timeoutSetting;

        /**
         * 多实例设置
         */
        private MultiInstanceSetting multiInstanceSetting;

        /**
         * 子流程发起人配置
         */
        @Data
        @Valid
        public static class StartUserSetting {

            /**
             * 子流程发起人类型
             */
            @NotNull(message = "子流程发起人类型")

            private Integer type;

            /**
             * 表单
             */
            private String formField;

            /**
             * 当子流程发起人为空时类型
             */
            @NotNull(message = "当子流程发起人为空时类型不能为空")

            private Integer emptyType;

        }

        /**
         * 超时设置
         */
        @Data
        @Valid
        public static class TimeoutSetting {

            /**
             * 是否开启超时设置
             */
            @NotNull(message = "是否开启超时设置不能为空")
            private Boolean enable;

            /**
             * 时间类型
             */

            private Integer type;

            /**
             * 时间表达式
             */
            private String timeExpression;

        }

        /**
         * 多实例设置
         */
        @Data
        @Valid
        public static class MultiInstanceSetting {

            /**
             * 是否开启多实例
             */
            @NotNull(message = "是否开启多实例不能为空")
            private Boolean enable;

            /**
             * 是否串行
             */
            @NotNull(message = "是否串行不能为空")
            private Boolean sequential;

            /**
             * 完成比例
             */
            @NotNull(message = "完成比例不能为空")
            private Integer approveRatio;

            /**
             * 多实例来源类型
             */
            @NotNull(message = "多实例来源类型不能为空")
            private Integer sourceType;

            /**
             * 多实例来源
             */
            @NotNull(message = "多实例来源不能为空")
            private String source;

        }

    }
}
