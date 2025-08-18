package com.yy.ppm.common.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.bean.dto.SelectResultDTO;
import com.yy.ppm.common.bean.dto.TDisPoundInfoDTO;
import com.yy.ppm.dispatch.bean.dto.TDisCostInfoPO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisLowerCabinPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageGbCargoInfoDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageQueryDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ShipInfoMapper {



    TDisShipvoyagePO getDisShipVoyage(Long id);

    List<TDisShipDynamicDTO> getByShipvoyageId(Long shipvoyageId);

    List<TDisShipDynamicDTO> getTFByShipvoyageId(Long shipvoyageId,String impExp);

    List<TDisShipvoyageDTO> listDisShipVoyage(TDisShipvoyageQueryDTO query);

    List<TDisLowerCabinPO> getgetShipDoorInfo(TDisLowerCabinPO tDisLowerCabinPO);
    List<TPrdPortStorageGbCargoInfoDTO> listPortStorageGbCargoInfo(TPrdPortStorageQueryDTO query);

    List<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query);

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

    List<TDisCostInfoPO> getCostInfo(Long shipVoyageId);

    List<String> getCargoInfoNoByShipVoyageId(Long shipVoyageId);

    List<TDisPoundInfoDTO> getPoundInfo(@Param("list") List<String> cargoInfoNo);
}

