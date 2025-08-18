package com.yy.ppm.finance.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.finance.bean.dto.*;
import com.yy.ppm.finance.bean.po.TFdDebtorpaymentDetailPO;
import com.yy.ppm.finance.controller.TFdDebtorpaymentController;
import com.yy.ppm.finance.mapper.TFdBankCustomerPrepaymentMapper;
import com.yy.ppm.finance.mapper.TFdDebtorpaymentDetailMapper;
import com.yy.ppm.finance.mapper.TFdDebtorpaymentMapper;
import com.yy.ppm.finance.mapper.TFdInvoiceMapper;
import com.yy.ppm.finance.service.TFdDebtorpaymentService;
import com.yy.ppm.statement.bean.dto.costShip.TDisShipvoyageItemDTO;
import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpayment)ServiceImpl
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Service
public class TFdDebtorpaymentServiceImpl implements TFdDebtorpaymentService {

    @Resource
    private TFdDebtorpaymentMapper debtorpaymentMapper;
    @Resource
    private TFdDebtorpaymentDetailMapper detailMapper;
    @Resource
    private TFdInvoiceMapper invoiceMapper;
    @Resource
    private TFdBankCustomerPrepaymentMapper prepaymentMapper;
    @Resource
    private SysFileService sysFileService;
    @Autowired
    private CommonService commonService;

    @Resource
    private Snowflake snowflake;

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(TFdDebtorpaymentController.class);

