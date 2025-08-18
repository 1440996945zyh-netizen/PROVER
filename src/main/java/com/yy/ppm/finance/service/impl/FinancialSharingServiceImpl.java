package com.yy.ppm.finance.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.common.util.SpringUtils;
import org.springframework.beans.factory.annotation.Value;
import com.yy.common.util.DateUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.mapper.TBusCargoInfoMapper;
import com.yy.ppm.business.mapper.TBusCustomerMapper;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.finance.bean.dto.*;
import com.yy.ppm.finance.mapper.TFdCreditDebitBillDetailMapper;
import com.yy.ppm.finance.mapper.TFdCreditDebitBillMapper;
import com.yy.ppm.finance.mapper.TFdInvoiceDetailMapper;
import com.yy.ppm.finance.mapper.TFdInvoiceMapper;
import com.yy.ppm.finance.service.FinancialSharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票子表(TFdInvoiceDetail)ServiceImpl
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Service
public class FinancialSharingServiceImpl implements FinancialSharingService {

    @Resource
    private TFdInvoiceMapper invoiceMapper;
    @Resource
    private TFdInvoiceDetailMapper tFdInvoiceDetailMapper;
    @Resource
    private TFdCreditDebitBillMapper tFdCreditDebitBillMapper;
    @Resource
    private TFdCreditDebitBillDetailMapper tFdCreditDebitBillDetailMapper;
    @Resource
    private TDisShipVoyageMapper shipVoyageMapper;
    @Resource
    private TBusCargoInfoMapper tBusCargoInfoMapper;
    @Resource
    private TBusCustomerMapper customerMapper;

    @Resource
    private Snowflake snowflake;

    private final String FEE_ZUOFEI_URL = "/GatewayService/api/income/change/cancel"; //计费单作废接口
    private final String F_LYXTBH = "F_LYXTBH";//单据来源系统编号

    private static String SFHBKP = "0";//是否合并开票
    private static String JFDJLX = "ZX";//计费单据类型
    private static String BZBH = "CNY";//币种
    private static String FKFS = "SX";//销售方式
    private static String SFZG = "0";//是否暂估

    /**
     * 计费单发送   businessType 默认传空
     *
     * @param id
     * @return
     */
    public void sendBilling(Long id, String isLease, boolean cnDn, String hc,String businessType) {
        try {
            if (("0".equals(isLease) && !cnDn) || "zxJfdByInvoice".equals(businessType)){//装卸计费单，正常发票
                zxJfdByInvoice(id,"/GatewayService/api/income/receive/bill");
            } else if (("1".equals(isLease) && !cnDn) ||"zlJfdByInvoice".equals(businessType)){//租赁计费单，正常发票
                zlJfdByInvoice(id,"/GatewayService/api/income/receive/bill");
            } else if (("0".equals(isLease) && cnDn) ||"zxJfdByCnDnInvoice".equals(businessType)){//装卸计费单，正常发票
                zxJfdByCnDnInvoice(id,hc,"/GatewayService/api/income/receive/bill");
            } else if (("1".equals(isLease) && cnDn)||"zlJfdByCnDnInvoice".equals(businessType)) {//租赁计费单，正常发票
                zlJfdByCnDnInvoice(id,hc,"/GatewayService/api/income/receive/bill");
            }
        } catch (Exception e) {
            throw new BusinessRuntimeException(e.getMessage());
        }
    }

    /**
     * 装卸计费单，正常发票
     * @return
     */
    private void zxJfdByInvoice(Long id, String url) {
        try {
            //获取发票信息
            TFdInvoiceDTO invoiceDTO = invoiceMapper.getById(id);
            List<TFdInvoiceDetailDTO> invoiceDetailDTOS = tFdInvoiceDetailMapper.getListByInvoiceId(id);
            TBusCustomerDTO customerDTO = customerMapper.getById(invoiceDTO.getCustomerId());
            //组装数据
            FinancialSharingDTO<ZxDetailDTO> financialSharingDTO = new FinancialSharingDTO();
            setFormByInvoice(invoiceDTO, invoiceDetailDTOS, financialSharingDTO);
            setZxDetailByInvoice(invoiceDTO, invoiceDetailDTOS, financialSharingDTO, customerDTO);
        } catch (Exception e) {
            throw new BusinessRuntimeException(e.getMessage());
        }
    }

