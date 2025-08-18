package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.bean.dto.MStorageStackDTO;
import com.yy.ppm.master.service.MStorageService;
import com.yy.ppm.master.bean.dto.MStorageDTO;
import com.yy.ppm.master.bean.dto.MStorageSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 库场信息(MStorage)Controller
 * @Description
 * @createTime 2023年06月05日 17:38:00
 */
@RestController
@RequestMapping("/api/v1/internal/mStorage")
public class MStorageController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MStorageController.class);

    @Autowired
    private MStorageService mStorageService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MStorageSearchDTO searchDTO) {
        final String methodName = "MStorageController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MStorageDTO> pages = mStorageService.getList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

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
        final String methodName = "MStorageController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MStorageDTO result = mStorageService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mStorageDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MStorageDTO mStorageDTO) {
        final String methodName = "MStorageController:add";
        LOGGER.enter(methodName + "[start]", "mStorageDTO:" + mStorageDTO);

        boolean flag = mStorageService.doSave(mStorageDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param mStorageDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MStorageDTO mStorageDTO) {
        final String methodName = "MStorageController:update";
        LOGGER.enter(methodName + "[start]", "mStorageDTO:" + mStorageDTO);

        boolean flag = mStorageService.doSave(mStorageDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "MStorageController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mStorageService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }


    /**
     * 获取垛位列表（翻页）
     *
     * @param storageCode
     * @return
     */
    @GetMapping("/getStackList")
    public Map<String, Object> getStackList(String storageCode, String stackName) {
        final String methodName = "MStorageController:getStackList";
        LOGGER.enter(methodName + "[start]", "storageCode:" + storageCode);

        List<MStorageStackDTO> pages = mStorageService.getStackList(storageCode, stackName);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条垛位记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getStackDetail")
    public Map<String, Object> getStackDetail(@RequestParam("id") Long id) {
        final String methodName = "MStorageController:getStackDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MStorageStackDTO result = mStorageService.getStackDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建垛位
     *
     * @param mStorageStackDTO
     * @return
     */
    @PostMapping("/addStack")
    public Map<String, Object> addStack(@RequestBody MStorageStackDTO mStorageStackDTO) {
        final String methodName = "MStorageController:add";
        LOGGER.enter(methodName + "[start]", "mStorageStackDTO:" + mStorageStackDTO);

        boolean flag = mStorageService.doSaveStack(mStorageStackDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改垛位
     *
     * @param mStorageStackDTO
     * @return
     */
    @PutMapping("/updateStack")
    public Map<String, Object> updateStack(@RequestBody MStorageStackDTO mStorageStackDTO) {
        final String methodName = "MStorageController:updateStack";
        LOGGER.enter(methodName + "[start]", "mStorageStackDTO:" + mStorageStackDTO);

        boolean flag = mStorageService.doSaveStack(mStorageStackDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除垛位
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteStack/{id}")
    public Map<String, Object> deleteStackById(@PathVariable("id") Long id) {
        final String methodName = "MStorageController:deleteStackById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mStorageService.deleteStackById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

