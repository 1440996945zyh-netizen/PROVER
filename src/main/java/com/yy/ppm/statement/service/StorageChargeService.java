package com.yy.ppm.statement.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.CalculateStorageFeeDTO;
import com.yy.ppm.statement.bean.dto.FStorageFeeHisDTO;
import com.yy.ppm.statement.bean.dto.FStorageFieldDTO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;

import java.text.ParseException;

public interface StorageChargeService {

    Pages<TBusHandoverlistDTO> getHandoverlist(TBusHandoverlistQueryDTO dto, PageParameter parameter);

    FStorageFieldDTO getContractList(Long customerId, Long companyId,Long cargoInfoId);

    FStorageFeeHisDTO calculateStorageFees(CalculateStorageFeeDTO dto) throws ParseException;

    void saveStorageFeesData(FStorageFeeHisDTO dto);

    FStorageFeeHisDTO getHistoryByGid(Long historyGid);

    /**
     * 根据历史结算Gid获取历史结算信息
     * @param historyGid
     * @return
     */
    void deleteHistoryByGid(Long historyGid,Long cargoInfoId);

    void generateStatement(Long historyGid,Long cargoInfoId);

    void cancelStatement(Long historyGid);

    void confirm(Long historyGid);

    void cancelConfirm(Long historyGid);
}
