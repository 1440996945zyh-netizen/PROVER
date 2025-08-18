package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceSearchDTO;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票表(TFdInvoice)Service
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
public interface TFdInvoiceService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdInvoiceDTO> getList(TFdInvoiceSearchDTO searchDTO);

    String invoiceDownload(TFdInvoiceSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdInvoiceDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdInvoiceDTO
     * @return 是否成功
     */
    boolean doSave(TFdInvoiceDTO tFdInvoiceDTO);

    /**
     * 更新逻辑 别使
     * @param tFdInvoiceDTO
     * @return
     */
    boolean updateData(TFdInvoiceDTO tFdInvoiceDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean voidInvoice(Long id);

    Pages<TFdInvoiceDetailDTO> getStatementList(TFdInvoiceSearchDTO searchDTO);


    boolean updateInvoiceCode(TFdInvoiceDTO searchDTO);

    TFdInvoiceDTO getInvoice(Long id);

    TFdInvoiceDTO getCountAmount(TFdInvoiceSearchDTO searchDTO);
}

