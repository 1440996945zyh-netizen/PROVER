package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.dto.TSdInvoiceRequestDTO;
import com.yy.ppm.finance.bean.dto.TSdInvoiceRequestSearchDTO;
import com.yy.ppm.finance.bean.dto.TSdRedInvoiceApplyDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceRequest)Mapper
 * @Description
 * @createTime 2024年11月08日 10:42:00
 */
@Repository
public interface TSdInvoiceRequestMapper {

    /**
     * 获取列表
     *
     * @param tSdInvoiceRequestSearchVo
     * @return
     */
    Page<TSdInvoiceRequestDTO> getList(TSdInvoiceRequestSearchDTO tSdInvoiceRequestSearchVo);

    /**
     * 导出列表
     *
     * @param tSdInvoiceRequestSearchDTO
     * @return
     */
    List<TSdInvoiceRequestDTO> exportList(TSdInvoiceRequestSearchDTO tSdInvoiceRequestSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    TSdInvoiceRequestDTO getById(Long id);

    TSdInvoiceRequestDTO getByInvoiceId(Long id);

    TSdInvoiceRequestDTO invoiceByDetailId(Long id);

    List<TSdInvoiceRequestDTO> invoiceByInvoiceCode(@Param("invoiceCode") String invoiceCode);

    List<TSdInvoiceRequestDTO> invoiceByCnDnCode(@Param("cnDnCode") String cnDnCode);

    TSdRedInvoiceApplyDTO redInvoiceApplyByDetailId(Long id);

    List<TSdRedInvoiceApplyDTO> redInvoiceApplyByInvoiceCode(String invoiceCode);

    TSdInvoiceRequestDTO getByaInvoiceId(Long id);

    /**
     * 新增
     *
     * @param tSdInvoiceRequestDTO
     * @return
     */
    @Edit
    int insert(TSdInvoiceRequestDTO tSdInvoiceRequestDTO);

    /**
     * 修改
     *
     * @param tSdInvoiceRequestDTO
     * @return
     */
    @Edit
    int update(TSdInvoiceRequestDTO tSdInvoiceRequestDTO);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

