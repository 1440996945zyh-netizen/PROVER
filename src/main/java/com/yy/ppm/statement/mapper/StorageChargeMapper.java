package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusContractRateDTO;
import com.yy.ppm.business.bean.po.TBusCargoInfoPO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.statement.bean.dto.FStorageFeeHisDTO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.po.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther yangcl
 * @Description
 */
public interface StorageChargeMapper {

    Page<TBusHandoverlistDTO> getHandoverlist(TBusHandoverlistQueryDTO dto);

    List<TBusContractDTO> getContractList(@Param("customerId") Long customerId,@Param("companyId")Long companyId);

    List<TBusContractRateDTO> getContractRate(@Param("contractId")Long contractId, @Param("cargoCode")String cargoCode, @Param("rateItemCode")String rateItemCode);

    List<TPrdPortStorageDTO> getPrdPortStorage(@Param("cargoInfoId") Long cargoInfoId,@Param("companyId")Long companyId);

    List<TStorageFeeHisPO> getLastStorageFee(@Param("cargoInfoId") Long cargoInfoId ,@Param("hisGid")Long hisGid);

    List<TPrdPortStorageDetailPO> getInOutDetail(@Param("cargoInfoId")Long cargoInfoId,@Param("startDate")String startDate,@Param("endDate")String endDate);

    Integer getFreeDaysById(@Param("contractId")Long contractId);

    TStorageFeeDetailPO getLastStorageFeeDetail(Long hisGid);

    List<TStorageFeeMiddlePO> getLastMiddleData(Long hisId);

    int insertStorageHis(TStorageFeeHisPO po);

    int updateHisInfo(TStorageFeeHisPO po);

    /**
     * 根据历史记录GID删除历史计算详情*/
    Integer deleteHistoryDetailByGid(@Param("gid")Long gid);

    /**
     * 根据历史记录GID删除历史计算详情*/
    Integer deleteMiddleData(@Param("gid")Long gid);

    int insertStorageFeeDetail(@Param("detailList") List<TStorageFeeDetailPO> detailList);

    int insertStorageMiddle(@Param("detailList") List<TStorageFeeMiddlePO> detailList);

    /**
     * 根据历史记录GID获取历史*/
    FStorageFeeHisDTO getHistoryByGid(@Param("gid")Long gid);

    /**
     * 根据历史记录GID获取历史计算详情*/
    List<TStorageFeeDetailPO> getHistoryDetailByHisgid(@Param("gid")Long gid);

    /**
     * 获取最后一次计算的历史结算GID*/
    Long getLastHisGidByTicket(@Param("cargoInfoId")Long cargoInfoId);

    /**
     * 根据历史记录GID删除历史计算*/
    Integer deleteHistoryByGid(@Param("gid")Long gid);

    TBusCargoInfoPO getCargoInfoById(@Param("id")Long id);

    @Edit
    int insertCostStatement(TCostStatementPO po);

    @Edit
    int insertCostStatementDetail(TCostStatementDetailPO po);

    int updateHisStatus(@Param("id")Long id,@Param("status")int status);

    TCostStatementDetailPO getCostStatementDetail(Long historyGid);

    int deleteCostStatement(Long id);

    int deleteCostStatementDetail(Long historyGid);

    TCostStatementPO getCostStatement(Long statementId);

    int updateCostStatement(TCostStatementPO costStatement);

    int updateCargoInfo(TBusCargoInfoPO cargoInfo);
}
