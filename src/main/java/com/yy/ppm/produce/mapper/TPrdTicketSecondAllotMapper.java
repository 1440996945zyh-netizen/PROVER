package com.yy.ppm.produce.mapper;

import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondaryDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdTicketSeconAllotQuery;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketAttenceUserDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketResDTO;
import com.yy.ppm.produce.bean.po.TPrdWorkTicketEquipmentPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-14 15:38
 */
public interface TPrdTicketSecondAllotMapper {

    List<TPrdWorkTicketResDTO> getTicketList(TPrdTicketSeconAllotQuery query);

    ArrayList<TPrdWorkTicketDetailDTO> getTicketDetailList(TPrdTicketSeconAllotQuery query);

    List<TPrdWorkTicketEquipmentPO> getEquipmentInfoByTicket(@Param("workTicketDetials") List<Long> workTicketDetials);

    List<TPrdDispatchSecondaryDTO> getSecondDisptchInfo(TPrdTicketSeconAllotQuery query);


    List<TPrdWorkTicketAttenceUserDTO> getAttendanceList(@Param("planId") Long workPlanId, @Param("deptIds") List<Long> deptIds);

    void deleteWorkTicketDetail(@Param("ticketId") Long ticketId,@Param("allotType") String allotType);

    List<SysDeptDTO> getDepts(@Param("allotType") String allotType);

    Map<String, Object> getDeptLevelByDeptId(@Param("deptLevel") String deptLevel,@Param("deptId") Long deptId);

    String getShipBerth(@Param("shipVoyageItemIds") List<String> collect);

    List<TPrdWorkTicketDetailDTO> getTallListForAllot(@Param("planId") Long id);
    List<Map<String,Object>> getTallListForAllotWithMap(@Param("planId") Long id);

    void deleteWorkTicketDetailByWorkPlanId(@Param("workPlanId") Long workPlanId);
}
