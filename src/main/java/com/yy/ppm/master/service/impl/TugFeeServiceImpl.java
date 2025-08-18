package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.master.bean.dto.tufFee.MTugFeeDTO;
import com.yy.ppm.master.bean.po.MTugFeePO;
import com.yy.ppm.master.mapper.TugFeeMapper;
import com.yy.ppm.master.service.TugFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-27 10:12
 */
@Service
public class TugFeeServiceImpl implements TugFeeService {

    @Autowired
    private TugFeeMapper tugFeeMapper;

    @Autowired
    private Snowflake snowflake;

    @Override
    public void insertTugFee(MTugFeePO tugFee) {
        tugFee.setId(snowflake.nextId());
        tugFeeMapper.insertTugFee(tugFee);
    }

    @Override
    public Pages<MTugFeeDTO> listTugFee(MTugFeePO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> {
            return tugFeeMapper.listTugFee(query);
        });
    }

    @Override
    public void updateTugFee(MTugFeePO tugFee) {
        tugFeeMapper.updateTugFee(tugFee);
    }

    @Override
    public void deleteTugFee(Long id) {
        tugFeeMapper.deleteTugFee(id);
    }
}
