package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.BusTrustResponseDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)Service
 * @Description
 * @createTime 2023年09月14日 10:30:00
 */
public interface TFdBankCustomerPrepaymentService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdBankCustomerPrepaymentDTO> getList(TFdBankCustomerPrepaymentSearchDTO searchDTO);

    /**
     * 获取余额列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdBankCustomerPrepaymentDTO> getBalanceList(TFdBankCustomerPrepaymentSearchDTO searchDTO);

    /**
     * 扣款明细列表
     * @param searchDTO
     * @return
     */
    Pages<TFdBankCustomerPaymentDTO> getBalanceDetailList(TFdBankCustomerPrepaymentSearchDTO searchDTO);
    byte[] exportBalanceDetailList(TFdBankCustomerPrepaymentSearchDTO searchDTO);

    Map<String,Object> getBankCustomerPrepayment(TFdBankCustomerPrepaymentSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdBankCustomerPrepaymentDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdBankCustomerPrepaymentDTO
     * @return 是否成功
     */
    boolean doSave(TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    boolean voidHandle(TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO);

    List<BusTrustResponseDTO> getBusTrustList(String keyWord);

    List<BusTrustResponseDTO> getTrustOrderList(TFdBankCustomerPrepaymentSearchDTO searchDTO);

    List<TFdBankCustomerPrepaymentDTO> getPrepaymentCodeList(TFdBankCustomerPrepaymentSearchDTO searchDTO);

    Map<String,Object> getAmountInfo(TFdBankCustomerPrepaymentSearchDTO searchDTO);
}

