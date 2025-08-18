package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.statement.bean.dto.RejectStatementResDTO;
import com.yy.ppm.statement.bean.dto.RejectStatementSearchDTO;

public interface RejectStatementMapper {

    Page<RejectStatementResDTO> getList(RejectStatementSearchDTO dto);
}
