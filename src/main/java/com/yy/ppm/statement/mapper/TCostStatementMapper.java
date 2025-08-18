package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementDetailDTO;
import com.yy.ppm.statement.bean.dto.bizCostStatement.TCostStatementExportDTO;
import com.yy.ppm.statement.bean.po.TCostStatementPO;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-20 11:24
 */
public interface TCostStatementMapper {


    <T> Page<T> queryAll(TCostStatementDTO tCostStatementDTO);
    <T> Page<T> queryAllDetail(TCostStatementDTO tCostStatementDTO);
    Cursor<TCostStatementExportDTO> getExportList(TCostStatementDTO tCostStatementDTO);

    TCostStatementPO queryAllSum(TCostStatementDTO tCostStatementDTO);
    TCostStatementPO queryAllDetailSum(TCostStatementDTO tCostStatementDTO);

    TCostStatementPO queryById(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateReview(TCostStatementDTO tCostStatementDTO);
    @Edit
    void printMark(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateMarketReview(TCostStatementDTO tCostStatementDTO);

    void updateMarketReviewZero(TCostStatementDTO tCostStatementDTO);

    @Edit
    void financeReview(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateMarketfinanceReview(TCostStatementDTO tCostStatementDTO);

    List<TBusRateDTO> queryRate();

    @Edit
    void insert(TCostStatementDTO tCostStatementDTO);

    @Edit
    void insertItem(TCostStatementDetailDTO dto);

    TCostStatementDTO queryStatement(TCostStatementDTO tCostStatementDTO);
    TCostStatementDTO queryTugWorkTime(TCostStatementDTO tCostStatementDTO);

    List<TCostStatementDetailDTO> queryItemById(TCostStatementDTO tCostStatementDTO);
    List<TCostStatementDetailDTO> queryItemByIdzk(TCostStatementDTO tCostStatementDTO);

    void deleteDetailById(TCostStatementDTO tCostStatementDTO);

    void deleteById(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateById(TCostStatementDTO tCostStatementDTO);

    List<Map<String, Object>> queryRateItem();

    List<TCostStatementPO> queryById1(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateRecheck(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateMarketRecheck(TCostStatementDTO tCostStatementDTO);
    @Edit
    void updateApplyInvoice(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateMarketApplyInvoice(TCostStatementDTO tCostStatementDTO);

    List<Map<String, Object>> queryWaterFiles(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateStatementItem(TCostStatementDetailDTO tCostStatementDetailDTO);

    @Edit
    void updateStatementItemD(TCostStatementDetailDTO tCostStatementDetailDTO);

    @Edit
    void updateHandoverlistByStatementId(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateStatementById(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateStatementItemById(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateCostShipByShipvoyageId(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateMiscBillingById(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateTugByStatementId(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateWorkLoadByStatementId(TCostStatementDTO tCostStatementDTO);

    @Edit
    void updateStorageSettleByStatementId(TCostStatementDTO tCostStatementDTO);
}