    /**
     * 租赁计费单，正常发票
     * @return
     */
    private void zlJfdByInvoice(Long id, String url) {
        try {
            //获取发票信息
            TFdInvoiceDTO invoiceDTO = invoiceMapper.getById(id);
            List<TFdInvoiceDetailDTO> invoiceDetailDTOS = tFdInvoiceDetailMapper.getListByInvoiceId(id);
            TBusCustomerDTO customerDTO = customerMapper.getById(invoiceDTO.getCustomerId());
            //组装数据
            FinancialSharingDTO<ZlDetailDTO> financialSharingDTO = new FinancialSharingDTO();
            setFormByInvoice(invoiceDTO, invoiceDetailDTOS, financialSharingDTO);
            setZlDetailByInvoice(invoiceDTO, invoiceDetailDTOS, financialSharingDTO, customerDTO);
        } catch (Exception e) {
            throw new BusinessRuntimeException(e.getMessage());
        }
    }

    /**
     * 设置正常发票表单数据
     * @param invoiceDTO
     * @param invoiceDetailDTOS
     * @param financialSharingDTO
     */
    private void setFormByInvoice(TFdInvoiceDTO invoiceDTO, List<TFdInvoiceDetailDTO> invoiceDetailDTOS, FinancialSharingDTO financialSharingDTO) {
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (TFdInvoiceDetailDTO e : invoiceDetailDTOS) {
            taxAmount = taxAmount.add(e.getTaxAmount());
        }
        financialSharingDTO.setF_JFDID(String.valueOf(invoiceDTO.getId()));
        financialSharingDTO.setF_ZZJG("TEST");//公司编码
        financialSharingDTO.setF_ZZJGMC("测试公司");//公司名称
        financialSharingDTO.setF_LYXTBH("生产系统");//单据来源系统
        financialSharingDTO.setF_SFHBKP(SFHBKP);//是否合并开票
        financialSharingDTO.setF_JFDJLX(JFDJLX);//计费单据类型
        financialSharingDTO.setF_FSSL(0);
        financialSharingDTO.setF_SFZG(SFZG);
//        financialSharingDTO.setF_HTBH("");//合同编号
        financialSharingDTO.setF_BZBH(BZBH);
        financialSharingDTO.setF_JFJEHJ(invoiceDTO.getInvoiceAmount());
        financialSharingDTO.setF_YHJEHJ(BigDecimal.ZERO);
        financialSharingDTO.setF_JMJEHJ(BigDecimal.ZERO);
        financialSharingDTO.setF_YSJEHJ(invoiceDTO.getInvoiceAmount());
        financialSharingDTO.setF_SEHJ(taxAmount);//税额
        financialSharingDTO.setF_CUSER(String.valueOf(invoiceDTO.getCreateBy()));
        financialSharingDTO.setF_CNAME(invoiceDTO.getCreateByName());
        financialSharingDTO.setF_FKFS(FKFS);
//        financialSharingDTO.setF_BHCID();//被红冲 ID
    }

