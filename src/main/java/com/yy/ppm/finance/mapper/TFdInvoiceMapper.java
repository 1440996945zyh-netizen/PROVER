package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.finance.bean.dto.*;
import com.yy.ppm.statement.bean.dto.costShip.TDisShipvoyageItemDTO;
import com.yy.ppm.statement.bean.po.TCostStatementDetailPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票表(TFdInvoice)Mapper
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
@Repository
public interface TFdInvoiceMapper {

    /**
     * 获取发票表列表
     *
     * @param tFdInvoiceSearchVo
     * @return
     */
    Page<TFdInvoiceDTO> getList(TFdInvoiceSearchDTO tFdInvoiceSearchVo);
    String invoiceDownload(TFdInvoiceSearchDTO tFdInvoiceSearchVo);

    Page<TFdInvoiceDetailDTO> getStatement(TFdInvoiceSearchDTO tFdInvoiceSearchVo);
    //计算金额的时候用来判断输入的数量是否合法专用
    TFdInvoiceDetailDTO getStatementByID(Long id);

    /***
     *  //根据获取的发票的子表获取对应的结算详情
     * @param dtos
     * @return
     */
    Page<TFdInvoiceDetailDTO> getStatementByInvoiceDetailList(@Param("list") List<TFdInvoiceDetailDTO> dtos);
    List<TFdInvoiceDetailDTO> getStatementByParentIds(@Param("list") List<Long> parentIds);

    /**
     * 导出发票表列表
     *
     * @param tFdInvoiceSearchDTO
     * @return
     */
    List<TFdInvoiceDTO> exportList(TFdInvoiceSearchDTO tFdInvoiceSearchDTO);

    /**
     * 根据id获取发票表
     *
     * @param id 主键
     * @return
     */
    TFdInvoiceDTO getById(Long id);

    /**
     * 新增发票表
     *
     * @param tFdInvoiceDTO
     * @return
     */
    @Edit
    int insert(TFdInvoiceDTO tFdInvoiceDTO);

    /**
     * 修改发票表
     *
     * @param tFdInvoiceDTO
     * @return
     */
    @Edit
    int update(TFdInvoiceDTO tFdInvoiceDTO);

    int updateByInvoiceNumber(Map map);

    int updateRedStatusByInvoiceId(@Param("id") Long id,@Param("redStatus") String redStatus,@Param("redStatusLabel")String redStatusLabel);

    /**
     * 根据id删除发票表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);

    /**
     * 根据id作废发票表
     *
     * @param dto
     * @return
     */
    @Edit
    int voidInvoice(TFdInvoiceDTO dto);
    @Edit
    int updateStatementDetailBatch(@Param("list") List<TCostStatementDetailPO> dto);
    @Edit
    int updateStatementStatusBatch(@Param("list") List<StatementStatusUpdateDTO> ids);

    String getInvoiceCode(String startWith);
    @Edit
    int updateInvoiceCode(TFdInvoiceDTO searchDTO);

    TFdInvoiceDTO getInvoiceByCustomerId(Long id);

    List<String> getFeeItemList(@Param("list") List<String> rateItemCodeList);

    List<TFdInvoiceDetailDTO> getStatementIsTingBoByID(@Param("statementId") Long statementId);

    void updateShipVoyageItem(TDisShipvoyageItemDTO disShipvoyageItem);

    void updateShipVoyage(TDisShipvoyageDTO shipvoyageDTO);

    TDisShipvoyageItemDTO getShipVoyageItemByItemId(Long shipvoyageItemId);

    List<TDisShipvoyageItemDTO> getAllItemByOneItemId(Long id);

    List<TFdInvoiceDetailDTO> getStatementListByIds(@Param("ids") List<Long> statementIds);

    List<Map<Object, Object>> getCostStatemmentByInvoiceDto(Long id);
}

