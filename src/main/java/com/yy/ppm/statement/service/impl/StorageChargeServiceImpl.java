package com.yy.ppm.statement.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusContractRateDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.common.enums.*;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.statement.bean.dto.CalculateStorageFeeDTO;
import com.yy.ppm.statement.bean.dto.FStorageFeeHisDTO;
import com.yy.ppm.statement.bean.dto.FStorageFieldDTO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.po.*;
import com.yy.ppm.statement.mapper.StorageChargeMapper;
import com.yy.ppm.statement.service.StorageChargeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 堆存费结算impl
 * @author yangcl*/
@Service
public class StorageChargeServiceImpl implements StorageChargeService {

    @Resource
    StorageChargeMapper storageChargeMapper;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private CommonService commonService;

    /**
     * 查询船舶交接清单
     * */
    @Override
    public Pages<TBusHandoverlistDTO> getHandoverlist(TBusHandoverlistQueryDTO dto,PageParameter parameter) {

        return PageHelperUtils.limit(parameter,()->storageChargeMapper.getHandoverlist(dto));
    }

    /**
     * 根据作业公司及货主等信息查询合同列表
     * */
    @Override
    public FStorageFieldDTO getContractList(Long customerId, Long companyId, Long cargoInfoId) {
        FStorageFieldDTO dto = new FStorageFieldDTO();

        List<TBusContractDTO> contractList = storageChargeMapper.getContractList(customerId,companyId);
        //取费率信息
        if(contractList!=null && !contractList.isEmpty()){
            contractList.forEach(contract->{
                contract.setRateList(storageChargeMapper.getContractRate(contract.getId(), null ,null));
            });
        }

        dto.setContractList(contractList);

        dto.setStorageDetailList(storageChargeMapper.getPrdPortStorage(cargoInfoId,companyId));
        if(dto.getStorageDetailList()==null && dto.getStorageDetailList().isEmpty()){
            throw new BusinessRuntimeException("选择的交接清单未查询到场存信息！！！");
        }

        dto.setHisList(storageChargeMapper.getLastStorageFee(cargoInfoId,null));

        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(dto.getHisList()==null || dto.getHisList().isEmpty()){
            List<TPrdPortStorageDetailPO> detailPOList = storageChargeMapper.getInOutDetail(cargoInfoId, null, null);
            if(CollectionUtils.isNotEmpty(detailPOList)){
                date = sdf.format(detailPOList.get(0).getInoutDate());
            }
        }else{
            Calendar cal = Calendar.getInstance();
            cal.setTime(dto.getHisList().get(0).getEndDate());
            cal.add(Calendar.DAY_OF_MONTH,1);
            date = sdf.format(cal.getTime());
        }

        dto.setLastDate(date);

        return dto;
    }

