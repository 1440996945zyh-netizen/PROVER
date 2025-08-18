package com.yy.ppm.statement.service.impl;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Snowflake;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.Page;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.business.bean.dto.cargoInfo.ExportDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.enums.HandoverlistTypeEnum;
import com.yy.ppm.common.enums.StatementStatusEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.MiscSearchDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingExportDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import com.yy.ppm.statement.mapper.MiscBillingMapper;
import com.yy.ppm.statement.service.MiscBillingService;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/***
 * 杂项计费
 * @author yangcl
 * */
@Service
public class MiscBillingServiceImpl implements MiscBillingService {

    @Resource
    MiscBillingMapper miscBillingMapper;

    @Autowired
    private CommonService commonService;

    @Resource
    private SecurityUtils securityUtils;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private TransactionTemplate transactionTemplate;
    /**
     * 查询费率列表
     * @return
     */
    @Override
    public List<TBusRateDTO> getRateList(TBusRateDTO tBusRateDTO) {
        return miscBillingMapper.getRateList(tBusRateDTO);
    }

    /**
     * 保存杂项计费信息
     * @return
     */
    @Override
    public void saveMiscBilling(TMiscBillingPO po) {
        if(po.getId()==null){
            //插入
            po.setId(snowflake.nextId());
            po.setStatus(10);//新增默认为状态是10
            miscBillingMapper.addMiscBilling(po);
        }else{
            TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(po.getId());
            if(dto==null){
                throw new BusinessRuntimeException("更新失败，杂项计费已不存在");
            }
            if(dto.getStatus()!=10){
                throw new BusinessRuntimeException("更新失败，该杂项计费已不是待计费状态");
            }

            //更新
            miscBillingMapper.updateByPrimaryKey(po);
        }
    }

    /**
     * 查询杂项计费信息
     * @return
     */
    @Override
    public Pages<TMiscBillingDTO> getList(MiscSearchDTO dto) {
        return  PageHelperUtils.limit(dto, () -> {
            Page<TMiscBillingDTO> pageList = miscBillingMapper.getList(dto);
            pageList.forEach(o->{
                if(o.getStatus()==10){
                    o.setAmountMoney(BigDecimal.ZERO);
                    o.setTaxAmount(BigDecimal.ZERO);
                    o.setTaxRate(BigDecimal.ZERO);
                    o.setRate(BigDecimal.ZERO);
                }

                if(o.getVoyageId()!=null){
                    List<String> berthNames = miscBillingMapper.getShipAllBerthName(o.getVoyageId());
                    o.setBerthName(CollectionUtils.isEmpty(berthNames)?"":String.join(",",berthNames));
                }
            });
            return pageList;
        });
    }

    /**
     * 根据id删除杂项计费信息
     * @return
     */
    @Override
    public int delete(Long id) {
        TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(id);
        if(dto==null){
            throw new BusinessRuntimeException("删除失败，杂项计费已不存在");
        }

        if("0".equals(dto.getIsMainIncome())){
            if(dto.getStatus()!=20){
                throw new BusinessRuntimeException("删除失败，开票申请已不是待计费状态");
            }
        }else if(dto.getStatus()!=10){
            throw new BusinessRuntimeException("删除失败，杂项计费已不是待计费状态");
        }

        if(dto.getStatementId()==null){
            return miscBillingMapper.deleteMisc(id);
        }
        TCostStatementDTO statementDTO = miscBillingMapper.getCostStatementById(dto.getStatementId());
        if("31".equals(statementDTO.getStatus())){
            throw new BusinessRuntimeException("删除失败，请先取消商务审核");
        }
        if("40".equals(statementDTO.getStatus())){
            throw new BusinessRuntimeException("删除失败，结算单已经部分开票");
        }
        if("50".equals(statementDTO.getStatus())){
            throw new BusinessRuntimeException("删除失败，结算单已经开票");
        }
        return miscBillingMapper.deleteMisc(id);
    }

