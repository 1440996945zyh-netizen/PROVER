package com.yy.ppm.statement.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.enums.WorkProcessEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.po.TBusServicePO;
import com.yy.ppm.business.mapper.TBusCargoInfoMapper;
import com.yy.ppm.business.mapper.TBusCargoTransferMapper;
import com.yy.ppm.business.mapper.TBusContractMapper;
import com.yy.ppm.business.mapper.TBusRateMapper;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import com.yy.ppm.statement.mapper.MiscBillingMapper;
import com.yy.ppm.statement.mapper.TProdCostStatementMapper;
import com.yy.ppm.statement.service.TProdCostStatementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Stream;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-11 17:32
 */
@Service
public class TProdCostStatementServiceImpl implements TProdCostStatementService {

    @Resource
    private TProdCostStatementMapper tProdCostStatementMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CommonService commonService;

    @Resource
    private TBusContractMapper contractMapper;

    @Resource
    private TBusRateMapper tBusRateMapper;
    @Resource
    private MiscBillingMapper miscBillingMapper;
    @Resource
    private TBusCargoInfoMapper tBusCargoInfoMapper;
    @Resource
    private TBusCargoTransferMapper tBusCargoTransferMapper;

    private static final String IMP_EXP_IN = "IN";
    private static final String RATE_STATUS_YES = "10";
    private static final String RATE_ITEM_GKBG = "02"; // 港口包干费
    private static final String PROCESS_CODE_BONDED_AREA = "1037"; // 作业过程-进保税区
    private static final String TRUST_TYPE_XC = "卸船"; // 通知单类型-卸船
    private static final String TRUST_TYPE_JG = "集港"; // 通知单类型-集港
    private static final String TRUST_TYPE_CXJG = "拆箱集港"; // 通知单类型-拆箱集港
    private static final Integer MISC_STATUS = 10; // 杂项费审核状态-未发布

    @Override
    public Pages<TBusHandoverlistDTO> listBusHandoverlist(TBusHandoverlistQueryDTO query, PageParameter parameter) {
        Pages<TBusHandoverlistDTO> result =  PageHelperUtils.limit(parameter, () -> {
            return tProdCostStatementMapper.listBusHandoverlist(query);
        });
        for (TBusHandoverlistDTO page : result.getPages()) {
            if(page.getShipvoyageItemId()!=null){
                List<String> strings = tProdCostStatementMapper.getShipAllBerthName(page.getShipvoyageItemId());
                page.setBerthName(CollectionUtils.isEmpty(strings)?"":String.join(",",strings));
            }

        }
        return result;
    }

    @Override
    public List<TCostStatementDTO> listCostStatement(Long handoverlistId,Long isFinal) {
        //type  用来区分生产结算还是预结算的请求  预结算的请求是0 生产结算的请求是1
        return tProdCostStatementMapper.listCostStatement(handoverlistId,isFinal);
    }

    @Override
    public List<Map<String, BigDecimal>> listPreSettlement(Long handoverlistId) {
        return tProdCostStatementMapper.listPreSettlement(handoverlistId);
    }