    /**
     * 计算堆存费
     * */
    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public FStorageFeeHisDTO calculateStorageFees(CalculateStorageFeeDTO dto) throws ParseException {

        List<TStorageFeeDetailPO> dataList = new ArrayList<>();
        //取堆存流水
        List<TPrdPortStorageDetailPO> storageList = storageChargeMapper.getInOutDetail(dto.getCargoInfoId(),dto.getStartDate(), dto.getEndDate());
        //取堆存费率信息
        List<TBusContractRateDTO> rateList = storageChargeMapper.getContractRate(dto.getContractId(), dto.getCargoCode(), RateCodeEnum.STOWAGE.getCode());
        if(CollectionUtils.isEmpty(rateList)){
            throw  new BusinessRuntimeException("未查询到堆存费率");
        }

        List<TStorageFeeHisPO> hisList = storageChargeMapper.getLastStorageFee(dto.getCargoInfoId(),dto.getHistoryGid());
        TStorageFeeHisPO hisPO = null;
        if(CollectionUtils.isEmpty(storageList) && (hisList==null || hisList.isEmpty())){
            throw  new BusinessRuntimeException("未查询到堆存信息");
        }

        //取上次历史结算信息
        if(hisList!=null && !hisList.isEmpty()){
            hisPO = hisList.get(0);
        }

        //过滤掉场存变化为0的数据
        if(CollectionUtils.isNotEmpty(storageList)){
            storageList = storageList.stream().filter(obj->obj.getTon().compareTo(BigDecimal.ZERO)!=0 || obj.getQuantity().intValue()!=0).collect(Collectors.toList());
        }
        //取免堆存期
        Integer freeDays = storageChargeMapper.getFreeDaysById(dto.getContractId());
        freeDays = freeDays==null?0:freeDays;

        FStorageFeeHisDTO hisDTO = new FStorageFeeHisDTO();
        //临时存放已堆存天数 key:作业指令ID value:已堆存天数
        HashMap<Long,Long> tempDaysMap = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        if(hisPO!=null){
            dataList.add(storageChargeMapper.getLastStorageFeeDetail(hisPO.getId()));
            List<TStorageFeeMiddlePO> middleList = storageChargeMapper.getLastMiddleData(hisPO.getId());

            if(CollectionUtils.isNotEmpty(middleList)){
                middleList.stream().filter(val->val.getType()==2).forEach(val->{
                    tempDaysMap.put(val.getCargoInfoId(),val.getAmount().longValue());
                });
            }
            //判断上次结算与第一条场存流水之间有没有时间差
            if(CollectionUtils.isNotEmpty(storageList)){
                Long times = (storageList.get(0).getInoutDate().getTime() - dataList.get(0).getStorageDate().getTime())/ (24 * 60 * 60 * 1000);
                if(times>0){
                    Integer finalFreeDays2 = freeDays;
                    for(int i=1;i<times;i++){
                        TStorageFeeDetailPO po = new TStorageFeeDetailPO();
                        BeanUtils.copyProperties(dataList.get(dataList.size()-1),po);

                        po.setEntryWeight(BigDecimal.ZERO);
                        po.setAppearanceWeight(BigDecimal.ZERO);

                        cal.setTime(dataList.get(dataList.size()-1).getStorageDate());
                        cal.add(Calendar.DAY_OF_MONTH,1);
                        po.setStorageDate(cal.getTime());

                        if(dataList.get(dataList.size()-1).getFieldStockWeight().compareTo(BigDecimal.ZERO)==0){
                            po.setDays(0);
                        }else{
                            po.setDays(dataList.get(dataList.size()-1).getDays()+1);
                        }

                        tempDaysMap.keySet().stream().forEach(key->{
                            tempDaysMap.put(key, tempDaysMap.get(key)+1);
                            if(tempDaysMap.get(key)> finalFreeDays2){
                                po.setFeeStorage(BigDecimal.ZERO);
                                po.setSettlementVolume(po.getFieldStockWeight());
                            }
                        });

                        dataList.add(po);
                    }
                }
            }
        }

        //计算每天的堆存、进、出、免堆存量
        if(CollectionUtils.isNotEmpty(storageList)){
            for(int i=0;i<storageList.size();i++){
                TPrdPortStorageDetailPO detailPO = storageList.get(i);
                if(!tempDaysMap.containsKey(detailPO.getCargoInfoId())){
                    tempDaysMap.put(detailPO.getCargoInfoId(), 0L);
                }

                if(i>0){
                    Long times = (detailPO.getInoutDate().getTime()-storageList.get(i-1).getInoutDate().getTime())/ (24 * 60 * 60 * 1000);
                    if(times>0){
                        Integer finalFreeDays = freeDays;
                        for(int j=1;j<times;j++){
                            TStorageFeeDetailPO po = new TStorageFeeDetailPO();
                            BeanUtils.copyProperties(dataList.get(dataList.size()-1),po);

                            po.setEntryWeight(BigDecimal.ZERO);
                            po.setAppearanceWeight(BigDecimal.ZERO);

                            cal.setTime(storageList.get(i-1).getInoutDate());
                            cal.add(Calendar.DAY_OF_MONTH,j);
                            po.setStorageDate(cal.getTime());

                            if(dataList.get(dataList.size()-1).getFieldStockWeight().compareTo(BigDecimal.ZERO)==0){
                                po.setDays(0);
                            }else{
                                po.setDays(dataList.get(dataList.size()-1).getDays()+1);
                            }

                            tempDaysMap.keySet().stream().forEach(key->{
                                tempDaysMap.put(key, tempDaysMap.get(key)+1);
                                if(tempDaysMap.get(key)> finalFreeDays){
                                    po.setFeeStorage(BigDecimal.ZERO);
                                    po.setSettlementVolume(po.getFieldStockWeight());
                                }
                            });

                            dataList.add(po);
                        }

                        TStorageFeeDetailPO po = new TStorageFeeDetailPO();
                        po.setDays(dataList.get(dataList.size()-1).getDays()+1);
                        po.setFieldStockWeight(dataList.get(dataList.size()-1).getFieldStockWeight().add(detailPO.getTon()));
                        po.setStorageDate(detailPO.getInoutDate());
                        if(detailPO.getTon().compareTo(BigDecimal.ZERO)>0){

                            po.setEntryWeight(detailPO.getTon());
                            tempDaysMap.keySet().stream().forEach(key->{
                                tempDaysMap.put(key, tempDaysMap.get(key)+1);
                                if(tempDaysMap.get(key)> finalFreeDays){
                                    po.setFeeStorage(BigDecimal.ZERO);
                                    po.setSettlementVolume(po.getFieldStockWeight());
                                }else{
                                    po.setFeeStorage(BigDecimal.ONE);
                                    po.setSettlementVolume(dataList.get(dataList.size()-1).getSettlementVolume());
                                }
                            });
                            dataList.add(po);
                        }else{
                            po.setAppearanceWeight(detailPO.getTon().abs());
                            tempDaysMap.keySet().stream().forEach(key->{
                                tempDaysMap.put(key, tempDaysMap.get(key)+1);
                                if(tempDaysMap.get(key)> finalFreeDays){
                                    po.setFeeStorage(BigDecimal.ZERO);
                                    po.setSettlementVolume(po.getFieldStockWeight());
                                }else{
                                    po.setFeeStorage(BigDecimal.ONE);
                                    po.setSettlementVolume(dataList.get(dataList.size()-1).getSettlementVolume());
                                }
                            });
                            dataList.add(po);
                        }
                    }else if(times<=0){
                        TStorageFeeDetailPO tempDetailPO = dataList.get(dataList.size()-1);
                        tempDetailPO.setFieldStockWeight( tempDetailPO.getFieldStockWeight().add(detailPO.getTon()));
                        if(tempDetailPO.getDays()<=freeDays){
                            tempDetailPO.setFeeStorage(BigDecimal.ONE);
                        }else{
                            tempDetailPO.setSettlementVolume(tempDetailPO.getFieldStockWeight());
                        }
                    }
                }else if(i==0){
                    TStorageFeeDetailPO po = new TStorageFeeDetailPO();
                    po.setStorageDate(detailPO.getInoutDate());
                    if(detailPO.getTon().compareTo(BigDecimal.ZERO)>0){
                        po.setEntryWeight(detailPO.getTon());
                    }else{
                        po.setAppearanceWeight(detailPO.getTon().abs());
                    }
                    po.setDays(1);
                    po.setFieldStockWeight(detailPO.getTon());
                    po.setFeeStorage(BigDecimal.ZERO);
                    po.setSettlementVolume(detailPO.getTon());

                    if(hisPO!=null){
                        po.setDays(dataList.get(dataList.size()-1).getDays()+1);
                        po.setFieldStockWeight(hisPO.getBalancesAmount().add(detailPO.getTon()));
                        po.setSettlementVolume(po.getFieldStockWeight());
                    }

                    if(tempDaysMap.containsKey(detailPO.getCargoInfoId()) && tempDaysMap.get(detailPO.getCargoInfoId())<=freeDays){
                        tempDaysMap.put(detailPO.getCargoInfoId(), tempDaysMap.get(detailPO.getCargoInfoId())+1);
                        po.setFeeStorage(BigDecimal.ONE);
                        po.setSettlementVolume(BigDecimal.ZERO);
                    }else if(!tempDaysMap.containsKey(detailPO.getCargoInfoId()) && freeDays>0 ){
                        tempDaysMap.put(detailPO.getCargoInfoId(), 1L);
                        po.setFeeStorage(BigDecimal.ONE);
                        po.setSettlementVolume(BigDecimal.ZERO);
                    }

                    dataList.add(po);
                }
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //计算最后一条场存到结算结束日期的差并填充数据
        Long times = (sdf.parse(dto.getEndDate()).getTime()-dataList.get(dataList.size()-1).getStorageDate().getTime())/ (24 * 60 * 60 * 1000);
        if(dataList.get(dataList.size()-1).getFieldStockWeight().compareTo(BigDecimal.ZERO)>0 && times>0){
            Integer finalFreeDays1 = freeDays;
            for(int i=1;i<=times;i++){
                TStorageFeeDetailPO po = new TStorageFeeDetailPO();
                BeanUtils.copyProperties(dataList.get(dataList.size()-1),po);

                po.setEntryWeight(BigDecimal.ZERO);
                po.setAppearanceWeight(BigDecimal.ZERO);

                cal.setTime(dataList.get(dataList.size()-1).getStorageDate());
                cal.add(Calendar.DAY_OF_MONTH,1);
                po.setStorageDate(cal.getTime());

                if(dataList.get(dataList.size()-1).getFieldStockWeight().compareTo(BigDecimal.ZERO)==0){
                    po.setDays(0);
                }else{
                    po.setDays(dataList.get(dataList.size()-1).getDays()+1);
                }

                tempDaysMap.keySet().stream().forEach(key->{
                    tempDaysMap.put(key, tempDaysMap.get(key)+1);
                    if(tempDaysMap.get(key)> finalFreeDays1){
                        po.setFeeStorage(BigDecimal.ZERO);
                        po.setSettlementVolume(po.getFieldStockWeight());
                    }
                });

                dataList.add(po);
            }
        }

        final BigDecimal[] amount = {BigDecimal.ZERO};
        final BigDecimal[] out = {BigDecimal.ZERO};
        final BigDecimal[] in = {BigDecimal.ZERO};
        if(hisPO!=null && CollectionUtils.isNotEmpty(dataList)){
            dataList.remove(0);
        }
        if(CollectionUtils.isNotEmpty(dataList)){
            dataList.forEach(data->{
                if(data.getDays().intValue()==0){
                    return;
                }

                TBusContractRateDTO tempDto = rateList.get(0);
                data.setRate(tempDto.getRate());
                data.setAmount(data.getSettlementVolume().multiply(tempDto.getRate()).setScale(2, RoundingMode.HALF_UP));

                amount[0] = amount[0].add(data.getAmount()==null?BigDecimal.ZERO:data.getAmount());
                out[0] = out[0].add(data.getAppearanceWeight()==null?BigDecimal.ZERO:data.getAppearanceWeight());
                in[0] = in[0].add(data.getEntryWeight()==null?BigDecimal.ZERO:data.getEntryWeight());
            });
        }

        hisDTO.setDetailList(dataList);
        hisDTO.setTempDaysMap(tempDaysMap);
        hisDTO.setAmount(amount[0]);
        hisDTO.setRate(rateList.get(0).getRate());
        hisDTO.setRateId(rateList.get(0).getRateId());
        hisDTO.setTaxRate(rateList.get(0).getTax());
        hisDTO.setTaxCast(amount[0].multiply(rateList.get(0).getTax()));
        hisDTO.setAfterTax(hisDTO.getAmount().subtract(hisDTO.getTaxCast()));
        hisDTO.setStartDate(sdf.parse(dto.getStartDate()));
        hisDTO.setEndDate(sdf.parse(dto.getEndDate()));
        hisDTO.setContractId(dto.getContractId());
        hisDTO.setCargoInfoId(dto.getCargoInfoId());
        hisDTO.setBalancesAmount(dataList.get(dataList.size()-1).getFieldStockWeight());
        hisDTO.setTotalEntry(in[0]);
        hisDTO.setTotalAppearance(out[0]);

        return hisDTO;
    }

    /**
     * 保存堆存费
     * */
    @Override
    public void saveStorageFeesData(FStorageFeeHisDTO dto) {
        // 修改票货结算状态
        TBusCargoInfoPO cargoInfo = storageChargeMapper.getCargoInfoById(dto.getCargoInfoId());
        if (HandoverlistStatusEnum._30.getCode().equals(cargoInfo.getStatementStatusCode())) {
            throw new BusinessRuntimeException("结算失败，当前票货已最终结算");
        }
        if (IsFinalEnum.TRUE.getCode().equals(dto.getIsFinal())) {
            cargoInfo.setStatementStatusCode(HandoverlistStatusEnum._30.getCode());
            cargoInfo.setStatementStatusName(HandoverlistStatusEnum._30.getName());
            storageChargeMapper.updateCargoInfo(cargoInfo);
        } else {
            if (HandoverlistStatusEnum._10.getCode().equals(cargoInfo.getStatementStatusCode())) {
                cargoInfo.setStatementStatusCode(HandoverlistStatusEnum._20.getCode());
                cargoInfo.setStatementStatusName(HandoverlistStatusEnum._20.getName());
                storageChargeMapper.updateCargoInfo(cargoInfo);
            }
        }

        if(dto.getId()==null){
            dto.setId(snowflake.nextId());
            Integer times = Math.toIntExact((dto.getEndDate().getTime() - dto.getStartDate().getTime()) / (24 * 60 * 60 * 1000));
            dto.setSettlementDays(times+1);
            storageChargeMapper.insertStorageHis(dto);
        }else{
            storageChargeMapper.updateHisInfo(dto);
            storageChargeMapper.deleteMiddleData(dto.getId());
            storageChargeMapper.deleteHistoryDetailByGid(dto.getId());
        }

        if(CollectionUtils.isNotEmpty(dto.getDetailList())){
            dto.getDetailList().forEach(val->{
                val.setId(snowflake.nextId());
                val.setHistoryId(dto.getId());
            });
            storageChargeMapper.insertStorageFeeDetail(dto.getDetailList());
        }

        List<TStorageFeeMiddlePO> middleList = new ArrayList<>();
        if(dto.getInstructMap()!=null && !dto.getInstructMap().isEmpty()){
            dto.getInstructMap().keySet().forEach(val->{
                TStorageFeeMiddlePO po = new TStorageFeeMiddlePO();
                po.setId(snowflake.nextId());
                po.setHisGid(dto.getId());
                po.setCargoInfoId(val);
                po.setType(1);
                po.setAmount(dto.getInstructMap().get(val));

                middleList.add(po);
            });
        }

        if(dto.getTempDaysMap()!=null && !dto.getTempDaysMap().isEmpty()){
            dto.getTempDaysMap().keySet().forEach(val->{
                TStorageFeeMiddlePO po = new TStorageFeeMiddlePO();
                po.setId(snowflake.nextId());
                po.setHisGid(dto.getId());
                po.setCargoInfoId(val);
                po.setType(2);
                po.setAmount(new BigDecimal(dto.getTempDaysMap().get(val)));

                middleList.add(po);
            });
        }

        if(dto.getPassDaysMap()!=null && !dto.getPassDaysMap().isEmpty()){
            dto.getPassDaysMap().keySet().forEach(val->{
                TStorageFeeMiddlePO po = new TStorageFeeMiddlePO();
                po.setId(snowflake.nextId());
                po.setHisGid(dto.getId());
                po.setCargoInfoId(val);
                po.setType(3);
                po.setAmount(new BigDecimal(dto.getPassDaysMap().get(val)));

                middleList.add(po);
            });
        }

        if(CollectionUtils.isNotEmpty(middleList)){
            storageChargeMapper.insertStorageMiddle(middleList);
        }
    }

    /**
     * 根据历史结算Gid获取历史结算信息
     * */
    @Override
    public FStorageFeeHisDTO getHistoryByGid(Long historyGid) {
        FStorageFeeHisDTO dto = storageChargeMapper.getHistoryByGid(historyGid);

        List<TStorageFeeDetailPO> details = storageChargeMapper.getHistoryDetailByHisgid(historyGid);

        dto.setDetailList(details);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void deleteHistoryByGid(Long historyGid, Long cargoInfoId) {
        Long gid = storageChargeMapper.getLastHisGidByTicket(cargoInfoId);
        if (gid.longValue() != historyGid.longValue()) {
            throw new BusinessRuntimeException("只能选择删除最后一次结算信息！");
        }

        Integer count = storageChargeMapper.deleteHistoryByGid(historyGid);
        if(count>0){
            storageChargeMapper.deleteHistoryDetailByGid(historyGid);

            storageChargeMapper.deleteMiddleData(historyGid);
        }

        // 修改票货结算状态
        HandoverlistStatusEnum status;
        List<TStorageFeeHisPO> his = storageChargeMapper.getLastStorageFee(cargoInfoId, null);
        boolean anyMatch = his.stream().anyMatch(v1 -> IsFinalEnum.TRUE.getCode().equals(v1.getIsFinal()));
        if (anyMatch) {
            status = HandoverlistStatusEnum._30;
        } else {
            if (!his.isEmpty()) {
                status = HandoverlistStatusEnum._20;
            } else {
                status = HandoverlistStatusEnum._10;
            }
        }
        TBusCargoInfoPO cargoInfo = new TBusCargoInfoPO();
        cargoInfo.setId(cargoInfoId);
        cargoInfo.setStatementStatusCode(status.getCode());
        cargoInfo.setStatementStatusName(status.getName());
        storageChargeMapper.updateCargoInfo(cargoInfo);
    }

    /**
     * 生成结算单
     * */
    @Override
    @Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
    public void generateStatement(Long historyGid, Long cargoInfoId) {
        FStorageFeeHisDTO dto = storageChargeMapper.getHistoryByGid(historyGid);

        if(dto==null){
            throw new BusinessRuntimeException("生成失败，堆存费已不存在");
        }

        if(dto.getStatus()!=0){
            throw new BusinessRuntimeException("生成失败，堆存费结算单已生成");
        }
        //更新为已生成结算单
        storageChargeMapper.updateHisStatus(historyGid,1);

        TBusCargoInfoPO cargoInfoPO = storageChargeMapper.getCargoInfoById(cargoInfoId);

        //拼凑结算单主表数据
        TCostStatementPO costStatementPO = new TCostStatementPO();
        costStatementPO.setCompanyId(cargoInfoPO.getCompanyId());
        costStatementPO.setCompanyName(cargoInfoPO.getCompanyName());
        costStatementPO.setId(snowflake.nextId());
        costStatementPO.setStatementNo(commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.STATEMENT_NO, null));
        costStatementPO.setCustomerId(cargoInfoPO.getCargoOwnerId());
        costStatementPO.setCustomerName(cargoInfoPO.getCargoOwnerName());
        costStatementPO.setType(HandoverlistTypeEnum._50.getCode());
        costStatementPO.setShipvoyageId(cargoInfoPO.getShipvoyageId());
        costStatementPO.setShipvoyageItemId(cargoInfoPO.getShipvoyageItemId());
        costStatementPO.setSettlementDate(dto.getCreateTime());
        costStatementPO.setStatus(StatementStatusEnum._30.getCode());
        costStatementPO.setIsFinal("1");
        storageChargeMapper.insertCostStatement(costStatementPO);

        //拼凑结算单子主表数据
        TCostStatementDetailPO detailPO = new TCostStatementDetailPO();
        detailPO.setId(snowflake.nextId());
        detailPO.setStatement(costStatementPO.getId());
        detailPO.setRate(dto.getRate());
        detailPO.setRateId(dto.getRateId());
        detailPO.setRateItemCode(RateCodeEnum.STOWAGE.getCode());
        detailPO.setRateItemName(RateCodeEnum.STOWAGE.getLabel());
        detailPO.setNumber(dto.getBalancesAmount());
        detailPO.setAmount(dto.getAmount());
        detailPO.setTax(dto.getTaxRate());
        detailPO.setTaxAmount(dto.getTaxCast());
        detailPO.setBusinessId(historyGid);

        storageChargeMapper.insertCostStatementDetail(detailPO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void cancelStatement(Long historyGid) {
        FStorageFeeHisDTO dto = storageChargeMapper.getHistoryByGid(historyGid);
        if (dto == null) {
            throw new BusinessRuntimeException("取消失败，堆存费已不存在");
        }
        TCostStatementDetailPO detail = storageChargeMapper.getCostStatementDetail(historyGid);
        if (detail == null) {
            throw new BusinessRuntimeException("取消失败，堆存费未审核");
        }

        TCostStatementPO costStatement = storageChargeMapper.getCostStatement(detail.getStatement());
        if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
            throw new BusinessRuntimeException("取消失败，结算单已商务确认");
        }

        storageChargeMapper.updateHisStatus(historyGid, 0);
        storageChargeMapper.deleteCostStatement(detail.getStatement());
        storageChargeMapper.deleteCostStatementDetail(historyGid);
    }

    @Override
    public void confirm(Long historyGid) {
        TCostStatementDetailPO detail = storageChargeMapper.getCostStatementDetail(historyGid);
        if (detail == null) {
            throw new BusinessRuntimeException("商务确认失败，请先审核");
        }

        TCostStatementPO costStatement = storageChargeMapper.getCostStatement(detail.getStatement());
        if (!StatementStatusEnum._30.getCode().equals(costStatement.getStatus())) {
            throw new BusinessRuntimeException("商务确认失败，状态非计费审核");
        }

        costStatement.setStatus(StatementStatusEnum._31.getCode());
        storageChargeMapper.updateCostStatement(costStatement);
    }

    @Override
    public void cancelConfirm(Long historyGid) {
        TCostStatementDetailPO detail = storageChargeMapper.getCostStatementDetail(historyGid);
        if (detail == null) {
            return;
        }

        TCostStatementPO costStatement = storageChargeMapper.getCostStatement(detail.getStatement());
        if (!StatementStatusEnum._31.getCode().equals(costStatement.getStatus())) {
            throw new BusinessRuntimeException("取消商务确认失败，状态非商务确认");
        }

        costStatement.setStatus(StatementStatusEnum._30.getCode());
        storageChargeMapper.updateCostStatement(costStatement);
    }
}