    /**
     * 根据id获取杂项计费信息
     * @return
     */
    @Override
    public TMiscBillingDTO getMiscById(Long id) {
        return miscBillingMapper.getMiscBillingById(id);
    }

    /**
     * 审核杂项计费信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void publishMisc(Long id) {
        TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(id);
        if(dto==null){
            throw new BusinessRuntimeException("审核失败，杂项计费已不存在");
        }

        if(dto.getStatus()!=20){
            throw new BusinessRuntimeException("审核失败，杂项计费不是待审核状态");
        }
        dto.setStatus(30);


        TCostStatementPO costStatementPO = new TCostStatementPO();
        costStatementPO.setCompanyId(dto.getCompanyId());
        costStatementPO.setCompanyName(dto.getCompanyName());
        costStatementPO.setId(snowflake.nextId());
        costStatementPO.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        costStatementPO.setCustomerId(dto.getCustomerId());
        costStatementPO.setCustomerName(dto.getCustomerName());
        costStatementPO.setType(HandoverlistTypeEnum._40.getCode());
        //costStatementPO.setShipvoyageId(dto.getShipVoyage());
        costStatementPO.setShipvoyageItemId(dto.getVoyageId());
        costStatementPO.setSettlementDate(dto.getBillDate());
        if(!"2".equals(dto.getFeeType())){
            costStatementPO.setStatus(StatementStatusEnum._31.getCode());
        }else {
            costStatementPO.setStatus(StatementStatusEnum._30.getCode());
        }
        costStatementPO.setIsFinal("1");
        costStatementPO.setReceiptRemark(dto.getRemark());
        costStatementPO.setTaxInvoiceCode(dto.getTaxationInvoiceCode());
        costStatementPO.setTaxInvoiceName(dto.getTaxationInvoiceName());
        miscBillingMapper.insertCostStatement(costStatementPO);

        TCostStatementDetailPO detailPO = new TCostStatementDetailPO();
        detailPO.setId(snowflake.nextId());
        detailPO.setStatement(costStatementPO.getId());
        detailPO.setRate(dto.getRate());
        detailPO.setRateItemCode(dto.getRateItemCode());
        detailPO.setRateItemName(dto.getRateName());
        detailPO.setNumber(dto.getBillQuantity());
        detailPO.setNumber2(dto.getBillQuantity2());
        detailPO.setAmount(dto.getAmountMoney());
        detailPO.setTax(dto.getTaxRate());
        detailPO.setTaxAmount(dto.getTaxAmount());
        detailPO.setRateId(dto.getRateId());
        detailPO.setUnitCode(dto.getUnitCode());
        detailPO.setUnitName(dto.getUnitName());

        miscBillingMapper.insertCostStatementDetail(detailPO);

        dto.setStatementId(costStatementPO.getId());
        miscBillingMapper.updateStatus(dto);
    }

    /**
     * 撤销审核
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void revokeMisc(Long id) {
        TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("撤销审核失败，杂项计费已不存在");
        }

        if (dto.getStatus() < 30) {
            throw new BusinessRuntimeException("撤销审核失败，该数据已不是审核状态");
        }

        TCostStatementDTO statementDTO = miscBillingMapper.getCostStatementById(dto.getStatementId());
        if("31".equals(statementDTO.getStatus())&&"2".equals(dto.getFeeType())){
            throw new BusinessRuntimeException("撤销审核失败，请先取消商务审核");
        }
        if("40".equals(statementDTO.getStatus())){
            throw new BusinessRuntimeException("撤销审核失败，结算单已经部分开票");
        }
        if("50".equals(statementDTO.getStatus())){
            throw new BusinessRuntimeException("撤销审核失败，结算单已经开票");
        }

        dto.setStatus(20);
        miscBillingMapper.deleteCostStatement(dto.getStatementId());
        miscBillingMapper.deleteCostStatementDetail(dto.getStatementId());
        dto.setStatementId(null);
        miscBillingMapper.updateStatus(dto);
    }

    @Override
    public List<TMiscBillingDTO> getProcessByRateId(String rateId) {
        return miscBillingMapper.getProcessByRateId(rateId);
    }

    @Override
    public Pages<TMiscBillingDTO> getListForCargo(MiscSearchDTO dto) {
        return PageHelperUtils.limit(dto, ()-> miscBillingMapper.getListForCargo(dto));
    }

    @Override
    public boolean charging(Long id) {
        TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(id);
        if(dto==null){
            throw new BusinessRuntimeException("杂项计费已删除");
        }
        if(dto.getStatus()!=10){
            throw new BusinessRuntimeException("杂项计费已不是待计费状态，无法计费");
        }
        if(dto.getStatementId()!=null){
            throw new BusinessRuntimeException("杂项计费已审核！不能重新计费！");
        }
        TMiscBillingPO dataPo = new TMiscBillingPO();
        dataPo.setStatus(20);
        dataPo.setId(id);
        dataPo.setStatementBy(securityUtils.getLoginUserId());
        dataPo.setStatementTime(new Date());
        dataPo.setStatementByName(securityUtils.getLoginUserName());
        return miscBillingMapper.chargingUpdate(dataPo) ==1;
    }

    @Override
    public boolean cancleCharging(Long id) {
        TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(id);

        if(dto==null){
            throw new BusinessRuntimeException("杂项计费已删除");
        }
        if(dto.getStatus()==10){
            throw new BusinessRuntimeException("杂项计费未计费不能核销计费");
        }
        if(dto.getStatementId()!=null){
            throw new BusinessRuntimeException("撤销计费失败，杂项计费已审核");
        }
        TMiscBillingPO dataPo = new TMiscBillingPO();
        dataPo.setStatus(10);
        dataPo.setId(id);
        dataPo.setStatementBy(null);
        dataPo.setStatementTime(null);
        dataPo.setStatementByName(null);
        return miscBillingMapper.chargingUpdate(dataPo) ==1;
    }


    @Override
    public Pages<TMiscBillingDTO> getlistForInvoiceApply(MiscSearchDTO dto) {
        return PageHelperUtils.limit(dto, ()-> miscBillingMapper.getlistForInvoiceApply(dto));
    }

    @Override
    public void addInvoiceApply(TMiscBillingPO po) {
        if(po.getId()==null){
            //插入
            po.setId(snowflake.nextId());
            po.setStatus(20);//新增默认为状态是10
            miscBillingMapper.addMiscBilling(po);
        }else{
            TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(po.getId());
            if(dto==null){
                throw new BusinessRuntimeException("更新失败，该开票申请已不存在");
            }
            if(dto.getStatus()!=20){
                throw new BusinessRuntimeException("更新失败，该开票申请已不是待审核状态");
            }

            //更新
            miscBillingMapper.updateByPrimaryKey(po);
        }
    }

    @Override
    public void auditInvoiceApply(Long id) {
        TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(id);
        if(dto==null){
            throw new BusinessRuntimeException("审核失败，开票申请已不存在");
        }

        if(dto.getStatus()!=20){
            throw new BusinessRuntimeException("审核失败，开票申请不是待审核状态");
        }
        dto.setStatus(30);

        TCostStatementPO costStatementPO = new TCostStatementPO();
        costStatementPO.setCompanyId(dto.getCompanyId());
        costStatementPO.setCompanyName(dto.getCompanyName());
        costStatementPO.setId(snowflake.nextId());
        costStatementPO.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        costStatementPO.setCustomerId(dto.getCustomerId());
        costStatementPO.setCustomerName(dto.getCustomerName());
        costStatementPO.setType(HandoverlistTypeEnum._40.getCode());
        //costStatementPO.setShipvoyageId(dto.getShipVoyage());
        costStatementPO.setShipvoyageItemId(dto.getVoyageId());
        costStatementPO.setSettlementDate(dto.getBillDate());
        costStatementPO.setStatus(StatementStatusEnum._31.getCode());
        costStatementPO.setIsFinal("1");
        costStatementPO.setReceiptRemark(dto.getRemark());
        costStatementPO.setTaxInvoiceCode(dto.getTaxationInvoiceCode());
        costStatementPO.setTaxInvoiceName(dto.getTaxationInvoiceName());
        miscBillingMapper.insertCostStatement(costStatementPO);

        TCostStatementDetailPO detailPO = new TCostStatementDetailPO();
        detailPO.setId(snowflake.nextId());
        detailPO.setStatement(costStatementPO.getId());
        detailPO.setRate(dto.getRate());
        detailPO.setRateItemCode(dto.getRateItemCode());
        detailPO.setRateItemName(dto.getRateName());
        detailPO.setNumber(dto.getBillQuantity());
        detailPO.setNumber2(dto.getBillQuantity2());
        detailPO.setAmount(dto.getAmountMoney());
        detailPO.setTax(dto.getTaxRate());
        detailPO.setTaxAmount(dto.getTaxAmount());
        detailPO.setRateId(dto.getRateId());
        detailPO.setUnitCode(dto.getUnitCode());
        detailPO.setUnitName(dto.getUnitName());

        miscBillingMapper.insertCostStatementDetail(detailPO);

        dto.setStatementId(costStatementPO.getId());
        miscBillingMapper.updateStatus(dto);
    }

    /**
     * 开票申请消审
     * @param id
     */
    @Override
    public void removeAuditInvoiceApply(Long id) {
        TMiscBillingDTO dto = miscBillingMapper.getMiscBillingById(id);
        if (dto == null) {
            throw new BusinessRuntimeException("撤销审核失败，开票申请已不存在");
        }

        if (dto.getStatus() != 30) {
            throw new BusinessRuntimeException("撤销审核失败，该数据已不是审核状态");
        }

        TCostStatementDTO statementDTO = miscBillingMapper.getCostStatementById(dto.getStatementId());
        if("40".equals(statementDTO.getStatus())){
            throw new BusinessRuntimeException("撤销审核失败，结算单已经部分开票");
        }
        if("50".equals(statementDTO.getStatus())){
            throw new BusinessRuntimeException("撤销审核失败，结算单已经开票");
        }

        dto.setStatus(20);
        miscBillingMapper.deleteCostStatement(dto.getStatementId());
        miscBillingMapper.deleteCostStatementDetail(dto.getStatementId());
        dto.setStatementId(null);
        miscBillingMapper.updateStatus(dto);
    }

