package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.portStorage.*;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:41
 */
public interface TPrdPortStorageService {

    Pages<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query, PageParameter parameter);

    Pages<TPrdPortStorageGbCargoInfoDTO> listPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query, PageParameter parameter);

    Pages<TPrdPortStorageGbCargoOwnerDTO> listPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query, PageParameter parameter);

    Pages<TPrdPortStorageGbCargoDTO> listPortStorageGbCargo(TPrdPortStorageQueryDTO query, PageParameter parameter);

    Map<String, Object> summaryQuantityTon(TPrdPortStorageQueryDTO query);

    void insertPortStorage(List<TPrdPortStorageDetailPO> portStorageDetails);

    void cleanPortStorage(CleanPortStorageDTO cleanPortStorage);

    void cancelCleanPortStorage(CancelCleanPortStorageDTO cancelCleanPortStorage);

    Map<String, Object> getInoutDetail(InoutDetailQueryDTO query);

    byte[] exportPortStorage(TPrdPortStorageQueryDTO query);

    byte[] exportPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query);

    byte[] exportPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query);

    byte[] exportPortStorageGbCargo(TPrdPortStorageQueryDTO query);

    Map<String, Object> getCargoInoutDetail(InoutDetailQueryDTO query);

    byte[] stackSigns(List<StackSignReq> reqList, HttpServletResponse response);
}
