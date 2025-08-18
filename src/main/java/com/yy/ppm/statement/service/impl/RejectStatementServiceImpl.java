package com.yy.ppm.statement.service.impl;

import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.statement.bean.dto.RejectStatementResDTO;
import com.yy.ppm.statement.bean.dto.RejectStatementSearchDTO;
import com.yy.ppm.statement.mapper.RejectStatementMapper;
import com.yy.ppm.statement.service.RejectStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RejectStatementServiceImpl implements RejectStatementService {
    @Autowired
    RejectStatementMapper mapper;

    @Override
    public Pages<RejectStatementResDTO> getList(RejectStatementSearchDTO dto) {
        return  PageHelperUtils.limit(dto, () -> {
            Page<RejectStatementResDTO> pageList = mapper.getList(dto);
            return pageList;
        });

    }
}
