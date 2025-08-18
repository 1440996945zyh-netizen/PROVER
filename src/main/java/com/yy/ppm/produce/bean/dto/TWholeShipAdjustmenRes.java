package com.yy.ppm.produce.bean.dto;

import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class TWholeShipAdjustmenRes implements Serializable {
    //上部汇总集合
    List<TPrdWorkTicketDetailDTO> topCollect;
    //下部详情集合
    List<TPrdWorkTicketDetailDTO> bottomList;
    Map<String,Map<String, List<TPrdWorkTicketDetailDTO>>> topMap;
    Map<String,Map<String, List<TPrdWorkTicketDetailDTO>>> botMap;
}
