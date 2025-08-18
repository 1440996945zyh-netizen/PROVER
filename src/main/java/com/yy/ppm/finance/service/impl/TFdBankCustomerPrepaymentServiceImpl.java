package com.yy.ppm.finance.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.business.mapper.TBusTrustMapper;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.mapper.TDisShipVoyageMapper;
import com.yy.ppm.finance.bean.dto.BusTrustResponseDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentSearchDTO;
import com.yy.ppm.finance.mapper.TFdBankCustomerPrepaymentMapper;
import com.yy.ppm.finance.service.TFdBankCustomerPrepaymentService;
import com.yy.ppm.machine.enums.PlanTypeEnum;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.TPrdSalaryDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cursor.Cursor;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)ServiceImpl
 * @Description
 * @createTime 2023年09月14日 10:30:00
 */
@Service
public class TFdBankCustomerPrepaymentServiceImpl implements TFdBankCustomerPrepaymentService {


    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private TFdBankCustomerPrepaymentMapper tFdBankCustomerPrepaymentMapper;
    @Resource
    private TBusTrustMapper tBusTrustMapper;

    @Resource
    private Snowflake snowflake;
    @Autowired
    private CommonService  commonService;
    @Resource
    private SysFileService sysFileService;

    @Resource
    private TDisShipVoyageMapper tDisShipVoyageMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;


