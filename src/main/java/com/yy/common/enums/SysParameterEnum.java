package com.yy.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统参数字典枚举类
 */
@Getter
@AllArgsConstructor
public enum SysParameterEnum {

    /**
     * WebSocket 消息推送总开关
     * 取值范围: Y (推送) / N (不推送)
     */
    WEBSOCKET_MESSAGE_SWITCH("WEBSOCKET_MESSAGE_SWITCH", "WebSocket消息推送总开关", "Y"),

    ;

    /**
     * 参数编码 (对应数据库的 PARAM_CD)
     */
    private final String code;

    /**
     * 参数名称说明
     */
    private final String desc;

    /**
     * 系统默认值 (当数据库未配置此参数时，代码中可作为兜底值使用)
     */
    private final String defaultValue;

}