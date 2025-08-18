package com.yy.ppm.statement.service;

import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.RejectStatementResDTO;
import com.yy.ppm.statement.bean.dto.RejectStatementSearchDTO;

public interface RejectStatementService {
    Pages<RejectStatementResDTO> getList(RejectStatementSearchDTO dto);
}
