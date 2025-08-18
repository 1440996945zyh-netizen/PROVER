package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface BusCustomerEntrustMapper {
    Page<TBusCustomerEntrustDTO> getList(TBusCustomerEntrustReqDTO query);
    //TBusCustomerEntrustDTO getCustomerEntrust(TBusCustomerEntrustReqDTO query);

    List<TBusEntrustDetailDTO> getEntrustDetailList(TBusEntrustDetailReqDTO query);
    @Edit
    void insertEntrust(TBusCustomerEntrustDTO dto);
    @Edit
    void insertEntrustDetail(List<TBusEntrustDetailDTO> cargoList);
    @Edit
    void updateEntrustDetail(@Param("updateDetailList") List<TBusEntrustDetailDTO> updateDetailList);
    @Edit
    void delEntrustDetailByIds(@Param("entrustId") Long id, @Param("detailIds") List<Long> collect);
    @Edit
    void updateEntrust(TBusCustomerEntrustDTO dto);

    List<TBusTrustDTO> checkCargoTrust(@Param("cargoInfoIds") List<Long> collect);

    void delCustomerEntrustById(@Param("id") Long entrustId);

    TBusCustomerEntrustDTO getCustomerEntrustForAddTrust(TBusCustomerEntrustReqDTO tBusCustomerEntrustReqDTO);

    List<TBusEntrustDetailDTO> getEntrustDetailListForTrustAdd(TBusEntrustDetailReqDTO tBusEntrustDetailReqDTO);
}