    public static void main(String[] args) {
        BigDecimal divide = BigDecimal.valueOf(1.99)
                .divide(BigDecimal.ONE.add(BigDecimal.valueOf(6).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP)), 9, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(6)).divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_UP);
        System.out.println(divide);
    }

    @Override
    public List<TBusServicePO> listService(String impExp, String type) {
        return tProdCostStatementMapper.listService(impExp, type);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void statement(TCostStatementDTO dto) {
        TBusHandoverlistDTO handoverlist = tProdCostStatementMapper.getHandoverlist(dto.getHandoverlistId());
        if (HandoverlistStatusEnum._30.getCode().equals(handoverlist.getStatementStatusCode())) {
            throw new BusinessRuntimeException("结算失败，当前交接清单已最终结算");
        }
        if (IsFinalEnum.TRUE.getCode().equals(dto.getIsFinal())) {
            handoverlist.setStatementStatusCode(HandoverlistStatusEnum._30.getCode());
            handoverlist.setStatementStatusName(HandoverlistStatusEnum._30.getName());
            tProdCostStatementMapper.updateHandoverlist(handoverlist);
        } else {
            if (HandoverlistStatusEnum._10.getCode().equals(handoverlist.getStatementStatusCode())) {
                handoverlist.setStatementStatusCode(HandoverlistStatusEnum._20.getCode());
                handoverlist.setStatementStatusName(HandoverlistStatusEnum._20.getName());
                tProdCostStatementMapper.updateHandoverlist(handoverlist);
            }
        }
//  2023/11/10   临时注释
/*
        List<TCostStatementDetailPO> details = tProdCostStatementMapper.listCostStatementDetailByHandoverlistId(dto.getHandoverlistId());

        BigDecimal totalNumbers = details.stream().map(v1 -> Optional.ofNullable(v1.getNumber()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal _totalNumbers = dto.getDetails().stream().map(v1 -> Optional.ofNullable(v1.getNumber()).orElse(BigDecimal.ZERO)).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (handoverlist.getTon().compareTo(totalNumbers.add(_totalNumbers)) < 0) {
            throw new BusinessRuntimeException("超出交接清单量");
        }
*/

        TCostStatementPO costStatement = new TCostStatementPO();
        BeanUtils.copyProperties(dto, costStatement);

        costStatement.setId(snowflake.nextId());
        String nextStatementNo = commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null);
        costStatement.setStatementNo(nextStatementNo);
        if("20".equals(dto.getType())){
            costStatement.setType(HandoverlistTypeEnum._20.getCode());
        }else {
            costStatement.setType(HandoverlistTypeEnum._10.getCode());
        }
        costStatement.setSettlementDate(new Date());
        costStatement.setStatus(StatementStatusEnum._10.getCode());
        tProdCostStatementMapper.insertCostStatement(costStatement);

        dto.getDetails().forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setStatement(costStatement.getId());
            if(v1.getNumber()==null ){
                v1.setNumber(BigDecimal.valueOf(0));
            }
        });
        tProdCostStatementMapper.insertCostStatementDetail(dto.getDetails());
        // 判断是否存在进保税区的货物，根据货量生成杂项费用
        if (dto.getBondedAreaTon() != null && dto.getBondedAreaTon().compareTo(BigDecimal.ZERO) > 0) {
            TBusRateSearchDTO searchDTO = new TBusRateSearchDTO();
            searchDTO.setStatus(RATE_STATUS_YES);
            searchDTO.setRateItemCode(RATE_ITEM_GKBG);
            searchDTO.setProcessCode(PROCESS_CODE_BONDED_AREA);
            searchDTO.setCurrTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd")); // 卸船取靠泊时间，集港取首次过磅时间？？？
            List<TBusRateDTO> rateList = tBusRateMapper.getBusRateList(searchDTO);
            if (!CollectionUtils.isEmpty(rateList)) {
                if (rateList.size() != 1) {
                    throw new BusinessRuntimeException("匹配到多个进保税区费率，无法计算费用");
                }
                TBusRateDTO tBusRateDTO = rateList.get(0);
                TMiscBillingPO miscBillingPO = new TMiscBillingPO();
                miscBillingPO.setId(snowflake.nextId());
                miscBillingPO.setRateItemCode(tBusRateDTO.getRateItemCode());
                miscBillingPO.setRateName(tBusRateDTO.getRateItemName());
                miscBillingPO.setRate(tBusRateDTO.getRate());
                miscBillingPO.setTaxRate(tBusRateDTO.getTaxRate());
                miscBillingPO.setBillDate(new Date());
                miscBillingPO.setVoyageId(dto.getShipvoyageItemId());
                miscBillingPO.setShipVoyage(dto.getShipNameVoyage());
                miscBillingPO.setBillQuantity(dto.getBondedAreaTon());
                miscBillingPO.setAmountMoney(dto.getBondedAreaTon().multiply(tBusRateDTO.getRate()));
                BigDecimal taxRate = tBusRateDTO.getTaxRate().divide(new BigDecimal("100"));
                miscBillingPO.setTaxAmount(miscBillingPO.getAmountMoney()
                        .divide(taxRate.add(new BigDecimal("1")),9,BigDecimal.ROUND_HALF_UP)
                        .multiply(taxRate).setScale(2, BigDecimal.ROUND_HALF_UP));
                miscBillingPO.setCustomerId(dto.getCustomerId());
                miscBillingPO.setStatus(MISC_STATUS);
                miscBillingPO.setCompanyId(dto.getCompanyId());
                miscBillingPO.setCompanyName(dto.getCompanyName());
                miscBillingPO.setRateId(tBusRateDTO.getId());
                miscBillingPO.setUnitCode(tBusRateDTO.getMeasurementUnitCode1());
                miscBillingPO.setUnitName(tBusRateDTO.getMeasurementUnitName1());
                miscBillingPO.setProcessCode(tBusRateDTO.getProcessCode());
                miscBillingPO.setProcessName(tBusRateDTO.getProcessName());
                miscBillingPO.setCargoInfoId(dto.getCargoInfoId());
                miscBillingPO.setCargoInfoName(dto.getCargoInfoNo() + "-"
                        + dto.getCargoName() + "-" + dto.getTradeType());
                miscBillingPO.setOtherStatementId(costStatement.getId());
                miscBillingMapper.addMiscBilling(miscBillingPO);
            } else {
                throw new BusinessRuntimeException("未匹配到进保税区费率，无法计算费用");
            }
        }
    }

    @Override
    public BigDecimal getZQQuantity(Long handoverlistId,String type) {
        TBusHandoverlistDTO result;
        TBusHandoverlistDTO handoverlist = tProdCostStatementMapper.getHandoverlistById(handoverlistId);
        if (IMP_EXP_IN.equals(handoverlist.getImpExp())) { // 进口
            result = tProdCostStatementMapper.getImpZQQuantity(handoverlistId,type);
        } else { // 出口
            result = tProdCostStatementMapper.getExpZQQuantity(handoverlistId,type);
        }
        return result == null ? new BigDecimal("0") : result.getZqQuantity();
    }

    /**
     * 过磅量查询
     * @param handoverlistId
     * @return
     */
    @Override
    public TBusHandoverlistDTO getWeighCapacity(Long handoverlistId) {
        TBusHandoverlistDTO result = tProdCostStatementMapper.getWeighCapacity(handoverlistId);
        return result;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelStatement(Long id) {
        TCostStatementDTO costStatement = tProdCostStatementMapper.getCostStatement(id);
        if(StringUtils.isEmpty(costStatement.getStatus())){
            throw new BusinessRuntimeException("结算单状态为空");
        }
        if (Integer.valueOf(costStatement.getStatus())>10) {
            throw new BusinessRuntimeException("已计费不可撤销");
        }
        // 查询是否生成进保税区杂项费，以及费用是否已审核
        List<TMiscBillingDTO> bondedAreaFeeList =
                miscBillingMapper.getListByStatementIdAndRate(costStatement.getId(), PROCESS_CODE_BONDED_AREA);
        if (!CollectionUtils.isEmpty(bondedAreaFeeList)) {
            bondedAreaFeeList.stream().forEach(item -> {
                if (!MISC_STATUS.equals(item.getStatus())) {
                    throw new BusinessRuntimeException("杂项费用已审核，无法撤销");
                }
                miscBillingMapper.deleteMisc(item.getId());
            });
        }

        tProdCostStatementMapper.deleteCostStatement(id);
        tProdCostStatementMapper.deleteCostStatementDetail(id);

        HandoverlistStatusEnum status;
        List<TCostStatementDTO> costStatements = tProdCostStatementMapper.listCostStatement(costStatement.getHandoverlistId(),null);
        boolean anyMatch = costStatements.stream().anyMatch(v1 -> IsFinalEnum.TRUE.getCode().equals(v1.getIsFinal()));
        if (anyMatch) {
            status = HandoverlistStatusEnum._30;
        } else {
            if (!costStatements.isEmpty()) {
                status = HandoverlistStatusEnum._20;
            } else {
                status = HandoverlistStatusEnum._10;
            }
        }
        TBusHandoverlistDTO handoverlist = new TBusHandoverlistDTO();
        handoverlist.setId(costStatement.getHandoverlistId());
        handoverlist.setStatementStatusCode(status.getCode());
        handoverlist.setStatementStatusName(status.getName());
        tProdCostStatementMapper.updateHandoverlist(handoverlist);
    }

    @Override
    public TCostStatementDTO getCostStatement(Long id,Long bhId) {
        List<Map<String, BigDecimal>> maps = tProdCostStatementMapper.listPreSettlement(bhId);
        TCostStatementDTO result = tProdCostStatementMapper.getCostStatement(id);
        List<TCostStatementDetailPO> details = tProdCostStatementMapper.listCostStatementDetail(id);
        details.forEach(o->{
            maps.stream().forEach(v->{
                if(String.valueOf(v.get("serviceContentId")).equals(String.valueOf(o.getServiceContentId()))){
                    o.setNumber2(v.get("number"));
                }
            });
        });
        result.setDetails(details);
        return result;
    }

    @Override
    public List<TBusContractDTO> getSettlementBasis(Long handoverlistId) {
        return tProdCostStatementMapper.getContractList(handoverlistId);
    }

    @Override
    public TBusTrustDTO getSettlementBasisByHandover(Long handoverlistId) {
        return tProdCostStatementMapper.getSettlementBasisByHandover(handoverlistId);
    }

    @Override
    public BigDecimal getBondedAreaTon(Long cargoInfoId) {
        return tProdCostStatementMapper.getBondedAreaTon(cargoInfoId);
    }


    @Override
    public TCostStatementDTO getJSGWeightByHandoverlistId(Long id) {
        TCostStatementDTO result = new TCostStatementDTO();
        result.setJGWeight(tProdCostStatementMapper.getJGWeightByHandoverlistId(id));
        result.setSGWeight(tProdCostStatementMapper.getSGWeightByHandoverlistId(id));
        return result;
    }
}
