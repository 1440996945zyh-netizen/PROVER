package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.dto.TSdRedInvoiceApplyDTO;
import com.yy.ppm.finance.bean.dto.TSdRedInvoiceApplySearchDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 红字确认单(TSdRedInvoiceApply)Mapper
 * @Description
 * @createTime 2024年11月07日 16:57:00
 */
@Repository
public interface TSdRedInvoiceApplyMapper {

    /**
     * 获取红字确认单列表
     *
     * @param tSdRedInvoiceApplySearchVo
     * @return
     */
    Page<TSdRedInvoiceApplyDTO> getList(TSdRedInvoiceApplySearchDTO tSdRedInvoiceApplySearchVo);

    /**
     * 导出红字确认单列表
     *
     * @param tSdRedInvoiceApplySearchDTO
     * @return
     */
    List<TSdRedInvoiceApplyDTO> exportList(TSdRedInvoiceApplySearchDTO tSdRedInvoiceApplySearchDTO);

    /**
     * 根据id获取红字确认单
     *
     * @param id 主键
     * @return
     */
    TSdRedInvoiceApplyDTO getById(Long id);

    List<TFdInvoiceDTO> getInvoiceById(Long id);

    List<TSdRedInvoiceApplyDTO> getByInvoiceId(Long invoiceId);

    /**
     * 新增红字确认单
     *
     * @param tSdRedInvoiceApplyDTO
     * @return
     */
    @Edit
    int insert(TSdRedInvoiceApplyDTO tSdRedInvoiceApplyDTO);

    /**
     * 修改红字确认单
     *
     * @param tSdRedInvoiceApplyDTO
     * @return
     */
    @Edit
    int update(TSdRedInvoiceApplyDTO tSdRedInvoiceApplyDTO);


    /**
     * 根据id删除红字确认单
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

