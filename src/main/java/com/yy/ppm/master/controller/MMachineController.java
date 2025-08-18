package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.service.MMachineService;
import com.yy.ppm.master.bean.dto.MMachineDTO;
import com.yy.ppm.master.bean.dto.MMachineSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 机械信息(MMachine)Controller
 * @Description
 * @createTime 2023年06月05日 17:28:00
 */
@RestController
@RequestMapping("/api/v1/internal/mMachine")
public class MMachineController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MMachineController.class);

    @Autowired
    private MMachineService mMachineService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MMachineSearchDTO searchDTO) {
        final String methodName = "MMachineController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MMachineDTO> pages = mMachineService.getList(searchDTO);

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
        final String methodName = "MMachineController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MMachineDTO result = mMachineService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mMachineDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MMachineDTO mMachineDTO) {
        final String methodName = "MMachineController:add";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + mMachineDTO);

        boolean flag = mMachineService.doSave(mMachineDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param mMachineDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MMachineDTO mMachineDTO) {
        final String methodName = "MMachineController:update";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + mMachineDTO);

        boolean flag = mMachineService.doSave(mMachineDTO);

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
        final String methodName = "MMachineController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mMachineService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

