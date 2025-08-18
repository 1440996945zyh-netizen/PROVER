package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdCreditDebitBillDetailSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 贷方解放票据主表(TFdCreditDebitBillDetail)Mapper
 * @Description
 * @createTime 2023年10月08日 16:19:00
 */
@Repository
public interface TFdCreditDebitBillDetailMapper {

    /**
     * 获取贷方解放票据主表列表
     *
     * @param tFdCreditDebitBillDetailSearchVo
     * @return
     */
    Page<TFdCreditDebitBillDetailDTO> getList(TFdCreditDebitBillDetailSearchDTO tFdCreditDebitBillDetailSearchVo);

    /**
     * 导出贷方解放票据主表列表
     *
     * @param tFdCreditDebitBillDetailSearchDTO
     * @return
     */
    List<TFdCreditDebitBillDetailDTO> exportList(TFdCreditDebitBillDetailSearchDTO tFdCreditDebitBillDetailSearchDTO);

    /**
     * 根据id获取贷方解放票据主表
     *
     * @param id 主键
     * @return
     */
    TFdCreditDebitBillDetailDTO getById(Long id);

    /**
     * 新增贷方解放票据主表
     *
     * @param tFdCreditDebitBillDetailDTO
     * @return
     */
    @Edit
    int insert(TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO);

    /**
     * 修改贷方解放票据主表
     *
     * @param tFdCreditDebitBillDetailDTO
     * @return
     */
    @Edit
    int update(TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO);


    /**
     * 根据id删除贷方解放票据主表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
    @Edit
    void insertBatch(@Param("list") List<TFdCreditDebitBillDetailDTO> detailDTOList);

    List<TFdCreditDebitBillDetailDTO> getByParentId(@Param("id") Long id);

    List<TFdCreditDebitBillDetailDTO> getListByInvoiceDetailIdList(@Param("list") List<Long> tmpDetailIds);

    List<String> getFeeItemList(TFdCreditDebitBillDetailDTO tFdCreditDebitBillDetailDTO);
}

