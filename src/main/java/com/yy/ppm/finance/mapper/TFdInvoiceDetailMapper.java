package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailSearchDTO;
import com.yy.ppm.finance.bean.po.TFdInvoiceDetailPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票子表(TFdInvoiceDetail)Mapper
 * @Description
 * @createTime 2023年09月15日 20:23:00
 */
@Repository
public interface TFdInvoiceDetailMapper {

    /**
     * 获取发票子表列表
     *
     * @param tFdInvoiceDetailSearchVo
     * @return
     */
    Page<TFdInvoiceDetailDTO> getList(TFdInvoiceDetailSearchDTO tFdInvoiceDetailSearchVo);

    /**
     * 导出发票子表列表
     *
     * @param tFdInvoiceDetailSearchDTO
     * @return
     */
    List<TFdInvoiceDetailDTO> exportList(TFdInvoiceDetailSearchDTO tFdInvoiceDetailSearchDTO);

    /**
     * 根据id获取发票子表
     *
     * @param id 主键
     * @return
     */
    TFdInvoiceDetailDTO getById(Long id);

    /**
     * 新增发票子表
     *
     * @param tFdInvoiceDetailDTO
     * @return
     */
    @Edit
    int insert(TFdInvoiceDetailDTO tFdInvoiceDetailDTO);

    /**
     * 修改发票子表
     *
     * @param tFdInvoiceDetailDTO
     * @return
     */
    @Edit
    int update(TFdInvoiceDetailDTO tFdInvoiceDetailDTO);


    /**
     * 根据id删除发票子表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
    @Edit
    int insertBatch(@Param("list") List<TFdInvoiceDetailDTO> statementList);

    int deleteByInvoiceId(Long id);

    List<TFdInvoiceDetailDTO> getListByInvoiceId(@Param("invoiceId") Long invoiceId);

    String getProductCodeByItemCd(@Param("itemCd") String itemCd);

    List<String> getProductCodeByRateId(@Param("id") Long id);

    List<TFdInvoiceDetailDTO> getListByInvoiceIds(@Param("list") List<TFdInvoiceDetailDTO> statementList);
}