    @Override
    public CostBillDtoSheetTemplate printFeeList(List<Long> ids) {


        List<TMiscBillingDTO> dataList = miscBillingMapper.getMiscFeeListByIds(ids);
        ArrayList<String> statementByNames = new ArrayList<>();
        ArrayList<String> reviewByNames = new ArrayList<>();
        ArrayList<String> confirmByNames = new ArrayList<>();
        ArrayList<String> reviewTimes = new ArrayList<>();

        CostBillDtoSheetTemplate result = new CostBillDtoSheetTemplate();
        result.setTin(dataList.get(0).getTin()==null?"":dataList.get(0).getTin());
        result.setBank(dataList.get(0).getBank()==null?"":dataList.get(0).getBank());
        result.setBankAccount(dataList.get(0).getBankAccount()==null?"":dataList.get(0).getBankAccount());
        result.setContactNumber(dataList.get(0).getContactNumber()==null?"":dataList.get(0).getContactNumber());
        result.setCustomerName(dataList.get(0).getCustomerName()==null?"":dataList.get(0).getCustomerName());
        result.setAddress(dataList.get(0).getAddress()==null?"":dataList.get(0).getAddress());

        List<TCostStatementDetailDTO> dataDetailList = new ArrayList<>();
        dataList.forEach(dataDto->{
            TCostStatementDetailDTO detailDTO = new TCostStatementDetailDTO();
            statementByNames.add(dataDto.getStatementByName());
            reviewByNames.add(dataDto.getReviewByName());
            confirmByNames.add(dataDto.getConfirmByName());
            reviewTimes.add(dataDto.getReviewTime());

            detailDTO.setId(1L);
            detailDTO.setContractId(2L);
            detailDTO.setRateItemCode("费目代码");
            detailDTO.setRateItemName("费目名称");
            detailDTO.setServiceContentId(3L);
            detailDTO.setServiceContentName("服务名称");
            detailDTO.setUnitCode("计费代码");
            detailDTO.setUnitName("计费代码名称");
            detailDTO.setRateId(4L);

            detailDTO.setAmount(dataDto.getAmountMoney());
            detailDTO.setTaxAmount(dataDto.getTaxAmount());
            detailDTO.setTax(dataDto.getTaxRate());
            detailDTO.setFeeName(dataDto.getRateName());
            detailDTO.setShipNameVoyage(dataDto.getShipVoyage());
            detailDTO.setCargoName(dataDto.getCargoName());
            detailDTO.setNumber(dataDto.getBillQuantity());
            detailDTO.setRate(dataDto.getRate());
            dataDetailList.add(detailDTO);
            result.setDetailList(dataDetailList);
        });

        result.setAmount(BigDecimal.ZERO);
        result.setNumberCount(BigDecimal.ZERO);

        dataDetailList.forEach(item->{
            result.setAmount(result.getAmount().add(item.getAmount()));
            result.setNumberCount( result.getNumberCount().add(item.getNumber()));
        });
        result.setConfirmByName(confirmByNames.stream().distinct().collect(Collectors.joining(",")));
        result.setReviewByName(reviewByNames.stream().distinct().collect(Collectors.joining(",")));
        result.setStatementByName(statementByNames.stream().distinct().collect(Collectors.joining(",")));
        result.setReviewTime(DateUtils.formatDate(dataList.get(0).getCreateTime(), CommonEnum.DateFormatType.E_1.getCode()));
        result.setCompanyName(dataList.get(0).getCompanyName());
        result.setSheetName("杂项计费账单1");
        return result;
    }


