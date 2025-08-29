package com.yy.ppm.common.enums;

import lombok.Getter;

/**
 * @Author linqi
 * @Description 并发锁钥前缀
 * @Date 2023-07-06 10:43
 */
@Getter
public enum DistributedLockKeyPrefixEnum {

    /**
     * - 禁止已预约车队进行修改和删除；
     * - 控制预约的件数重量不能超过指派车队的量；
     */
    ASSIGN_FLEET_AND_TRUST_TRADE_RESERVATION_KEY("ASSIGN_FLEET_AND_TRUST_TRADE_RESERVATION_KEY_"),

    /**
     * - 保证船舶状态的一致性；
     */
    SHIPVOYAGE_KEY("SHIPVOYAGE_KEY_"),

    /**
     * - 禁止已关联指令的船舶进行修改删除和作废；
     */
    SHIPVOYAGE_BUS_TRUST_KEY("SHIPVOYAGE_BUS_TRUST_KEY_"),

    /**
     * - 保证作业票数据的一致性；
     */
    WORK_TICKET_KEY("WORK_TICKET_KEY_"),
    /**
     * - 保证数据上报船舶动态数据的一致性；
     */
    SHIP_UPLOAD_DYNAMIC_KEY("SHIP_UPLOAD_DYNAMIC_KEY_"),

    /**
     * - 保证数据上报船数据的一致性；
     */
    SHIP_UPLOAD_KEY("SHIP_UPLOAD_KEY_"),

    /**
     * - 保证场存数据的一致性；
     */
    PORT_STORAGE_KEY("PORT_STORAGE_KEY_"),

    /**
     * - 保证票货转存时剩余货权量数据的一致性；
     */
    CARGO_INFO_TRANSFER_KEY("CARGO_INFO_TRANSFER_KEY"),

    /**
     * - 保证理货数据的一致性；
     */
    TALLY_KEY("TALLY_KEY_"),;

    private final String code;

    DistributedLockKeyPrefixEnum(String code) {
        this.code = code;
    }
}
