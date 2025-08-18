package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.tufFee.MTugFeeDTO;
import com.yy.ppm.master.bean.po.MTugFeePO;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-27 10:37
 */
public interface TugFeeMapper {

    @Edit
    int insertTugFee(MTugFeePO tugFee);

    @Edit
    Page<MTugFeeDTO> listTugFee(MTugFeePO query);

    @Edit
    int updateTugFee(MTugFeePO tugFee);

    int deleteTugFee(Long id);
}
