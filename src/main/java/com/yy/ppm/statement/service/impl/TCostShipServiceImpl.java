package com.yy.ppm.statement.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.github.pagehelper.Page;
import com.yy.common.enums.CommonEnum;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.DateUtils;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SpringContextUtils;
import com.yy.common.util.SpringUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import com.yy.ppm.statement.bean.dto.costShip.*;
import com.yy.ppm.statement.bean.po.TCostShipPO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import com.yy.ppm.statement.mapper.MiscBillingMapper;
import com.yy.ppm.statement.mapper.TCostShipMapper;
import com.yy.ppm.statement.service.TCostShipService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.ast.Var;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-20 11:24
 */
@Service
public class TCostShipServiceImpl implements TCostShipService {

    @Resource
    private TCostShipMapper tCostShipMapper;

    @Autowired
    private Snowflake snowflake;

    @Resource
    MiscBillingMapper miscBillingMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private static final String STATUS_00 = "00"; // 未结算
    private static final String STATUS_40 = "40"; // 部分开票
    private static final String STATUS_50 = "50"; // 已开票

    private static final String LEVER_PORT = "110"; // 离泊
    private static final String ENTER_PORT = "50"; // 靠泊

    private static final List<String> FEE_SHUI_CODE = Arrays.asList("MS00288","MS00287","MS00308");
    private static final List<String> FEE_DIAN_CODE = Collections.singletonList("MS00276");

    /**
     * 审核状态-未审核
     */
    private static final String STATUS_10 = "10";

    /**
     * 审核状态-已审核
     */
    private static final String STATUS_20 = "20";

    @Autowired
    private CommonService commonService;

