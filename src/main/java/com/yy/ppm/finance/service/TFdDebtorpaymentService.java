package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDetailDTO;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentDTO;
import com.yy.ppm.finance.bean.dto.TFdDebtorpaymentSearchDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 收据主表(TFdDebtorpayment)Service
 * @Description
 * @createTime 2023年09月20日 11:44:00
 */
public interface TFdDebtorpaymentService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TFdDebtorpaymentDTO> getList(TFdDebtorpaymentSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TFdDebtorpaymentDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tFdDebtorpaymentDTO
     * @return 是否成功
     */
    boolean doSave(TFdDebtorpaymentDTO tFdDebtorpaymentDTO);

    boolean update(TFdDebtorpaymentDTO tFdDebtorpaymentDTO);



    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    List<TFdDebtorpaymentDetailDTO> getSearchList(TFdDebtorpaymentSearchDTO searchDTO);

    boolean voidDebtorpayById(Long id);
}

