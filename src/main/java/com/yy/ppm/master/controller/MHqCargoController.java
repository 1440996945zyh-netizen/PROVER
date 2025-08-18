package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MHqCargoDTO;
import com.yy.ppm.master.bean.dto.MHqCargoSearchDTO;
import com.yy.ppm.master.service.MHqCargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/internal/mHqCargo")
public class MHqCargoController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MHqCargoController.class);

    @Autowired
    private MHqCargoService mHqCargoService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MHqCargoSearchDTO searchDTO) {
        final String methodName = "MHqCargoController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MHqCargoDTO> pages = mHqCargoService.getList(searchDTO);

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
        final String methodName = "MHqCargoController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MHqCargoDTO result = mHqCargoService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mHqCargoDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MHqCargoDTO mHqCargoDTO) {
        final String methodName = "MHqCargoController:add";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + mHqCargoDTO);

        boolean flag = mHqCargoService.doSave(mHqCargoDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param mHqCargoDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MHqCargoDTO mHqCargoDTO) {
        final String methodName = "MHqCargoController:update";
        LOGGER.enter(methodName + "[start]", "mMachineDTO:" + mHqCargoDTO);

        boolean flag = mHqCargoService.doSave(mHqCargoDTO);

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
        final String methodName = "MHqCargoController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mHqCargoService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

