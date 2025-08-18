package com.yy.ppm.statement.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkPlanQuery;
import com.yy.ppm.statement.bean.dto.ConfirmForMiscAndStorageDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;

import java.util.List;
import java.util.Map;

public interface TCostStatementService {
    Pages<TCostStatementPO> queryAll(TCostStatementDTO tCostStatementDTO, PageParameter parameter);
    Pages<TCostStatementPO> queryAllDetail(TCostStatementDTO tCostStatementDTO, PageParameter parameter);

    void review(TCostStatementDTO tCostStatementDTO);
    void printMark(TCostStatementDTO tCostStatementDTO);

    void financeReview(TCostStatementDTO tCostStatementDTO);

    List<TBusRateDTO> queryRate();

    void insert(TCostStatementDTO tCostStatementDTO);

    TCostStatementDTO queryById(TCostStatementDTO tCostStatementDTO);
    TCostStatementDTO queryByIdzk(TCostStatementDTO tCostStatementDTO);

    void update(TCostStatementDTO tCostStatementDTO);

    void deleteById(TCostStatementDTO tCostStatementDTO);

    List<Map<String, Object>> queryRateItem();

    void saveConfirmFile(ConfirmForMiscAndStorageDTO dto);

    void recheck(TCostStatementDTO tCostStatementDTO);

    void applyInvoice(TCostStatementDTO tCostStatementDTO);

    List<Map<String, Object>> queryFiles(TCostStatementDTO tCostStatementDTO);

    void updateStatementItem(TCostStatementDetailDTO tCostStatementDetailDTO);
    void updateStatementItemD(TCostStatementDetailDTO tCostStatementDetailDTO);

    void redRush(TCostStatementDTO tCostStatementDTO);

    byte[] exportExcel(TCostStatementDTO query);
}
