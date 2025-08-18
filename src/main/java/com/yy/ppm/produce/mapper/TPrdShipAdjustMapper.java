package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.TicketTonDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupDetailPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.stream.Stream;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:03
 */
public interface TPrdShipAdjustMapper {

    Page<TBusHandoverlistDTO> list(Long shipvoyageItemId,String shipName, String voyage);

    List<TPrdWorkTicketDetailDTO> listTicket(Long shipvoyageItemId);

    @Edit
    int updateTon(List<TPrdWorkTicketDetailDTO> list);

    List<TPrdSalaryPO> getSalaryList(Long id);
    @Edit
    void updateSalaryList(List<TPrdSalaryPO> listSalary);

    List<TPrdPortStorageDetailPO> getStorageDetailList(Long id);

    List<TPrdWorkTicketDetailDTO> getTicketByShipVoyageItemID(@Param("shipItemID") Long shipvoyageItemId);

    void updateWorkTicketBatch(@Param("list") List<TPrdWorkTicketDetailDTO> dtoList);

    List<TPrdWorkTicketDetailDTO> getTicketDetailByDetailIds(@Param("list") List<Long> detailList);
    TPrdWorkTicketDetailDTO getTicketDetailByTicketId( @Param("id") Long tpwtId);

    List<TPrdWorkTicketDTO> getTicketByIds(@Param("list") List<Long> ticketIds);

    TPrdWorkTicketDTO getTicketById(@Param("id") Long tpwtId);

    int updateTicket(TPrdWorkTicketDetailDTO tmpDetail);

    String getProcessInfo(@Param("processCd") String processDetailCode);

    SysDeptDTO getDeptLevel2ByDeptId(Long deptId);

    Long getWorkPlanId(Long id);
}
