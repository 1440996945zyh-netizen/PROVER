package com.yy.ppm.produce.service;

import com.yy.ppm.produce.bean.dto.WorkTicketTableDTO;

import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:37
 */
public interface WaiFuExService {

    Map<String, Object> prdEx(WorkTicketTableDTO query);

    Map<String, Object> v2PrdEx(WorkTicketTableDTO query);

    Map<String, Object> v2PrdExV(WorkTicketTableDTO query);


    List<Map<String ,Object>> getDeptList();

    Map<String, Object> prdExV(WorkTicketTableDTO query);

    Map<String, Object> hrEx(WorkTicketTableDTO query);

    Map<String, Object> hrExV(WorkTicketTableDTO query);

    Map<String, Object> v2HrEx(WorkTicketTableDTO query);

    Map<String, Object> v2HrExV(WorkTicketTableDTO query);

    Map<String, Object> getNowUser();

    Map<String, Object> isTrueCompany(WorkTicketTableDTO query);

    Map<String, Object> v2HrNewEx(WorkTicketTableDTO query);

    Map<String, Object> v2HrNewExV(WorkTicketTableDTO query);
}
