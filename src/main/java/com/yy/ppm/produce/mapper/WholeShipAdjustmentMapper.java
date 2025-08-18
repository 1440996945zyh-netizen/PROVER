package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TDisShipVoyageDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondaryDTO;
import com.yy.ppm.produce.bean.dto.TWholeShipAdjustmentQueryDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdTicketSeconAllotQuery;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TWholeShipAdjustmentExaminePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface WholeShipAdjustmentMapper {
    Page<TDisShipVoyageDTO> getList(TWholeShipAdjustmentQueryDTO queryDTO);

    //获取机械分配签票
    List<TPrdWorkTicketDetailDTO> getWorkTIcketInfoListForMachine(TWholeShipAdjustmentQueryDTO queryDTO);
    List<TPrdWorkTicketDetailDTO> getWorkTIcketInfoListForLabor(TWholeShipAdjustmentQueryDTO queryDTO);
    //获取船舶动态信息
    List<Map<Long, String>> getShipInfo(TWholeShipAdjustmentQueryDTO queryDTO);

    @Edit
    void updateTicketDetailBatch(@Param("updateList") List<TPrdWorkTicketDetailDTO> reqList);
    //获取二次配工信息
    List<TPrdDispatchSecondaryDTO> getSecondDisptchInfo(TPrdTicketSeconAllotQuery query);

    //获取签票类型为1的签票
    List<TPrdWorkTicketDetailDTO> getWorkTicketList(@Param("ids") List<Long> collect2);
    List<TPrdWorkTicketDetailDTO> getWorkTicketListForFuZhu(@Param("ids") List<Long> collect2);

    void updateShipStatus(@Param("shipvoyageItemId") Long shipvoyageItemId,@Param("status") String status);
    void updateShipStatusForLabor(@Param("shipvoyageItemId") Long shipvoyageItemId,@Param("status") String status);

    List<Map<String, Object>> getShipPLanTicketStatus(TWholeShipAdjustmentQueryDTO queryDTO);

    List<TPrdWorkTicketDetailDTO> getWorkTicketListWithFuZhu(TWholeShipAdjustmentQueryDTO queryDTO);
    @Edit
    void personClearConfirm(TWholeShipAdjustmentExaminePO po);
    @Edit
    void macClearConfirm(TWholeShipAdjustmentExaminePO po);

    TWholeShipAdjustmentExaminePO getDeatilById(Long shipvoyageItemId);

    void updateShipAdjustClearPersonStatus(Long shipvoyageItemId);

    void updateShipAdjustClearMacStatus(Long shipvoyageItemId);

    List<Map<Date, String>> getHrExTicketList(@Param("list") List<Date> collectDateCheck);
}
