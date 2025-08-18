package com.yy.ppm.dispatch.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailSearchDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import cn.hutool.core.date.DateTime;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 11:18
 */
public interface TDisShipVoyageMapper {

    @Edit
    int insertDisShipVoyage(TDisShipvoyageDTO disShipvoyage);

    @Edit
    int insertDisShipVoyageItem(TDisShipvoyageItemPO disShipvoyageItem);

    @Edit
    int updateDisShipVoyageItem(TDisShipvoyageItemPO disShipvoyageItem);

    List<TDisShipvoyageItemPO> getListByCustomerId(Long customerId);

    Map<String,Object> getPaySumByVoyageId(@Param("voyageId") Long voyageId,@Param("customerId") String customerId);

    Map<String,Object> getBankCustomerPrepayment(@Param("customerId") String customerId,@Param("companyId") Long companyId);

    List<TDisShipvoyageItemPO> getDisShipVoyageItemById(Long id);
    List<TDisShipvoyageItemPO> getShipVoyageItemByVoyageId(Long shipvoyageId);
    Map<String,String> getShipVoyageById(@Param("shipVoyageId") Long shipVoyageId);

    Map<String,Object> getShipInfoByItemId(@Param("shipVoyageItemId") Long shipVoyageItemId);

    List<TBusTrustPO> listTrust(Long id);

    @Edit
    int updateDisShipVoyage(TDisShipvoyageDTO disShipvoyage);

    @Edit
    int updateSjsbStatus(@Param("id") Long id, @Param("sjsbStatus") String sjsbStatus);

    @Edit
    int updateSjsbLogStatus(@Param("id") Long id, @Param("status") String status);

    int updateDisShipVoyageByBHTId(TDisShipvoyageDTO disShipvoyage);

    int deleteDisShipvoyageItem(Long id);

    /**
     * 根据航次和进出口删除
     * @param id
     * @param impExp
     * @return
     */
    int deleteDisShipvoyageItemByCondition(Long id,String impExp);

    int deleteDisShipvoyageItemByBHTId(Long boHaiTongId);

    TDisShipvoyagePO getDisShipVoyage(Long id);

    TDisShipvoyagePO getDisShipVoyageItemId(Long id);

    List<Map<String,Object>> getDisShipVoyageById(Long id);

    TDisShipvoyagePO getDisShipVoyageByBHTId(Long boHaiTongId);

    List<TDisShipvoyagePO> getDisShipVoyageByShipId(Long shipId);

    @Edit
    int deleteDisShipvoyage(Long id);

    @Edit
    int deleteDisShipvoyageByBHTId(Long id);

    Page<TDisShipvoyageDTO> listDisShipVoyage(TDisShipvoyageQueryDTO query);
    Page<TDisShipvoyageItemPO> listDisShipItemVoyage(TDisShipvoyageQueryDTO query);
    List<TDisShipvoyageDTO> exportList(TDisShipvoyageQueryDTO query);

    @Edit
    int voidDisShipvoyage(@Param("id") Long id, @Param("delRemark") String delRemark, @Param("base") BasePO base);
    @Edit
    int rejectionDisShipvoyage(@Param("id") Long id, @Param("rejectionRemark") String rejectionRemark, @Param("base") BasePO base);

    @Edit
    int receiveDisShipvoyage(@Param("id") Long id, @Param("base") BasePO base);

    int receiveDisShipvoyageItem(Long id);

    Long changeAmount(BigDecimal dwt, String tradeType);

    List<Map<String,Object>> getShipVoyageList(TDisCloseSailSearchDTO searchDTO);

    Map<String,Object> getShipSumTon(@Param("startTime")  Date startTime,@Param("endTime") Date endTime,@Param("cargoName") String cargoName,@Param("workType") String workType);

    List<TDisShipDynamicDTO> getAllShipDynamic(@Param("startTime")Date startTime,@Param("endTime")Date endTime,@Param("cargoName")String cargoName);

    List<Map<String,Object>> getAtThePortShip(@Param("time") String time);

    List<Map<String,Object>> getDoorRecord(@Param("startTime")Date startTime,@Param("endTime")Date endTime,@Param("cargoName")String cargoName);

    List<Map<String,Object>> getIncomeInfo(@Param("startTime")DateTime startTime,@Param("endTime")DateTime endTime,@Param("cargoName")String cargoName);

    @DS("energy")
    List<Map<String,Object>> getEnergyInfo(@Param("startTime")String startTime,@Param("endTime")String endTime);

    List<Map<String,Object>> getStdEnergy(@Param("startTime")String startTime,@Param("endTime")String endTime);

    List<Map<String,Object>> getCostInfo(@Param("startTime")DateTime startTime,@Param("endTime")DateTime endTime,@Param("cargoName")String cargoName);

    List<Map<String,String>> getTrustInfo1(@Param("shipvoyageItemId") Long shipvoyageItemId,
                                          @Param("processCd") String processCd,
                                          @Param("workDate") String workDate,
                                          @Param("classCode") String classCode);

    List<Map<String, Object>> getSecCargoCateList();

    List<Map<String, Object>> getWorkTonByTime(@Param("startDate") DateTime startDate,
                                               @Param("endDate")   DateTime endDate);
}