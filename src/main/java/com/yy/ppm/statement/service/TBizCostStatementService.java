package com.yy.ppm.statement.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.CostBillDtoSheetTemplate;
import com.yy.ppm.statement.bean.dto.TMiscBillingDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TBusContractDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementQueryDTO;

import java.util.Date;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-18 10:37
 */
public interface TBizCostStatementService {

    /**
     * 结算单列表
     *
     * @param query
     * @param parameter
     * @return
     */
    Pages<TCostStatementDTO> listCostStatement(TCostStatementQueryDTO query, PageParameter parameter);

    List<TCostStatementDetailDTO> listCostStatementDetail(Long statementId);

    List<TBusContractDTO> listContract(Long statementId, Date time);

    void statement(TCostStatementDTO dto);

    void cancelStatement(Long statementId);

    void review(Long statementId);

    void cancelReview(Long statementId);

    void confirm(TCostStatementDTO dto);
    void reject(TCostStatementDTO dto);

    void cancelConfirm(TCostStatementDTO dto);

    CostBillDtoSheetTemplate exportCostBill(TCostStatementDTO dto);

    void saveFile(TCostStatementDTO dto);

    CostBillDtoSheetTemplate exportCostBillBath(TCostStatementDTO dto);

    boolean getContractFlag(List<Long> statementIds);

    TCostStatementDetailDTO getPreNumberCount(Long statementId);

    List<TBusContractDTO> listContractForLULS(Long statementId, Date time);

    List<TMiscBillingDTO> getMiscFee(Long statementId);

    List<TBusContractDTO> listContractDefault(Long statementId, Date time);

    byte[] pageExport(TCostStatementQueryDTO query);
}
