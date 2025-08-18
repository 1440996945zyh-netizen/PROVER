package com.yy.ppm.business.service;


import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.business.bean.dto.trust.TrustDTO;
import com.yy.ppm.master.bean.po.MTrustTypePO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业指令表(TBusTrust)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月05日 09:21:00
 */
public interface TBusTrustService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TBusTrustDTO> getList(TBusTrustSearchDTO searchDTO);
    public Map<String,Object> isTrust(Long shipVoyageId);
    public Pages<TBusTrustDTO> getStorageYardList(TBusTrustSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TBusTrustDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tBusTrustDTO
     * @return 是否成功
     */
    public void add(TBusTrustDTO tBusTrustDTO);

    /**
     * 保存
     *
     * @param tBusTrustDTO
     * @return 是否成功
     */
    public void update(TBusTrustDTO tBusTrustDTO);

    /**
     * 驳回
     *
     * @param tBusTrustDTO
     * @return 是否成功
     */
    public void reject(TBusTrustDTO tBusTrustDTO);

     /**
      * 发布
      *
      * @param id
      * @return 是否成功
      */
     public boolean doRelease(Long id);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

     Pages<TBusCargoInfoDTO> getTicketInfo(Long companyId, String tradeType, Long cargoAgentId,String cargoOwnerId, PageParameter pageParameter, String isLuxiao, String isShugang,String scn,String shipvoyageItemId, String cargoInfoNo, String businessNo,String trustType);

    Pages<TBusCargoInfoDTO> getOrderCargoName(String billNo,String shipvoyageItemId, String cargoInfoNo,PageParameter pageParameter);

    void cancelRelease(Long trustId,String type);

    void updateAfterRelease(TrustDTO dto);

    /**
     * 更新委托人
     * @param dto
     */
    void updateConsigner(TrustDTO dto);

    List<Map<String, Object>> listContract(Long cargoOwnerId, String cargoCode, String tradeType);

    Map<String, Object> getPreferentialRate(Long contractId, String contractName,String cargoCode);

    List<MTrustTypePO> listTrustType();

    List<Map<String, Object>> listShipvoyageItemFile(Long id);

    Boolean kcjhCancelAudit(Long id);

    TrustFeeExportDTO exportFeeEvent(Long id);

    boolean isStopStatus(TBusTrustCargoDTO tBusTrustCargoDTO);

    TBusTrustDTO getTrustCargoById(Long id);

    TBusTrustDTO getDetailAdd(Long id);

    Pages<TrustStopLogRes> getStopLogList(Long trustCargoId, Long cargoInfoId, Long trustId);
}