    private static final int CURSOR_LIMIT = 5_000;
    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdBankCustomerPrepaymentDTO> getList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {

        Pages<TFdBankCustomerPrepaymentDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdBankCustomerPrepaymentMapper.getList(searchDTO);
        });
        List<TFdBankCustomerPrepaymentDTO> tmpResult = pages.getPages();
        tmpResult.forEach(item->{
            item.setResidualAmount(item.getPrepaymentAmount().subtract(item.getUtilizedAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
        });
        pages.setPages(tmpResult);
        return pages;
    }

    /**
     * 获取余额列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdBankCustomerPrepaymentDTO> getBalanceList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {

        Pages<TFdBankCustomerPrepaymentDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdBankCustomerPrepaymentMapper.getBalanceList(searchDTO);
        });
        List<TFdBankCustomerPrepaymentDTO> tmpResult = pages.getPages();
        tmpResult.forEach(item->{
            item.setResidualPreAmount(item.getPrepaymentAmount()
                    .subtract(item.getUtilizedAmount()));
            item.setResidualAmount(item.getPrepaymentAmount()
                    .subtract(item.getUtilizedAmount())
                    .subtract(item.getInvoiceAmount()));
            BigDecimal deposit = BigDecimal.ZERO;
            if (PrepaymentTypeEnum._10.getCode().equals(String.valueOf(item.getPrepaymentTypeCode()))) {
                deposit = deposit.add(item.getCargoDeposit());
                item.setShipDeposit(BigDecimal.ZERO);
                item.setRealResidueAmount(item.getResidualPreAmount());
            }
            if (PrepaymentTypeEnum._30.getCode().equals(String.valueOf(item.getPrepaymentTypeCode()))) {
                deposit = deposit.add(item.getShipDeposit());
                item.setCargoDeposit(BigDecimal.ZERO);
                item.setRealResidueAmount(item.getResidualAmount().subtract(item.getShipDeposit()));
            }
            if (PrepaymentTypeEnum._40.getCode().equals(String.valueOf(item.getPrepaymentTypeCode()))) {
                item.setShipDeposit(BigDecimal.ZERO);
                item.setCargoDeposit(BigDecimal.ZERO);
            }
            item.setEstiResidualAmount(item.getResidualAmount()
                    .subtract(deposit));
        });
        pages.setPages(tmpResult);
        return pages;
    }

    /**
     * 获取扣款明细列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<TFdBankCustomerPaymentDTO> getBalanceDetailList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {

        Pages<TFdBankCustomerPaymentDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return tFdBankCustomerPrepaymentMapper.getBalanceDetailList(searchDTO);
        });
        return pages;
    }

    /**
     * 根据客户id过滤出预缴是船方的数据
     * @param searchDTO
     * @return
     */
    @Override
    public Map<String,Object> getBankCustomerPrepayment(TFdBankCustomerPrepaymentSearchDTO searchDTO){
        Map<String,Object> map = Maps.newHashMap();
//        Map<String,Object> customerPayMap = tFdBankCustomerPrepaymentMapper.getPrePaymentAmountSum(searchDTO);
//        BigDecimal customerPaymentAmountSum = (CollectionUtils.isEmpty(customerPayMap)|| ObjectUtil.isEmpty(customerPayMap.get("CUSTOMER_AMOUNT")))?BigDecimal.ZERO:new BigDecimal(String.valueOf(customerPayMap.get("CUSTOMER_AMOUNT")));
//        Map<String,Object> payMap = tDisShipVoyageMapper.getPaySumByVoyageId(searchDTO.getVoyageId(),searchDTO.getCustomerId());
//        BigDecimal paySum = (CollectionUtils.isEmpty(payMap)||ObjectUtil.isEmpty(payMap.get("PAYMENT_SUM")))?BigDecimal.ZERO:new BigDecimal(String.valueOf(payMap.get("PAYMENT_SUM")));
//        customerPaymentAmountSum = customerPaymentAmountSum.subtract(paySum);
//        customerPaymentAmountSum.setScale(2,BigDecimal.ROUND_HALF_UP);
        Map<String,Object> payMap = tDisShipVoyageMapper.getBankCustomerPrepayment(searchDTO.getCustomerId(),searchDTO.getCompanyId());
        map.put("residualAmount",CollectionUtils.isEmpty(payMap)?null:payMap.get("amount"));
        return map;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public TFdBankCustomerPrepaymentDTO getDetail(Long id) {
        TFdBankCustomerPrepaymentDTO result = tFdBankCustomerPrepaymentMapper.getById(id);
        List<BusTrustResponseDTO> busTrustList = tFdBankCustomerPrepaymentMapper.getBusTrustList(result.getCompanyId(), result.getBusTrustId(), result.getCustomerId(),result.getCargoInfoId());
        if(!CollectionUtils.isEmpty(busTrustList)){
            result.setTrustLabel(busTrustList.get(0).getLabel());
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
    public boolean doSave(TFdBankCustomerPrepaymentDTO dto) {

        if(dto == null){
            throw new BusinessRuntimeException("传入的数据为空！");
        }
        if(dto.getPrepaymentTypeCode()==null){
            throw new BusinessRuntimeException("预缴类型未选择！");
        }
        if(dto.getPrepayModeCode()==null){
            throw new BusinessRuntimeException("预缴方式未选择！");
        }
        if(dto.getPrepaymentAmount().compareTo(BigDecimal.ZERO)<0){
            throw new BusinessRuntimeException("预缴金额不能小于零");
        }


        // 新增
        if (dto.getId() == null) {
            if(dto.getPrePayId()!=null){
                //更新原来的预缴信息
                TFdBankCustomerPrepaymentDTO tmpPrePayDto = tFdBankCustomerPrepaymentMapper.getById(dto.getPrePayId());
                dto.setTmpAmount(BigDecimal.ZERO);
                //作废的不能预缴
                if(tmpPrePayDto.getStatus()==2L){
                    throw new BusinessRuntimeException("选择的预缴编号对应的预缴已经作废!");

                }
                //不是大预缴的也要报错
                if(!"10".equals(tmpPrePayDto.getPrepayModeCode())){
                    throw new BusinessRuntimeException("选择的预缴编号对应的预缴类型不是大预缴!");

                }
                if(dto.getCustomerId().compareTo(tmpPrePayDto.getCustomerId())!=0){
                    throw new BusinessRuntimeException("选择的预缴信息与通知单相关的客户信息不一致!");
                }
                if(!dto.getPrepaymentTypeCode().equals(tmpPrePayDto.getPrepaymentTypeCode())){
                    throw new BusinessRuntimeException("货物预缴的预缴类型与选择的预缴的预缴类型不一致!");

                }
                if(tmpPrePayDto.getPrepaymentAmount().subtract(tmpPrePayDto.getUtilizedAmount()).subtract(dto.getPrepaymentAmount()).compareTo(BigDecimal.ZERO)<0){
                    throw new BusinessRuntimeException("预缴失败，货物预缴的预缴金额大于选择的预缴信息中的可用金额!");
                }

                tmpPrePayDto.setUtilizedAmount(tmpPrePayDto.getUtilizedAmount().add(dto.getPrepaymentAmount()));
                dto.setSourcePrepayCode(dto.getPrePayCode());//记录货物预缴用的是用的那个预缴

                tFdBankCustomerPrepaymentMapper.updateWithMode20(tmpPrePayDto);
            }
            dto.setId(snowflake.nextId());

            if("20".equals(dto.getPrepayModeCode())){
                dto.setPrepaymentCode( commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.CARGO_BANK_CUSTOMER_PREPAYMENT,""));

            }else  if("10".equals(dto.getPrepayModeCode())){
                dto.setPrepaymentCode( commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.BIG_BANK_CUSTOMER_PREPAYMENT,""));

            }

            dto.setUtilizedAmount(BigDecimal.ZERO);
            //1正常 2作废
            dto.setStatus(new Long("1"));
            sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
            //判断是否是完全预缴了
            if((new Long("10").compareTo(dto.getPrepaymentTypeCode()) == 0)&&"20".equals(dto.getPrepayModeCode())){
                //获取trust_cargo中的预估金额
                TBusTrustCargoDTO  tmpTbtcDto= tFdBankCustomerPrepaymentMapper.getTrustCargo(dto.getBusTrustId(),dto.getCargoInfoId());
                //获取改票货的历史的预缴记录
                List<TFdBankCustomerPrepaymentDTO> tmpPrePayList = tFdBankCustomerPrepaymentMapper.getListBycargoInfoId(dto.getCargoInfoId());
                //计算实际可用金额
                BigDecimal realPrePayAmount = tmpPrePayList.stream().map(TFdBankCustomerPrepaymentDTO::getPrepaymentAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO).add(dto.getPrepaymentAmount());
                if(tmpTbtcDto==null){
                    throw new BusinessRuntimeException("没有对应的票货信息");
                }
                //判断票货的预缴状态
                if(tmpTbtcDto.getEstAmount().compareTo(realPrePayAmount)==0){
                    //更新票货状态
                    tFdBankCustomerPrepaymentMapper.updateBusCargoInfo(dto.getCargoInfoId(),"30" );
                }
                //判断票货的预缴状态
                if(tmpTbtcDto.getEstAmount().compareTo(realPrePayAmount)>0){
                    //更新票货状态
                    tFdBankCustomerPrepaymentMapper.updateBusCargoInfo(dto.getCargoInfoId(),"20" );
                }
                if(tmpTbtcDto.getEstAmount().compareTo(realPrePayAmount)<0){
                    throw new BusinessRuntimeException("预缴金额大于票货的预估金额");
                }
            }


            int count = tFdBankCustomerPrepaymentMapper.insert(dto);
            return count == 1;

            // 修改
        } else {
            sysFileService.saveFileBusRelation(dto.getFileIds(),dto.getId());
            return tFdBankCustomerPrepaymentMapper.update(dto) == 1;
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {

        return tFdBankCustomerPrepaymentMapper.deleteById(id) == 1;

    }

    /**
     * 预缴作废
     * @param tFdBankCustomerPrepaymentDTO
     * @return
     */
    @Override
    public boolean voidHandle(TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO) {

        if(tFdBankCustomerPrepaymentDTO==null){
            throw new BusinessRuntimeException("请输入正确的数据");
        }
        TFdBankCustomerPrepaymentDTO prepaymentDTO = tFdBankCustomerPrepaymentMapper.getById(tFdBankCustomerPrepaymentDTO.getId());
        if(prepaymentDTO.getDebtorpaymentId() != null){
            throw  new BusinessRuntimeException("已经开具收据，不可作废");
        }
        if (prepaymentDTO.getSourceDebtorpaymentId() != null){
            throw  new BusinessRuntimeException("预缴是通过付款收据自动生成的，不可作废");
        }
        //回退票货的预缴状态
        if((new Long("10").compareTo(prepaymentDTO.getPrepaymentTypeCode()) == 0)&&"20".equals(prepaymentDTO.getPrepayModeCode())){
            //获取trust_cargo中的预估金额
            TBusTrustCargoDTO  tmpTbtcDto= tFdBankCustomerPrepaymentMapper.getTrustCargo(prepaymentDTO.getBusTrustId(),prepaymentDTO.getCargoInfoId());
            //获取该票货的历史的预缴记录
            List<TFdBankCustomerPrepaymentDTO> tmpPrePayList = tFdBankCustomerPrepaymentMapper.getListBycargoInfoId(prepaymentDTO.getCargoInfoId());
            //计算实际可用金额
            BigDecimal realPrePayAmount = tmpPrePayList.stream().map(o->Optional.ofNullable(o.getPrepaymentAmount()).orElse(BigDecimal.ZERO)).reduce(BigDecimal::add).orElse(BigDecimal.ZERO).subtract(prepaymentDTO.getPrepaymentAmount());

            if(tmpTbtcDto==null){
                throw new BusinessRuntimeException("没有对应的票货信息");
            }
            //判断票货的预缴状态
            if(tmpTbtcDto.getEstAmount().compareTo(realPrePayAmount)==0){
                //更新票货状态
                tFdBankCustomerPrepaymentMapper.updateBusCargoInfo(prepaymentDTO.getCargoInfoId(),"30" );
            }
            //判断票货的预缴状态
            if(tmpTbtcDto.getEstAmount().compareTo(realPrePayAmount)>0 && realPrePayAmount.compareTo(BigDecimal.ZERO)>0){
                //更新票货状态
                tFdBankCustomerPrepaymentMapper.updateBusCargoInfo(prepaymentDTO.getCargoInfoId(),"20" );
            }
            if(tmpTbtcDto.getEstAmount().compareTo(realPrePayAmount)>0 && realPrePayAmount.compareTo(BigDecimal.ZERO)<=0){
                tFdBankCustomerPrepaymentMapper.updateBusCargoInfo(prepaymentDTO.getCargoInfoId(),"10" );
            }

        }


        Integer tmpCount = tFdBankCustomerPrepaymentMapper.countSourcePrepayCode(prepaymentDTO.getPrepaymentCode());

        if(tmpCount!=0){
            throw new BusinessRuntimeException("该预缴已经被其他货物预缴所用不能作废");
        }

        //货物预缴回退金额
        if(prepaymentDTO.getPrepayModeCode()!=null && "20".equals(prepaymentDTO.getPrepayModeCode())){
            if(StringUtils.isBlank(prepaymentDTO.getSourcePrepayCode())){
                throw new BusinessRuntimeException("作废失败，货物预缴没有关联的预缴编号");
            }
            TFdBankCustomerPrepaymentDTO dto = tFdBankCustomerPrepaymentMapper.getByPrePayCode(prepaymentDTO.getSourcePrepayCode());
            dto.setPrepaymentCode(prepaymentDTO.getSourcePrepayCode());
            dto.setUtilizedAmount(dto.getUtilizedAmount().subtract(prepaymentDTO.getPrepaymentAmount()));
            int i = tFdBankCustomerPrepaymentMapper.updateWithMode20(dto);
            if(i!=1){
                throw new BusinessRuntimeException("作废失败，回退金额失败");
            }
        }


        //状态改为2
        tFdBankCustomerPrepaymentDTO.setStatus(2L);

        int count = tFdBankCustomerPrepaymentMapper.voidHandle(tFdBankCustomerPrepaymentDTO);
        return count == 1;

    }

    /**
     *
     * @param keyWord
     * @return
     */
    @Override
    public List<BusTrustResponseDTO> getBusTrustList(String keyWord) {


        List<BusTrustResponseDTO> responseDTOS  = tFdBankCustomerPrepaymentMapper.getBusTrustListWithKeyWord(keyWord);
        List<BusTrustResponseDTO> result =  new ArrayList<>();

        //船舶计划进口、集疏港计划源为车
        for (BusTrustResponseDTO item : responseDTOS) {
            if(PlanTypeEnum.PLAN_TYPE_1.getCode().equals(item.getTrustType()) && ImpExpEnum.IN.getName().equals(item.getImpExp()) ||
               PlanTypeEnum.PLAN_TYPE_2.getCode().equals(item.getTrustType()) && SourceTargetTypeEnum._03.getCode().equals(item.getSourceCd())
            ){
                result.add(item);
            }
        }

        return result;
    }


    /**
     * 获取作业通知单/客户列表
     * @param searchDTO
     * @return
     */
    @Override
    public List<BusTrustResponseDTO> getTrustOrderList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {
        //获取作业通知单中没有作废，且有预缴金额的按照通知单编号和客户id进行分类；

        if(searchDTO==null || searchDTO.getCompanyId()==null){
            throw new BusinessRuntimeException("请选择作业公司");
        }
        return tFdBankCustomerPrepaymentMapper.getBusTrustList(searchDTO.getCompanyId(), null,StringUtils.isNotBlank(searchDTO.getCustomerId())?Long.valueOf(searchDTO.getCustomerId()):null,null);
    }


    /**
     * 获取预缴编号
     * @return
     */
    @Override
    public List<TFdBankCustomerPrepaymentDTO> getPrepaymentCodeList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {
        List<TFdBankCustomerPrepaymentDTO> prepaymentCodeList = tFdBankCustomerPrepaymentMapper.getPrepaymentCodeList(searchDTO);
        List<TFdBankCustomerPrepaymentDTO> result = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(prepaymentCodeList)){
            result = prepaymentCodeList.stream().filter(o -> o.getPrepaymentAmount().subtract(o.getUtilizedAmount()).compareTo(BigDecimal.ZERO) == 1).collect(Collectors.toList());
            result.forEach(o->{
                o.setPrePayCodeLabel(o.getPrepaymentCode()+" ( "+o.getPrepaymentAmount().subtract(o.getUtilizedAmount())+"元 )");
            });
        }
        return result;
    }

    /**
     * 获取金额相关信息
     * @param searchDTO
     * @return
     */
    @Override
    public Map<String,Object> getAmountInfo(TFdBankCustomerPrepaymentSearchDTO searchDTO) {
        if(searchDTO==null){
            throw new BusinessRuntimeException("获取金额相关信息的条件不足");
        }
        if(searchDTO.getBusTrustId()==null){
            throw new BusinessRuntimeException("请选择指令编号");
        }

        if(searchDTO.getCompanyId()==null){
            throw new BusinessRuntimeException("请选择公司");
        }
        if(StringUtils.isBlank(searchDTO.getCustomerId())){
            throw new BusinessRuntimeException("请选择客户信息");
        }


        //  合同预缴金额/已经预缴金额/剩余金额

        TFdBankCustomerPrepaymentSearchDTO tmpSearch = new TFdBankCustomerPrepaymentSearchDTO();
        tmpSearch.setBusTrustId(searchDTO.getBusTrustId());
        TFdBankCustomerPrepaymentDTO dto = tFdBankCustomerPrepaymentMapper.getByTrustId(searchDTO.getBusTrustId(),searchDTO.getCargoInfoId());

        List<BusTrustResponseDTO> busTrustList = tFdBankCustomerPrepaymentMapper.getBusTrustList(searchDTO.getCompanyId(), searchDTO.getBusTrustId(), Long.parseLong(searchDTO.getCustomerId()),searchDTO.getCargoInfoId());
        if (busTrustList.isEmpty()) {
            throw new BusinessRuntimeException("作业指令集合为空");
        }
        BusTrustResponseDTO busTrustResponseDTO = busTrustList.get(0);
        if(busTrustResponseDTO == null){
            throw new BusinessRuntimeException("没有找到指令信息");
        }
        if(busTrustResponseDTO.getESTAmount()==null){
            busTrustResponseDTO.setESTAmount(BigDecimal.ZERO);
        }
        if(dto ==null || dto.getPrepaymentAmount()==null){
            dto = new TFdBankCustomerPrepaymentDTO();

            dto.setPrepaymentAmount(BigDecimal.ZERO);
        }

        Map<String,Object> map = Maps.newHashMap();
        map.put("resString",    "预估金额:"+busTrustResponseDTO.getESTAmount()+"  ；已预缴金额:"
                                +dto.getPrepaymentAmount()+"  ；剩余金额："
                                +busTrustResponseDTO.getESTAmount().subtract(dto.getPrepaymentAmount()));
        map.put("amount",busTrustResponseDTO.getESTAmount().subtract(dto.getPrepaymentAmount()));

        return map;
    }

    @Override
    public byte[] exportBalanceDetailList(TFdBankCustomerPrepaymentSearchDTO searchDTO) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TFdBankCustomerPaymentDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try (Cursor<TFdBankCustomerPaymentDTO> cursor = tFdBankCustomerPrepaymentMapper.exportBalanceDetailList(searchDTO)) {
                    Iterator<TFdBankCustomerPaymentDTO> iterator = cursor.iterator();
                    if (iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            List<TFdBankCustomerPaymentDTO> details = new ArrayList<>();
                            for (int i = 0; i < CURSOR_LIMIT && iterator.hasNext(); i++) {
                                details.add(iterator.next());
                            }
                            excelWriter.write(details, writeSheet);
                        }
                    } else {
                        List<TFdBankCustomerPaymentDTO> details = new ArrayList<>();
                        excelWriter.write(details, writeSheet);
                    }

                } catch (IOException e) {
                    throw new IORuntimeException(e);
                }
            });
        }
        return os.toByteArray();
    }
}

