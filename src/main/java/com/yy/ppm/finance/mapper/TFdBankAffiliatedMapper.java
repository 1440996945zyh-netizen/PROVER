package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedDTO;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 关联银行维护(TFdBankAffiliated)Mapper
 * @Description
 * @createTime 2023年09月13日 15:16:00
 */
@Repository
public interface TFdBankAffiliatedMapper {

    /**
     * 获取关联银行维护列表
     *
     * @param tFdBankAffiliatedSearchVo
     * @return
     */
    Page<TFdBankAffiliatedDTO> getList(TFdBankAffiliatedSearchDTO tFdBankAffiliatedSearchVo);

    /**
     * 导出关联银行维护列表
     *
     * @param tFdBankAffiliatedSearchDTO
     * @return
     */
    List<TFdBankAffiliatedDTO> exportList(TFdBankAffiliatedSearchDTO tFdBankAffiliatedSearchDTO);

    /**
     * 根据id获取关联银行维护
     *
     * @param id 主键
     * @return
     */
    TFdBankAffiliatedDTO getById(Long id);

    /**
     * 新增关联银行维护
     *
     * @param tFdBankAffiliatedDTO
     * @return
     */
    @Edit
    int insert(TFdBankAffiliatedDTO tFdBankAffiliatedDTO);

    /**
     * 修改关联银行维护
     *
     * @param tFdBankAffiliatedDTO
     * @return
     */
    @Edit
    int update(TFdBankAffiliatedDTO tFdBankAffiliatedDTO);


    /**
     * 根据id删除关联银行维护
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