    @Override
    public Pages<TDisShipvoyageItemDTO> listShipvoyageItem(TDisShipvoyageItemQueryDTO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            Page<TDisShipvoyageItemDTO> page = tCostShipMapper.listShipvoyageItem(query);
            page.getResult().forEach(v1 -> {
                try {
                    TDisShipvoyageItemQueryDTO tDisShipvoyageItemQueryDTO = new TDisShipvoyageItemQueryDTO();
                    tDisShipvoyageItemQueryDTO.setShipvoyageItemId(v1.getShipvoyageItemId());
                    List<TDisShipvoyageItemDTO> specialDynamicList = SpringUtils.getBean(this.getClass()).getSpecialDynamicList(tDisShipvoyageItemQueryDTO, new PageParameter());
                    BigDecimal specialBerthHours = specialDynamicList.get(0).getBerthHours()==null?BigDecimal.ZERO:specialDynamicList.get(0).getBerthHours();
                    BigDecimal v =(BigDecimal.valueOf(SpringUtils.getBean(this.getClass()).getBerthMillis(v1.getShipvoyageItemId()))
                            .divide(new BigDecimal(60*60*1000),5,BigDecimal.ROUND_HALF_UP)
                    .subtract(specialBerthHours)).divide(BigDecimal.valueOf(24L), 0,BigDecimal.ROUND_UP);

                    v1.setBerthDays( v );
                }catch (Exception e){
                    v1.setBerthDays(null);
                }
                // 查询状态
                v1.setStatus(getStatusByVoyageItemId(v1.getShipvoyageItemId()));
            });
            return page;
        });
    }

    /**
     * 根据航次子表id查询状态（未使用）
     * @param shipvoyageItemId
     * @return
     */
    private String getStatusByVoyageItemId(Long shipvoyageItemId) {
        List<TCostShipStatusDTO> costShipStatusList = tCostShipMapper.getCostShipStatus(shipvoyageItemId);
        if (CollectionUtils.isEmpty(costShipStatusList)) {
            return STATUS_00;
        }
        TCostShipStatusDTO costStatementStatus = tCostShipMapper.getCostStatementStatus(shipvoyageItemId);
        if (costStatementStatus == null) {
            return costShipStatusList.get(0).getStatus();
        } else {
            if (STATUS_40.equals(costStatementStatus.getStatus()) || STATUS_50.equals(costStatementStatus.getStatus())) {
                return costStatementStatus.getStatus();
            }
            return STATUS_20;
        }
    }

    @Override
    public List<TDisShipDynamicDTO> listStopRecord(Long shipvoyageId) {
        List<TDisShipDynamicDTO> shipDynamics = tCostShipMapper.listStopRecord(shipvoyageId);

        return shipDynamics.stream()
                .filter(v1 -> shipDynamics.indexOf(v1) % 2 == 0 && shipDynamics.indexOf(v1) + 1 < shipDynamics.size())
                .peek(v1 -> {
                    long millis = shipDynamics.get(shipDynamics.indexOf(v1) + 1).getDynamicStartTime().getTime() - v1.getDynamicStartTime().getTime();
                    int hours = (int) Math.ceil((double) millis / TimeUnit.HOURS.toMillis(1));
                    v1.setStopHours(hours);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TDisShipDynamicDTO> listStopRecordNew(Long shipvoyageId) {
        List<TDisShipDynamicDTO> shipDynamics = tCostShipMapper.listStopRecord(shipvoyageId);

        return shipDynamics.stream()
                .filter(v1 -> shipDynamics.indexOf(v1) % 2 == 0 && shipDynamics.indexOf(v1) + 1 < shipDynamics.size())
                .peek(v1 -> {
                    long millis = shipDynamics.get(shipDynamics.indexOf(v1) + 1).getDynamicStartTime().getTime() - v1.getDynamicStartTime().getTime();
                    int hours = (int) Math.ceil((double) millis / TimeUnit.DAYS.toMillis(1));
                    v1.setStopHours(hours);
                })
                .filter(o -> "2".equals(o.getStopReasonTypeCode()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TBusRatePO> listRate(Long shipvoyageId) {
        return tCostShipMapper.listRate(shipvoyageId);
    }

    @Override
    public void statement(List<TCostShipPO> costShips) {
        List<TCostShipPO> tempCostShips = tCostShipMapper.listCostShipByVoyage(costShips.get(0).getShipvoyageItemId());
        if (CollectionUtils.isNotEmpty(tempCostShips)) {
            throw new BusinessRuntimeException("航次停泊费已结算，无法再次结算");
        }

        costShips.forEach(v1 -> {
            v1.setId(snowflake.nextId());
            v1.setStatus(STATUS_10);
        });
        tCostShipMapper.insertCostShip(costShips);
    }

    @Override
    public List<TCostShipPO> listCostShip(Long shipvoyageItemId) {
        return tCostShipMapper.listCostShip(shipvoyageItemId);
    }

    @Override
    public void cancelStatement(Long shipvoyageItemId) {
        List<TCostShipPO> costShips = tCostShipMapper.listCostShip(shipvoyageItemId);
        boolean anyMatch = costShips.stream().anyMatch(v1 -> STATUS_20.equals(v1.getStatus()));
        if (anyMatch) {
            throw new BusinessRuntimeException("已审核无法撤销结算，请先销审");
        }

        tCostShipMapper.deleteCostShip(shipvoyageItemId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void review(Long shipvoyageItemId) {
        List<TCostShipPO> costShips = tCostShipMapper.listCostShip(shipvoyageItemId);
        if (costShips.isEmpty()) {
            throw new BusinessRuntimeException("未结算无法审核");
        }

        boolean anyMatch = costShips.stream().anyMatch(v1 -> STATUS_20.equals(v1.getStatus()));
        if (anyMatch) {
            throw new BusinessRuntimeException("已审核无法再次审核");
        }

        TCostShipPO costShip = new TCostShipPO();
        costShip.setShipvoyageItemId(shipvoyageItemId);
        costShip.setStatus(STATUS_20);
        tCostShipMapper.updateCostShip(costShip);

        TCostStatementPO costStatement = new TCostStatementPO();
        costStatement.setId(snowflake.nextId());
        costStatement.setCompanyId(costShips.get(0).getCompanyId());
        costStatement.setCompanyName(costShips.get(0).getCompanyName());
        costStatement.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        costStatement.setCustomerId(costShips.get(0).getCustomerId());
        costStatement.setCustomerName(costShips.get(0).getCustomerName());
        costStatement.setType(HandoverlistTypeEnum._30.getCode());
        costStatement.setShipvoyageId(costShips.get(0).getShipvoyageId());
        costStatement.setShipvoyageItemId(costShips.get(0).getShipvoyageItemId());
        costStatement.setSettlementDate(new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate());
        costStatement.setStatus(StatementStatusEnum._30.getCode());
        costStatement.setIsFinal(IsFinalEnum.TRUE.getCode());

        List<TCostStatementDetailPO> costStatementDetails = costShips.stream().map(v1 -> {
            TCostStatementDetailPO costStatementDetail = new TCostStatementDetailPO();
            costStatementDetail.setId(snowflake.nextId());
            costStatementDetail.setStatement(costStatement.getId());
            costStatementDetail.setRateItemCode(v1.getRateItemCode());
            costStatementDetail.setRateItemName(v1.getRateItemName());
            costStatementDetail.setRate(v1.getRate());
            costStatementDetail.setUnitCode(v1.getUnitCode());
            costStatementDetail.setUnitName(v1.getUnitName());
            costStatementDetail.setNumber(v1.getNumber());
            costStatementDetail.setAmount(v1.getAmount());
            costStatementDetail.setTax(v1.getTaxRate());
            costStatementDetail.setTaxAmount(v1.getTaxAmount());
            costStatementDetail.setRemark(v1.getRemark());
            costStatementDetail.setBusinessId(v1.getId());
            costStatementDetail.setRateId(v1.getRateId());
            costStatementDetail.setNumber2(v1.getNumber2() == null ? null : BigDecimal.valueOf(v1.getNumber2()));
            return costStatementDetail;
        }).collect(Collectors.toList());

        tCostShipMapper.insertCostStatement(costStatement);
        tCostShipMapper.insertCostStatementDetail(costStatementDetails);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void cancelReview(Long shipvoyageItemId) {
        TCostStatementPO costStatement = tCostShipMapper.getCostStatement(shipvoyageItemId);
        if (costStatement == null) {
            throw new BusinessRuntimeException("未审核无法销审");
        }
        if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
            throw new BusinessRuntimeException("结算单已开票，无法销审");
        }

        TCostShipPO costShip = new TCostShipPO();
        costShip.setShipvoyageItemId(shipvoyageItemId);
        costShip.setStatus(STATUS_10);
        tCostShipMapper.updateCostShip(costShip);

        tCostShipMapper.deleteCostStatement(costStatement.getId());
        tCostShipMapper.deleteCostStatementDetail(costStatement.getId());
    }

    /**
     * 停泊费、杂项计费账单打印
     * @param shipvoyageId
     * @param shipvoyageItemId
     * @return
     */
    @Override
    public TCostShipExportDTO exportFee(Long shipvoyageId, Long shipvoyageItemId) {

        TCostShipExportDTO tCostFee = new TCostShipExportDTO();
        tCostFee.setSheetName("sheet1");
        TDisShipvoyageItemQueryDTO query = new TDisShipvoyageItemQueryDTO();
        PageParameter parameter = new PageParameter();
        parameter.setStartPage(1);
        parameter.setPageSize(20);
        query.setIsLeavePort("1");
        query.setShipvoyageItemId(shipvoyageItemId);
        Pages<TDisShipvoyageItemDTO> pagesTDisShipvoyageItemDTO = this.listShipvoyageItem(query,parameter);
        if(pagesTDisShipvoyageItemDTO == null || CollectionUtils.isEmpty(pagesTDisShipvoyageItemDTO.getPages())){
            throw  new BusinessRuntimeException("没有查询到船舶信息");
        }
        List<TBusRatePO> tBusRatePOS = this.listRate(shipvoyageId);
        if(CollectionUtils.isEmpty(tBusRatePOS)){
           throw  new BusinessRuntimeException("没有匹配到费率");
        }

        List<TCostShipPO> tCostShipPOS = this.listCostShip(shipvoyageItemId);
        if(CollectionUtils.isEmpty(tCostShipPOS)){
            throw new BusinessRuntimeException("没有结算不能导出");
        }
        CostShipDetailExportDTO tmpCostShip = new CostShipDetailExportDTO();
        tmpCostShip.setFeeItem("停泊费");
        List<CostShipDetailExportDTO> detailList = new ArrayList<>();

        //主列表数据
        TDisShipvoyageItemDTO tmpShipVoyageItemDto = pagesTDisShipvoyageItemDTO.getPages().get(0);
        //费用
        TBusRatePO tBusRatePO = tBusRatePOS.get(0);
        //详情
        TCostShipPO tCostShipPO = tCostShipPOS.get(0);

        //停泊费
        TCostStatementPO costStatement = tCostShipMapper.getCostStatement(tCostShipPO.getShipvoyageItemId());


        //计费人
        tCostFee.setFeeManName(tCostShipPO.getCreateByName());
        //审核人
        tCostFee.setReviewByName(costStatement.getCreateByName());
        //审核时间
        tCostFee.setReviewTime(DateUtils.formatDate(costStatement.getCreateTime(), CommonEnum.DateFormatType.E_1.getCode()));

        //设置客户名称
        tmpCostShip.setCustomerName(tCostShipPO.getCustomerName());

        //停泊费用
        tmpCostShip.setAmount(tCostShipPO.getAmount());

        Date strDateTime = tmpShipVoyageItemDto.getBerthTime(); //  list.getBerthTime() 返回一个 Date 对象
        SimpleDateFormat outputFormat = new SimpleDateFormat("yy年MM月dd日HH时mm分");
        String berthTime = outputFormat.format(strDateTime);//靠泊时间
    
        Date DateTime = tmpShipVoyageItemDto.getLeaveBerthTime(); //  list.getBerthTime() 返回一个 Date 对象
        SimpleDateFormat endFormat = new SimpleDateFormat("yy年MM月dd日HH时mm分");
        String leaveTime = endFormat.format(DateTime);//离泊时间

        //停泊费计算依据
        tmpCostShip.setPayBasis(tBusRatePO.getInteFore()+",净吨:"+
                tCostShipPO.getNumber2()+",停泊时间:"+berthTime+
                "至"+leaveTime +",共:"+BigDecimal.valueOf(SpringUtils.getBean(this.getClass()).getBerthMillis(shipvoyageItemId)/(60 * 60 * 1000)).setScale(2,BigDecimal.ROUND_HALF_UP)+"小时,合计:"+SpringUtils.getBean(this.getClass()).getBerthDyas(shipvoyageItemId)+"天停泊费=计费吨×日×基准费率="+
                tCostShipPO.getNumber2()+"×"+tmpShipVoyageItemDto.getBerthDays() +"×"+tBusRatePO.getRate()+"="+tCostShipPO.getAmount());
        //备注
        tmpCostShip.setRemark(tBusRatePO.getRemark());
        //结算单号
        tCostFee.setStatementNo(tmpShipVoyageItemDto.getStatementNo());
        //设置船舶类型
        tCostFee.setShipKindName(tCostShipMapper.getShipKindName(costStatement.getShipvoyageId()));

        //添加停泊费到要打印的账单数据上
        detailList.add(tmpCostShip);
        //查询特殊停泊费
        List<CostShipDetailExportDTO> specialCostShipList = tCostShipMapper.getSpecialCostShipListByShip(shipvoyageId, shipvoyageItemId);
        CostShipDetailExportDTO specialCostShipDto = new CostShipDetailExportDTO();
        if(!specialCostShipList.isEmpty()){
            //specialCostShipDto 的长度永远为1
            for (CostShipDetailExportDTO costShipDetailExportDTO : specialCostShipList) {
                specialCostShipDto.setAmount(costShipDetailExportDTO.getAmount());
                specialCostShipDto.setFeeItem(costShipDetailExportDTO.getFeeItem());
            }
            //设置结算依据
            List<TDisShipvoyageItemDTO> specialDynamicByShipvoyageItemId = tCostShipMapper.getSpecialDynamicByShipvoyageItemId(shipvoyageItemId);
            Map<String, BigDecimal> rateMap = tBusRatePOS.stream().collect(Collectors.toMap(TBusRatePO::getRateItemCode, TBusRatePO::getRate, (k1, k2) -> k2));

            specialCostShipDto.setPayBasis(tBusRatePO.getInteFore()
                    +",净吨:"+
                    tCostShipPO.getNumber2()
                    +",特殊停泊开始时间:"
                    + DateUtils.formatDate(specialDynamicByShipvoyageItemId.get(0).getDynamicStartTime(),"yyyy-MM-dd HH:mm:ss")
                    +"至"
                    +DateUtils.formatDate(specialDynamicByShipvoyageItemId.get(0).getDynamicEndTime(),"yyyy-MM-dd HH:mm:ss")
                    +",共:"
                    +specialCostShipList.get(0).getSpecialNumber()
                    +"小时,合计:"
                    +("内贸".equals(tBusRatePO.getInteFore())?specialCostShipList.get(0).getSpecialNumber()+"天特殊外贸":specialCostShipList.get(0).getSpecialNumber()+"小时特殊外贸")
                    +"停泊费=计费吨×"
                    +("内贸".equals(tBusRatePO.getInteFore())?"天":"小时")
                    +"×基准费率="
                    + tCostShipPO.getNumber2()+"×"
                    +("内贸".equals(tBusRatePO.getInteFore())?specialCostShipList.get(0).getSpecialNumber():specialCostShipList.get(0).getSpecialNumber())
                    +"×"
                    +("内贸".equals(tBusRatePO.getInteFore())?rateMap.get("MS00260"):rateMap.get("MS00261"))
                    +"="
                    +specialCostShipDto.getAmount());
            detailList.add(specialCostShipDto);
        }

        //20240627 计费提出 只打印杂项中和停泊费 加水接电相关的费用
        List<TCostStatementPO> costStatementList =
                miscBillingMapper.getCostStatementList(costStatement.getShipvoyageId(),
                        costStatement.getShipvoyageItemId(), tCostShipPO.getCustomerId());

        if(CollectionUtils.isNotEmpty(costStatementList)){
            costStatementList.forEach(o->{
               CostShipDetailExportDTO tmpMiscBill = new CostShipDetailExportDTO();
               tmpMiscBill.setFeeItem(o.getRateItemName());
               //设置客户名称
               tmpMiscBill.setCustomerName(tCostShipPO.getCustomerName());

               //备注显示结算单号
                tmpMiscBill.setRemark(o.getStatementNo());
               //显示金额
               tmpMiscBill.setAmount(o.getAmount());
                if(FEE_SHUI_CODE.stream().anyMatch(tmp->tmp.equals(o.getRateItemCode()))){
                    tmpMiscBill.setPayBasis("加水:" + o.getNumber()+"吨。合计：总"
                            +StringUtils.substring(o.getRateItemName(),0,StringUtils.indexOf(o.getRateItemName(),"（"))
                            +"=加水吨数X单价="+o.getNumber()+"×"+o.getRate()+"="+o.getAmount() );

                }else if(FEE_DIAN_CODE.stream().anyMatch(tmp->tmp.equals(o.getRateItemCode()))){
                    tmpMiscBill.setPayBasis("岸电:" + o.getNumber()+"度。合计：总"
                            +StringUtils.substring(o.getRateItemName(),0,StringUtils.indexOf(o.getRateItemName(),"（"))
                            +"=用电度数X单价="+o.getNumber()+"×"+o.getRate()+"="+o.getAmount() );
                }

               detailList.add(tmpMiscBill);
            });

       }
        //设置序号
        if(CollectionUtils.isNotEmpty(detailList)){
            tCostFee.setAmount(BigDecimal.ZERO);
            for (int i = 0; i < detailList.size(); i++) {
                detailList.get(i).setSortNum(i+1);
                tCostFee.setAmount(tCostFee.getAmount().add( detailList.get(i).getAmount()));
            }
            tCostFee.setDetailList(detailList);

        }else {
            throw new BusinessRuntimeException("没有要打印的账单");
        }

        //内外贸
        tCostFee.setInteFore(tBusRatePO.getInteFore());
        //船名
        tCostFee.setShipName(tmpShipVoyageItemDto.getShipNameExport());
        //航次
        tCostFee.setVoyage(tmpShipVoyageItemDto.getVoyage());
        //结算单编号
        if(costStatement!=null){
            tCostFee.setStatementNo(costStatement.getStatementNo());
        }
        tCostFee.setCompanyName(costStatement.getCompanyName());
        return tCostFee;
    }

    @Override
    public List<TCostShipPO> listOtherCostShip(Long shipvoyageItemId) {

        List<TCostShipPO> tCostShipPOS = tCostShipMapper.listOtherCostShip(shipvoyageItemId);
        //根据船名航次id查询杂项计费表非货方费用
        List<TCostShipPO>  tMiscBillingPOS = tCostShipMapper.getMiscByShipItemId(shipvoyageItemId);

        tCostShipPOS.addAll(tMiscBillingPOS);
        return tCostShipPOS;
    }

    @Override
    public Integer getBerthDyas(Long shipvoyageItemId) {
        return Integer.valueOf(
                String.valueOf(
                        BigDecimal.valueOf(SpringUtils.getBean(this.getClass()).getBerthMillis(shipvoyageItemId))
                                .divide(new BigDecimal(24*60*60*1000),0,BigDecimal.ROUND_UP)));
    }

    public long getBerthMillis(Long shipvoyageItemId){
        TDisShipvoyagePO shipvoyage = tCostShipMapper.getShipvoyageByItemId(shipvoyageItemId);
        long BerthDays = 0;
        TDisShipvoyageItemDTO tDisShipvoyageItemDTO = new TDisShipvoyageItemDTO();
        tDisShipvoyageItemDTO.setShipvoyageItemId(shipvoyageItemId);
        tDisShipvoyageItemDTO.setShipvoyageId(String.valueOf(shipvoyage.getId()));

        long allInOutDays = 0;
        if (InOutPortEnum.INOUT.getCode().equals(shipvoyage.getImpExp())) {

            // 进出口的航次，以进口完工时间为分隔计算停泊天数
            List<TDisShipvoyageItemPO> shipvoyageItems = tCostShipMapper.listShipvoyageItemByShipvoyageId(shipvoyage.getId());
            TDisShipvoyageItemPO shipvoyageItem = shipvoyageItems.stream()
                    .filter(v2 -> shipvoyageItemId.equals(v2.getId()))
                    .findFirst().orElseThrow(null);
            if(shipvoyageItem.getWorkEndTime()==null){
                throw new BusinessRuntimeException("进口信息不完善，没有进口完工时间");
            }
            LocalDateTime endDate = shipvoyageItem.getWorkEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            List<TDisShipvoyageItemDTO> tDisShipvoyageItemDTOS = null;
            List<TDisShipvoyageItemDTO> dynamicList = null;
            if (InOutPortEnum.IN.getCode().equals(shipvoyageItem.getImpExp())) {
                TDisShipvoyageItemDTO queryIn = new TDisShipvoyageItemDTO();
                queryIn.setShipvoyageItemId(shipvoyageItem.getId());
                queryIn.setShipvoyageId(String.valueOf(shipvoyage.getId()));
                dynamicList = tCostShipMapper.getShipVoyageDynamicList(queryIn);
                LocalDateTime localDateTime = dynamicList.get(0).getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                allInOutDays = Duration.between(localDateTime, endDate).toMillis();

                tDisShipvoyageItemDTOS = dynamicList.subList(1, dynamicList.size());
            }
            if (InOutPortEnum.OUT.getCode().equals(shipvoyageItem.getImpExp())) {
                shipvoyageItem = shipvoyageItems.stream()
                        .filter(v2 -> !shipvoyageItemId.equals(v2.getId()))
                        .findFirst().orElseThrow(null);
                LocalDateTime beginDate = null;
                if (shipvoyageItem.getWorkEndTime() != null && shipvoyage.getLeaveBerthTime() != null) {
                    beginDate = shipvoyageItem.getWorkEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
                TDisShipvoyageItemDTO queryIn = new TDisShipvoyageItemDTO();
                queryIn.setShipvoyageItemId(shipvoyageItemId);
                queryIn.setShipvoyageId(String.valueOf(shipvoyage.getId()));
                dynamicList = tCostShipMapper.getShipVoyageDynamicList(queryIn);
                LocalDateTime outEndTime = dynamicList.get(dynamicList.size()-1).getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                allInOutDays = Duration.between(beginDate,outEndTime).toMillis();
                tDisShipvoyageItemDTOS = dynamicList.subList(0, dynamicList.size()-1);

            }

            dynamicList = this.outVoyageLevePortTime(shipvoyageItem);

            if(CollectionUtils.isNotEmpty(tDisShipvoyageItemDTOS)&&dynamicList.size()>=2){
                if(tDisShipvoyageItemDTOS.size()%2!=0){
                    throw new BusinessRuntimeException("靠离泊信息不完善");
                }
                for (int i = 0; i < dynamicList.size(); i+=2) {
                    TDisShipvoyageItemDTO tDisShipvoyageItemDTOFirst = dynamicList.get(i);
                    TDisShipvoyageItemDTO tDisShipvoyageItemDTOSecond = dynamicList.get(i+1);
                    if(!LEVER_PORT.equals(tDisShipvoyageItemDTOFirst.getDynamicTypeCode()))
                    {
                        throw new BusinessRuntimeException("缺少离泊信息");
                    }
                    if(!ENTER_PORT.equals(tDisShipvoyageItemDTOSecond.getDynamicTypeCode()))
                    {
                        throw new BusinessRuntimeException("缺少靠泊信息");
                    }
                    LocalDateTime beginDate = tDisShipvoyageItemDTOFirst.getDynamicStartTime().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime endDate2 = tDisShipvoyageItemDTOSecond.getDynamicStartTime().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
                    allInOutDays = allInOutDays - Duration.between(beginDate, endDate2).toMillis();

                }
            }
            BerthDays = allInOutDays;

        }else{
            List<TDisShipvoyageItemDTO> dynamicList = tCostShipMapper.getShipVoyageDynamicList(tDisShipvoyageItemDTO);

            if(CollectionUtils.isNotEmpty(dynamicList)&&dynamicList.size()>=2){

                if(dynamicList.size()%2!=0){
                    throw new BusinessRuntimeException("靠离泊信息不完善");
                }
                TDisShipvoyageItemDTO startDynamic = dynamicList.get(0);
                TDisShipvoyageItemDTO endDynamic = dynamicList.get(dynamicList.size()-1);
                LocalDateTime startDynamicDate = startDynamic.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime endDynamicDate = endDynamic.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                long allBerthDays = Duration.between(startDynamicDate, endDynamicDate).toMillis();

                //获取间隔时间
                List<TDisShipvoyageItemDTO> data = dynamicList.subList(1, dynamicList.size() - 1);
                for (int i = 0; i < data.size(); i+=2) {
                    TDisShipvoyageItemDTO tDisShipvoyageItemDTOFirst = data.get(i);
                    TDisShipvoyageItemDTO tDisShipvoyageItemDTOSecond = data.get(i+1);
                    if(!LEVER_PORT.equals(tDisShipvoyageItemDTOFirst.getDynamicTypeCode()))
                    {
                        throw new BusinessRuntimeException("缺少离泊信息");
                    }
                    if(!ENTER_PORT.equals(tDisShipvoyageItemDTOSecond.getDynamicTypeCode()))
                    {
                        throw new BusinessRuntimeException("缺少靠泊信息");
                    }
                    LocalDateTime beginDate = tDisShipvoyageItemDTOFirst.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    LocalDateTime endDate = tDisShipvoyageItemDTOSecond.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    allBerthDays = allBerthDays - Duration.between(beginDate, endDate).toMillis();

                }
                BerthDays = allBerthDays;

            }
        }
        return BerthDays;
    }

    private List<TDisShipvoyageItemDTO> outVoyageLevePortTime(TDisShipvoyageItemPO shipvoyageItem) {
        List<TDisShipvoyageItemDTO> result = new ArrayList<>();
        List<TDisShipvoyageItemDTO> lastPortDynamicList = tCostShipMapper.getLastPort(shipvoyageItem);
        if(CollectionUtils.isEmpty(lastPortDynamicList)){
            return lastPortDynamicList;
        }
        if(InOutPortEnum.IN.getCode().equals(shipvoyageItem.getImpExp())){
            result = lastPortDynamicList.subList(1,lastPortDynamicList.size());
        }
        if(InOutPortEnum.OUT.getCode().equals(shipvoyageItem.getImpExp())){
            if(CollectionUtils.isNotEmpty(lastPortDynamicList)){
                if(!"50".equals(lastPortDynamicList.get(0).getDynamicTypeCode())){
                    throw new BusinessRuntimeException("完工之后未先操作离泊");
                }
                result = lastPortDynamicList.subList(0,lastPortDynamicList.size()-1);
            }
        }

        return result;
    }
    /**
     * 特殊停泊费
     * @param query
     * @param parameter
     * @return
     */

    @Override
    public List<TDisShipvoyageItemDTO> getSpecialDynamicList(TDisShipvoyageItemQueryDTO query, PageParameter parameter) {
        if(query==null){
            throw new BusinessRuntimeException("请先选中一条数据");
        }
        if(query.getShipvoyageItemId()==null){
            throw new BusinessRuntimeException("没有船名航次子ID");
        }
        List<TDisShipvoyageItemDTO> result= new ArrayList<>();
        TDisShipvoyagePO shipvoyageByItemId = tCostShipMapper.getShipvoyageByItemId(query.getShipvoyageItemId());
        TDisShipvoyageItemDTO itemDTO = new TDisShipvoyageItemDTO();
        itemDTO.setNetWeight(shipvoyageByItemId.getNumber2()==null ? BigDecimal.ONE :BigDecimal.valueOf(shipvoyageByItemId.getNumber2()));
        itemDTO.setRateItemCode("内贸".equals(shipvoyageByItemId.getTradeType())?"MS00260":("外贸".equals(shipvoyageByItemId.getTradeType())?"MS00261":""));
        List<TDisShipvoyageItemDTO> specialDynamic = tCostShipMapper.getSpecialDynamicByShipvoyageItemId(query.getShipvoyageItemId());
        if(!specialDynamic.isEmpty()){
            long allBerthTime = 0;
            for (TDisShipvoyageItemDTO tDisShipvoyageItemDTO : specialDynamic) {
                LocalDateTime startDynamicDate = tDisShipvoyageItemDTO.getDynamicStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime endDynamicDate = tDisShipvoyageItemDTO.getDynamicEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                allBerthTime += Duration.between(startDynamicDate, endDynamicDate).toMillis();
            }
            if("内贸".equals(shipvoyageByItemId.getTradeType())){
                //内贸用天
                itemDTO.setBerthDays(
                        new BigDecimal(allBerthTime) .divide(new BigDecimal(24*60 * 60 * 1000),0,BigDecimal.ROUND_UP)
                );
                itemDTO.setBerthHours(new BigDecimal(allBerthTime) .divide(new BigDecimal(60 * 60 * 1000),5,BigDecimal.ROUND_HALF_UP));
            }
            if("外贸".equals(shipvoyageByItemId.getTradeType())){
                //外贸单位小时
                itemDTO.setBerthHours(
                        SpringUtils.getBean(
                                this.getClass()).fiveNear(
                                        (new BigDecimal(allBerthTime) .divide(new BigDecimal(60 * 60 * 1000),5,BigDecimal.ROUND_HALF_UP)).doubleValue()
                        )
                );
                //显示时间用
                itemDTO.setBerthDays(
                        SpringUtils.getBean(
                                this.getClass()).fiveNear(
                                        (new BigDecimal(allBerthTime) .divide(new BigDecimal(60 * 60 * 1000),5,BigDecimal.ROUND_HALF_UP)).doubleValue()
                        )
                );

            }
        }
        result.add(itemDTO);
        return result;
    }


    public BigDecimal fiveNear(double source){
        BigDecimal bigDecimalResult = BigDecimal.ZERO;
        BigDecimal bigDecimal = BigDecimal.valueOf(source).setScale(2, BigDecimal.ROUND_HALF_UP);
        String s = bigDecimal.toString();
        String[] split = s.split("\\.");
        if(Integer.parseInt(split[1])!=0&&Integer.parseInt(split[1])<50){
            split[1] = "50";
            String stringResult = split[0]+"."+split[1];
            bigDecimalResult = BigDecimal.valueOf(Double.parseDouble(stringResult));
        }else {
            bigDecimalResult = BigDecimal.valueOf(Math.ceil(source));
//            bigDecimalResult = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
        }
        return bigDecimalResult;
    }


}
