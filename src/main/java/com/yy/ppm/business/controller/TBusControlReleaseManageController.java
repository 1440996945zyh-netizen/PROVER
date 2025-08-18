package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.business.bean.dto.TBusCargoInfoSearchDTO;
import com.yy.ppm.business.bean.dto.TBusReleaseManageDTO;
import com.yy.ppm.business.bean.dto.TBusReleaseManageSearchDTO;
import com.yy.ppm.business.service.TBusCargoInfoService;
import com.yy.ppm.business.service.TBusControlReleaseManageService;
import com.yy.ppm.produce.bean.dto.MWeightRulesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/controlReleaseManage")
public class TBusControlReleaseManageController {


    @Autowired
    private TBusControlReleaseManageService tBusControlReleaseManageService;
    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TBusReleaseManageSearchDTO searchDTO) {
        Pages<TBusReleaseManageDTO> pages = tBusControlReleaseManageService.getList(searchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        TBusReleaseManageDTO result = tBusControlReleaseManageService.getDetail(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 放货原因填写
     *
     * @param tBusReleaseManageDTO
     * @return
     */
    @PostMapping("/release")
    public Map<String, Object> release(@RequestBody TBusReleaseManageDTO tBusReleaseManageDTO) {
        boolean flag = tBusControlReleaseManageService.release(tBusReleaseManageDTO);
        return Response.SUCCESS.newBuilder().out(flag ? "放货成功" : "放货失败").toResult();

    }

    /**
     * 修改
     *
     * @param id
     * @return
     */
    @GetMapping("/revokeRelease/{id}")
    public Map<String, Object> revokeRelease(@PathVariable("id") Long id) {
        boolean flag = tBusControlReleaseManageService.revokeRelease(id);
        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }
}
