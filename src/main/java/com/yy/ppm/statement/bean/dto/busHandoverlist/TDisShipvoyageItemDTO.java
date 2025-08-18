package com.yy.ppm.statement.bean.dto.busHandoverlist;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 14:27
 */
@Setter
@Getter
public class TDisShipvoyageItemDTO {

    /**
     * 海轮预报ID
     */
    private Long id;

    /**
     * 船舶航次ID
     */
    private Long shipvoyageItemId;
    /**
     * 到港编号信息
     */
    private String scn;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 船名航次
     */
    private String shipName;

    /**
     * 船名航次
     */
    private String voyage;

    /**
     * 装卸
     */
    private String loadUnload;


    private String wharf;

    /**
     * 内外贸编码
     */
    private String tradeType;

    /**
     * 内外贸
     */
    private String tradeTypeLabel;

    /**
     * 货代
     */
    private String customerName;

    /**
     * 靠泊时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;

    /**
     * 离泊时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leaveBerthTime;

    /**
     * 离港时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leavePortTime;

    /**
     * 状态
     */
    private String shipStatusName;

    /**
     * 货种名称
     */
    private String cargoCategoryName;

    /**
     * 是否有交接清单 0否/1是
     */
    private String isHaveHandoverlist;

    /**
     * 是否有交接清单Label
     */
    private String isHaveHandoverlistLabel;

    /**
     * 附件ID
     */
    private List<Long> fileIds;
    /**
     * 重量
     */
    private BigDecimal handoverlistTon;

    /**
     * 作业公司信息
     */
    private String companyName;
    private Long companyId;

    /**
     * 舱口信息
     */
    private Integer hatchNums;
}
