package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TFdBankPayDTO;
import com.yy.ppm.finance.bean.dto.TFdBankPaySearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author rzg
 * @version 1.0.0
 * @ClassName 付款银行维护(TFdBankPay)Mapper
 * @Description
 * @createTime 2023年09月13日 16:23:00
 */
@Repository
public interface TFdBankPayMapper {

    /**
     * 获取付款银行维护列表
     *
     * @param tFdBankPaySearchVo
     * @return
     */
    Page<TFdBankPayDTO> getList(TFdBankPaySearchDTO tFdBankPaySearchVo);

    /**
     * 导出付款银行维护列表
     *
     * @param tFdBankPaySearchDTO
     * @return
     */
    List<TFdBankPayDTO> exportList(TFdBankPaySearchDTO tFdBankPaySearchDTO);

    /**
     * 根据id获取付款银行维护
     *
     * @param id 主键
     * @return
     */
    TFdBankPayDTO getById(Long id);

    /**
     * 新增付款银行维护
     *
     * @param tFdBankPayDTO
     * @return
     */
    @Edit
    int insert(TFdBankPayDTO tFdBankPayDTO);

    /**
     * 修改付款银行维护
     *
     * @param tFdBankPayDTO
     * @return
     */
    @Edit
    int update(TFdBankPayDTO tFdBankPayDTO);


    /**
     * 根据id删除付款银行维护
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

