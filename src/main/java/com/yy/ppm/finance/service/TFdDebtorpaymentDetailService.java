package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymenDetailSearchDTO;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpaymenDetail)Service
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
public interface TFdDebtorpaymentDetailService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdDebtorpaymentDetailDTO> getList(TFdDebtorpaymenDetailSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdDebtorpaymentDetailDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdDebtorpaymenDetailDTO
     * @return 是否成功
     */
    boolean doSave(TFdDebtorpaymentDetailDTO tFdDebtorpaymenDetailDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

