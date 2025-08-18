package com.yy.ppm.statement.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.statement.bean.dto.RejectStatementResDTO;
import com.yy.ppm.statement.bean.dto.RejectStatementSearchDTO;
import com.yy.ppm.statement.service.RejectStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/external/rejectStatement")
@Validated
public class RejectStatementController {
    @Autowired
    RejectStatementService service;
    @GetMapping("/getList")
    public Map<String, Object> getList(RejectStatementSearchDTO dto){

        Pages<RejectStatementResDTO> result = service.getList(dto);

        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
