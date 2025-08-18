package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TSdInvoiceRequestItemDTO;
import com.yy.ppm.finance.bean.dto.TSdInvoiceRequestItemSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceRequestItem)Mapper
 * @Description
 * @createTime 2024年11月07日 10:15:00
 */
@Repository
public interface TSdInvoiceRequestItemMapper {

    /**
     * 获取列表
     *
     * @param tSdInvoiceRequestItemSearchVo
     * @return
     */
    Page<TSdInvoiceRequestItemDTO> getList(TSdInvoiceRequestItemSearchDTO tSdInvoiceRequestItemSearchVo);

    /**
     * 导出列表
     *
     * @param tSdInvoiceRequestItemSearchDTO
     * @return
     */
    List<TSdInvoiceRequestItemDTO> exportList(TSdInvoiceRequestItemSearchDTO tSdInvoiceRequestItemSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    TSdInvoiceRequestItemDTO getById(Long id);

    /**
     * 新增
     *
     * @param tSdInvoiceRequestItemDTO
     * @return
     */
    @Edit
    int insert(TSdInvoiceRequestItemDTO tSdInvoiceRequestItemDTO);

    /**
     * 修改
     *
     * @param tSdInvoiceRequestItemDTO
     * @return
     */
    @Edit
    int update(TSdInvoiceRequestItemDTO tSdInvoiceRequestItemDTO);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

