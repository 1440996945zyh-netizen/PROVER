package com.yy.common.flowable.enums;

import cn.hutool.core.util.ArrayUtil;
import com.yy.common.flowable.common.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户任务的审批类型枚举
 *
 */
@Getter
@AllArgsConstructor
public enum BpmUserTaskRejectHandlerTypeEnum implements ArrayValuable<Integer> {
    FINISH_PROCESS_INSTANCE(1, "终止流程"),
    RETURN_USER_TASK(2, "驳回到指定任务节点");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(BpmUserTaskRejectHandlerTypeEnum::getType).toArray(Integer[]::new);

    public static BpmUserTaskRejectHandlerTypeEnum typeOf(Integer type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
