package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymenDetailSearchDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpaymenDetail)Mapper
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
@Repository
public interface TFdDebtorpaymentDetailMapper {

    /**
     * 获取收据主表列表
     *
     * @param tFdDebtorpaymenDetailSearchVo
     * @return
     */
    Page<TFdDebtorpaymentDetailDTO> getList(TFdDebtorpaymenDetailSearchDTO tFdDebtorpaymenDetailSearchVo);

    /**
     * 导出收据主表列表
     *
     * @param tFdDebtorpaymenDetailSearchDTO
     * @return
     */
    List<TFdDebtorpaymentDetailDTO> exportList(TFdDebtorpaymenDetailSearchDTO tFdDebtorpaymenDetailSearchDTO);

    /**
     * 根据id获取收据主表
     *
     * @param id 主键
     * @return
     */
    TFdDebtorpaymentDetailDTO getById(Long id);

    List<TFdDebtorpaymentDetailDTO> getByParentId(Long id);



    /**
     * 新增收据主表
     *
     * @param tFdDebtorpaymenDetailDTO
     * @return
     */
    @Edit
    int insert(TFdDebtorpaymentDetailDTO tFdDebtorpaymenDetailDTO);

    /**
     * 修改收据主表
     *
     * @param tFdDebtorpaymenDetailDTO
     * @return
     */
    @Edit
    int update(TFdDebtorpaymentDetailDTO tFdDebtorpaymenDetailDTO);


    /**
     * 根据id删除收据主表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
    @Edit
    int insertBatch(List<TFdDebtorpaymentDetailDTO> detailList);

    List<TFdDebtorpaymentDetailDTO> getCreditDebit(@Param("list") List<TFdDebtorpaymentDetailDTO> tmpDNList);

    List<TFdDebtorpaymentDetailDTO> getInvoiceList(List<TFdDebtorpaymentDetailDTO> invoiceTmpList);
}

