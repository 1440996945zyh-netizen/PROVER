package com.yy.ppm.finance.bean.dto.FinacialSharing;

import lombok.Data;

import java.util.List;

@Data
public class ShareCommonReqDTO {
//    计费单 ID
    private String F_JFDID;
//    发票类型
    private String F_FPLX;
//    操作类型 FPKJ:发票开具；FPZF:发票作废FPHC:发票红冲
    private String F_CZLX;
//    开票人编号
    private String F_KPRBH;
//    开票人名称
    private String F_KPRMC;
//    开票备注
    private String F_KFBZ;
//发票号码
    private String F_FPHM;
//  发票代码
    private String F_FPDM;
    //开票时间
    private String F_KPSJ;
//    不含税金额
    private String F_BHSJE;
//    开票税率
    private String F_KPSL;
//    开票税额
    private String F_KPSE;
//    价税合计总额
    private String F_JSHJZE;
//    购方名称
    private String F_GFMC;
//    F_GFSH
    private String F_GFSH;
//    购方地址电话
    private String F_GFDZDH;
//    购方银行账户
    private String F_GFYHZH;
//    销方名称
    private String F_XFMC;
//    销方税号
    private String F_XFSH;
//    销方地址电话
    private String F_XFDZDH;
//    销方银行账户
    private String F_XFYHZH;
//    商品名称
    private String F_SPMC;
//    发票影像
    private String F_FPURL;
//     详情
    private List<ShareInvoiceDetailDto> mxdetail;

}
