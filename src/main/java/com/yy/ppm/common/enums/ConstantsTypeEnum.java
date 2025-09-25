package com.yy.ppm.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-12-08 11:38
 */
@Getter
public enum ConstantsTypeEnum {

    CARGO_RATE_STATUS,
    WORK_PLAN_STATUS,
    END_DAY_TYPE,
    FREE_ITEM_STATUS,
    BUS_TRUST_STATUS,
    SHIP_STATUS,
    RESHIPMENT_TYPE,
    DEPT_TYPE,
    CONTRACT_STATUS,
    SHIP_PLAN_STATUS,
    COMPANY_TYPE,
    RATE_STATUS,
    REVIEW_STATUS,
    SUNDRY_RATE_STATUS,
    SHIP_COST_TYPE,
    USER_STATUS,
    START_DAY_TYPE,
    SHIP_VOYAGE_STATUS,
    TRANSFER_CARGO_OWNER_FLAG,
    LOAD_FLAG,
    CUSTOMER_STATUS,
    PRO_COST_TYPE,
    AD_SEARCH_COL_TYPE;

    public static String match(String code) {
        return Arrays.stream(ConstantsTypeEnum.values()).map(Enum::name).filter(v1 -> v1.equals(code)).findFirst().orElse(StringUtils.EMPTY);
    }
}
