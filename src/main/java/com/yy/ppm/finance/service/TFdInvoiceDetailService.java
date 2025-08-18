package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDetailSearchDTO;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 发票子表(TFdInvoiceDetail)Service
 * @Description
 * @createTime 2023年09月15日 20:22:00
 */
public interface TFdInvoiceDetailService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdInvoiceDetailDTO> getList(TFdInvoiceDetailSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdInvoiceDetailDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdInvoiceDetailDTO
     * @return 是否成功
     */
    boolean doSave(TFdInvoiceDetailDTO tFdInvoiceDetailDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    TFdInvoiceDetailDTO calculateAmount(TFdInvoiceDetailDTO tFdInvoiceDetailDTO);
}

