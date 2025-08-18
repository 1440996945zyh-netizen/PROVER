package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailSearchDTO;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBillDetail)Service
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
public interface TFdCreditDebitBillDetailService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdCreditDebitBillDetailDTO> getList(TFdCreditDebitBillDetailSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdCreditDebitBillDetailDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdCreditDebitBillDetailDTO
     * @return 是否成功
     */
    boolean doSave(TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

