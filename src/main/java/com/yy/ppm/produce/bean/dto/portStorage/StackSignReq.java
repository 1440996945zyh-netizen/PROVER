package com.yy.ppm.produce.bean.dto.portStorage;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class StackSignReq {
    @NotNull(message = "票货号不应为空")
    private Long cargoInfoId;
    @NotNull(message = "垛位信息不应为空")
    private Long stackId;
    /**
     * sheet名称
     */
    private String sheetName;
    /**
     * 垛位名称
     */
    private String stackName;
    /**
     * 包装 规格
     */
    private String packageName;
    /**
     * 票货号
     */
    private String cargoInfoNo;
    /**
     * 船名航次
     */
    private String shipNameVoyage;
    /**
     * 客户名
     */
    private String customerName;
    //货名
    private String cargoName;
    //重量
    private String ton;
    //倒运或集港时间
    private String timeString;
    //首次跑垛位人，垛长
    private String createName;
    //倒运机械  作业机械
    private String machineString;
    /**
     *垛位件数
     */
    private String stackQua;
    /**
     *垛位重量
     */
    private String stackTon;
}
