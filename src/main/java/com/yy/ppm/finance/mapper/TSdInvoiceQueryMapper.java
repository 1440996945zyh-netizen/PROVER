package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TSdInvoiceQueryDTO;
import com.yy.ppm.finance.bean.dto.TSdInvoiceQuerySearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TSdInvoiceQuery)Mapper
 * @Description
 * @createTime 2024年11月08日 10:42:00
 */
@Repository
public interface TSdInvoiceQueryMapper {

    /**
     * 获取列表
     *
     * @param tSdInvoiceQuerySearchVo
     * @return
     */
    Page<TSdInvoiceQueryDTO> getList(TSdInvoiceQuerySearchDTO tSdInvoiceQuerySearchVo);

    /**
     * 导出列表
     *
     * @param tSdInvoiceQuerySearchDTO
     * @return
     */
    List<TSdInvoiceQueryDTO> exportList(TSdInvoiceQuerySearchDTO tSdInvoiceQuerySearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    TSdInvoiceQueryDTO getById(Long id);

    TSdInvoiceQueryDTO getByInvoiceId(Long id);

    /**
     * 新增
     *
     * @param tSdInvoiceQueryDTO
     * @return
     */
    @Edit
    int insert(TSdInvoiceQueryDTO tSdInvoiceQueryDTO);

    /**
     * 修改
     *
     * @param tSdInvoiceQueryDTO
     * @return
     */
    @Edit
    int update(TSdInvoiceQueryDTO tSdInvoiceQueryDTO);


    /**
     * 根据id删除
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

