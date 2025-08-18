package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MHqStorageStackDTO;
import com.yy.ppm.master.bean.dto.MHqStorageStackSearchDTO;
import com.yy.ppm.master.service.MHqStorageStackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/internal/mHqStorageStack")
public class MHqStorageStackController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MHqStorageStackController.class);

    @Autowired
    private MHqStorageStackService mHqStorageStackService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MHqStorageStackSearchDTO searchDTO) {
        final String methodName = "MHqStorageStackController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MHqStorageStackDTO> pages = mHqStorageStackService.getList(searchDTO);

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
        final String methodName = "MHqStorageStackController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MHqStorageStackDTO result = mHqStorageStackService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mHqStorageStackDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MHqStorageStackDTO mHqStorageStackDTO) {
        final String methodName = "MHqStorageStackController:add";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + mHqStorageStackDTO);

        boolean flag = mHqStorageStackService.doSave(mHqStorageStackDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param mHqStorageStackDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MHqStorageStackDTO mHqStorageStackDTO) {
        final String methodName = "MHqStorageStackController:update";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + mHqStorageStackDTO);

        boolean flag = mHqStorageStackService.doSave(mHqStorageStackDTO);

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
        final String methodName = "MHqStorageStackController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mHqStorageStackService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

