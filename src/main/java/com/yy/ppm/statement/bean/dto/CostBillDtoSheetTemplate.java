package com.yy.ppm.statement.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.excel.export.bean.SheetMapping;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class CostBillDtoSheetTemplate extends SheetMapping {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 作业公司id
     */
    private Long companyId;

    /**
     * 作业公司NAME
     */
    private String companyName;

    /**
     * 结算单编号
     */
    private String statementNo;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 结算单类型（10.船舶货方结算单、20.陆集陆疏货方结算单 ）30.船方计费 40.杂项计费
     */
    private String type;

    /**
     * 指令ID
     */
    private Long trustId;

    /**
     * 指令票货id
     */
    private Long trustCargoId;

    /**
     * 货物清单ID
     */
    private Long handoverlistId;

    /**
     * 航次ID
     */
    private Long shipvoyageId;

    /**
     * 航次子表ID
     */
    private Long shipvoyageItemId;

    /**
     * 货物代码
     */
    private String cargoCode;

    /**
     * 货名
     */
    private String cargoName;

    /**
     * 贸别，内贸、外贸
     */
    private String tradeType;

    /**
     * 结算日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date settlementDate;

    /**
     * 状态（1.生产结算 2.商务结算 3.计费审核 4.开票）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否最终结算 0否/1是
     */
    private String isFinal;

    /**
     * 是否最终结算
     */
    private String isFinalLabel;

    /**
     * 结算单类型
     */
    private String typeLabel;

    /**
     * 贸别Label
     */
    private String tradeTypeLabel;

    /**
     * 结算状态
     */
    private String statusLabel;

    /**
     * 货主名称
     */
    private String cargoOwnerName;

    /**
     * 货代名称
     */
    private String cargoAgentName;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 进出口Label
     */
    private String impExpLabel;

    /**
     * 交接清单量
     */
    private BigDecimal ton;

    /**
     * scn 1108 回显预结算主列表的scn
     */
    private String scn;

    /**
     * 靠泊时间 1108 回显预结算主列表的靠泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date berthTime;

    /**
     * 离泊时间 1108 回显预结算主列表的离泊时间
     */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date leavePortTime;

    private BigDecimal number;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 结算方式
     */
    private String settlementBasisName;

    /**
     *
     *
     * costNumerName
     */
    private String costNumberName;



    /***
     * 计费人相关
     */
    private Long statementBy;
    private String statementByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date statementTime;

    /***
     * 审核人相关
     */
    private Long reviewBy;
    private String reviewByName;

    /**
     * 商务确认人相关信息
     */
    private Long confirmBy;
    private String confirmByName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date confirmTime;



    /**
     * 结算单详情
     */
    List<TCostStatementDetailDTO> detailList;


    /** 纳税人识别号 */
    private String tin;
    /** 开户行 */
    private String bank;
    /** 银行账号 */
    private String bankAccount;
    /**
     * 客户的联系电话
     */
    private String contactNumber;

    /**
     * 商务确认日期 nowDate
     */
    private String  reviewTime;

    /**
     * 企业地址
     */
    private String  address;

    /**
     * 小计金额
     */
    private BigDecimal amount;
    /**
     * 小计数量
     */
    private BigDecimal numberCount;

    //包干费最外层备注
    private String outSideRemark;

}
