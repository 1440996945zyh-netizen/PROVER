package com.yy.ppm.master.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.tufFee.MTugFeeDTO;
import com.yy.ppm.master.bean.po.MTugFeePO;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-27 10:12
 */
public interface TugFeeService {

    void insertTugFee(MTugFeePO tugFee);

    Pages<MTugFeeDTO> listTugFee(MTugFeePO query, PageParameter parameter);

    void updateTugFee(MTugFeePO tugFee);

    void deleteTugFee(Long id);
}
