package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;
import com.yy.ppm.business.service.BusServiceService;
import com.yy.ppm.common.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * (BusService)表控制层
 *
 * @author 韩旭
 * @date 2021-03-18 10:53:20
 */
@RestController
@RequestMapping(value = "/api/external/service/")
@Validated
public class BusServiceController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(BusServiceController.class);
    /**
     * 服务对象
     */
    @Autowired
    private BusServiceService busServiceService;

    @Autowired
    private CommonService commonService;

    /**
     * 根据实体类筛选数据列表
     *
     * @param busServiceSearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getlist")
    @PreAuthorize("hasAuthority('master:serviceContent:query')")
    public Map<String, Object> getList(BusServiceSearchDTO busServiceSearchDTO) {
        final String methodName = "getList";
        LOGGER.enter("BusServiceController:" + methodName + "[start]", "sysUserSearchDTO:" + busServiceSearchDTO);

        Pages<TBusServiceDTO> busServiceList = busServiceService.getList(busServiceSearchDTO);

        LOGGER.exit("BusServiceController:" + methodName + "result:" + busServiceList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(busServiceList);
    }

    /**
     * 根据id获取
     *
     * @return
     */
    @GetMapping("/getbyid/{id}")
    @PreAuthorize("hasAuthority('master:serviceContent:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "getById";
        LOGGER.enter("BusServiceController:" + methodName + "[start]", "id:" + id);

        TBusServiceDTO busServiceDTO = busServiceService.getById(id);

        LOGGER.exit("BusServiceController:" + methodName + "result:" + busServiceDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(busServiceDTO);
    }

    /**
     * 新增
     *
     * @param busServiceDTO
     * @return
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('master:serviceContent:add')")
    public Map<String, Object> insert(@RequestBody TBusServiceDTO busServiceDTO) {
        final String methodName = "insert";
        LOGGER.enter("BusServiceController:" + methodName + "[start]", "busServiceDTO:" + busServiceDTO);

        // 验证服务名重复
        commonService.isRepeate("t_bus_service", "service_nm", busServiceDTO.getServiceNm(), getString(busServiceDTO.getId()), "服务名", null);

        int count = busServiceService.save(busServiceDTO);

        LOGGER.exit("BusServiceController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
    }

    /**
     * 修改
     *
     * @param busServiceDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:serviceContent:update')")
    public Map<String, Object> update(@RequestBody TBusServiceDTO busServiceDTO) {

        final String methodName = "update";
        LOGGER.enter("BusServiceController:" + methodName + "[start]", "busServiceDTO:" + busServiceDTO);

        // 验证服务名重复
        commonService.isRepeate("t_bus_service", "service_nm", busServiceDTO.getServiceNm(), getString(busServiceDTO.getId()), "服务名", null);

        int count = busServiceService.save(busServiceDTO);

        LOGGER.exit("BusServiceController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deletebyid/{id}")
    @PreAuthorize("hasAuthority('master:serviceContent:delete')")
    public Map<String, Object> deletebyid(@PathVariable("id") String id) {
        final String methodName = "deletebyid";
        LOGGER.enter("BusServiceController:" + methodName + "[start]", "id:" + id);
        //删除服务表
        int count = commonService.delete("t_bus_service", "id", id);
        //删除服务主过程关系表
        count = commonService.delete("t_bus_service_process", "bus_service_gid", id);
        LOGGER.exit("BusServiceController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }
}