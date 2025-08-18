package com.yy.ppm.finance.bean.dto;

import com.yy.ppm.finance.bean.po.FFeeItemPO;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author czk
 * @date 2021-03-29 11:09:46
 */
@ToString
@Data
public class SdInvoiceDTO {

    private static final long serialVersionUID = -78645124685819669L;

    /**
     * 购方企业名称,是
     */
//    @NotBlank(message = "购方企业名称不能为空")
    private String buyername;
    /**
     * 购方企业税号,是
     */
//    @NotBlank(message = "购方企业税号不能为空")
    private String taxnum;
    /**
     * 购方企业地址,是
     */
//    @NotBlank(message = "购方企业地址不能为空")
    private String address;
    /**
     * 购方企业银行开户行及账号,是
     */
    private String account;
    /**
     * 购方企业电话
     */
//    @NotBlank(message = "购方企业电话不能为空")
    private String telephone;
    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderno;
    /**
     * 单据时间
     */
    @NotBlank(message = "单据时间不能为空")
    private String invoicedate;

    /**
     * 销方企业税号
     */
    @NotBlank(message = "销方企业税号不能为空")
    private String saletaxnum;
    /**
     * 销方企业银行开户行及账号
     */
    @NotBlank(message = "销方企业银行开户行及账号不能为空")
    private String saleaccount;
    /**
     * 销方企业地址
     */
    @NotBlank(message = "销方企业地址不能为空")
    private String saleaddress;
    /**
     * 发票类型，1:正票;2：红票
     */
    @NotBlank(message = "发票类型不能为空")
    private String kptype;
    /**
     * 立即开票标志：0不进行开票，1：进行开票
     */
    @NotBlank(message = "立即开票标志不能为空")
    private String invoiceNow;
    /**
     * 备注（不同开票服务器类型支持的备注长度不同，提交后会有相应校验）冲红时，必须在 备注中注明 “对 应正数
     * 发票码 :XXXXXXXXX号码:YYYYYYYY”文案，其中“X”为发票代码， “Y”为发票号 码，否则接口会自动添 加该文案
     */
    private String message;
    /**
     * 开票员
     */
    @NotBlank(message = "开票员不能为空")
    private String clerk;

    //收款人
    private String payee;

    //复核人
    private String checker;

    //非数电红票必填，数电冲红可为空，对应非数电蓝票发票代码
    private String fpdm;
    /**
     * 红票必填，对应蓝票发票号码
     */
    private String fphm;

    //默认1,推送方式， -1: 不推送; 0: 邮箱; 1:手机(默认);2:邮箱&手机
    private String tsfs;

    //推送邮箱（tsfs 为0或2 时，此项为必填）
    private String email;

    /**
     * 推送手机( 开票成功 会短信提醒)
     */
    @NotBlank(message = "推送手机不能为空")
    private String phone;

    //默认为0，卷票r不支持清单。清单标志，0:非清单,1:清单,根据项目名称数，自动生成清单;
    private String qdbz;
    //qdbz 为1时,此项为必填,注意：税局要求清单项目名称为（详见销货清单）
    private String qdxmmc;
    //代开标志
    private String dkbz;
    //部门门店 id（航天信息提供）
    private String deptid;
    //开票员 id（航天信息系统中的 id）
    private String clerkid;
    /**
     * 发票种类
     * p:电子增值税普通发票，
     * c:增值税普通发票(纸票) ，
     * s:增值税专用发票，
     * e:收购发票(电子)，
     * f:收购发票(纸质) ，
     * r:增值税普通发票(卷式) ，
     * b:增值税电子专用发票，
     * bs:电子发票(增值税专用发票)-即数电专票(电子),
     * pc:电子发票(普通发票)-即数电普票(电子),
     * es: 数电纸质发票(增值税专用发票 )- 即数电专票 ( 纸质)，
     * ec:数电纸质发票
     * (普通发票)-即数电普票(纸质）
     */
    @NotBlank(message = "发票种类不能为空")
    private String invoiceLine;

