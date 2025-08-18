package com.yy.ppm.master.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.master.bean.dto.MPortDTO;
import com.yy.ppm.master.bean.dto.MPortSearchDTO;
import com.yy.ppm.master.bean.po.MPortPO;
import com.yy.ppm.master.service.MPortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 港口信息Controller
 *
 * @author yangcl
 */
@RestController
@RequestMapping(value = "/api/internal/port")
@Validated
public class MPortController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MPortController.class);

    @Autowired
    MPortService mPortService;

    /**
     * 查询港口信息列表
     *
     * @param searchDTO  港口信息实体类
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getList")
    @Log(OperateTypeEnum.QUERY)
    @PreAuthorize("hasAuthority('master:port:query')")
    public Map<String, Object> getList(MPortSearchDTO searchDTO)
            throws Exception {
        final String methodName = "MPortController:getList";
        LOGGER.enter(methodName, "查询港口[start] po"+searchDTO);

        Pages<MPortDTO> pages = mPortService.getList(searchDTO);

        LOGGER.exit(methodName, "查询港口[end]");
        return Response.SUCCESS.newBuilder().toResult(pages);
    }


    /**
     * 根据ID查询港口信息
     *
     * @param id 港口信息id
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getDetail/{id}")
    @Log(OperateTypeEnum.QUERY)
    @PreAuthorize("hasAuthority('master:port:query')")
    public Map<String, Object> getPortById(@PathVariable("id") Long id)
            throws Exception {
        final String methodName = "PortController:getPortById";
        LOGGER.enter(methodName, "查询港口[start] id:"+id);

        MPortPO po = mPortService.getPortById(id);

        LOGGER.exit(methodName, "查询港口[end]");
        return Response.SUCCESS.newBuilder().toResult(po);
    }

    /**
     * 新增港口信息
     *
     * @param po  港口信息实体类
     * @return 响应数据
     * @throws Exception
     */
    @PostMapping("/insert")
    @Log(OperateTypeEnum.INSERT)
    @PreAuthorize("hasAuthority('master:port:add')")
    public Map<String, Object> insertPort(@Validated @RequestBody MPortDTO po, BindingResult result)
            throws Exception {
        final String methodName = "MPortController:insertPort";
        LOGGER.enter(methodName, "新增港口[start] po:"+po);
        mPortService.savePort(po);
        LOGGER.exit(methodName, "新增港口[end]");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改港口信息
     */
    @PostMapping("/update")
    @Log(OperateTypeEnum.UPDATE)
    @PreAuthorize("hasAuthority('master:port:update')")
    public Map<String, Object> updatePort(@RequestBody MPortDTO po)
            throws Exception {
        final String methodName = "DictController:updateDictType";
        LOGGER.enter(methodName, "修改港口[start]");

        mPortService.savePort(po);

        LOGGER.exit(methodName, "修改港口[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @Log(OperateTypeEnum.DELETE)
    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('master:port:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "PortController: deleteByid";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        mPortService.deleteById(id);

        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
