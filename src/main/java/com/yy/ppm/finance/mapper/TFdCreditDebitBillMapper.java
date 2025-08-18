package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusRateDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillSearchDTO;
import com.yy.ppm.finance.bean.dto.TFdInvoiceDTO;
import com.yy.ppm.finance.bean.po.TFdCreditDebitBillDetailPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBill)Mapper
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Repository
public interface TFdCreditDebitBillMapper {

    /**
     * 获取贷方解放票据主表列表
     *
     * @param tFdCreditDebitBillSearchVo
     * @return
     */
    Page<TFdCreditDebitBillDTO> getList(TFdCreditDebitBillSearchDTO tFdCreditDebitBillSearchVo);

    /**
     * 导出贷方解放票据主表列表
     *
     * @param tFdCreditDebitBillSearchDTO
     * @return
     */
    List<TFdCreditDebitBillDTO> exportList(TFdCreditDebitBillSearchDTO tFdCreditDebitBillSearchDTO);

    /**
     * 根据id获取贷方解放票据主表
     *
     * @param id 主键
     * @return
     */
    TFdCreditDebitBillDTO getById(Long id);

    /**
     * 新增贷方解放票据主表
     *
     * @param tFdCreditDebitBillDTO
     * @return
     */
    @Edit
    int insert(TFdCreditDebitBillDTO tFdCreditDebitBillDTO);

    /**
     * 修改贷方解放票据主表
     *
     * @param tFdCreditDebitBillDTO
     * @return
     */
    @Edit
    int update(TFdCreditDebitBillDTO tFdCreditDebitBillDTO);

    int updateByNumber(Map map);
    int updateInvoiceCodeById(Map map);

    /**
     * 根据id删除贷方解放票据主表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 获取发票信息
     * @param dto
     * @return
     */
    List<TFdCreditDebitBillDetailDTO> getInvoiceList(TFdCreditDebitBillSearchDTO dto);
    List<TFdCreditDebitBillDetailDTO> getDnList(TFdCreditDebitBillSearchDTO dto);
    String getCNDNCode(@Param("cndnCode") String cndn);
    @Edit
    int doVoid(TFdCreditDebitBillDTO dto);

    List<TFdCreditDebitBillDTO> getListBySysInvoiceCode(String sysInvoiceCode);

    List<TBusRateDTO> getRateList(TFdCreditDebitBillSearchDTO dto);

    TFdInvoiceDTO getInvoiceInfoBySysInvoiceCode(@Param("syInvoiceCode") String sysInvoiceCode);
}

