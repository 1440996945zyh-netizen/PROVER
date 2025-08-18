package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedDTO;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedSearchDTO;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 关联银行维护(TFdBankAffiliated)Service
 * @Description
 * @createTime 2023年09月13日 15:16:00
 */
public interface TFdBankAffiliatedService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdBankAffiliatedDTO> getList(TFdBankAffiliatedSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdBankAffiliatedDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdBankAffiliatedDTO
     * @return 是否成功
     */
    boolean doSave(TFdBankAffiliatedDTO tFdBankAffiliatedDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

