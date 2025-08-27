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

    TEST,;

    public static String match(String code) {
        return Arrays.stream(ConstantsTypeEnum.values()).map(Enum::name).filter(v1 -> v1.equals(code)).findFirst().orElse(StringUtils.EMPTY);
    }
}
