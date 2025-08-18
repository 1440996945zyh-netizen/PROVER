package com.yy.ppm.finance.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillSearchDTO;
import com.yy.ppm.finance.mapper.TFdCreditDebitBillDetailMapper;
import com.yy.ppm.finance.mapper.TFdCreditDebitBillMapper;
import com.yy.ppm.finance.service.TFdCreditDebitBillService;
import com.yy.ppm.system.mapper.SysParameterMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBill)ServiceImpl
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Service
public class TFdCreditDebitBillServiceImpl implements TFdCreditDebitBillService {

    @Resource
    private TFdCreditDebitBillMapper tFdCreditDebitBillMapper;

    @Resource
    private TFdCreditDebitBillDetailMapper cndnBillDetailMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private SysParameterMapper sysParameterMapper;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdCreditDebitBillDTO> getList(TFdCreditDebitBillSearchDTO searchDTO) {

        Pages<TFdCreditDebitBillDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdCreditDebitBillMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TFdCreditDebitBillDTO getDetail(Long id) {
        TFdCreditDebitBillDTO result = tFdCreditDebitBillMapper.getById(id);
        result.setDetailList(cndnBillDetailMapper.getByParentId(id));
        return result;
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(TFdCreditDebitBillDTO dto) {

        if(dto==null){
            throw new BusinessRuntimeException("请完善表格信息");
        }
        dto.setId(snowflake.nextId());
        dto.setStatus(1L);

        SimpleDateFormat tmpFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(new Date());
        try {
            dto.setCndnTime(simpleDateFormat.parse(format));

        }catch (Exception e){
            throw new BusinessRuntimeException("开具日期生成异常");
        }
/*        TFdInvoiceDTO invoiceDTO =  tFdCreditDebitBillMapper.getInvoiceInfoBySysInvoiceCode(dto.getSysInvoiceCode());
        if(invoiceDTO==null){
            throw new BusinessRuntimeException("未查询到发票信息");
        }
        if(invoiceDTO.getInvoiceAmount()==null){
            throw new BusinessRuntimeException("未查询到发票的金额信息");
        }*/
        BigDecimal eliminateAmount = dto.getEliminateAmount();

        //贷方
        if(dto.getCndnType()==1L){
            dto.setCndnCode(autoCNDNCode("CN"));
            //贷方类型
            dto.setEliminateAmount(BigDecimal.ZERO);
                // 保存子列表
                if(CollectionUtils.isEmpty(dto.getDetailList())){
                    throw new BusinessRuntimeException("详情为空！");
                }
            TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO = dto.getDetailList().get(0);
            List<String> rateItemCodeResult = cndnBillDetailMapper.getFeeItemList(tFdCreditDebitBillDetailDTO);
            if(rateItemCodeResult==null || rateItemCodeResult.size()<1){
                throw new BusinessRuntimeException("获取费目类型失败");
            }
            if (CollectionUtils.isEmpty(rateItemCodeResult)){
                throw new BusinessRuntimeException("未能通过标准费率找到对应的费目类型");
            }
            if(rateItemCodeResult.size()>1){
                throw new BusinessRuntimeException("匹配到多种费目类型");
            }
            if("1".equals(rateItemCodeResult.get(0))){
                dto.setPrepaymentTypeCode("30");
            }else if("2".equals(rateItemCodeResult.get(0))){
                dto.setPrepaymentTypeCode("10");
            }else if("3".equals(rateItemCodeResult.get(0))){
                dto.setPrepaymentTypeCode("40");
            }else {
                dto.setPrepaymentTypeCode(rateItemCodeResult.get(0));
            }
            dto.getDetailList().forEach(o->{
                    o.setId(snowflake.nextId());
                    o.setCndnId(dto.getId());
                    o.setCreateTimeNew(tmpFormat.format(date));
                    dto.setEliminateAmount(dto.getEliminateAmount().add(o.getAmount()));
                });
//                if(invoiceDTO.getInvoiceAmount().compareTo(dto.getEliminateAmount())==-1){
                if(eliminateAmount.compareTo(dto.getEliminateAmount())==-1){
                    throw new BusinessRuntimeException(" 实际冲销金额大于发票金额！");
                }
            cndnBillDetailMapper.insertBatch(dto.getDetailList());
            boolean result = tFdCreditDebitBillMapper.insert(dto) == 1;
            return result;
        }else if(dto.getCndnType()==2L){
            dto.setCndnCode(autoCNDNCode("DN"));
                //借方类型
                dto.setEliminateAmount(BigDecimal.ZERO);

                // 保存子列表
                if(CollectionUtils.isEmpty(dto.getDetailList())){
                    throw new BusinessRuntimeException("多收款发票为空！");
                }
            TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO = dto.getDetailList().get(0);
            List<String> rateItemCodeResult = cndnBillDetailMapper.getFeeItemList(tFdCreditDebitBillDetailDTO);
            if (CollectionUtils.isEmpty(rateItemCodeResult)){
                throw new BusinessRuntimeException("未能通过标准费率找到对应的费目类型");
            }
            if(rateItemCodeResult.size()>1){
                throw new BusinessRuntimeException("匹配到多种费目类型");
            }
            if("1".equals(rateItemCodeResult.get(0))){
                dto.setPrepaymentTypeCode("30");
            }else if("2".equals(rateItemCodeResult.get(0))){
                dto.setPrepaymentTypeCode("10");
            }else if("3".equals(rateItemCodeResult.get(0))){
                dto.setPrepaymentTypeCode("40");
            }else {
                dto.setPrepaymentTypeCode(rateItemCodeResult.get(0));
            }
//
                dto.getDetailList().forEach(o->{
                    o.setId(snowflake.nextId());
                    o.setCndnId(dto.getId());
                    o.setCreateTimeNew(tmpFormat.format(date));
                    dto.setEliminateAmount(dto.getEliminateAmount().add(o.getAmount()));

                });

            if(eliminateAmount.compareTo(dto.getEliminateAmount())==-1){
                throw new BusinessRuntimeException("发票金额大于冲销金额！");
            }

            cndnBillDetailMapper.insertBatch(dto.getDetailList());
            if(dto.getCndnType()==2L){
                dto.setHasNotReceiptAmount(dto.getEliminateAmount());
            }
            return tFdCreditDebitBillMapper.insert(dto) == 1;

        }else{
            throw new BusinessRuntimeException("没有符合条件的cndnType");
        }
    }

    /**
     * 修改
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TFdCreditDebitBillDTO dto) {
        return tFdCreditDebitBillMapper.update(dto) == 1;
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tFdCreditDebitBillMapper.deleteById(id) == 1;

    }



    /**
     * 获取发票信息
     * @param dto
     * @return
     */
    @Override
    public List<TFdCreditDebitBillDetailDTO> getInvoiceList(TFdCreditDebitBillSearchDTO dto) {

        List<TFdCreditDebitBillDetailDTO> invoiceList = null;

        List<TFdCreditDebitBillDTO> tmpCNDNList = tFdCreditDebitBillMapper.getListBySysInvoiceCode(dto.getSysInvoiceCode());
        if(!CollectionUtils.isEmpty(tmpCNDNList)){
            throw new BusinessRuntimeException("发票已冲销！");
        }
        //贷方  CN
        if(dto.getCndnType()==1L){
            //CN
            invoiceList = tFdCreditDebitBillMapper.getInvoiceList(dto);
            if(CollectionUtils.isEmpty(invoiceList)){
                throw new BusinessRuntimeException("没有查询到发票信息");
            }
            if(dto.getCndnBillTypeCode()==1L){
                //多收款发票只能查询货方发票
                if(invoiceList.get(0).getInvoiceTypeCode()!=10L){
                    throw new BusinessRuntimeException("请输入货物发票编号！");
                }
            }
        }

        //借方 DN
        if(dto.getCndnType()==2L){
            //DN
            invoiceList = tFdCreditDebitBillMapper.getDnList(dto);
            if(CollectionUtils.isEmpty(invoiceList)){
                throw new BusinessRuntimeException("没有查询到发票信息");
            }
            if("2".equals(invoiceList.get(0).getCndnType())){
                throw new BusinessRuntimeException("请输入贷方发票编号！");
            }
        }

        for (int i = 0; i < invoiceList.size(); i++) {
            invoiceList.get(i).setSerialNumber(i+1);
        }

        return invoiceList;
    }
    /**
     * 自动生成编号
     * @param cndn
     * @return
     */
    public String autoCNDNCode(String cndn){
        String tmpCNDNCode=  tFdCreditDebitBillMapper.getCNDNCode(cndn);
        int year = LocalDateTime.now().getYear();
        String.valueOf(year).substring(2);
        if(StringUtils.isBlank(tmpCNDNCode)){
            return cndn+String.valueOf(year).substring(2)+String.format("%05d", 2);
        }
        return cndn+String.valueOf(year).substring(2)+String.format("%05d", Integer.valueOf(tmpCNDNCode.substring(4))+1);
    }

    /**
     * 接方贷方作废 CNDN作废 借贷作废
     * @param dto
     * @return
     */
    @Override
    public boolean doVoid(TFdCreditDebitBillDTO dto) {
        if(dto.getId()==null){
            throw new BusinessRuntimeException("作废的数据ID为空");
        }
        TFdCreditDebitBillDTO tmpDto = tFdCreditDebitBillMapper.getById(dto.getId());
        if(tmpDto==null){
            throw new BusinessRuntimeException("没有找到对应的借贷票据");
        }
        if(tmpDto.getDebtorpaymentId()!=null){
            throw new BusinessRuntimeException("已生成收据！");
        }
        if(StringUtils.isBlank(tmpDto.getCndnCode())){
            throw new BusinessRuntimeException("借贷发票开具异常，请刷新页面重试");
        }

        dto.setStatus(2L);
        return tFdCreditDebitBillMapper.doVoid(dto)==1;
    }
    /**
     * 计算税率税额
     * @param dto
     * @return
     */
    @Override
    public TFdCreditDebitBillDetailDTO calculate(TFdCreditDebitBillDetailDTO dto) {
        if(dto==null){
            throw new BusinessRuntimeException("没有数据！");
        }
        if(dto.getOldRate()==null){
            throw new BusinessRuntimeException("请输入费率！");
        }
        if(dto.getTaxRate()==null){
            throw new BusinessRuntimeException("请输入税率！");
        }
        if(dto.getMea()==null){
            throw new BusinessRuntimeException("请输入数量！");
        }
        BigDecimal tmpRate = BigDecimal.ZERO;
        if(dto.getNewRate() == null){
            tmpRate = dto.getOldRate();
        }else {
            tmpRate = dto.getNewRate();
        }
        if(tmpRate.compareTo(BigDecimal.ZERO)==0){
            throw new BusinessRuntimeException("税率为0!");
        }
        //计算金额 金额=新费率*数量
        dto.setAmount(tmpRate.multiply(dto.getMea().multiply((dto.getNumberCount2()==null)?BigDecimal.ONE:dto.getNumberCount2())).setScale(2,BigDecimal.ROUND_HALF_UP));
        //税额=已开票金额*税率/（1+税率
        dto.setTaxAmount(dto.getAmount().multiply(dto.getTaxRate().divide(BigDecimal.valueOf(100L))).divide(BigDecimal.ONE.add(dto.getTaxRate().divide(BigDecimal.valueOf(100L))),2,BigDecimal.ROUND_HALF_UP));
        return dto;
    }

    @Override
    public List<TBusRateDTO> getRateList(TFdCreditDebitBillSearchDTO dto) {
        return tFdCreditDebitBillMapper.getRateList(dto);
    }
}

