package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.TicketTonDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupDetailPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;

import java.util.List;
import java.util.Map;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:03
 */
public interface TPrdShipAdjustService {
     Pages<TBusHandoverlistDTO> list(Long shipvoyageItemId,String shipName, String voyage, PageParameter parameter);

     List<TPrdWorkTicketDetailDTO> listTicket(Long shipvoyageItemId);

     void updateTon(TicketTonDTO ticketTonDTO);

    Map<String, Object> getTicket(Long shipvoyageItemId);

    boolean updateWorkTicket(List<TPrdWorkTicketDetailDTO> dtoList);
}
