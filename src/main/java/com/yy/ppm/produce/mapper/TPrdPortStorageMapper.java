package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.portStorage.*;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-24 13:43
 */
public interface TPrdPortStorageMapper {

    Page<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query);

    Page<TPrdPortStorageGbCargoInfoDTO> listPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query);

    Page<TPrdPortStorageGbCargoOwnerDTO> listPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query);

    Page<TPrdPortStorageGbCargoDTO> listPortStorageGbCargo(TPrdPortStorageQueryDTO query);

    Map<String, Object> summaryQuantityTon(TPrdPortStorageQueryDTO query);
    Map<String, Object> summaryQuantityTonById(Long cargoInfoId);

    List<TPrdPortStorageDetailPO> listPortStorageDetail(
            @Param("cargoInfoId") Long cargoInfoId,
            @Param("storehouseId") Long storehouseId,
            @Param("regionId") Long regionId,
            @Param("massId") Long massId,
            @Param("beginWorkDate") Date beginWorkDate,
            @Param("beginClassCode") String beginClassCode,
            @Param("endWorkDate") Date endWorkDate,
            @Param("endClassCode") String endClassCode,
            @Param("processDetailCode") String processDetailCode
    );

    @Edit
    int cleanPortStorage(Map<String, Object> param);

    @Edit
    int cleanPortStorageDetail(Map<String, Object> param);

    int cancelCleanPortStorage(Map<String, Object> param);

    int cancelCleanPortStorageDetail(Map<String, Object> param);

    Cursor<TPrdPortStorageDTO> cursorListPortStorage(TPrdPortStorageQueryDTO query);

    Cursor<TPrdPortStorageGbCargoInfoDTO> cursorListPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query);

    Cursor<TPrdPortStorageGbCargoOwnerDTO> cursorListPortStorageGbCargoOwner(TPrdPortStorageQueryDTO query);

    Cursor<TPrdPortStorageGbCargoDTO> cursorListPortStorageGbCargo(TPrdPortStorageQueryDTO query);

    /**
     * 撤销清场
     * @param tPrdPortStorageDetailPO
     */
    @Edit
    void revokeCleanStorageDetail(TPrdPortStorageDetailPO tPrdPortStorageDetailPO);

    TPrdPortStorageDTO getPortStorage(Long id);

    void revokeCleanStorage(TPrdPortStorageDTO prdPortStorageDTO);

    int getIsClearByCargoInfoID(Long cargoInfoId);

    List<TPrdPortStorageDetailPO> getCargoInoutDetail(@Param("cargoInfoId") Long cargoInfoId,
                                                      @Param("beginWorkDate") Date beginWorkDate,
                                                      @Param("inoutType") String inoutType,
                                                      @Param("beginClassCode") String beginClassCode,
                                                      @Param("endWorkDate") Date endWorkDate,
                                                      @Param("endClassCode") String endClassCode,
                                                      @Param("processDetailCode") String processDetailCode);

    List<StackSignReq> getStackSigns(@Param("list") List<StackSignReq> reqList);
}
