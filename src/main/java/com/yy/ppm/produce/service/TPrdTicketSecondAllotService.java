package com.yy.ppm.produce.service;

import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.workTicket.*;
import com.yy.ppm.system.bean.dto.SysDeptDTO;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:37
 */
public interface TPrdTicketSecondAllotService {

    List<TPrdWorkTicketResDTO> listTicket(TPrdTicketSeconAllotQuery query);

    List<TPrdWorkTicketDetailDTO> listDetailForAllot(TPrdTicketSeconAllotQuery query);

    void insertWorkTicket(TPrdWorkTicketDTO workTicket);

    void deleteAllot(Long ticketId, String allotType);

    List<SysDeptDTO> getDepts(String allotType);

    List<TPrdWorkTicketDetailDTO> listDetailForAllotNoCargo(TPrdTicketSeconAllotQuery query);

    void insertWorkTicketNoCargo(TPrdWorkTicketDTO workTicket);
}