    @Override
    public TBusCustomerDTO getCustomerInfo(Long id) {
         return miscBillingMapper.getCustomerInfoByMiscId(id);
    }

    @Override
    public byte[]  pageExport(MiscSearchDTO dto) {
        List<TMiscBillingExportDTO> pageList = miscBillingMapper.pageExportList(dto);
        pageList.forEach(o->{
            if("10".equals(o.getStatus())){
                o.setAmountMoney(BigDecimal.ZERO);
                o.setTaxAmount(BigDecimal.ZERO);
                o.setTaxRate(BigDecimal.ZERO);
                o.setRate(BigDecimal.ZERO);
            }

            switch (o.getStatus()){
                case "10" :o.setStatus("未计费"); break;
                case "20" :o.setStatus("已计费"); break;
                case "30" :o.setStatus("已审核"); break;
                case "31" :o.setStatus("回执确认"); break;
                case "40" :o.setStatus("部分开票"); break;
                case "50" :o.setStatus("已开票"); break;
            }

            if(o.getVoyageId()!=null){
                List<String> berthNames = miscBillingMapper.getShipAllBerthName(o.getVoyageId());
                o.setBerthName(CollectionUtils.isEmpty(berthNames)?"":String.join(",",berthNames));
            }
        });
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TMiscBillingExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try  {
                        excelWriter.write(pageList, writeSheet);
                } catch (Exception e) {
                    throw new IORuntimeException("杂项计费导出异常"+e.getMessage());
                }
            });
        }
        return os.toByteArray();
    }
}
