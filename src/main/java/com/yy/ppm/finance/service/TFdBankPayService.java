package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdBankPayDTO;
import com.yy.ppm.finance.bean.dto.TFdBankPaySearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @author rzg
 * @version 1.0.0
 * @ClassName 付款银行维护(TFdBankPay)Service
 * @Description
 * @createTime 2023年09月13日 16:23:00
 */
public interface TFdBankPayService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdBankPayDTO> getList(TFdBankPaySearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdBankPayDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdBankPayDTO
     * @return 是否成功
     */
    boolean doSave(TFdBankPayDTO tFdBankPayDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    List<Map<String, Object>> getSelectList();
}

