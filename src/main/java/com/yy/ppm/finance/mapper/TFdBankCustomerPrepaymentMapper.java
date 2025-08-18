package com.yy.ppm.finance.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusTrustCargoDTO;
import com.yy.ppm.finance.bean.dto.BusTrustResponseDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdBankCustomerPrepaymentSearchDTO;
import com.yy.ppm.produce.bean.dto.salary.TPrdSalaryDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 客户预缴(TFdBankCustomerPrepayment)Mapper
 * @Description
 * @createTime 2023年09月14日 10:30:00
 */
@Repository
public interface TFdBankCustomerPrepaymentMapper {

    /**
     * 获取客户预缴列表
     *
     * @param tFdBankCustomerPrepaymentSearchVo
     * @return
     */
    Page<TFdBankCustomerPrepaymentDTO> getList(TFdBankCustomerPrepaymentSearchDTO tFdBankCustomerPrepaymentSearchVo);

    /**
     * 获取客户余额列表
     *
     * @param tFdBankCustomerPrepaymentSearchVo
     * @return
     */
    Page<TFdBankCustomerPrepaymentDTO> getBalanceList(TFdBankCustomerPrepaymentSearchDTO tFdBankCustomerPrepaymentSearchVo);
    /**
     * 获取客户扣款明细列表
     *
     * @param tFdBankCustomerPrepaymentSearchVo
     * @return
     */
    Page<TFdBankCustomerPaymentDTO> getBalanceDetailList(TFdBankCustomerPrepaymentSearchDTO tFdBankCustomerPrepaymentSearchVo);
    Cursor<TFdBankCustomerPaymentDTO> exportBalanceDetailList(TFdBankCustomerPrepaymentSearchDTO tFdBankCustomerPrepaymentSearchVo);


    /**
     * 导出客户预缴列表
     *
     * @param tFdBankCustomerPrepaymentSearchDTO
     * @return
     */
    List<TFdBankCustomerPrepaymentDTO> exportList(TFdBankCustomerPrepaymentSearchDTO tFdBankCustomerPrepaymentSearchDTO);
    Map<String,Object> getPrePaymentAmountSum(TFdBankCustomerPrepaymentSearchDTO tFdBankCustomerPrepaymentSearchDTO);

    /**
     * 根据id获取客户预缴
     *
     * @param id 主键
     * @return
     */
    TFdBankCustomerPrepaymentDTO getById(@Param("id") Long id);
    TFdBankCustomerPrepaymentDTO getByPrePayCode(String sourcePrepayCode);

    /**
     * 新增客户预缴
     *
     * @param tFdBankCustomerPrepaymentDTO
     * @return
     */
    @Edit
    int insert(TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO);

    /**
     * 修改客户预缴
     *
     * @param tFdBankCustomerPrepaymentDTO
     * @return
     */
    @Edit
    int update(TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO);


    /**
     * 根据id删除客户预缴
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
    @Edit
    int voidHandle(TFdBankCustomerPrepaymentDTO tFdBankCustomerPrepaymentDTO);

    List<BusTrustResponseDTO> getBusTrustListWithKeyWord(@Param("keyWord") String keyword);

    List<BusTrustResponseDTO> getBusTrustList(@Param("companyId") Long companyId, @Param("id") Long id,
                                              @Param("customerId") Long customerId, @Param("cargoInfoId") Long cargoInfoId);

    List<TFdBankCustomerPrepaymentDTO> getPrepaymentCodeList(TFdBankCustomerPrepaymentSearchDTO searchDTO);
    @Edit
    int updateWithMode20(TFdBankCustomerPrepaymentDTO dto);

    TFdBankCustomerPrepaymentDTO getByTrustId(@Param("trustId") Long busTrustId,@Param("cargoInfoId") Long cargoInfoId);

    Integer countSourcePrepayCode(@Param("sourcePrepayCode") String sourcePrepayCode);

    TBusTrustCargoDTO getTrustCargo(@Param("trustId") Long busTrustId,@Param("cargoInfoId") Long cargoInfoId);

    void updateBusCargoInfo(@Param("cargoInfoId") Long cargoInfoId,@Param("status") String status);

    List<TFdBankCustomerPrepaymentDTO> getListBycargoInfoId(@Param("cargoInfoId") Long cargoInfoId);
}

