package com.yy.ppm.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description 字典枚举
 * @Date 2023-10-10 14:14
 */
@Getter
public enum DictTypeEnum {
    DEPT_LEVEL("DEPT_LEVEL","按级别查询组织架构"),
    TEST("TEST", "测试案例");


    private final String type;

    private final String name;

    DictTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String match(String type) {
        return Arrays.stream(DictTypeEnum.values()).map(DictTypeEnum::getType).filter(v1 -> v1.equals(type)).findFirst().orElse(StringUtils.EMPTY);
    }

    @Getter
    public enum DictDataEnum {

        SOURCE_TARGET_TYPE_01("SOURCE_TARGET_TYPE", "01", "船舶"),
        SOURCE_TARGET_TYPE_02("SOURCE_TARGET_TYPE", "02", "驳船"),
        SOURCE_TARGET_TYPE_03("SOURCE_TARGET_TYPE", "03", "汽车"),
        SOURCE_TARGET_TYPE_04("SOURCE_TARGET_TYPE", "04", "火车"),
        SOURCE_TARGET_TYPE_05("SOURCE_TARGET_TYPE", "05", "场地"),
        UNIT_TON("UNIT", "ton", "吨"),
        UNIT_HOUR("UNIT", "hour", "小时"),
        UNIT_DAY("UNIT", "day", "天"),
        UNIT_DAYNRT("UNIT", "DAYNRT", "天数*净吨"),
        UNIT_VOL("UNIT", "VOL", "体积"),
        UNIT_NRT("UNIT", "NRT", "船舶净吨"),
        UNIT_HEAD("UNIT", "HEAD", "人数"),
        UNIT_MOVE("UNIT", "MOVE", "移动"),
        UNIT_STOW("UNIT", "STOW", "存储(周)"),
        UNIT_FANG("UNIT", "FANG", "方"),
        UNIT_BDAY("UNIT", "BDAY", "靠泊天数"),
        UNIT_STOD("UNIT", "STOD", "存储(天)"),
        UNIT_YEAR("UNIT", "YEAR", "年度"),
        UNIT_TEU("UNIT", "TEU", "TEU"),
        UNIT_SCN("UNIT", "SCN", "航次"),
        UNIT_KWH("UNIT", "KWH", "度"),
        UNIT_WEEK("UNIT", "WEEK", "周期"),
        UNIT_WG("UNIT", "WG", "吨,最大量"),
        UNIT_HHR("UNIT", "HHR", "半小时"),
        UNIT_HP("UNIT", "HP", "马力"),
        UNIT_LOA("UNIT", "LOA", "全长(船)"),
        UNIT_QTY("UNIT", "QTY", "件数"),
        UNIT_UNIT("UNIT", "UNIT", "次"),
        UNIT_ACRE("UNIT", "ACRE", "亩数"),
        UNIT_RMB("UNIT", "RMB", "元"),
        UNIT_ITEM("UNIT", "item", "个"),
        UNIT_DMT("UNIT", "dmt", "吨/天"),
        STOP_REASON_CLASS_1("STOP_REASON_CLASS", "1", "港方原因"),
        STOP_REASON_CLASS_2("STOP_REASON_CLASS", "2", "船方原因"),
        STOP_REASON_CLASS_3("STOP_REASON_CLASS", "3", "货方原因"),
        STOP_REASON_CLASS_4("STOP_REASON_CLASS", "4", "其他原因"),
        STOP_REASON_CLASS_5("STOP_REASON_CLASS", "5", "天气原因"),
        DEPT_LEVEL_0("DEPT_LEVEL", "0", "集团"),
        DEPT_LEVEL_1("DEPT_LEVEL", "1", "公司"),
        DEPT_LEVEL_2("DEPT_LEVEL", "2", "部门"),
        DEPT_LEVEL_3("DEPT_LEVEL", "3", "班组"),
        DEPT_LEVEL_4("DEPT_LEVEL", "4", "作业班组");

        private final String type;

        private final String value;

        private final String label;

        DictDataEnum(String type, String value, String label) {
            this.type = type;
            this.value = value;
            this.label = label;
        }

        public static List<DictDataEnum> listDictData(String type) {
            return Arrays.stream(DictDataEnum.values()).filter(v1 -> v1.getType().equals(type)).collect(Collectors.toList());
        }

        public static String match(DictTypeEnum type, String code) {
            return Arrays.stream(DictDataEnum.values())
                    .filter(v1 -> v1.getType().equals(type.getType()) && v1.getValue().equals(code))
                    .map(DictDataEnum::getValue)
                    .findFirst()
                    .orElse(StringUtils.EMPTY);
        }
    }
}
