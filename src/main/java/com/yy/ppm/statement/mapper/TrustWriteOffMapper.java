package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.dto.TBusTrustSearchDTO;
import com.yy.ppm.business.bean.po.TBusRatePO;
import com.yy.ppm.business.bean.po.TBusTrustPO;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.po.TMiscBillingPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-11 13:50
 */
public interface TrustWriteOffMapper {

    public Page<TBusTrustDTO> getWriteOffList(TBusTrustSearchDTO tBusTrustSearchVo);

    List<Map<String, Object>> listShipvoyageItemByTrustIds(@Param("trustIds") List<Long> trustIds);

    List<Map<String, Object>> getWriteOffById(Long trustId);

    List<Map<String, Object>> listWaterElectricity(Long trustId);

    TBusTrustPO getTrust(Long trustId);

    @Edit
    void writeOff(TBusTrustPO tBusTrustPO);

    @Edit
    void cancelWriteOff(TBusTrustPO tBusTrustPO);

    List<TBusRatePO> listRate(String processCode);

    @Edit
    int insertMiscBilling(TMiscBillingPO miscBilling);

    List<TMiscBillingDTO> getBillingListByTrustId(@Param("trustId")Long trustId);

    List<TBusCargoInfoDTO> getCargoByTrust(@Param("trustId")Long trustId);

    List<Map<String, Object>> listCargoWater(Long trustId);
}
