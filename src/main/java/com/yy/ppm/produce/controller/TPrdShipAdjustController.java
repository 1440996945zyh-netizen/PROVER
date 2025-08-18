package com.yy.ppm.produce.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.TicketTonDTO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkTicketDetailDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupDetailPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.produce.service.TPrdGroupService;
import com.yy.ppm.produce.service.TPrdShipAdjustService;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-16 14:01
 */
@RestController
@RequestMapping("/api/external/adjust")
@Validated
public class TPrdShipAdjustController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TPrdShipAdjustController.class);

    @Autowired
    private TPrdShipAdjustService tPrdShipAdjustService;


    @Autowired
    private CommonService commonService;

    /**
     * 查询主列表
     *
     * @param shipvoyageItemId
     * @param parameter
     * @return
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('produce:adjust:query')")
    public Map<String, Object> list(Long shipvoyageItemId,String shipName, String voyage, PageParameter parameter) {
        Pages<TBusHandoverlistDTO> result = tPrdShipAdjustService.list(shipvoyageItemId,shipName,voyage,parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据航次子表ID查询作业票详情
     *
     * @param shipvoyageItemId
     * @return
     */
    @GetMapping("/listTicket")
    @PreAuthorize("hasAuthority('produce:adjust:query')")
    public Map<String, Object> listTicket(Long shipvoyageItemId) {
        List<TPrdWorkTicketDetailDTO> result = tPrdShipAdjustService.listTicket(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 修改作业票量
     *
     * @param ticketTonDTO
     * @return
     */
    @PostMapping("/updateTon")
    @PreAuthorize("hasAuthority('produce:shipBalance:adjust')")
    public Map<String, Object> updateTon(@RequestBody TicketTonDTO ticketTonDTO) {
        tPrdShipAdjustService.updateTon(ticketTonDTO);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult();
    }

    /**
     * 查询主列表
     *
     * @param shipvoyageItemId
     * @return
     */
    @GetMapping("/getTicket")
    public Map<String, Object> getTicket(Long shipvoyageItemId) {
        Map<String, Object> result = tPrdShipAdjustService.getTicket(shipvoyageItemId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 整船调整
     *
     * @param dtoList
     * @return
     */
    @PostMapping("/updateWorkTicket")
    public Map<String, Object> updateWorkTicket(@RequestBody ArrayList<TPrdWorkTicketDetailDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)){
            throw new BusinessRuntimeException("没有要更新的数据");
        }
        boolean result = tPrdShipAdjustService.updateWorkTicket(dtoList);
        return Response.SUCCESS.newBuilder().out(result?"调整成功":"调整失败").toResult(result);
    }

}
