package com.yy.common.util;


import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author 朱浩
 * @date 2019-12-16
 * @description 生成唯一标识
 */
public class UUIDUtils {

    /**
     * 调拨订单编号
     */
    private static final String DB_ORDER_CODE = "DB";

    /**
     * 保险维修流程编号
     */
    private static final String BX_ORDER_CODE = "BX";

    /**
     * 日常维修流程工单编号
     */
    private static final String RC_ORDER_CODE = "RC";

    /**
     * 日常维修流程工单编号
     */
    private static final String RHBY_ORDER_CODE = "BY";

    /**
     * 大修计划流程工单编号
     */
    private static final String PR_ORDER_CODE = "PR";

    /**
     * 封存流程工单编号
     */
    private static final String FC_ORDER_CODE = "FC";

    /**
     * 启封流程工单编号
     */
    private static final String QF_ORDER_CODE = "QF";

    /**
     * 启封流程工单编号
     */
    private static final String BF_ORDER_CODE = "BF";

    /**
     * 结算审核工单号
     */
    private static final String JS_ORDER_CODE = "JS";

    /**
     * 故障管理中：故障代码自动生成
     */
    private static final String GZ_CODE = "GZ";

    /**
     * 故障管理中：故障代码自动生成
     */
    private static final String TOOlS_CODE = "TC";

    /**
     * 工属具发放单编号自动生成
     */
    private static final String ISSUE_CODE = "IT";

    /**
     * 工属具回收单编号自动生成
     */
    private static final String RECYCLE_CODE = "RT";

    /**
     * 出租流程工单编号
     */
    private static final String CZ_ORDER_CODE = "CZ";

    /**
     * 转卖流程工单编号
     */
    private static final String ZM_ORDER_CODE = "ZM";

    /**
     * 设备整改工单编号
     */
    private static final String ZHG_ORDER_CODE = "ZHG";

    /**
     * 随即编码
     */
    private static final int[] r = new int[]{7, 9, 6, 2, 8, 1, 3, 0, 5, 4};

    /**
     * 随机数总长度
     */
    private static final int maxLength = 3;

    /**
     * 根据用户id生成后三位随机数
     */
    private static String toCode(Long id) {
        String idStr = id.toString();
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i >= 1; i--) {
            Random random = new Random();
            Math.random();
            if (idStr.length() < 3) {
                int s = (int) (Math.random() * r.length);
                sb.append(s);
            } else {
                int j = random.nextInt(idStr.length() - 1);
                sb.append(j);
            }
        }
        // Pad the result with leading zeros if necessary
        String result = sb.toString();
        if (result.length() < 3) {
            result = String.format("%03d", Integer.parseInt(result));
        }