    //成品油标志,默认为0,成品油标志：0 非成品油，1 成品油
    private String cpybz;

    //分机号（只能为空或者数字）
    private String fjh;

    //终端号（只能为空或者数字）
    private String terminalNumber;
    /**
     * 红字信息表编号
     * 专票冲红时，此项必填。且必须在备注中注明“ 开具红字增值税专用发票信息表编号
     * 例： ZZZZZZZZZZZZZZZZ”字样，其中 “Z”为开具红字增值税专用发票所需要的长度为 16位信息表编号。
     */
    private String billInfoNo;

    @NotEmpty(message = "发票明细不能为空")
    List<InvoiceDetailDTO> detail;

    @Data
    @ToString
    public static class InvoiceDetailDTO{

        /**
         * 商品名称
         * 如FPHXZ=1，则此商品行为折扣行，此版本折扣行不允许多行折扣，折扣行必须紧邻被折扣行，项目名称必须与被折扣行一致。
         */
        @NotBlank(message = "明细名称不能为空")
        private String goodsname;
        /**
         * 数量
         * 数量、单价必须都不填，或都必填，不可只填一个;
         * 当数量、 单价都不填时，不含税金额、税额、含税金额都必填;
         * 当数量、单价都填时，不含税金 额、税额、含税金额可为空;
         * 开具成品油发票时必填。建议保留小数点后 8 位;
         * 冲红时项目数量为负数
         */
        private String num;
        /**
         * 数量
         * 数量、单价必须都不填，或都必填，不可只填一个;
         * 当数量、 单价都不填时，不含税金额、税额、含税金额都必填;
         * 当数量、单价都填时，不含税金 额、税额、含税金额可为空;
         * 开具成品油发票时必填。建议保留小数点后 8 位;
         * 冲红时项目单价为正数
         */
        private String price;
        /**
         * 单价含税标志，0:不含税,1:含税
         */
        @NotBlank(message = "单价含税标志不能为空")
        private String hsbz;
        /**
         * 税率
         */
        @NotBlank(message = "税率不能为空")
        private String taxrate;
        /**
         * 规格型号
         */
        private String spec;
        /**
         * 单位，开具成品油发票时必填， 必须为”升”或“ 吨”。
         */
        @NotBlank(message = "单位不能为空")
        private String unit;
        /**
         * 税收分类编码，签订免责协议客户不传入， 由接口进行匹配，如对接口速度敏感、赋码准确要求高的企业，建议传入该字段
         */
        private String spbm;

        //自行编码
        private String zsbm;

        /**
         * 发票行性质，
         * 0:正常行;
         * 1:折扣行;
         * 2:被折扣行
         */
        @NotBlank(message = "发票行性质不能为空")
        private String fphxz;
        /**
         * 优惠政策标识,0:不使用;1:使用
         */
        @NotBlank(message = "优惠政策标识不能为空")
        private String yhzcbs;

        //增值税特殊管理，如：即征即退、免税、简易征收等,当 yhzcbs 为1时，此项必填
        private String zzstsgl;

        //零税率标识，空:非零税率;1:免税;2:不征税;3:普通零税率
        private String lslbs;

        //扣除额
        private String kce;

        //不含税金额,精确到小数点后 面两位，红票为负。
        //不含税金额、税额、含税金额任何一个不传时，会根据传入 的单价，数量进行计算，可能和实际数值存在误差，建议都传入
        private String taxfreeamt;

        /**
         * 税额
         * 精确到小数点后 面两位，红票为 负。
         * 不含税金额、税额、含税金额任何一个不传时，会根据传入 的单价，数量进行计算，可能和实际数值存在误差，建议都传入
         */
        private String tax;

        /**
         * 含税金额
         * 精确到小数点后 面两位，红票为 负。
         * 不含税金额、税额、含税金额任何一个不传时，会根据传入 的单价，数量进行计算，可能和实际数值存在误差，建议都传入
         */
        private String taxamt;


    }

}
