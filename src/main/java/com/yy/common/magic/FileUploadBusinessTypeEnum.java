package com.yy.common.magic;

import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 文件上传业务类型枚举
 *
 * @author ChenLP
 **/
@Getter
public enum FileUploadBusinessTypeEnum {

    BUSINESS_0001("0001", "", ""),
    BUSINESS_CONTRACT_01("BUSINESS_CONTRACT_01", "", "合同资料"),
    BUSINESS_CONTRACT_02("BUSINESS_CONTRACT_02", "", "合同附件"),
    BUSINESS_CUSTOMER_AUTHORIZATION("BUSINESS_CUSTOMER_AUTHORIZATION", "", "授权书"),
    BUSINESS_CUSTOMER_BILLING("BUSINESS_CUSTOMER_BILLING", "", "企业开票"),
    BUSINESS_CUSTOMER_LICENSE("BUSINESS_CUSTOMER_LICENSE", "", "营业执照"),
    PAYMENT_PROOF("PAYMENT_PROOF", "", "付款凭证"),
    INVOICE_PROOF("INVOICE_PROOF", "", "发票证明"),
    BUSINESS_WATER("BUSINESS_WATER", "", "加水接电"),
    BUSINESS_TALLY("BUSINESS_TALLY", "", "理货"),
    MASTER_SHIP_01("MASTER_SHIP_01", "", "船舶资料"),
    REPORT_TEMPLATES("REPORT_TEMPLATES", "", "报表模板"),
    DISPATCH_TDISLOG("DISPATCH_TDISLOG","","调度日志"),
    HANDOVERLIST("HANDOVERLIST", "", "交接清单"),
    HANDOVERLIST_HG("HANDOVERLIST_HG", "", "交接清单-海关报关单"),
    HANDOVERLIST_JC("HANDOVERLIST_JC", "", "交接清单-第三方检测报告"),
    INVENTORY("INVENTORY", "", "作业通知单清单"),
    VERSION_CONTROL("VERSION_CONTROL", "", "版本控制"),
    BUSINESS_RECEIPT("BUSINESS_RECEIPT", "", "商务回执单"),
    SUNDRY_ACCESSORY("SUNDRY_ACCESSORY", "", "杂货计划附件"),
    CARGO_REDUCE_PROOF("CARGO_REDUCE_PROOF", "", "票货减免凭证"),
    RELEASE_MANAGE("RELEASE_MANAGE", "", "放货凭证"),
    ORDER_DELIVERY("ORDER_DELIVERY", "", "提货委托单");

    FileUploadBusinessTypeEnum(String code, String route, String comment) {
        this.code = code;
        this.route = route;
        this.comment = comment;
    }

    /**
     * 编码
     **/
    private String code;

    /**
     * 路由
     **/
    private String route;

    /**
     * 注释
     **/
    private String comment;

    /**
     * 验证枚举
     *
     * @param code 编码
     * @return true|存在, false|不存在
     **/
    public static boolean valid(String code) {
        return Stream.of(FileUploadBusinessTypeEnum.values()).anyMatch(val -> val.getCode().equals(code));
    }

    /**
     * 获取业务类型
     *
     * @param code 编码
     * @return 业务类型
     **/
    public static String getRoute(String code) {
        Optional<FileUploadBusinessTypeEnum> opt = Stream.of(FileUploadBusinessTypeEnum.values())
                .filter(val -> val.getCode().equals(code)).findFirst();
        return opt.get().getRoute();
    }
}
