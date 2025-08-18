package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.bean.dto.MBerthBollardDTO;
import com.yy.ppm.master.service.MBerthService;
import com.yy.ppm.master.bean.dto.MBerthDTO;
import com.yy.ppm.master.bean.dto.MBerthSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 泊位信息(MBerth)Controller
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
@RestController
@RequestMapping("/api/v1/internal/mBerth")
public class MBerthController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MBerthController.class);

    @Autowired
    private MBerthService mBerthService;

    /**
     * 获取泊位列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('master:berth:query')")
    public Map<String, Object> getList(MBerthSearchDTO searchDTO) {
        final String methodName = "MBerthController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MBerthDTO> pages = mBerthService.getList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条泊位
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('master:berth:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "MBerthController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MBerthDTO result = mBerthService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询主泊位信息
     */
    @GetMapping("/getParentBerth")
    @PreAuthorize("hasAuthority('master:berth:query')")
    public Map<String,Object> getParentBerth() {
        final String methodName = "MBerthController:getDetail";

        List<MBerthDTO> berthDTOList = mBerthService.getParentBerth();

        LOGGER.exit(methodName + "result:" + berthDTOList);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(berthDTOList);
    }

    /**
     * 新建泊位
     *
     * @param mBerthDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('master:berth:add')")
    public Map<String, Object> add(@RequestBody MBerthDTO mBerthDTO) {
        final String methodName = "MBerthController:add";
        LOGGER.enter(methodName + "[start]", "mBerthDTO:" + mBerthDTO);

        boolean flag = mBerthService.doSave(mBerthDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改泊位
     *
     * @param mBerthDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:berth:update')")
    public Map<String, Object> update(@RequestBody MBerthDTO mBerthDTO) {
        final String methodName = "MBerthController:update";
        LOGGER.enter(methodName + "[start]", "mBerthDTO:" + mBerthDTO);

        boolean flag = mBerthService.doSave(mBerthDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除泊位
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "MBerthController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mBerthService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }


    /**
     * 获取泊位缆桩列表
     *
     * @param berthId
     * @return
     */
    @GetMapping("/getBollardList")
    public Map<String, Object> getBollardList(Long berthId, String bollardName) {
        final String methodName = "MBerthBollardController:getBollardList";
        LOGGER.enter(methodName + "[start]", "berthId:" + berthId);

        List<MBerthBollardDTO> pages = mBerthService.getBollardList(berthId, bollardName);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条泊位缆桩记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getBollardDetail")
    public Map<String, Object> getBollardDetail(@RequestParam("id") Long id) {
        final String methodName = "MBerthBollardController:getBollardDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MBerthBollardDTO result = mBerthService.getBollardDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建泊位缆桩
     *
     * @param mBerthBollardDTO
     * @return
     */
    @PostMapping("/addBollard")
    public Map<String, Object> add(@RequestBody MBerthBollardDTO mBerthBollardDTO) {
        final String methodName = "MBerthBollardController:addBollard";
        LOGGER.enter(methodName + "[start]", "mBerthBollardDTO:" + mBerthBollardDTO);

        boolean flag = mBerthService.doSaveBollard(mBerthBollardDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改泊位缆桩
     *
     * @param mBerthBollardDTO
     * @return
     */
    @PutMapping("/updateBollard")
    public Map<String, Object> update(@RequestBody MBerthBollardDTO mBerthBollardDTO) {
        final String methodName = "MBerthBollardController:updateBollard";
        LOGGER.enter(methodName + "[start]", "mBerthBollardDTO:" + mBerthBollardDTO);

        boolean flag = mBerthService.doSaveBollard(mBerthBollardDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除泊位缆桩
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteBollard/{id}")
    public Map<String, Object> deleteBollard(@PathVariable("id") Long id) {
        final String methodName = "MBerthBollardController:deleteBollard";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mBerthService.deleteBollardById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    @GetMapping("/getBollard")
    public Map<String, Object> getBollard(Long id) {
        final String methodName = "MBerthBollardController:getBollard";
        LOGGER.enter(methodName + "[start]", "id:" + id);
        List<Map<String,Object>> map = mBerthService.getBollard(id);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(map);
    }


}