        return result;
    }


    /**
     * 生成时间戳
     *
     * @return
     */
    private static String getDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        return sdf.format(new Date());
    }

    /**
     * 设备系统自编号生成时间戳
     *
     * @return
     */
    public static String getSystemDateTime() {
        DateFormat sdf = new SimpleDateFormat("yyMMdd");
        return sdf.format(new Date());
    }

    public static String getEquipSystemCode() {
        int random = (int) ((Math.random() * 12 + 1) * 10000);
        return getSystemDateTime() + random;
    }

    /**
     * 生成固定长度的随机码
     *
     * @param n
     * @return
     */
    private static long getRandom(long n) {
        long min = 1, max = 10;
        for (int i = 1; i < n; i++) {
            min *= 10;
            max *= 10;
        }
        long rangeLong = (((long) (new Random().nextDouble() * (max - min)))) + min;
        return rangeLong;
    }

    /**
     * 生成不带类别标头的编码
     *
     * @param userId
     * @return
     */
    public static synchronized String getCode(Long userId) {
        userId = userId == null ? 0 : userId;
        return getDateTime() + toCode(userId);
    }

    /**
     * 生成调拨工单编号
     *
     * @param userId 用户id
     * @return
     */
    public static String getDbOrderCode(Long userId) {
        return DB_ORDER_CODE + getCode(userId);
    }

    /**
     * 生成保险维修流程工单编号
     * 生成保险维修记录工单编号
     *
     * @param userId 用户id
     * @return
     */
    public static String getBXOrderCode(Long userId) {
        return BX_ORDER_CODE + getCode(userId);
    }

    /**
     * 生成日常维修流程工单编号
     * 生成日常维修记录工单编号
     *
     * @param userId 用户id
     * @return
     */
    public static String getRCOrderCode(Long userId) {
        return RC_ORDER_CODE + getCode(userId);
    }

    /**
     * 生成润滑保养记录工单号
     *
     * @param userId 用户id
     * @return
     */
    public static String getRhbyOrderCode(Long userId) {
        return RHBY_ORDER_CODE + getCode(userId);
    }

    /**
     * 生成大修计划记录工单号
     *
     * @param userId 用户id
     * @return
     */
    public static String getPROrderCode(Long userId) {
        return PR_ORDER_CODE + getCode(userId);
    }

    /**
     * 闲置封存工单号
     *
     * @param userId 用户id
     */
    public static String getIdleMothballFCCode(Long userId) {
        return FC_ORDER_CODE + getCode(userId);
    }

    /**
     * 启封工单号
     *
     * @param userId 用户id
     */
    public static String getIdleMothballQFCode(Long userId) {
        return QF_ORDER_CODE + getCode(userId);
    }

    /**
     * 报废工单号
     *
     * @param userId
     * @return
     */
    public static String getScrapCode(Long userId) {
        return BF_ORDER_CODE + getCode(userId);
    }

    /**
     * 结算审核工单号
     *
     * @param userId
     * @return
     */
    public static String getApprovalCode(Long userId) {
        return JS_ORDER_CODE + getCode(userId);
    }

    public static void main(String[] args) {
        String dbOrderCode = getEquipToolsCode(1000004111L);
        System.out.println(dbOrderCode);
    }

    /**
     * 故障管理中：故障代码自动生成
     *
     * @param length 长度
     * @return
     */
    public static String getGZCode(Long length) {
        return GZ_CODE + getRandom(length);
    }

    /**
     * 工属具编号自动生成
     *
     * @param length 长度
     * @return
     */
    public static String getEquipToolsCode(Long length) {
        return TOOlS_CODE + getCode(length);
    }

    /**
     * 工属具发放工单编号自动生成
     *
     * @param length 长度
     * @return
     */
    public static String getIssueCode(Long length) {
        return ISSUE_CODE + getCode(length);
    }

    /**
     * 工属具回收工单编号自动生成
     *
     * @param length 长度
     * @return
     */
    public static String getRecycleCode(Long length) {
        return RECYCLE_CODE + getCode(length);
    }

    /**
     * 出租工单号
     *
     * @param userId
     * @return
     */
    public static String getRentalCode(Long userId) {
        return CZ_ORDER_CODE + getCode(userId);
    }

    /**
     * 转卖工单号
     *
     * @param userId
     * @return
     */
    public static String getResaleCode(Long userId) {
        return ZM_ORDER_CODE + getCode(userId);
    }

    /**
     * 整改工单号
     *
     * @param userId
     * @return
     */
    public static String getEquipRectificationCode(Long userId) {
        return ZHG_ORDER_CODE + getCode(userId);
    }

    private static String count = "0000000";
    private static String dateValue = "20131115";
    /**
     * 产生流水号
     */
    public synchronized static String getMoveOrderNo(String orderNo) {
        long No = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String nowDate = sdf.format(new Date());
        No = Long.parseLong(nowDate);
        if (!(String.valueOf(No)).equals(dateValue)) {
            if (StringUtils.isNotBlank(orderNo)){
                count = orderNo;
            } else {
                count = "0000000";
            }
            dateValue = String.valueOf(No);
        }
        String num = String.valueOf(No);
        num += getNo(count);
        num = JS_ORDER_CODE + num;
        return num;
    }

    /**
     * 返回当天的订单数+1
     */
    public static String getNo(String s) {
        String rs = s;
        int i = Integer.parseInt(rs);
        i += 1;
        rs = "" + i;
        for (int j = rs.length(); j < 7; j++) {
            rs = "0" + rs;
        }
        count = rs;
        return rs;
    }
}

