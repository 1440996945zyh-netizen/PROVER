package com.yy.ppm.statement.mapper.storageAmountCalculate;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.statement.bean.dto.StorageDemoDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TBusCargoInfoDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TBusCargoInfoQueryDTO;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDetailDTO;
import com.yy.ppm.statement.bean.po.TCostStorageAmtCalcRecPO;
import com.yy.ppm.statement.bean.po.TCostStorageSettlePO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StorageAmountCalculateMapper {

    /**
     * 票货列表（游标查询）
     *
     * @param query
     * @return
     */
    Cursor<TBusCargoInfoDTO> cursorListCargoInfo(TBusCargoInfoQueryDTO query);

    /**
     * 票货ID查堆存费结算列表
     *
     * @param cargoInfoIds
     * @return
     */
    List<TCostStorageSettlePO> listStorageSettle(@Param("cargoInfoIds") List<Long> cargoInfoIds);

    /**
     * 票货ID查堆存费结算明细
     *
     * @param cargoInfoIds
     * @return
     */
    List<TCostStorageSettleDetailDTO> listStorageSettleDetail(@Param("cargoInfoIds") List<Long> cargoInfoIds);

    /**
     * 合同列表
     *
     * @param cargoInfoIds
     * @param date
     * @return
     */
    List<Map<String, Object>> listContract(@Param("cargoInfoIds") List<Long> cargoInfoIds, @Param("date") Date date);

    /**
     * 新增堆存费金额计算记录
     *
     * @param storageAmtCalcRecs
     * @return
     */
    @Edit
    int insertStorageAmountCalculateRecord(@Param("storageAmtCalcRecs") List<TCostStorageAmtCalcRecPO> storageAmtCalcRecs);
    int insertDemo(@Param("storageAmtCalcRecs") List<StorageDemoDTO> storageAmtCalcRecs);

    void delByTimeNow(@Param("twoDayAgo") String toString);


    List<Long> getContractByCus(Long id);

    List<Long> getContractWithCompany(@Param("contractIds") List<Long> contractIds, @Param("cargoInfoId") Long id);

    List<Long> getContractWithNotInTime(Long id);

    List<Long> getContractWithRate(@Param("contractIds") List<Long> contractIds, @Param("cargoInfoId") Long id);

    Page<StorageDemoDTO> getListDemo(StorageDemoDTO dto);

    void delByTimeNowDemo(String string);
}
