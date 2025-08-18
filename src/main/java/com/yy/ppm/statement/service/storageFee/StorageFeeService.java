package com.yy.ppm.statement.service.storageFee;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.storageFee.*;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StorageFeeService {

    /**
     * 票货列表
     *
     * @param parameter
     * @param query
     * @return
     */
    Pages<TBusCargoInfoDTO> listCargoInfo(PageParameter parameter, TBusCargoInfoQueryDTO query);

    /**
     * 待结算明细列表
     *
     * @param cargoInfoId
     * @param isCalculate
     * @param isFinal
     * @param endDate
     * @param isXC
     * @return
     */
    List<TCostStorageSettleDetailDTO> listDetail(Long cargoInfoId, String isCalculate, String isFinal, LocalDate endDate, String isXC,String isUseReduce,String reduceType);

    /**
     * 合同列表
     *
     * @param cargoInfoId
     * @param date
     * @return
     */
    List<Map<String, Object>> listContract(Long cargoInfoId, Date date);

    /**
     * 待结算明细列表（已选合同）
     *
     * @param cargoInfoId
     * @param isCalculate
     * @param isFinal
     * @param endDate
     * @param freeStorageDays
     * @param rate
     * @param tax
     * @param isUseReduce
     * @param isXC
     * @return
     */
    List<TCostStorageSettleDetailDTO> listDetailWithContract(Long cargoInfoId, String isCalculate, String isFinal, LocalDate endDate, Integer freeStorageDays, BigDecimal rate, BigDecimal tax, String isUseReduce, String isXCS,String reduceType);

    /**
     * 结算
     *
     * @param storageSettle
     */
    void settle(TCostStorageSettleDTO storageSettle);

    /**
     * 堆存费结算列表
     *
     * @param cargoInfoId
     * @return
     */
    List<TCostStorageSettleDTO> listStorageSettle(Long cargoInfoId);

    /**
     * 撤销结算
     *
     * @param storageSettleId
     */
    void cancelSettle(Long storageSettleId);

    /**
     * 审核
     *
     * @param storageSettleId
     */
    void review(Long storageSettleId);

    /**
     * 销审
     *
     * @param storageSettleId
     */
    void cancelReview(Long storageSettleId);

    /**
     * 保存商务确认凭证
     *
     * @param dto
     */
    void saveConfirmFile(ConfirmForMiscAndStorageDTO dto);

    /**
     * 商务回执确认
     *
     * @param dto
     */
    void confirm(ConfirmForMiscAndStorageDTO dto);

    /**
     * 取消商务确认
     *
     * @param dto
     */
    void cancelConfirm(ConfirmForMiscAndStorageDTO dto);

    Pages<TBusCargoInfoDTO> ListStatementStackFee(PageParameter parameter, TBusCargoInfoQueryDTO query);

    List<TCostStorageSettleDTO> listStorageSettleById(Long storageSettleId);

    CostBillDtoSheetTemplate printFeeList(TReqStorageStatementExportDTO dto);

    TBusCargoInfoDTO getTaxInvoiceCode(Long id);

    void exportDetail(TStorageCostDetailExportDTO dto, HttpServletResponse response);

    BigDecimal getHandoverlistTon(Long cargoInfoId);

    List<Map<String, Object>> getMixRecordList(Long cargoInfoId);

    byte[] pageExport(TBusCargoInfoQueryDTO query);
}
