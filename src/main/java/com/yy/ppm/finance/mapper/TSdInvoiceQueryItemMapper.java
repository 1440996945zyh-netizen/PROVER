package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TSdInvoiceQueryItemDTO;
import com.yy.ppm.finance.bean.dto.TSdInvoiceQueryItemSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceQueryItem)Mapper
 * @Description
 * @createTime 2024年11月07日 10:13:00
 */
@Repository
public interface TSdInvoiceQueryItemMapper {

    /**
     * 获取列表
     *
     * @param tSdInvoiceQueryItemSearchVo
     * @return
     */
    Page<TSdInvoiceQueryItemDTO> getList(TSdInvoiceQueryItemSearchDTO tSdInvoiceQueryItemSearchVo);

    /**
     * 导出列表
     *
     * @param tSdInvoiceQueryItemSearchDTO
     * @return
     */
    List<TSdInvoiceQueryItemDTO> exportList(TSdInvoiceQueryItemSearchDTO tSdInvoiceQueryItemSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    TSdInvoiceQueryItemDTO getById(Long id);

    /**
     * 新增
     *
     * @param tSdInvoiceQueryItemDTO
     * @return
     */
    @Edit
    int insert(TSdInvoiceQueryItemDTO tSdInvoiceQueryItemDTO);

    /**
     * 修改
     *
     * @param tSdInvoiceQueryItemDTO
     * @return
     */
    @Edit
    int update(TSdInvoiceQueryItemDTO tSdInvoiceQueryItemDTO);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

