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
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.business.bean.dto.TBusCustomerDTO;
import com.yy.ppm.business.mapper.TBusCustomerMapper;
import com.yy.ppm.common.enums.StatementStatusEnum;
import com.yy.ppm.common.service.SysFileService;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.TMiscBillingExportDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.*;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.mapper.TBizCostStatementMapper;
import com.yy.ppm.statement.mapper.storageFee.StorageFeeMapper;
import com.yy.ppm.statement.service.TBizCostStatementService;
import org.apache.axis.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-18 10:38
 */
@Service
public class TBizCostStatementServiceImpl implements TBizCostStatementService {

    @Resource
    private TBizCostStatementMapper tBizCostStatementMapper;

    @Autowired
    private Snowflake snowflake;

    @Resource
    private SecurityUtils securityUtils;

    @Resource
    private TBusCustomerMapper customerMapper;

    @Resource
    private SysFileService sysFileService;

    @Resource
    private StorageFeeMapper storageFeeMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;


    /**
     * 结算单查询
     *
     * @param query
     * @param parameter
     * @return
     */
    @Override
    public Pages<TCostStatementDTO> listCostStatement(TCostStatementQueryDTO query, PageParameter parameter) {
        Pages<TCostStatementDTO> result = PageHelperUtils.limit(parameter, () -> {
            Page<TCostStatementDTO> page = tBizCostStatementMapper.listCostStatement(query);
            if (!page.isEmpty()) {
                List<Long> ids = page.stream().map(TCostStatementDTO::getId).collect(Collectors.toList());
                List<Map<String, Object>> inTimes = tBizCostStatementMapper.listInTime(ids);
                page.forEach(v1 -> {
                    if(v1.getId()!=null){
                        Map<String, Object> inTimeMap = inTimes.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(null);
                        if (inTimeMap != null) {
                            if(inTimeMap.get("inTime")!=null){
                                v1.setInTime(String.valueOf(inTimeMap.get("inTime")));
                            }else {
                                //陆集陆疏  并且是货转的入库时间
                                if("2".equals(query.getRouteType())||"5".equals(query.getRouteType())||"6".equals(query.getRouteType())){
                                    String cargoInfoNo = v1.getCargoInfoNo();
                                    TBusCargoInfoDTO tBusCargoInfoDTO  = tBizCostStatementMapper.getCargoInfoByNo(cargoInfoNo);
                                    if("30".equals(tBusCargoInfoDTO.getSource())){
                                        List<String> inTime = tBizCostStatementMapper.getInTimeForHZandLJLS(cargoInfoNo);
                                        if (!inTime.isEmpty()){
                                            v1.setInTime(inTime.get(0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //获取泊位
                    if(v1.getShipvoyageItemId()!=null){
                        List<String> berthNames = tBizCostStatementMapper.getShipAllBerthName(v1.getShipvoyageItemId());
                        v1.setBerthName(CollectionUtils.isEmpty(berthNames)?"":String.join(",",berthNames));
                    }
                });
            }
            return page;
        });
        return result;
    }

    /**
     * 结算单明细查询
     *
     * @param statementId
     * @return
     */
    @Override
    public List<TCostStatementDetailDTO> listCostStatementDetail(Long statementId) {
        List<TCostStatementDetailDTO> result = tBizCostStatementMapper.listCostStatementDetailList(statementId);
        result.forEach(v1 -> {
            if (v1.getRate() != null) {
                v1.setTempRate(v1.getRate().add(Optional.ofNullable(v1.getPreferentialRate()).orElse(BigDecimal.ZERO)));
            }
        });
        return result;
    }

    @Override
    public List<TBusContractDTO> listContract(Long statementId, Date date) {
        List<TBusContractDTO> result = tBizCostStatementMapper.listContract(statementId, date);
        if (result.isEmpty()) {
            throw new BusinessRuntimeException("未匹配到合同，请确认有效期、贸别和货名");
        }
        result.forEach(v1 -> {
            TBusTrateItemDTO item;
            if ((item = v1.getItem()) != null) {
                BigDecimal number = tBizCostStatementMapper.getAccNumber(item.getId());
                item.setAccNumber(Optional.ofNullable(item.getOriginAccNumber()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(number).orElse(BigDecimal.ZERO)));
            }
        });
        return result;
    }

    @Override
    public List<TBusContractDTO> listContractForLULS(Long statementId, Date date) {
        List<TBusContractDTO> result = tBizCostStatementMapper.listContractForLULS(statementId, null, date);
        result.forEach(v1 -> {
            TBusTrateItemDTO item;
            if ((item = v1.getItem()) != null) {
                BigDecimal number = tBizCostStatementMapper.getAccNumber(item.getId());
                item.setAccNumber(Optional.ofNullable(item.getOriginAccNumber()).orElse(BigDecimal.ZERO).add(Optional.ofNullable(number).orElse(BigDecimal.ZERO)));
            }
        });
        return result;
    }

    @Override
    public List<TBusContractDTO> listContractDefault(Long statementId, Date date) {
        try {
            List<TBusContractDTO> tBusContractDTOS = SpringUtils.getBean(this.getClass()).listContract(statementId, date);
            return tBusContractDTOS;
        }catch (Exception ww){
            return Arrays.asList();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void statement(TCostStatementDTO dto) {
        TCostStatementPO costStatement = tBizCostStatementMapper.getCostStatement(dto.getId());
        if (!StatementStatusEnum._10.getCode().equals(costStatement.getStatus())) {
            throw new BusinessRuntimeException("结算失败，状态非生产结算");
        }

        costStatement = new TCostStatementPO();
        BeanUtils.copyProperties(dto, costStatement);
        costStatement.setStatus(StatementStatusEnum._20.getCode());
        costStatement.setStatementBy(securityUtils.getLoginUserId());
        costStatement.setStatementByName(securityUtils.getLoginUserName());
        costStatement.setStatementTime(new Date());
        costStatement.setSettlementBasisCode(dto.getSettlementBasisCode());
        costStatement.setSettlementBasisName(dto.getSettlementBasisName());
        tBizCostStatementMapper.updateCostStatement(costStatement);

        dto.getDetails().stream().filter(v1 -> !"1".equals(v1.getIsDerived())).forEach(v1 -> {
            tBizCostStatementMapper.statement(v1);
        });
        dto.getDetails().stream().filter(v1 -> "1".equals(v1.getIsDerived())).forEach(v1 -> {
            v1.setId(snowflake.nextId());
            tBizCostStatementMapper.insertCostStatementDetail(v1);
        });
    }

    /**
     * 撤销计费
     *
     * @param statementId
     */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void cancelStatement(Long statementId) {
        TCostStatementPO costStatement = tBizCostStatementMapper.getCostStatement(statementId);

        //开票后不允许取消商务确认
        if (StringUtils.isEmpty(costStatement.getStatus())) {
            throw new BusinessRuntimeException("数据状态为空，撤销计费失败");
        }
        if (Integer.parseInt(costStatement.getStatus()) > 20) {
            throw new BusinessRuntimeException("撤销计费失败，已开票或部分开票");
        }

        costStatement = new TCostStatementPO();
        costStatement.setId(statementId);
        costStatement.setStatus(StatementStatusEnum._10.getCode());
        tBizCostStatementMapper.updateCostStatement(costStatement);

        List<TCostStatementDetailDTO> statementDetails = tBizCostStatementMapper.listCostStatementDetail(statementId);
        List<TCostStatementDetailDTO> notDerivedDetails = statementDetails.stream().filter(v1 -> !"1".equals(v1.getIsDerived())).collect(Collectors.toList());
        List<TCostStatementDetailDTO> derivedDetails = statementDetails.stream().filter(v1 -> "1".equals(v1.getIsDerived())).collect(Collectors.toList());
        notDerivedDetails.forEach(v1 -> {
            BigDecimal number = derivedDetails.stream()
                    .filter(v2 -> v1.getServiceContentId().equals(v2.getServiceContentId()))
                    .map(TCostStatementDetailDTO::getNumber)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            tBizCostStatementMapper.cancelStatement(v1.getId(), v1.getNumber().add(number));
        });
        if (!derivedDetails.isEmpty()) {
            List<Long> derivedDetailIds = derivedDetails.stream().map(TCostStatementDetailDTO::getId).collect(Collectors.toList());
            tBizCostStatementMapper.deleteCostStatementDetail(derivedDetailIds);
        }
    }

    @Override
    public void review(Long statementId) {
        TCostStatementPO costStatement = tBizCostStatementMapper.getCostStatement(statementId);
        if (!StatementStatusEnum._20.getCode().equals(costStatement.getStatus())) {
            throw new BusinessRuntimeException("审核失败，状态非商务结算");
        }

        costStatement = new TCostStatementPO();
        costStatement.setId(statementId);
        costStatement.setStatus(StatementStatusEnum._30.getCode());
        costStatement.setReviewBy(securityUtils.getLoginUserId());
        costStatement.setReviewByName(securityUtils.getLoginUserName());
        costStatement.setReviewTime(new Date());
        tBizCostStatementMapper.updateCostStatement(costStatement);
    }

    /**
     * 撤销计费审核
     *
     * @param statementId
     */
    @Override
    public void cancelReview(Long statementId) {
        TCostStatementPO tmpCostStatement = tBizCostStatementMapper.getCostStatement(statementId);
        if (StringUtils.isEmpty(tmpCostStatement.getStatus())) {
            throw new BusinessRuntimeException("数据状态为空，撤销计费审核失败");
        }
        if (Integer.parseInt(tmpCostStatement.getStatus()) > 30) {
            throw new BusinessRuntimeException("已经商务确认过，不能撤销计费审核");
        }

        TCostStatementPO costStatement = new TCostStatementPO();
        costStatement = new TCostStatementPO();
        costStatement.setId(statementId);
        costStatement.setStatus(StatementStatusEnum._20.getCode());
        costStatement.setStatementTime(tmpCostStatement.getStatementTime());
        costStatement.setStatementBy(tmpCostStatement.getStatementBy());
        costStatement.setStatementByName(tmpCostStatement.getStatementByName());
        tBizCostStatementMapper.updateCostStatement(costStatement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(TCostStatementDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("请选择操作的数据");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("结算单id不能为空");
        }
        dto.getIds().forEach(tmpId -> {

            TCostStatementPO costStatement = tBizCostStatementMapper.getCostStatement(tmpId);
            if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
                throw new BusinessRuntimeException("商务确认失败，状态非计费审核");
            }
            TCostStatementPO tCostStatementPO = new TCostStatementPO();
            tCostStatementPO.setConfirmBy(securityUtils.getLoginUserId());
            tCostStatementPO.setConfirmByName(securityUtils.getLoginUserName());
            tCostStatementPO.setConfirmTime(new Date());
            tCostStatementPO.setStatus(StatementStatusEnum._31.getCode());
            tCostStatementPO.setReceiptRemark(dto.getReceiptRemark());
            tCostStatementPO.setTaxInvoiceCode(dto.getTaxInvoiceCode());
            tCostStatementPO.setTaxInvoiceName(dto.getTaxInvoiceName());
            tCostStatementPO.setId(tmpId);
            tBizCostStatementMapper.updateCostStatement(tCostStatementPO);
        });
    }

    /**
     * 驳回
     *
     * @param dto
     */
    @Override
    public void reject(TCostStatementDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("请选择操作的数据");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("结算单id不能为空");
        }
        dto.getIds().forEach(tmpId -> {

            TCostStatementPO costStatement = tBizCostStatementMapper.getCostStatement(tmpId);
            if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
                throw new BusinessRuntimeException("驳回失败，状态非计费审核");
            }
            TCostStatementPO tCostStatementPO = new TCostStatementPO();
            tCostStatementPO.setRejectBy(securityUtils.getLoginUserId());
            tCostStatementPO.setRejectByName(securityUtils.getLoginUserName());
            tCostStatementPO.setRejectTime(new Date());
            tCostStatementPO.setStatus(StatementStatusEnum._20.getCode());
            tCostStatementPO.setRejectReason(dto.getRejectReason());
            tCostStatementPO.setId(tmpId);
            tBizCostStatementMapper.rejectCostStatement(tCostStatementPO);
        });
    }


    /**
     * 取消商务确认
     *
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelConfirm(TCostStatementDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("请选择操作得数据");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("结算单id不能为空");
        }
        dto.getIds().forEach(tmpId -> {

            TCostStatementPO costStatement = tBizCostStatementMapper.getCostStatement(tmpId);
            if (!StatementStatusEnum._31.getCode().equals(costStatement.getStatus())) {
                throw new BusinessRuntimeException("操作失败，当前状态非回执已确认");
            }

            costStatement.setReceiptRemark(null);
            costStatement.setTaxInvoiceName(null);
            costStatement.setTaxInvoiceCode(null);
            costStatement.setStatus(StatementStatusEnum._30.getCode());
            costStatement.setConfirmTime(null);
            costStatement.setConfirmByName(null);
            costStatement.setConfirmBy(null);
            tBizCostStatementMapper.updateCostStatement(costStatement);
        });
    }

    /**
     * 包干费导出
     *
     * @param dto
     * @return
     */
    @Override
    public CostBillDtoSheetTemplate exportCostBill(TCostStatementDTO dto) {

        if (dto == null) {
            throw new BusinessRuntimeException("没有要打印的数据");
        }
        if (dto.getId() == null) {
            throw new BusinessRuntimeException("没有结算单id");
        }

        Long statementId = dto.getId();
        ArrayList<Long> statementIds = new ArrayList<>();
        statementIds.add(statementId);
        //导出的对象
        CostBillDtoSheetTemplate costBill = new CostBillDtoSheetTemplate();
        List<TCostStatementDetailDTO> printList = new ArrayList<>();

        TCostStatementQueryDTO query = new TCostStatementQueryDTO();
        query.setId(statementIds.get(0));
        Page<TCostStatementDTO> tCostStatementDTOS = tBizCostStatementMapper.listCostStatement(query);
        if (CollectionUtils.isEmpty(tCostStatementDTOS)) {
            throw new BusinessRuntimeException("没有结算");

        }
        //结算单信息
        TCostStatementDTO costStatement = tCostStatementDTOS.get(0);

        costBill.setCustomerName(costStatement.getCustomerName());
        //客户信息
        TBusCustomerDTO customerDTO = customerMapper.getById(costStatement.getCustomerId());

        if (customerDTO == null) {
            throw new BusinessRuntimeException("没有客户信息");
        }
        //客户信息
        costBill.setTin(customerDTO.getTin());
        //开户行
        costBill.setBank(customerDTO.getBank());
        //银行账号
        costBill.setBankAccount(customerDTO.getBankAccount());
        //联系电话
        costBill.setContactNumber(customerDTO.getContactNumber());
        //审核日期
        costBill.setReviewTime(DateUtils.formatDate(costStatement.getReviewTime(), CommonEnum.DateFormatType.E_1.getCode()));
        //设置地址
        costBill.setAddress(customerDTO.getAddress());
        //设置结算方式
        costBill.setSettlementBasisName(costStatement.getSettlementBasisName());
        costBill.setNumberCount(BigDecimal.ZERO);
        costBill.setAmount(BigDecimal.ZERO);

        StringBuilder contractNo = new StringBuilder();
        StringBuilder statementName = new StringBuilder();
        StringBuilder reviewName = new StringBuilder();
        StringBuilder confirmName = new StringBuilder();
        StringBuilder settlementBasisName = new StringBuilder();

        costBill.setAmount(BigDecimal.ZERO);
        costBill.setNumber(BigDecimal.ZERO);

        List<TCostStatementDetailDTO> statementDTOS = tBizCostStatementMapper.getcostStatementListByStatementIds(statementIds);
        if(!CollectionUtils.isEmpty(statementDTOS)){
            statementDTOS.forEach(o->{
                if(!StringUtils.isEmpty(o.getContactNo())&&!contractNo.toString().contains(o.getContactNo())){
                    contractNo.append(o.getContactNo()).append("_");
                }
                if(!StringUtils.isEmpty(o.getReviewName())&&!reviewName.toString().contains(o.getReviewName())){
                    reviewName.append(o.getReviewName()).append("/");
                }
                if(!StringUtils.isEmpty(o.getStatementName())&&!statementName.toString().contains(o.getStatementName())){
                    statementName.append(o.getStatementName()).append("/");
                }
                if(!StringUtils.isEmpty(o.getConfirmName())&&!confirmName.toString().contains(o.getConfirmName())){
                    confirmName.append(o.getConfirmName()).append("/");
                }
                if(!StringUtils.isEmpty(o.getSettlementBasisName())&&!settlementBasisName.toString().contains(o.getSettlementBasisName())){
                    settlementBasisName.append(o.getSettlementBasisName()).append("/");
                }
                costBill.setAmount(costBill.getAmount().add(o.getAmount()));
                costBill.setNumber(costBill.getNumber().add(o.getNumber()));
            });
        }
        costBill.setDetailList(statementDTOS);
        costBill.setConfirmByName(confirmName.length()>1?confirmName.substring(0,confirmName.length()-1):"");
        costBill.setReviewByName(reviewName.length()>1?reviewName.substring(0,reviewName.length()-1):"");
        costBill.setStatementByName(statementName.length()>1?statementName.substring(0,statementName.length()-1):"");
        costBill.setContractNo(contractNo.length()>1?contractNo.substring(0,contractNo.length()-1):"");
        costBill.setSettlementBasisName(settlementBasisName.length()>1?settlementBasisName.substring(0,settlementBasisName.length()-1):"");
        costBill.setNumberCount(statementDTOS.stream().map(TCostStatementDetailDTO::getNumber).reduce(BigDecimal.ZERO, BigDecimal::add));
        //sheet的名称
        costBill.setSheetName("包干费sheet");
        costBill.setCompanyName(tCostStatementDTOS.get(0).getCompanyName());
        return costBill;

    }

    /**
     * 商务回执单确认
     *
     * @param dto
     */
    @Override
    public void saveFile(TCostStatementDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("请选择操作得数据");
        }
        if (CollectionUtils.isEmpty(dto.getIds())) {
            throw new BusinessRuntimeException("结算单ID为空");
        }
        dto.getIds().forEach(tmpId -> {
            TCostStatementPO costStatement = tBizCostStatementMapper.getCostStatement(tmpId);
            if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
                throw new BusinessRuntimeException("商务确认失败，状态非计费审核");
            }
            sysFileService.saveFileBusRelation(dto.getFileIds(), tmpId);
        });

    }

    /**
     * 账单批量打印
     *
     * @param dto
     * @return
     */
    @Override
    public CostBillDtoSheetTemplate exportCostBillBath(TCostStatementDTO dto) {
        if (dto == null) {
            throw new BusinessRuntimeException("请选择要导出得数据");
        }
        if (CollectionUtils.isEmpty(dto.getStatementIds())) {
            throw new BusinessRuntimeException("结算单id为空");
        }
        List<Long> statementIds = dto.getStatementIds();
        //导出的对象
        CostBillDtoSheetTemplate costBill = new CostBillDtoSheetTemplate();
        List<TCostStatementDetailDTO> printList = new ArrayList<>();

        TCostStatementQueryDTO query = new TCostStatementQueryDTO();
        query.setId(statementIds.get(0));
        Page<TCostStatementDTO> tCostStatementDTOS = tBizCostStatementMapper.listCostStatement(query);
        if (CollectionUtils.isEmpty(tCostStatementDTOS)) {
            throw new BusinessRuntimeException("没有结算");

        }
        //结算单信息
        TCostStatementDTO costStatement = tCostStatementDTOS.get(0);

        costBill.setCustomerName(costStatement.getCustomerName());
        //客户信息
        TBusCustomerDTO customerDTO = customerMapper.getById(costStatement.getCustomerId());

        if (customerDTO == null) {
            throw new BusinessRuntimeException("没有客户信息");
        }
        //客户信息
        costBill.setTin(customerDTO.getTin());
        //开户行
        costBill.setBank(customerDTO.getBank());
        //银行账号
        costBill.setBankAccount(customerDTO.getBankAccount());
        //联系电话
        costBill.setContactNumber(customerDTO.getContactNumber());
        //审核日期
        costBill.setReviewTime(DateUtils.formatDate(costStatement.getReviewTime(), CommonEnum.DateFormatType.E_1.getCode()));
        //设置地址
        costBill.setAddress(customerDTO.getAddress());
        //设置结算方式
        costBill.setSettlementBasisName(costStatement.getSettlementBasisName());
        costBill.setNumberCount(BigDecimal.ZERO);
        costBill.setAmount(BigDecimal.ZERO);

        StringBuilder contractNo = new StringBuilder();
        StringBuilder statementName = new StringBuilder();
        StringBuilder reviewName = new StringBuilder();
        StringBuilder confirmName = new StringBuilder();
        StringBuilder settlementBasisName = new StringBuilder();

        costBill.setAmount(BigDecimal.ZERO);
        costBill.setNumber(BigDecimal.ZERO);
        List<TCostStatementDetailDTO> tmpCountStatements = new ArrayList<>(3);
        List<TCostStatementDetailDTO> statementDTOS = tBizCostStatementMapper.getcostStatementListByStatementIds(dto.getStatementIds());
        if(!CollectionUtils.isEmpty(statementDTOS)){
            statementDTOS =  statementDTOS.stream().filter(o->(o.getNumber()!=null)&&BigDecimal.ZERO.compareTo(o.getNumber())!=0).collect(Collectors.toList());

        }
        //商务只打印包干费的
        if("3".equals(dto.getRouteType())||"6".equals(dto.getRouteType())){
            if(!CollectionUtils.isEmpty(statementDTOS)){
                TCostStatementDetailDTO detailDTO = new TCostStatementDetailDTO();
                detailDTO.setFeeName("小计（包干费）");
                detailDTO.setAmount(BigDecimal.ZERO);
                detailDTO.setNumber(BigDecimal.ZERO);
                statementDTOS.forEach(o->{
                    detailDTO.setAmount(detailDTO.getAmount().add(o.getAmount()));
                    detailDTO.setNumber(detailDTO.getNumber().add(o.getNumber()));
                });
                statementDTOS.add(detailDTO);
                tmpCountStatements.add(detailDTO);
            }
        }else{

            if(!CollectionUtils.isEmpty(statementDTOS)){
                TCostStatementDetailDTO detailDTO = new TCostStatementDetailDTO();
                detailDTO.setFeeName("小计（包干费）");
                detailDTO.setAmount(BigDecimal.ZERO);
                detailDTO.setNumber(BigDecimal.ZERO);
                statementDTOS.forEach(o->{
                    detailDTO.setAmount(detailDTO.getAmount().add(o.getAmount()));
                    detailDTO.setNumber(detailDTO.getNumber().add(o.getNumber()));
                });
                statementDTOS.add(detailDTO);
                tmpCountStatements.add(detailDTO);
            }

            //堆存费
            List<Long> cargoInfoIds = tCostStatementDTOS.stream().map(TCostStatementDTO::getCargoInfoId).distinct().collect(Collectors.toList());
            List<TCostStatementDetailDTO> DCFstatementDTOS = storageFeeMapper.getStorageSettleStatementList(cargoInfoIds);
            if(!CollectionUtils.isEmpty(DCFstatementDTOS)){
                DCFstatementDTOS = DCFstatementDTOS.stream().filter(o->(o.getNumber()!=null)&&BigDecimal.ZERO.compareTo(o.getNumber())!=0).collect(Collectors.toList());
            }
            if(CollectionUtils.isEmpty(statementDTOS)){
                statementDTOS = DCFstatementDTOS;
            }else {
                statementDTOS.addAll(DCFstatementDTOS);
            }
            if(!CollectionUtils.isEmpty(DCFstatementDTOS)){
                TCostStatementDetailDTO DCFDetail = new TCostStatementDetailDTO();
                DCFDetail.setFeeName("小计（堆存费）");
                DCFDetail.setAmount(BigDecimal.ZERO);
                DCFDetail.setNumber(BigDecimal.ZERO);
                DCFstatementDTOS.forEach(o->{
                    DCFDetail.setAmount(DCFDetail.getAmount().add(o.getAmount()));
                    DCFDetail.setNumber(DCFDetail.getNumber().add(o.getNumber()));
                });
                statementDTOS.add(DCFDetail);
                tmpCountStatements.add(DCFDetail);

            }
            //杂项
            List<Long> cargoInfoIdList = tCostStatementDTOS.stream().map(TCostStatementDTO::getCargoInfoId).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(cargoInfoIdList)){
                List<TCostStatementDetailDTO> MISCList = storageFeeMapper.getMISCStatementList(cargoInfoIdList);
                if(CollectionUtils.isEmpty(statementDTOS)){
                    statementDTOS = MISCList;
                }else {
                    statementDTOS.addAll(MISCList);

                }
                if(!CollectionUtils.isEmpty(MISCList)){
                    TCostStatementDetailDTO MISCDetail = new TCostStatementDetailDTO();
                    MISCDetail.setFeeName("小计（其他费用）");
                    MISCDetail.setAmount(BigDecimal.ZERO);
                    MISCDetail.setNumber(BigDecimal.ZERO);
                    MISCList.forEach(o->{
                        MISCDetail.setAmount(MISCDetail.getAmount().add(o.getAmount()));
                        MISCDetail.setNumber(MISCDetail.getNumber().add(o.getNumber()));
                    });
                    statementDTOS.add(MISCDetail);
                    tmpCountStatements.add(MISCDetail);
                }
            }
        }


        if(!CollectionUtils.isEmpty(statementDTOS)){
            statementDTOS.forEach(o->{
                if(!StringUtils.isEmpty(o.getContactNo())&&!contractNo.toString().contains(o.getContactNo())){
                    contractNo.append(o.getContactNo()).append("_");
                }
                if(!StringUtils.isEmpty(o.getReviewName())&&!reviewName.toString().contains(o.getReviewName())){
                    reviewName.append(o.getReviewName()).append("/");
                }
                if(!StringUtils.isEmpty(o.getStatementName())&&!statementName.toString().contains(o.getStatementName())){
                    statementName.append(o.getStatementName()).append("/");
                }
                if(!StringUtils.isEmpty(o.getConfirmName())&&!confirmName.toString().contains(o.getConfirmName())){
                    confirmName.append(o.getConfirmName()).append("/");
                }
                if(!StringUtils.isEmpty(o.getSettlementBasisName())&&!settlementBasisName.toString().contains(o.getSettlementBasisName())){
                    settlementBasisName.append(o.getSettlementBasisName()).append("/");
                }
            });
        }
        tmpCountStatements.forEach(o->{
            costBill.setAmount(costBill.getAmount().add(o.getAmount()));
            costBill.setNumberCount(costBill.getNumberCount().add(o.getNumber()));
        });
        costBill.setDetailList(statementDTOS);
        costBill.setConfirmByName(confirmName.length()>1?confirmName.substring(0,confirmName.length()-1):"");
        costBill.setReviewByName(reviewName.length()>1?reviewName.substring(0,reviewName.length()-1):"");
        costBill.setStatementByName(statementName.length()>1?statementName.substring(0,statementName.length()-1):"");
        costBill.setContractNo(contractNo.length()>1?contractNo.substring(0,contractNo.length()-1):"");
        costBill.setSettlementBasisName(settlementBasisName.length()>1?settlementBasisName.substring(0,settlementBasisName.length()-1):"");
        //sheet的名称
        costBill.setSheetName("包干费sheet");
        costBill.setCompanyName(tCostStatementDTOS.get(0).getCompanyName());
        return costBill;
    }

    /**
     * 校验一批交接清单是不是同属于一个合同
     *
     * @param statementIds
     * @return
     */
    @Override
    public boolean getContractFlag(List<Long> statementIds) {

        ArrayList<TCostStatementDetailDTO> tmpList = new ArrayList<>();
        for (Long statementId : statementIds) {
            List<TCostStatementDetailDTO> tmpDetailList = tBizCostStatementMapper.listCostStatementDetailList(statementId);
            if (CollectionUtils.isEmpty(tmpDetailList)) {
                throw new BusinessRuntimeException("没有找到合同数据");
            }
            tmpList.addAll(tmpDetailList);
        }
        Map<Long, List<TCostStatementDetailDTO>> tmpMap = tmpList.stream().collect(Collectors.groupingBy(TCostStatementDetailDTO::getContractId));
        if (!CollectionUtils.isEmpty(tmpMap) && tmpMap.size() > 1) {
            return false;
        }
        return true;
    }


    /**
     * 根据结算单获取预结算量
     *
     * @param statementId
     * @return
     */
    @Override
    public TCostStatementDetailDTO getPreNumberCount(Long statementId) {
        return tBizCostStatementMapper.getCostStatementDetailByStatementID(statementId);
    }

    /**
     * 其他关联费用查询
     *
     * @param statementId
     * @return
     */
    @Override
    public List<TMiscBillingDTO> getMiscFee(Long statementId) {
        return tBizCostStatementMapper.getMiscFee(statementId);
    }


    @Override
    public byte[] pageExport(TCostStatementQueryDTO query) {

            Page<TCostStatementExportDTO> page = tBizCostStatementMapper.listCostStatementExport(query);
            if (!page.getResult().isEmpty()) {
                List<Long> ids = page.getResult().stream().map(TCostStatementExportDTO::getId).collect(Collectors.toList());
                List<Map<String, Object>> inTimes = tBizCostStatementMapper.listInTime(ids);
                page.getResult().forEach(v1 -> {
                    if(v1.getId()!=null){
                        Map<String, Object> inTimeMap = inTimes.stream().filter(v2 -> v1.getId().equals(Long.valueOf(String.valueOf(v2.get("id"))))).findFirst().orElse(null);
                        if (inTimeMap != null) {
                            if(inTimeMap.get("inTime")!=null){
                                v1.setInTime(String.valueOf(inTimeMap.get("inTime")));
                            }else {
                                //陆集陆疏  并且是货转的入库时间
                                if("2".equals(query.getRouteType())||"5".equals(query.getRouteType())||"6".equals(query.getRouteType())){
                                    String cargoInfoNo = v1.getCargoInfoNo();
                                    TBusCargoInfoDTO tBusCargoInfoDTO  = tBizCostStatementMapper.getCargoInfoByNo(cargoInfoNo);
                                    if("30".equals(tBusCargoInfoDTO.getSource())){
                                        List<String> inTime = tBizCostStatementMapper.getInTimeForHZandLJLS(cargoInfoNo);
                                        if (!inTime.isEmpty()){
                                            v1.setInTime(inTime.get(0));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //获取泊位
                    if(v1.getShipvoyageItemId()!=null){
                        List<String> berthNames = tBizCostStatementMapper.getShipAllBerthName(v1.getShipvoyageItemId());
                        v1.setBerthName(CollectionUtils.isEmpty(berthNames)?"":String.join(",",berthNames));
                    }
                });
            }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (ExcelWriter excelWriter = EasyExcel.write(os, TCostStatementExportDTO.class).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet0").build();
            transactionTemplate.executeWithoutResult(status -> {
                try  {
                    excelWriter.write(page.getResult(), writeSheet);
                } catch (Exception e) {
                    throw new IORuntimeException("包干费导出异常"+e.getMessage());
                }
            });
        }
        return os.toByteArray();
    }
}