    /**
     * 设置装卸信息
     *  @param invoiceDTO
     * @param invoiceDetailDTOS
     * @param financialSharingDTO
     */
    private void setZxDetailByInvoice(TFdInvoiceDTO invoiceDTO, List<TFdInvoiceDetailDTO> invoiceDetailDTOS, FinancialSharingDTO financialSharingDTO, TBusCustomerDTO customerDTO) {
        List<ZxDetailDTO> list = Lists.newArrayList();
        for (TFdInvoiceDetailDTO e : invoiceDetailDTOS) {
            Long shipVoyageItemId = e.getShipvoyageItemId();
            Map<String, Object> shipInfoMap = shipVoyageMapper.getShipInfoByItemId(shipVoyageItemId);
            Map<String, Object> rateInfoMap = tBusCargoInfoMapper.getRateInfo(e.getCargoInfoId());
            ZxDetailDTO zxDetailDTO = new ZxDetailDTO();
            zxDetailDTO.setF_MXID(String.valueOf(e.getId()));
            zxDetailDTO.setF_JFDID(String.valueOf(invoiceDTO.getId()));
            zxDetailDTO.setF_JFRQ(DateUtils.formatDate(invoiceDTO.getInvoiceTime(), "yyyy-MM-dd HH:mm:ss"));
            zxDetailDTO.setF_SFFLX("船方");//收费方类型
            zxDetailDTO.setF_FMBH(String.valueOf(rateInfoMap.get("rateCodeEas")));//费目代码,取金蝶代码
            zxDetailDTO.setF_FMMC(String.valueOf(rateInfoMap.get("rateNameEas")));//费目名称,取金蝶代码
//            zxDetailDTO.setF_JFDW();//计费单位,取金蝶代码
            zxDetailDTO.setF_JLDW(e.getUnitName());//计量单位,取金蝶代码
            zxDetailDTO.setF_JFSL(e.getNumberCount());//数量（计费量）
            zxDetailDTO.setF_BZBH(BZBH);
            zxDetailDTO.setF_YBDJ(BigDecimal.ZERO);
            zxDetailDTO.setF_YBJFJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBZKE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBFYJMJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBYSJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSJHSDJ(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSJDJ(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSRJE(BigDecimal.ZERO);
            zxDetailDTO.setF_HL(BigDecimal.ONE);
            zxDetailDTO.setF_HSDJ(e.getPieceAmount());
            zxDetailDTO.setF_JFJE(e.getAmount());
            zxDetailDTO.setF_YHJE(BigDecimal.ZERO);
            zxDetailDTO.setF_JMJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YSJE(e.getAmount());
            zxDetailDTO.setF_SZ(e.getTax());
            zxDetailDTO.setF_SE(e.getTaxAmount());
            zxDetailDTO.setF_BHSJE(BigDecimal.ZERO);
//            zxDetailDTO.setF_TYDH();//提运单号
//            zxDetailDTO.setF_ZYWTDBH();//作业委托单编号
            zxDetailDTO.setF_BOWEI(String.valueOf(shipInfoMap.get("berthName")));//泊位
            zxDetailDTO.setF_NWMBS(String.valueOf(shipInfoMap.get("tradeType")));//内外贸
            zxDetailDTO.setF_JCKBS(String.valueOf(shipInfoMap.get("impExp")));//进出口
            zxDetailDTO.setF_TBSC(String.valueOf(shipInfoMap.get("停泊时长")));//停泊时长
            zxDetailDTO.setF_TBSCDW("小时");//停泊时长时间单位
            zxDetailDTO.setF_ZXLX(String.valueOf(shipInfoMap.get("loadUnload")));//装卸类型
            zxDetailDTO.setF_BZFS(String.valueOf(shipInfoMap.get("packing")));//包装方式
//            zxDetailDTO.setF_YSFS();//运输方式
            zxDetailDTO.setF_KBRQ(String.valueOf(shipInfoMap.get("berthTime")));//靠泊日期
//            zxDetailDTO.setF_JD();//净吨
//            zxDetailDTO.setF_ZYXM();//作业项目
//            zxDetailDTO.setF_ZYXM();//作业项目
//            zxDetailDTO.setF_DCTS();//堆存天数
//            zxDetailDTO.setF_SHIPNO();//船号
            zxDetailDTO.setF_SHIPMC(String.valueOf(shipInfoMap.get("shipName")));//中文船名
            zxDetailDTO.setF_SHIPMCENG(String.valueOf(shipInfoMap.get("shipNameEn")));//英文船名
//            zxDetailDTO.setF_ZDBH();//账单编号
            zxDetailDTO.setF_HANGCI(String.valueOf(shipInfoMap.get("voyage")));//航次
            zxDetailDTO.setF_HLBH(String.valueOf(rateInfoMap.get("cargoCategoryCode")));//货类编号
            zxDetailDTO.setF_HLMC(String.valueOf(rateInfoMap.get("cargoCategoryName")));//货类名称
            zxDetailDTO.setF_HWBM(String.valueOf(rateInfoMap.get("cargoCode")));//货物编码
            zxDetailDTO.setF_HWMC(String.valueOf(rateInfoMap.get("cargoName")));//货物名称
//            zxDetailDTO.setF_HWJJDZ();//货物交接地址
//            zxDetailDTO.setF_GSSJ();//管输时间
//            zxDetailDTO.setF_NOTE();//备注
//            zxDetailDTO.setF_ZY();//摘要
            zxDetailDTO.setF_KHBH(customerDTO.getCustomerCodeEas());//金蝶,客户编号
            zxDetailDTO.setF_KHMC(customerDTO.getCustomerNameEas());//金蝶,客户名称
//            zxDetailDTO.setF_BMBH();//部门编号
//            zxDetailDTO.setF_BMMC();//部门名称
//            zxDetailDTO.setF_SCHLBH();//生产货类编号
//            zxDetailDTO.setF_SCHLMC();//生产货类名称
//            zxDetailDTO.setF_SCHWBM();//生产货物名称
//            zxDetailDTO.setF_SCHWMC();//生产货物名称
//            zxDetailDTO.setF_FPHM();//发票号码
//            zxDetailDTO.setF_MSLX();//免税标志
//            zxDetailDTO.setF_BGFLX();//包干费类型
            zxDetailDTO.setF_CMHC(String.valueOf(shipInfoMap.get("shipVoyage")));//船名航次
            list.add(zxDetailDTO);
        }
        financialSharingDTO.setDataDetail(list);
    }

    /**
     * 设置租赁信息
     * @param invoiceDTO
     * @param invoiceDetailDTOS
     * @param financialSharingDTO
     */
    private void setZlDetailByInvoice(TFdInvoiceDTO invoiceDTO, List<TFdInvoiceDetailDTO> invoiceDetailDTOS, FinancialSharingDTO financialSharingDTO, TBusCustomerDTO customerDTO) {
        List<ZlDetailDTO> list = Lists.newArrayList();
        for (TFdInvoiceDetailDTO e : invoiceDetailDTOS) {
            Long shipVoyageItemId = e.getShipvoyageItemId();
            Map<String, Object> shipInfoMap = shipVoyageMapper.getShipInfoByItemId(shipVoyageItemId);
            Map<String, Object> rateInfoMap = tBusCargoInfoMapper.getRateInfo(e.getCargoInfoId());
            ZlDetailDTO zlDetailDTO = new ZlDetailDTO();
            zlDetailDTO.setF_MXID(String.valueOf(e.getId()));
            zlDetailDTO.setF_JFDID(String.valueOf(invoiceDTO.getId()));
            zlDetailDTO.setF_JFRQ(DateUtils.formatDate(invoiceDTO.getInvoiceTime(), "yyyy-MM-dd HH:mm:ss"));
            zlDetailDTO.setF_FMBH(String.valueOf(rateInfoMap.get("rateCodeEas")));//费目代码,取金蝶代码
            zlDetailDTO.setF_FMMC(String.valueOf(rateInfoMap.get("rateNameEas")));//费目名称,取金蝶代码
            zlDetailDTO.setF_JLDW(e.getUnitName());//计量单位,取金蝶代码
            zlDetailDTO.setF_JFSL(e.getNumberCount());//数量（计费量）
            zlDetailDTO.setF_BZBH(BZBH);
            zlDetailDTO.setF_YBDJ(BigDecimal.ZERO);
            zlDetailDTO.setF_YBJFJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBZKE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBFYJMJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBYSJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSJHSDJ(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSJDJ(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSRJE(BigDecimal.ZERO);
            zlDetailDTO.setF_HL(BigDecimal.ONE);
            zlDetailDTO.setF_JFJE(e.getAmount());
            zlDetailDTO.setF_YHJE(BigDecimal.ZERO);
            zlDetailDTO.setF_JMJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YSJE(e.getAmount());
            zlDetailDTO.setF_SZ(e.getTax());
            zlDetailDTO.setF_SE(e.getTaxAmount());
            zlDetailDTO.setF_BHSJE(BigDecimal.ZERO);
//            zxDetailDTO.setF_NOTE();//备注
//            zxDetailDTO.setF_ZY();//摘要
            zlDetailDTO.setF_KHBH(customerDTO.getCustomerCodeEas());//金蝶,客户编号
            zlDetailDTO.setF_KHMC(customerDTO.getCustomerNameEas());//金蝶,客户名称
//            zlDetailDTO.setF_BMBH();//部门编号
//            zlDetailDTO.setF_BMMC();//部门名称
//            zlDetailDTO.setF_FPHM();//发票号码
//            zlDetailDTO.setF_ZCBM();//资产编码
//            zlDetailDTO.setF_ZCMC();//资产名称
//            zlDetailDTO.setF_ZLXMBH();//租赁项目编码
//            zlDetailDTO.setF_ZLXMMC();//租赁项目名称
//            zlDetailDTO.setF_JDYWLXBH();//金蝶业务类型编号
//            zlDetailDTO.setF_JDYWLXMC();//金蝶业务类型名称
//            zlDetailDTO.setF_ZLYWLXBH();//租赁业务类型编号
//            zlDetailDTO.setF_ZLYWLXMC();//租赁业务类型名称
//            zlDetailDTO.setF_WLLXBH();//金蝶往来类型编号
//            zlDetailDTO.setF_WLLXMC();//金蝶往来类型名称
//            zlDetailDTO.setF_GDZCZLLX();//固定资产租赁类型
//            zlDetailDTO.setF_MSLX();//免税标志
            list.add(zlDetailDTO);
        }
        financialSharingDTO.setDataDetail(list);
    }

    /**
     * 装卸计费单，cnDn发票
     * @return
     */
    private void zxJfdByCnDnInvoice(Long id, String hc, String url) {
        try {
            //获取发票信息
            TFdCreditDebitBillDTO dto = tFdCreditDebitBillMapper.getById(id);
            List<TFdCreditDebitBillDetailDTO> creditDebitBillDetailDTOS = tFdCreditDebitBillDetailMapper.getByParentId(id);
            TBusCustomerDTO customerDTO = customerMapper.getById(dto.getCustomerId());
            TFdInvoiceDTO invoiceDTO = tFdCreditDebitBillMapper.getInvoiceInfoBySysInvoiceCode(dto.getSysInvoiceCode());
            List<TFdInvoiceDetailDTO> invoiceDetailDTOS = tFdInvoiceDetailMapper.getListByInvoiceId(invoiceDTO.getId());
            //组装数据
            FinancialSharingDTO<ZxDetailDTO> financialSharingDTO = new FinancialSharingDTO();
            setFormByCnDnInvoice(dto, creditDebitBillDetailDTOS, invoiceDTO, financialSharingDTO);
            setZxDetailByCnDnInvoice(dto, creditDebitBillDetailDTOS, invoiceDTO, invoiceDetailDTOS, financialSharingDTO, customerDTO);
            //发送数据
            Map<String, Object> params = Maps.newHashMap();
            String requestId = String.valueOf(snowflake.nextId());
            params.put("requestId", requestId);
            params.put("data", Arrays.asList(financialSharingDTO));
        } catch (Exception e) {
            throw new BusinessRuntimeException(e.getMessage());
        }
    }

    /**
     * 设置表单数据
     * @param dto
     * @param detailDTOS
     * @param financialSharingDTO
     */
    private void setFormByCnDnInvoice(TFdCreditDebitBillDTO dto,List<TFdCreditDebitBillDetailDTO> detailDTOS, TFdInvoiceDTO invoiceDTO,FinancialSharingDTO financialSharingDTO){
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (TFdCreditDebitBillDetailDTO e : detailDTOS) { taxAmount = taxAmount.add(e.getTaxAmount());  }
        financialSharingDTO.setF_JFDID(String.valueOf(dto.getId()));
        financialSharingDTO.setF_ZZJG("TEST");//公司编码
        financialSharingDTO.setF_ZZJGMC("测试公司");//公司名称
        financialSharingDTO.setF_LYXTBH("生产系统");//单据来源系统
        financialSharingDTO.setF_SFHBKP(SFHBKP);//是否合并开票
        financialSharingDTO.setF_JFDJLX(JFDJLX);//计费单据类型
        financialSharingDTO.setF_FSSL(0);
        financialSharingDTO.setF_SFZG(SFZG);
//        financialSharingDTO.setF_HTBH("");//合同编号
        financialSharingDTO.setF_BZBH(BZBH);
        financialSharingDTO.setF_JFJEHJ(dto.getEliminateAmount());
        financialSharingDTO.setF_YHJEHJ(BigDecimal.ZERO);
        financialSharingDTO.setF_JMJEHJ(BigDecimal.ZERO);
        financialSharingDTO.setF_SEHJ(taxAmount);//税额
        financialSharingDTO.setF_CUSER(String.valueOf(dto.getCreateBy()));
        financialSharingDTO.setF_CNAME(dto.getCreateByName());
        financialSharingDTO.setF_FKFS(FKFS);
        financialSharingDTO.setF_BHCID(String.valueOf(invoiceDTO.getId()));//sh
    }

    /**
     * 设置装卸信息
     * @param dto
     * @param detailDTOS
     * @param financialSharingDTO
     */
    private void setZxDetailByCnDnInvoice(TFdCreditDebitBillDTO dto,List<TFdCreditDebitBillDetailDTO> detailDTOS,TFdInvoiceDTO invoiceDTO,List<TFdInvoiceDetailDTO> invoiceDetailDTOS,FinancialSharingDTO financialSharingDTO,TBusCustomerDTO customerDTO){
        Map<Long,List<TFdInvoiceDetailDTO>> invoiceMap = invoiceDetailDTOS.stream().collect(Collectors.groupingBy(e->e.getId()));
        List<ZxDetailDTO> list = Lists.newArrayList();
        for (TFdCreditDebitBillDetailDTO e : detailDTOS) {
            TFdInvoiceDetailDTO invoiceDetailDTO = CollectionUtil.isNotEmpty(invoiceMap.get(e.getInvoiceDetailId()))?invoiceMap.get(e.getInvoiceDetailId()).get(0):new TFdInvoiceDetailDTO();
            Long shipVoyageItemId = invoiceDetailDTO.getShipvoyageItemId();
            Map<String,Object> rateInfoMap = tBusCargoInfoMapper.getRateInfo(invoiceDetailDTO.getCargoInfoId());
            Map<String,Object> shipInfoMap = shipVoyageMapper.getShipInfoByItemId(shipVoyageItemId);
            ZxDetailDTO zxDetailDTO = new ZxDetailDTO();
            zxDetailDTO.setF_MXID(String.valueOf(e.getId()));
            zxDetailDTO.setF_JFDID(String.valueOf(dto.getId()));
            zxDetailDTO.setF_JFRQ(DateUtils.formatDate(dto.getCndnTime(),"yyyy-MM-dd HH:mm:ss"));
            zxDetailDTO.setF_SFFLX("船方");//收费方类型
            zxDetailDTO.setF_FMBH(CollectionUtil.isNotEmpty(rateInfoMap)?String.valueOf(rateInfoMap.get("rateCodeEas")):null);//费目代码,取金蝶代码
            zxDetailDTO.setF_FMMC(CollectionUtil.isNotEmpty(rateInfoMap)?String.valueOf(rateInfoMap.get("rateNameEas")):null);//费目名称,取金蝶代码
//            zxDetailDTO.setF_JFDW();//计费单位,取金蝶代码D
            zxDetailDTO.setF_JLDW(e.getUnitName());//计量单位,取金蝶代码
            zxDetailDTO.setF_JFSL(e.getMea());//数量（计费量）
            zxDetailDTO.setF_BZBH(BZBH);
            zxDetailDTO.setF_YBDJ(BigDecimal.ZERO);
            zxDetailDTO.setF_YBJFJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBZKE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBFYJMJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBYSJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSJHSDJ(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSJDJ(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSE(BigDecimal.ZERO);
            zxDetailDTO.setF_YBSRJE(BigDecimal.ZERO);
            zxDetailDTO.setF_HL(BigDecimal.ONE);
            zxDetailDTO.setF_HSDJ(e.getPieceAmount());
            zxDetailDTO.setF_JFJE(e.getAmount());
            zxDetailDTO.setF_YHJE(BigDecimal.ZERO);
            zxDetailDTO.setF_JMJE(BigDecimal.ZERO);
            zxDetailDTO.setF_YSJE(e.getAmount());
            zxDetailDTO.setF_SZ(e.getTaxRate());
            zxDetailDTO.setF_SE(e.getTaxAmount());
            zxDetailDTO.setF_BHSJE(BigDecimal.ZERO);
//            zxDetailDTO.setF_TYDH();//提运单号
//            zxDetailDTO.setF_ZYWTDBH();//作业委托单编号
            zxDetailDTO.setF_BOWEI(String.valueOf(shipInfoMap.get("berthName")));//泊位
            zxDetailDTO.setF_NWMBS(String.valueOf(shipInfoMap.get("tradeType")));//内外贸
            zxDetailDTO.setF_JCKBS(String.valueOf(shipInfoMap.get("impExp")));//进出口
            zxDetailDTO.setF_TBSC(String.valueOf(shipInfoMap.get("停泊时长")));//停泊时长
//            zxDetailDTO.setF_TBSCDW("小时");//停泊时长时间单位
            zxDetailDTO.setF_ZXLX(String.valueOf(shipInfoMap.get("loadUnload")));//装卸类型
            zxDetailDTO.setF_BZFS(String.valueOf(shipInfoMap.get("packing")));//包装方式
//            zxDetailDTO.setF_YSFS();//运输方式
            zxDetailDTO.setF_KBRQ(String.valueOf(shipInfoMap.get("berthTime")));//靠泊日期
//            zxDetailDTO.setF_JD();//净吨
//            zxDetailDTO.setF_ZYXM();//作业项目
//            zxDetailDTO.setF_ZYXM();//作业项目
//            zxDetailDTO.setF_DCTS();//堆存天数
//            zxDetailDTO.setF_SHIPNO();//船号
            zxDetailDTO.setF_SHIPMC(String.valueOf(shipInfoMap.get("shipName")));//中文船名
            zxDetailDTO.setF_SHIPMCENG(String.valueOf(shipInfoMap.get("shipNameEn")));//英文船名
//            zxDetailDTO.setF_ZDBH();//账单编号
            zxDetailDTO.setF_HANGCI(String.valueOf(shipInfoMap.get("voyage")));//航次

            zxDetailDTO.setF_HLBH(CollectionUtil.isNotEmpty(rateInfoMap)?String.valueOf(rateInfoMap.get("cargoCategoryCode")):null);//货类编号
            zxDetailDTO.setF_HLMC(CollectionUtil.isNotEmpty(rateInfoMap)?String.valueOf(rateInfoMap.get("cargoCategoryName")):null);//货类名称
            zxDetailDTO.setF_HWBM(CollectionUtil.isNotEmpty(rateInfoMap)?String.valueOf(rateInfoMap.get("cargoCode")):null);//货物编码
            zxDetailDTO.setF_HWMC(CollectionUtil.isNotEmpty(rateInfoMap)?String.valueOf(rateInfoMap.get("cargoName")):null);//货物名称

//            zxDetailDTO.setF_HWJJDZ();//货物交接地址
//            zxDetailDTO.setF_GSSJ();//管输时间
//            zxDetailDTO.setF_NOTE();//备注
//            zxDetailDTO.setF_ZY();//摘要
            zxDetailDTO.setF_KHBH(customerDTO.getCustomerCodeEas());//金蝶,客户编号
            zxDetailDTO.setF_KHMC(customerDTO.getCustomerNameEas());//金蝶,客户名称
//            zxDetailDTO.setF_BMBH();//部门编号
//            zxDetailDTO.setF_BMMC();//部门名称
//            zxDetailDTO.setF_SCHLBH();//生产货类编号
//            zxDetailDTO.setF_SCHLMC();//生产货类名称
//            zxDetailDTO.setF_SCHWBM();//生产货物名称
//            zxDetailDTO.setF_SCHWMC();//生产货物名称
//            zxDetailDTO.setF_FPHM();//发票号码
//            zxDetailDTO.setF_MSLX();//免税标志
//            zxDetailDTO.setF_BGFLX();//包干费类型
//            zxDetailDTO.setF_CMHC(String.valueOf(shipInfoMap.get("shipVoyage")));//船名航次
            list.add(zxDetailDTO);
        }
        financialSharingDTO.setDataDetail(list);
    }


    /**
     * 租赁计费单，cnDn发票
     * @return
     */
    private void zlJfdByCnDnInvoice(Long id,String hc,String url){
        try{
            //获取发票信息
            TFdCreditDebitBillDTO dto = tFdCreditDebitBillMapper.getById(id);
            List<TFdCreditDebitBillDetailDTO> creditDebitBillDetailDTOS = tFdCreditDebitBillDetailMapper.getByParentId(id);
            TBusCustomerDTO customerDTO = customerMapper.getById(dto.getCustomerId());
            TFdInvoiceDTO invoiceDTO = tFdCreditDebitBillMapper.getInvoiceInfoBySysInvoiceCode(dto.getSysInvoiceCode());
            List<TFdInvoiceDetailDTO> invoiceDetailDTOS = tFdInvoiceDetailMapper.getListByInvoiceId(invoiceDTO.getId());
            //组装数据
            FinancialSharingDTO<ZlDetailDTO> financialSharingDTO = new FinancialSharingDTO();
            setFormByCnDnInvoice(dto,creditDebitBillDetailDTOS,invoiceDTO,financialSharingDTO);
            setZlDetailByCnDnInvoice(dto,creditDebitBillDetailDTOS,invoiceDTO, invoiceDetailDTOS,financialSharingDTO,customerDTO);
        }catch (Exception e){
            throw new BusinessRuntimeException(e.getMessage());
        }
    }

    /**
     * 设置租赁信息
     * @param dto
     * @param detailDTOS
     * @param financialSharingDTO
     */
    private void setZlDetailByCnDnInvoice(TFdCreditDebitBillDTO dto,List<TFdCreditDebitBillDetailDTO> detailDTOS,TFdInvoiceDTO invoiceDTO,List<TFdInvoiceDetailDTO> invoiceDetailDTOS,FinancialSharingDTO financialSharingDTO,TBusCustomerDTO customerDTO){
        Map<Long,List<TFdInvoiceDetailDTO>> invoiceMap = invoiceDetailDTOS.stream().collect(Collectors.groupingBy(e->e.getId()));
        List<ZlDetailDTO> list = Lists.newArrayList();
        for (TFdCreditDebitBillDetailDTO e : detailDTOS) {
            TFdInvoiceDetailDTO invoiceDetailDTO = CollectionUtil.isNotEmpty(invoiceMap.get(e.getInvoiceDetailId()))?invoiceMap.get(e.getInvoiceDetailId()).get(0):new TFdInvoiceDetailDTO();
            Map<String,Object> rateInfoMap = tBusCargoInfoMapper.getRateInfo(invoiceDetailDTO.getCargoInfoId());
//            Long shipVoyageItemId = invoiceDetailDTO.getShipvoyageItemId();
//            Map<String,Object> shipInfoMap = shipVoyageMapper.getShipInfoByItemId(shipVoyageItemId);

            ZlDetailDTO zlDetailDTO = new ZlDetailDTO();
            zlDetailDTO.setF_MXID(String.valueOf(e.getId()));
            zlDetailDTO.setF_JFDID(String.valueOf(dto.getId()));
            zlDetailDTO.setF_JFRQ(DateUtils.formatDate(dto.getCndnTime(),"yyyy-MM-dd HH:mm:ss"));
            zlDetailDTO.setF_FMBH(String.valueOf(rateInfoMap.get("rateCodeEas")));//费目代码,取金蝶代码`
            zlDetailDTO.setF_FMMC(String.valueOf(rateInfoMap.get("rateNameEas")));//费目名称,取金蝶代码
            zlDetailDTO.setF_JLDW(e.getUnitName());//计量单位,取金蝶代码
            zlDetailDTO.setF_JFSL(e.getMea());//数量（计费量）
            zlDetailDTO.setF_BZBH(BZBH);
            zlDetailDTO.setF_YBDJ(BigDecimal.ZERO);
            zlDetailDTO.setF_YBJFJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBZKE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBFYJMJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBYSJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSJHSDJ(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSJDJ(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSE(BigDecimal.ZERO);
            zlDetailDTO.setF_YBSRJE(BigDecimal.ZERO);
            zlDetailDTO.setF_HL(BigDecimal.ONE);
            zlDetailDTO.setF_JFJE(e.getAmount());
            zlDetailDTO.setF_YHJE(BigDecimal.ZERO);
            zlDetailDTO.setF_JMJE(BigDecimal.ZERO);
            zlDetailDTO.setF_YSJE(e.getAmount());
            zlDetailDTO.setF_SZ(e.getTaxRate());
            zlDetailDTO.setF_SE(e.getTaxAmount());
            zlDetailDTO.setF_BHSJE(BigDecimal.ZERO);
//            zxDetailDTO.setF_NOTE();//备注
//            zxDetailDTO.setF_ZY();//摘要
            zlDetailDTO.setF_KHBH(customerDTO.getCustomerCodeEas());//金蝶,客户编号
            zlDetailDTO.setF_KHMC(customerDTO.getCustomerNameEas());//金蝶,客户名称
//            zlDetailDTO.setF_BMBH();//部门编号
//            zlDetailDTO.setF_BMMC();//部门名称
//            zlDetailDTO.setF_FPHM();//发票号码
//            zlDetailDTO.setF_ZCBM();//资产编码
//            zlDetailDTO.setF_ZCMC();//资产名称
//            zlDetailDTO.setF_ZLXMBH();//租赁项目编码
//            zlDetailDTO.setF_ZLXMMC();//租赁项目名称
//            zlDetailDTO.setF_JDYWLXBH();//金蝶业务类型编号
//            zlDetailDTO.setF_JDYWLXMC();//金蝶业务类型名称
//            zlDetailDTO.setF_ZLYWLXBH();//租赁业务类型编号
//            zlDetailDTO.setF_ZLYWLXMC();//租赁业务类型名称
//            zlDetailDTO.setF_WLLXBH();//金蝶往来类型编号
//            zlDetailDTO.setF_WLLXMC();//金蝶往来类型名称
//            zlDetailDTO.setF_GDZCZLLX();//固定资产租赁类型
//            zlDetailDTO.setF_MSLX();//免税标志
            list.add(zlDetailDTO);
        }
        financialSharingDTO.setDataDetail(list);
    }

}