    @Getter
    public enum PREPAY_TYPE_CODE {
        _10("10", "货方"),
        _30("30", "船方"),
        _40("40", "杂项");
        PREPAY_TYPE_CODE(String code, String comment) {this.code = code;this.comment = comment;}
        private String code;
        private String comment;
    }

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdDebtorpaymentDTO> getList(TFdDebtorpaymentSearchDTO searchDTO) {

        Pages<TFdDebtorpaymentDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return debtorpaymentMapper.getList(searchDTO);
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
    public TFdDebtorpaymentDTO getDetail(Long id) {
        TFdDebtorpaymentDTO  result= debtorpaymentMapper.getById(id);
        List<TFdDebtorpaymentDetailDTO> tmpDetailList = detailMapper.getByParentId(id);

        if(!CollectionUtils.isEmpty(tmpDetailList)){
            List<TFdDebtorpaymentDetailDTO> detailResult = new ArrayList<>(tmpDetailList.size());

            Map<Long, List<TFdDebtorpaymentDetailDTO>> tmpMap = tmpDetailList.stream().collect(Collectors.groupingBy(TFdDebtorpaymentDetailDTO::getType));
            // 10 预缴 20 补缴 30 退还           //  type类型（1：发票，2：预缴
            List<TFdDebtorpaymentDetailDTO> tmpInvoiceList = tmpMap.get(1L);
            List<TFdDebtorpaymentDetailDTO> tmpPrepayList = tmpMap.get(2L);
            List<TFdDebtorpaymentDetailDTO> tmpCNList = tmpMap.get(3L);
            List<TFdDebtorpaymentDetailDTO> tmpDNList = tmpMap.get(4L);
            if(tmpPrepayList!=null&&tmpPrepayList.size()>0){
                for (TFdDebtorpaymentDetailDTO o : tmpPrepayList) {
                    if(o.getDebtorpayPaymentTypeCode()==null){
                        continue;
                    }
                    if(o.getDebtorpayPaymentTypeCode()==10L){
                        o.setSysInvoicePrepayNo("预缴:"+o.getSysInvoicePrepayNo());
                    }else if(o.getDebtorpayPaymentTypeCode()==20L){
                        o.setSysInvoicePrepayNo("补缴:"+o.getSysInvoicePrepayNo());
                    }
                }
            }
            if(!CollectionUtils.isEmpty(tmpPrepayList)){
                detailResult.addAll(tmpPrepayList);
            }
            if (!CollectionUtils.isEmpty(tmpInvoiceList)){
                detailResult.addAll(tmpInvoiceList);
            }
            if (!CollectionUtils.isEmpty(tmpCNList)){
                detailResult.addAll(tmpCNList);
            }
            if (!CollectionUtils.isEmpty(tmpDNList)){
                detailResult.addAll(tmpDNList);
            }
            detailResult.sort((k1,k2)->{return k1.getSerialNumber()-k2.getSerialNumber();});
            result.setReceiptList(detailResult);
        }
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
    public boolean doSave(TFdDebtorpaymentDTO dto) {
        final String methodName = "TFdDebtorpaymentController:add:doSave";

        LOGGER.enter(methodName,"新增收据 doSave 保存方法开始执行:dto:"+dto.toString());

        if(dto == null){
            throw new BusinessRuntimeException("数据为空！");
        }
        if (dto.getFormDataDo()==null){
            throw new BusinessRuntimeException("提示框种填写的信息为空！");
        }
        if(dto.getDebtorpaymentTime() == null){
            throw new BusinessRuntimeException("请选择开具日期！");
        }
        if(dto.getPrepaymentTypeCode()==null){
            throw new BusinessRuntimeException("请选择对账类型！");
        }

        dto.setId(snowflake.nextId());
        dto.setDebtorpaymentNo(getNo());

        List<TFdDebtorpaymentDetailDTO> detailList = dto.getReceiptList();
        if(CollectionUtils.isEmpty(detailList)){
            if (dto.getFormDataDo()==null){
                LOGGER.exit(methodName,"新增收据 doSave 保存方法结束:");
                return debtorpaymentMapper.insert(dto) == 1;
            }
        }
       //设置收据金额
        if(dto.getFormDataDo()!=null){
            dto.setAmount(dto.getFormDataDo().getInvoicePrepayAmount().setScale(2,BigDecimal.ROUND_HALF_UP));
        }else {
            dto.setAmount(BigDecimal.ZERO);
        }
        dto.setStatus(1L);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        for (int i = 0; i < detailList.size(); i++) {
            detailList.get(i).setSerialNumber(i+1);
            detailList.get(i).setId(snowflake.nextId());
            detailList.get(i).setDebtorpaymentId(dto.getId());
            detailList.get(i).setCreateTimeNew(sdf.format(new Date()));
        }

        Map<Long, List<TFdDebtorpaymentDetailDTO>> tmpMap = detailList.stream().collect(Collectors.groupingBy(TFdDebtorpaymentDetailDTO::getType));
        List<TFdDebtorpaymentDetailDTO> tmpInvoiceList= tmpMap.get(1L);
        List<TFdDebtorpaymentDetailDTO> tmpPrepayList= tmpMap.get(2L);
        List<TFdDebtorpaymentDetailDTO> tmpCNList= tmpMap.get(3L);
        List<TFdDebtorpaymentDetailDTO> tmpDNList= tmpMap.get(4L);


        //SpringUtils.getBean(this.getClass()).handlerShipInvoice(tmpInvoiceList);

        TFdDebtorpaymentDetailDTO tmpDetail = dto.getFormDataDo();


        dto.setTmpAmount( BigDecimal.ZERO);
        if(!CollectionUtils.isEmpty(tmpInvoiceList)){
            tmpInvoiceList.forEach(o->{
                dto.setTmpAmount(dto.getTmpAmount().subtract(o.getHasNotReceiptAmount()));
            });
        }
        List<TFdDebtorpaymentDetailDTO> tmpPrepayList1 = new ArrayList<>();
        if(!CollectionUtils.isEmpty(tmpPrepayList)){
            tmpPrepayList.forEach(o->{
                dto.setTmpAmount( dto.getTmpAmount().add(o.getInvoicePrepayAmount()));

                TFdDebtorpaymentDetailDTO tFdDebtorpaymentDetailDTO = new TFdDebtorpaymentDetailDTO();
                TFdDebtorpaymentDetailDTO prePayList = debtorpaymentMapper.getPrepayById(o.getInvoicePrepayId());
                tFdDebtorpaymentDetailDTO.setInvoicePrepayAmount(o.getInvoicePrepayAmount().add(prePayList.getUtilizedAmount()));
                tFdDebtorpaymentDetailDTO.setInvoicePrepayId(o.getInvoicePrepayId());
                tFdDebtorpaymentDetailDTO.setDebtorpaymentId(o.getDebtorpaymentId());
                tmpPrepayList1.add(tFdDebtorpaymentDetailDTO);

            });
        }
        //更新cndn
        if(!CollectionUtils.isEmpty(tmpCNList)){
            tmpCNList.forEach(o->{
                dto.setTmpAmount( dto.getTmpAmount().add(o.getInvoicePrepayAmount()));
            });
        }
        if(!CollectionUtils.isEmpty(tmpDNList)){
            tmpDNList.forEach(o->{
                dto.setTmpAmount(dto.getTmpAmount().subtract(o.getHasNotReceiptAmount()));
            });
        }
        // 10 预缴 20 补缴 30 退还
        //当金额是负数的时候 不允许退还
        if(dto.getTmpAmount().compareTo(BigDecimal.ZERO)==-1){
            if(tmpDetail.getDebtorpayPaymentTypeCode()==30L){
                throw new BusinessRuntimeException("总金额为负数，不允许退还！");
            }
            if(tmpDetail.getDebtorpayPaymentTypeCode()==10L){
                throw new BusinessRuntimeException("总金额为负数，不允许预缴！");
            }
        }else if (dto.getTmpAmount().compareTo(BigDecimal.ZERO)==1){
            if(tmpDetail.getDebtorpayPaymentTypeCode()==20L){
                throw new BusinessRuntimeException("总金额大于零，请选择预缴或退还！");
            }
            if(tmpDetail.getDebtorpayPaymentTypeCode()!=null && tmpDetail.getDebtorpayPaymentTypeCode()==40L){
                throw new BusinessRuntimeException("总金额大于零，请选择预缴或退还");
            }
        }


        //对该付款收据生成的预缴进行处理 0元不生成预缴 补缴 也不会生成数据
        if ((tmpDetail.getDebtorpayPaymentTypeCode()==null||tmpDetail.getDebtorpayPaymentTypeCode()!=40L)&&BigDecimal.ZERO.compareTo(dto.getFormDataDo().getInvoicePrepayAmount())!=0){
                //拼装预缴数据
                TFdBankCustomerPrepaymentDTO tmpPrepaymentDTO = new TFdBankCustomerPrepaymentDTO();
                tmpPrepaymentDTO.setId(snowflake.nextId());
                //预缴编号
                String autoNum = commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.BIG_BANK_CUSTOMER_PREPAYMENT,"");
                tmpPrepaymentDTO.setPrepaymentCode(autoNum);
                tmpPrepaymentDTO.setCompanyId(dto.getCompanyId());
                tmpPrepaymentDTO.setCompanyName(dto.getCompanyName());
                tmpPrepaymentDTO.setCustomerId(dto.getCustomerId());
                tmpPrepaymentDTO.setCustomerName(dto.getCustomerName());
                Date date = new Date();
                try {
                    String format = sdf.format(date);
                    date = sdf.parse(format);
                }catch (Exception e){
                    throw new BusinessRuntimeException("时间转换错误");
                }
                tmpPrepaymentDTO.setPrepaymentTime(date);
                tmpPrepaymentDTO.setStatus(1L);
                tmpPrepaymentDTO.setPrepaymentAmount(tmpDetail.getInvoicePrepayAmount());
                tmpPrepaymentDTO.setPaymentMethodCode(tmpDetail.getPaymentMethodCode());//付款方式
                tmpPrepaymentDTO.setPaymentMethodName(tmpDetail.getPaymentMethodName());//付款方式
                tmpPrepaymentDTO.setBankId(tmpDetail.getBankId());//设置付款银行
                tmpPrepaymentDTO.setBankName(tmpDetail.getBankName());//设置付款银行
                //设置子表数据
                tmpDetail.setId(snowflake.nextId());
                tmpDetail.setDebtorpaymentId(dto.getId());
                tmpDetail.setType(2L);
                tmpDetail.setInvoicePrepayTime(tmpPrepaymentDTO.getPrepaymentTime());
                tmpDetail.setInvoicePrepayId(tmpPrepaymentDTO.getId());
                tmpDetail.setSysInvoicePrepayNo(tmpPrepaymentDTO.getPrepaymentCode());
                if(CollectionUtils.isEmpty(detailList)){
                    tmpDetail.setSerialNumber(1);
                }else {
                    tmpDetail.setSerialNumber(detailList.size()+1);
                }
                tmpPrepaymentDTO.setPrepaymentTypeCode(dto.getPrepaymentTypeCode());
                tmpPrepaymentDTO.setPrepaymentTypeName(dto.getPrepaymentTypeName());

                // 10 预缴 20 补缴 30 退还            类型（1：发票，2：预缴 3:CN 4,DN

                if(tmpDetail.getDebtorpayPaymentTypeCode()==10L){
                    tmpPrepaymentDTO.setSourceDebtorpaymentId(dto.getId());
                    tmpPrepaymentDTO.setUtilizedAmount(BigDecimal.ZERO);
                    tmpPrepaymentDTO.setPrepayModeCode("10");
                    prepaymentMapper.insert(tmpPrepaymentDTO);

                }
                if(tmpDetail.getDebtorpayPaymentTypeCode()==20L){
                    dto.setAmount(tmpDetail.getInvoicePrepayAmount());
                    tmpPrepaymentDTO.setSourceDebtorpaymentId(dto.getId());
                    tmpPrepaymentDTO.setDebtorpaymentId(dto.getId());
                    tmpPrepaymentDTO.setUtilizedAmount(tmpDetail.getInvoicePrepayAmount());
                    prepaymentMapper.insert(tmpPrepaymentDTO);
                }
                if(tmpDetail.getDebtorpayPaymentTypeCode()==30L){
                    if(dto.getRepayAmount()==null){
                        dto.setRepayAmount(BigDecimal.ZERO);
                    }
                    BigDecimal subtractAmount = tmpDetail.getInvoicePrepayAmount().subtract(dto.getRepayAmount());
                    if(BigDecimal.ZERO.compareTo(subtractAmount)>0){
                        throw new BusinessRuntimeException("退还金额不能大于剩余的金额");
                    }
                    if(BigDecimal.ZERO.compareTo(subtractAmount)<0){
                        TFdBankCustomerPrepaymentDTO tmpPrePay = new TFdBankCustomerPrepaymentDTO();
                        BeanUtils.copyProperties(tmpPrepaymentDTO,tmpPrePay);
                        tmpPrePay.setSourceDebtorpaymentId(dto.getId());
                        tmpPrePay.setUtilizedAmount(BigDecimal.ZERO);
                        tmpPrePay.setPrepaymentAmount(subtractAmount);
                        prepaymentMapper.insert(tmpPrePay);

                        TFdDebtorpaymentDetailDTO tmpDetailPrepay = new TFdDebtorpaymentDetailDTO();
                        BeanUtils.copyProperties(tmpDetail, tmpDetailPrepay);
                        tmpDetailPrepay.setId(snowflake.nextId());
                        tmpDetailPrepay.setInvoicePrepayId(tmpPrePay.getId());
                        tmpDetailPrepay.setCreateTimeNew(sdf.format(new Date()) );
                        tmpDetailPrepay.setInvoicePrepayAmount(subtractAmount);
                        tmpDetailPrepay.setDebtorpayPaymentTypeCode(10L);
                        tmpDetailPrepay.setDebtorpayPaymentTypeName("预缴");
                        detailList.add(tmpDetailPrepay);
                    }
                    tmpDetail.setSysInvoicePrepayNo("退还");
                    tmpDetail.setInvoicePrepayAmount(dto.getRepayAmount());
                }
                tmpDetail.setCreateTimeNew(sdf.format(new Date()) );
                detailList.add(tmpDetail);
        }


        if(!CollectionUtils.isEmpty(tmpInvoiceList)){
            if(tmpDetail.getDebtorpayPaymentTypeCode()!=null && tmpDetail.getDebtorpayPaymentTypeCode()==40L){
                if(tmpPrepayList!=null && tmpPrepayList.size()>0){
                    for (TFdDebtorpaymentDetailDTO tmpDto : tmpInvoiceList) {
                        tmpDto.setHasNotReceiptAmount(tmpDto.getHasNotReceiptAmount().subtract(tmpPrepayList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                }
                if(tmpCNList!=null && tmpCNList.size()>0){
                    for (TFdDebtorpaymentDetailDTO tmpDto : tmpInvoiceList) {
                        tmpDto.setHasNotReceiptAmount(tmpDto.getHasNotReceiptAmount().subtract(tmpCNList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                }
                for (TFdDebtorpaymentDetailDTO tmpDto : tmpInvoiceList) {
                    if(tmpDto.getHasNotReceiptAmount().compareTo(BigDecimal.ZERO)>0){
                        tmpDto.setReceiptStatus("20");
                    }
                }
            }else{
                for (TFdDebtorpaymentDetailDTO tmpDto : tmpInvoiceList) {
                    tmpDto.setHasNotReceiptAmount(BigDecimal.ZERO);
                    tmpDto.setReceiptStatus("30");
                }
            }
            debtorpaymentMapper.updateInvoiceBatch(tmpInvoiceList);
        }
        if(!CollectionUtils.isEmpty(tmpPrepayList1)){
            debtorpaymentMapper.updatePrepayBatch(tmpPrepayList1);
        }
        //更新cndn
        if(!CollectionUtils.isEmpty(tmpCNList)){
            debtorpaymentMapper.updateCNDNBillBatch(tmpCNList);
        }
        if(!CollectionUtils.isEmpty(tmpDNList)){
            if(tmpDetail.getDebtorpayPaymentTypeCode()!=null && tmpDetail.getDebtorpayPaymentTypeCode()==40L){
                for (TFdDebtorpaymentDetailDTO tmpDto : tmpDNList) {
                    if(tmpPrepayList!=null && tmpPrepayList.size()>0){
                        tmpDto.setHasNotReceiptAmount(tmpDto.getHasNotReceiptAmount().subtract(tmpPrepayList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                    if(tmpCNList!=null && tmpCNList.size()>0){
                        tmpDto.setHasNotReceiptAmount(tmpDto.getHasNotReceiptAmount().subtract(tmpCNList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                }
                for (TFdDebtorpaymentDetailDTO tmpDto : tmpDNList) {
                    if(tmpDto.getHasNotReceiptAmount().compareTo(BigDecimal.ZERO)>0){
                        tmpDto.setReceiptStatus("20");
                    }
                }
            }else{
                for (TFdDebtorpaymentDetailDTO tmpDto : tmpDNList) {
                    tmpDto.setHasNotReceiptAmount(BigDecimal.ZERO);
                    tmpDto.setReceiptStatus("30");
                }
            }
            debtorpaymentMapper.updateCNDNBillBatch(tmpDNList);
        }

        //作废（ ，，2） ，，新增（"FKSJ",,1）  补缴退还
        detailMapper.insertBatch(detailList);
        if(tmpDetail.getDebtorpayPaymentTypeCode()!=null){
            dto.setDebTypeCode(tmpDetail.getDebtorpayPaymentTypeCode().toString());
            dto.setDebTypeName(tmpDetail.getDebtorpayPaymentTypeName());
        }
        return debtorpaymentMapper.insert(dto) == 1;
    }

    @Override
    public boolean update(TFdDebtorpaymentDTO dto) {
        return debtorpaymentMapper.update(dto) == 1;
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return debtorpaymentMapper.deleteById(id) == 1;
    }

    /**
     * 获取发票,预缴,借贷票据
     * @param searchDTO
     * @return
     */
    @Override
    public List<TFdDebtorpaymentDetailDTO> getSearchList(TFdDebtorpaymentSearchDTO searchDTO) {
        if(searchDTO.getCompanyId()==null){
            //收款方
            throw  new BusinessRuntimeException("收款方为空");
        }
        if(searchDTO.getCustomerId()==null){
            //付款人
            throw  new BusinessRuntimeException("付款人为空");
        }
        //发票类型查询条件
        //货方类型   货方查货方发票、堆存费发票、货方预缴）    发票类型	10：货物发票，30：船舶发票，40：杂项发票，50 堆存费发票
        if (searchDTO.getPrepaymentTypeCode()==10L){
            searchDTO.setTypeList(Arrays.asList(10L,50L));
        }
        //获取发票表的数据
        List<TFdDebtorpaymentDetailDTO> invoiceList = debtorpaymentMapper.getInvoiceList(searchDTO);

        if(!CollectionUtils.isEmpty(invoiceList)){
            //发票的id集合
            List<Long> invoiceIdList = invoiceList.stream().map(x -> x.getId()).collect(Collectors.toList());

            //返回过滤之后的发票的id集合
            List<Map<String,Object>> invoiceAfterIdList = debtorpaymentMapper.getCargoInfoIdByInvoiceId(invoiceIdList,searchDTO.getCargoInfoId());
            if(invoiceAfterIdList!=null&&invoiceAfterIdList.size()>0){
                Map<String, String> collect = invoiceAfterIdList.stream().collect(Collectors.toMap(o -> String.valueOf(o.get("id")), v -> String.valueOf(v.get("cargoInfoId")), (k1, k2) -> k1));

                // 将List转换为Set以提高查找效率
//                Set<Long> invoiceAfterIdSet = new HashSet<>(invoiceAfterIdList);

                // 重新过滤发票列表
                if(searchDTO.getCargoInfoId()!=null){
                    invoiceList = invoiceList.stream()
                            .filter(invoice -> collect.get(invoice.getId().toString())!=null)
                            .collect(Collectors.toList());
                }
                if(invoiceList!=null&&invoiceList.size()>0){
                    for (TFdDebtorpaymentDetailDTO dto : invoiceList) {
                        if(collect.get(dto.getId().toString())!=null&&!"null".equals(String.valueOf(collect.get(dto.getId().toString())))){
                            dto.setCargoInfoNo(String.valueOf(collect.get(dto.getId().toString())));
                        }
                    }
                }
            }
        }
        invoiceList.forEach(o->{
            o.setType(1L);
        });
        //获取预缴的数据
        List<TFdDebtorpaymentDetailDTO> tmpPrePayList = debtorpaymentMapper.getPrePayList(searchDTO);
        if(searchDTO.getCargoInfoId()!=null){
            tmpPrePayList.stream().filter(x->x.getPrepayModeCode().equals("10") || x.getCargoInfoId().equals(searchDTO.getCargoInfoId()));
        }
        tmpPrePayList.forEach(o->{
                o.setType(2L);
        });
        List<TFdDebtorpaymentDetailDTO> cndnList = debtorpaymentMapper.getCNDNList(searchDTO);
        if(!CollectionUtils.isEmpty(cndnList)){
            //cndn的id集合
            List<Long> cndnIdList = cndnList.stream().map(x -> x.getId()).collect(Collectors.toList());
            //返回根据票货id过滤之后的cndn的id集合
            List<Map<String, Object>> cndnAfterIdList = debtorpaymentMapper.getCargoInfoIdBycndnId(cndnIdList, searchDTO.getCargoInfoId());
            if(cndnAfterIdList!=null&&cndnAfterIdList.size()>0){
                Map<String, String> collect = cndnAfterIdList.stream().collect(Collectors.toMap(o -> String.valueOf(o.get("id")), v -> String.valueOf(v.get("cargoInfoId")), (k1, k2) -> k1));

                if(searchDTO.getCargoInfoId()!=null){
                    cndnList = cndnList.stream()
                            .filter(x -> collect.get(x.getId().toString())!=null)
                            .collect(Collectors.toList());
                }

                if(!CollectionUtils.isEmpty(cndnList)){
                    for (TFdDebtorpaymentDetailDTO dto : cndnList) {
                        if(collect.get(dto.getId().toString())!=null&&!"null".equals(String.valueOf(collect.get(dto.getId().toString())))){
                            dto.setCargoInfoNo(String.valueOf(collect.get(dto.getId().toString())));
                        }
                    }
                }
            }

        }
        cndnList.forEach(o->{
            if(o.getCndnType()==1L){
                o.setType(3L);
            }
            if(o.getCndnType()==2L){
                o.setType(4L);
            }
        });
        //拼接返回
        ArrayList<TFdDebtorpaymentDetailDTO> resultList = new ArrayList<>(tmpPrePayList.size() + cndnList.size() + invoiceList.size());
        resultList.addAll(tmpPrePayList);
        resultList.addAll(invoiceList);
        resultList.addAll(cndnList);
        for (int i = 0; i < resultList.size(); i++) {
            resultList.get(i).setSerialNumber(i+1);
        }
        return resultList;
    }

    /**
     * 付款收据作废
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean voidDebtorpayById(Long id) {
        //获取对应的子列表
        TFdDebtorpaymentDTO dtoById = debtorpaymentMapper.getById(id);
        List<TFdDebtorpaymentDetailDTO> detailDTOList = detailMapper.getByParentId(id);
        Map<Long, List<TFdDebtorpaymentDetailDTO>> tmpMap = detailDTOList.stream().collect(Collectors.groupingBy(TFdDebtorpaymentDetailDTO::getType));
        //  类型（1：发票，2：预缴
        List<TFdDebtorpaymentDetailDTO> invoiceTmpList = tmpMap.get(1L);
        List<TFdDebtorpaymentDetailDTO> prePayTmpList = tmpMap.get(2L);
        List<TFdDebtorpaymentDetailDTO> tmpCNList= tmpMap.get(3L);
        List<TFdDebtorpaymentDetailDTO> tmpDNList= tmpMap.get(4L);
        if( dtoById.getDebTypeCode() !=null && "40".equals(dtoById.getDebTypeCode())){
            if(tmpDNList!=null&&tmpDNList.size()>0){
                List<TFdDebtorpaymentDetailDTO> tmpDNDatas = detailMapper.getCreditDebit(tmpDNList);
                if(tmpDNDatas==null||tmpDNDatas.size()==0){
                    throw new BusinessRuntimeException("获取信贷发票失败");
                }
                //获取DN数据
                for (TFdDebtorpaymentDetailDTO dto : tmpDNList) {
                    dto.setHasNotReceiptAmount(tmpDNDatas.stream().filter(o->o.getId().equals(dto.getInvoicePrepayId())).findFirst().orElseThrow(()->new BusinessRuntimeException("信贷发票赋值剩余收据金额失败")).getHasNotReceiptAmount());
                    if(!CollectionUtils.isEmpty(tmpCNList)){
                        dto.setHasNotReceiptAmount(dto.getHasNotReceiptAmount()
                                .add(tmpCNList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                    if (!CollectionUtils.isEmpty(prePayTmpList)){
                        dto.setHasNotReceiptAmount(dto.getHasNotReceiptAmount()
                                .add(prePayTmpList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                    if(dto.getHasNotReceiptAmount().compareTo(BigDecimal.ZERO)>0){
                        if(dto.getHasNotReceiptAmount().compareTo(tmpDNDatas.stream().filter(o->o.getId().equals(dto.getInvoicePrepayId())).findFirst().orElseThrow(()->new BusinessRuntimeException("信贷发票赋值剩余收据金额失败")).getInvoicePrepayAmount())==0){
                            dto.setReceiptStatus("10");
                        }else{
                            dto.setReceiptStatus("20");
                        }
                    }
                }
            }
            if(invoiceTmpList!=null && invoiceTmpList.size()>0){
                List<TFdDebtorpaymentDetailDTO> invoiceDatas = detailMapper.getInvoiceList(invoiceTmpList);
                for (TFdDebtorpaymentDetailDTO dto : invoiceTmpList) {
                    dto.setHasNotReceiptAmount(invoiceDatas.stream().filter(o->o.getId().equals(dto.getInvoicePrepayId())).findFirst().orElseThrow(()->new BusinessRuntimeException("发票赋值剩余收据金额失败")).getHasNotReceiptAmount());
                    if(!CollectionUtils.isEmpty(tmpCNList)){
                        dto.setHasNotReceiptAmount(dto.getHasNotReceiptAmount()
                                .add(tmpCNList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                    if (!CollectionUtils.isEmpty(prePayTmpList)){
                        dto.setHasNotReceiptAmount(dto.getHasNotReceiptAmount()
                                .add(prePayTmpList.stream().map(TFdDebtorpaymentDetailPO::getInvoicePrepayAmount).reduce(BigDecimal.ZERO,BigDecimal::add)));
                    }
                    if(dto.getHasNotReceiptAmount().compareTo(BigDecimal.ZERO)>0){
                        if(dto.getHasNotReceiptAmount().compareTo(invoiceDatas.stream().filter(o->o.getId().equals(dto.getInvoicePrepayId())).findFirst().orElseThrow(()->new BusinessRuntimeException("信贷发票赋值剩余收据金额失败")).getInvoicePrepayAmount())==0){
                            dto.setReceiptStatus("10");
                        }else{
                            dto.setReceiptStatus("20");
                        }
                    }
                }
            }
        }else {
            if(tmpDNList!=null&&tmpDNList.size()>0){
                List<TFdDebtorpaymentDetailDTO> tmpDNDatas = detailMapper.getCreditDebit(tmpDNList);
                for (TFdDebtorpaymentDetailDTO dto : tmpDNList) {
                    dto.setHasNotReceiptAmount(tmpDNDatas.stream().filter(o->o.getId().equals(dto.getInvoicePrepayId())).findFirst().orElseThrow(()->new BusinessRuntimeException("信贷发票赋值剩余收据金额失败")).getInvoicePrepayAmount());
                    dto.setReceiptStatus("10");
                }
            }
            if(invoiceTmpList!=null&&invoiceTmpList.size()>0){
                List<TFdDebtorpaymentDetailDTO> invoiceDatas = detailMapper.getInvoiceList(invoiceTmpList);
                for (TFdDebtorpaymentDetailDTO dto : invoiceTmpList) {
                    dto.setHasNotReceiptAmount(invoiceDatas.stream().filter(o->o.getId().equals(dto.getInvoicePrepayId())).findFirst().orElseThrow(()->new BusinessRuntimeException("发票赋值剩余收据金额失败")).getInvoicePrepayAmount());
                    dto.setReceiptStatus("10");
                }
            }
        }

        //回退船舶押金
        //SpringUtils.getBean(this.getClass()).cancleWithShipInvoice(invoiceTmpList);

        //先判断有没有生成的预缴账单
        List<TFdDebtorpaymentDetailDTO> detailTwoIds = new ArrayList<>();
        List<TFdDebtorpaymentDetailDTO> detailOneIds = new ArrayList<>();
        List<TFdDebtorpaymentDetailDTO> detailThreeIds = new ArrayList<>();
        List<TFdDebtorpaymentDetailDTO> prepyUpdateList = new ArrayList<>();
        // 10 预缴 20 补缴 30 退还           //  类型（1：发票，2：预缴

        //对预缴的操作
        if(!CollectionUtils.isEmpty(prePayTmpList)){
            //区分补缴和预缴
            prePayTmpList.forEach(o->{

                if(o.getDebtorpayPaymentTypeCode()==null){
                    prepyUpdateList.add(o);
                }else if(o.getDebtorpayPaymentTypeCode()==10L){
                    o.setInvoicePrepayAmount(BigDecimal.ZERO);
                    detailOneIds.add(o);
                }else if(o.getDebtorpayPaymentTypeCode()==20L){
                    o.setInvoicePrepayAmount(BigDecimal.ZERO);
                    detailTwoIds.add(o);
                }else if(o.getDebtorpayPaymentTypeCode()==30L){
                    o.setInvoicePrepayAmount(BigDecimal.ZERO);
                    detailThreeIds.add(o);
                }
            });
            //获取表中的预缴集合通过debtorpaymentId判断预缴是否已经被使用了
            if(!CollectionUtils.isEmpty(detailOneIds)){
                List<TFdBankCustomerPrepaymentDTO> prepaymentDTOS = debtorpaymentMapper.getPrepayByIds(detailOneIds);
                boolean flag = false;
                for (TFdBankCustomerPrepaymentDTO prepaymentDTO : prepaymentDTOS) {
                    if(prepaymentDTO.getDebtorpaymentId() != null){
                        flag = true;
                    }
                }
                if(flag){
                    throw new BusinessRuntimeException("该收据生成的预缴单已经被使用!");
                }
            }
            detailOneIds.addAll(detailTwoIds);
            detailOneIds.addAll(detailThreeIds);
            detailOneIds.forEach(o->{
                o.setStatus(2L);
                o.setVoidRemark("付款收据作废");
            });
            if(!CollectionUtils.isEmpty(detailOneIds)){
                debtorpaymentMapper.updatePrepayStatusBatch(detailOneIds);
            }
            //更新预缴表
            if (!CollectionUtils.isEmpty(prepyUpdateList)){
                prepyUpdateList.forEach(o->{
                    TFdDebtorpaymentDetailDTO prePayList = debtorpaymentMapper.getPrepayById(o.getInvoicePrepayId());
                    o.setInvoicePrepayAmount(prePayList.getUtilizedAmount().subtract(o.getInvoicePrepayAmount()));
                });
                debtorpaymentMapper.updatePrepayStatusBatch(prepyUpdateList);
            }
        }
        //更新发票表
        if(!CollectionUtils.isEmpty(invoiceTmpList)){
            debtorpaymentMapper.updateInvoiceStatusBatch(invoiceTmpList);
        }
        //更新cndn
        if(!CollectionUtils.isEmpty(tmpCNList)){
            debtorpaymentMapper.updateCNDNBillStatusBatch(tmpCNList);
        }
        if(!CollectionUtils.isEmpty(tmpDNList)){
            debtorpaymentMapper.updateCNDNBillStatusBatch(tmpDNList);
        }
        //更新主表数据
        TFdDebtorpaymentDTO tFdDebtorpaymentDTO = new TFdDebtorpaymentDTO();
        tFdDebtorpaymentDTO.setId(id);
        tFdDebtorpaymentDTO.setStatus(2L);
        return debtorpaymentMapper.updateDebtorpayment(tFdDebtorpaymentDTO) == 1;
    }
    private String getNo(){
        List<String> tmpDebList = debtorpaymentMapper.getNo();
        if(CollectionUtils.isEmpty(tmpDebList)){
            String yearString = String.valueOf(LocalDateTime.now().getYear()).substring(2);
            return "SJ"+yearString+String.format("%05d", 1);
        }
        List<Integer> tmpSort = new ArrayList<>(tmpDebList.size());
        tmpDebList.forEach(o->{
            tmpSort.add(Integer.valueOf(o.substring(4)));
        });
        Collections.sort(tmpSort);
        Integer tmpInteger = tmpSort.get(tmpSort.size() - 1)+1;
        return "SJ"+String.valueOf(LocalDateTime.now().getYear()).substring(2)+String.format("%05d", tmpInteger);
    }

    @Transactional(rollbackFor = Exception.class)
    public void handlerShipInvoice(List<TFdDebtorpaymentDetailDTO> list){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        //获取停泊费
        List<Long> invoiceIds = list.stream().map(TFdDebtorpaymentDetailDTO::getInvoicePrepayId).collect(Collectors.toList());
        List<TFdInvoiceDetailDTO> invoiceDetails = debtorpaymentMapper.getIsTbFeeInvoiceDetailListByIds(invoiceIds);
        if(CollectionUtils.isEmpty(invoiceDetails)){
            return;
        }
        invoiceDetails.forEach(o->{
            if(o.getShipvoyageItemId()  == null ) {
                throw new BusinessRuntimeException("发票中没有航次子表ID");
            }
        });
        List<Long> shipvoyageItemIds = invoiceDetails.stream().map(TFdInvoiceDetailDTO::getShipvoyageItemId).collect(Collectors.toList());
        for (Long shipvoyageItemId : shipvoyageItemIds) {
            TDisShipvoyageItemDTO disShipvoyageItem = debtorpaymentMapper.getShipVoyageItemByItemId(shipvoyageItemId);
            if(disShipvoyageItem !=null && (disShipvoyageItem.getPaymentAmount()!=null && disShipvoyageItem.getPaymentAmount().compareTo(BigDecimal.ZERO) !=0)){
                //更新子表
                disShipvoyageItem.setPaymentAmountBack(disShipvoyageItem.getPaymentAmount());
                disShipvoyageItem.setPaymentAmount(BigDecimal.ZERO);
                debtorpaymentMapper.updateShipVoyageItem(disShipvoyageItem);
                //更新主表
                List<TDisShipvoyageItemDTO> tmpDetailList = debtorpaymentMapper.getAllItemByOneItemId(disShipvoyageItem.getId());
                TDisShipvoyageDTO shipvoyageDTO = new TDisShipvoyageDTO();
                shipvoyageDTO.setId(Long.parseLong(tmpDetailList.get(0).getShipvoyageId()));
                shipvoyageDTO.setPaymentAmount(BigDecimal.ZERO);
                tmpDetailList.forEach(o->{
                    shipvoyageDTO.setPaymentAmount(shipvoyageDTO.getPaymentAmount().add(o.getPaymentAmount()));
                });
                debtorpaymentMapper.updateShipVoyage(shipvoyageDTO);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancleWithShipInvoice(List<TFdDebtorpaymentDetailDTO> list){
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<Long> invoiceIds = list.stream().map(TFdDebtorpaymentDetailDTO::getInvoicePrepayId).collect(Collectors.toList());
        List<TFdInvoiceDetailDTO> invoiceDetails = debtorpaymentMapper.getIsTbFeeInvoiceDetailListByIds(invoiceIds);
        if(CollectionUtils.isEmpty(invoiceDetails)){
            return;
        }
        invoiceDetails.forEach(o->{
            if(o.getShipvoyageItemId()  == null ) {
                throw new BusinessRuntimeException("发票中没有航次子表ID");
            }
        });
        List<Long> shipvoyageItemIds = invoiceDetails.stream().map(TFdInvoiceDetailDTO::getShipvoyageItemId).collect(Collectors.toList());
        for (Long shipvoyageItemId : shipvoyageItemIds) {
            TDisShipvoyageItemDTO disShipvoyageItem = debtorpaymentMapper.getShipVoyageItemByItemId(shipvoyageItemId);
            if(disShipvoyageItem !=null){
                //更新子表
                if(disShipvoyageItem.getPaymentAmount()==null){
                    disShipvoyageItem.setPaymentAmount(BigDecimal.ZERO);
                }
                disShipvoyageItem.setPaymentAmount(disShipvoyageItem.getPaymentAmount().add(disShipvoyageItem.getPaymentAmountBack()));
                disShipvoyageItem.setPaymentAmountBack(BigDecimal.ZERO);
                debtorpaymentMapper.updateShipVoyageItem(disShipvoyageItem);
                //更新主表
                List<TDisShipvoyageItemDTO> tmpDetailList = debtorpaymentMapper.getAllItemByOneItemId(disShipvoyageItem.getId());
                TDisShipvoyageDTO shipvoyageDTO = new TDisShipvoyageDTO();
                shipvoyageDTO.setId(Long.parseLong(tmpDetailList.get(0).getShipvoyageId()));
                shipvoyageDTO.setPaymentAmount(BigDecimal.ZERO);
                tmpDetailList.forEach(o->{
                    shipvoyageDTO.setPaymentAmount(shipvoyageDTO.getPaymentAmount().add(o.getPaymentAmount()));
                });
                debtorpaymentMapper.updateShipVoyage(shipvoyageDTO);
            }
        }
    }
}
