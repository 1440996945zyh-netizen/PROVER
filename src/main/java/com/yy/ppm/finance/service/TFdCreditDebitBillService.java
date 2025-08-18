package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillSearchDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.po.TFdCreditDebitBillDetailPO;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBill)Service
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
public interface TFdCreditDebitBillService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdCreditDebitBillDTO> getList(TFdCreditDebitBillSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdCreditDebitBillDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdCreditDebitBillDTO
     * @return 是否成功
     */
    boolean doSave(TFdCreditDebitBillDTO tFdCreditDebitBillDTO);
    boolean update(TFdCreditDebitBillDTO tFdCreditDebitBillDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    List<TFdCreditDebitBillDetailDTO> getInvoiceList(TFdCreditDebitBillSearchDTO dto);

    boolean doVoid(TFdCreditDebitBillDTO dto);

    TFdCreditDebitBillDetailDTO calculate(TFdCreditDebitBillDetailDTO dto);

    List<TBusRateDTO> getRateList(TFdCreditDebitBillSearchDTO dto);
}

