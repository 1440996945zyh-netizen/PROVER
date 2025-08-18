package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.finance.bean.dto.*;
import com.yy.ppm.statement.bean.dto.costShip.TDisShipvoyageItemDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpayment)Mapper
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Repository
public interface TFdDebtorpaymentMapper {

    /**
     * 获取收据主表列表
     *
     * @param tFdDebtorpaymentSearchVo
     * @return
     */
    Page<TFdDebtorpaymentDTO> getList(TFdDebtorpaymentSearchDTO tFdDebtorpaymentSearchVo);
    //自动生成编号的时候用
    List<String> getNo();

    /**
     * 导出收据主表列表
     *
     * @param tFdDebtorpaymentSearchDTO
     * @return
     */
    List<TFdDebtorpaymentDTO> exportList(TFdDebtorpaymentSearchDTO tFdDebtorpaymentSearchDTO);

    /**
     * 根据id获取收据主表
     *
     * @param id 主键
     * @return
     */
    TFdDebtorpaymentDTO getById(Long id);

    /**
     * 新增收据主表
     *
     * @param tFdDebtorpaymentDTO
     * @return
     */
    @Edit
    int insert(TFdDebtorpaymentDTO tFdDebtorpaymentDTO);

    /**
     * 修改收据主表
     *
     * @param tFdDebtorpaymentDTO
     * @return
     */
    @Edit
    int update(TFdDebtorpaymentDTO tFdDebtorpaymentDTO);


    @Edit
    int updateDebtorpayment(TFdDebtorpaymentDTO tFdDebtorpaymentDTO);
    /**
     * 根据id删除收据主表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);

    List<TFdBankCustomerPrepaymentDTO> getPrepayByIds(List<TFdDebtorpaymentDetailDTO> detailOneIds);

    List<TFdDebtorpaymentDetailDTO> getPrePayList(TFdDebtorpaymentSearchDTO searchDTO);
    List<TFdDebtorpaymentDetailDTO> getInvoiceList(TFdDebtorpaymentSearchDTO searchDTO);
    List<TFdDebtorpaymentDetailDTO> getCNDNList(TFdDebtorpaymentSearchDTO searchDTO);
    //批量更新发票表 主要是作废的时候用
    //保存用
    @Edit
    int updateInvoiceBatch(@Param("list") List<TFdDebtorpaymentDetailDTO> dto);
    //作废用
    @Edit
    int updateInvoiceStatusBatch(@Param("list") List<TFdDebtorpaymentDetailDTO> dto);
    //批量更新预缴表 主要是作废的时候用来恢复数据
    //保存用
    @Edit
    void updatePrepayBatch(@Param("list") List<TFdDebtorpaymentDetailDTO> tmpInvoiceList);
    //作废用
    @Edit
    void updatePrepayStatusBatch(@Param("list") List<TFdDebtorpaymentDetailDTO> tmpInvoiceList);


    //保存用
    @Edit
    void updateCNDNBillBatch(List<TFdDebtorpaymentDetailDTO> tmpCNList);
    //作废用
    @Edit
    int updateCNDNBillStatusBatch(@Param("list") List<TFdDebtorpaymentDetailDTO> dto);

    List<TFdInvoiceDetailDTO> getIsTbFeeInvoiceDetailListByIds(@Param("list") List<Long> invoiceIds);

    TDisShipvoyageItemDTO getShipVoyageItemByItemId(@Param("id") Long shipvoyageItemIds);

    void updateShipVoyageItem(TDisShipvoyageItemDTO disShipvoyageItem);

    List<TDisShipvoyageItemDTO> getAllItemByOneItemId(Long id);

    void updateShipVoyage(TDisShipvoyageDTO shipvoyageDTO);

    TFdDebtorpaymentDetailDTO getPrepayById(Long id);

    List<Map<String,Object>> getCargoInfoIdByInvoiceId(@Param("list") List<Long> invoiceIdList, Long cargoInfoId);

    List<Map<String,Object>> getCargoInfoIdBycndnId(@Param("list") List<Long> cndnIdList, Long cargoInfoId);
}

