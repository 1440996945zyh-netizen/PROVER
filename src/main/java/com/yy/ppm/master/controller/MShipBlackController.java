package com.yy.ppm.master.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;
import com.yy.ppm.master.bean.po.MShipLogPO;
import com.yy.ppm.master.service.MShipBlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/mShipBlackList")
public class MShipBlackController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MShipBlackController.class);
    @Autowired
    private MShipBlackService mShipBlackService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('master:ship:query')")
    public Map<String, Object> getList(MShipSearchDTO searchDTO) {
        final String methodName = "MShipController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MShipDTO> pages = mShipBlackService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('master:ship:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "MShipController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MShipDTO result = mShipBlackService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }
    

    /**
     * 新建
     * @param mShipDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('master:ship:add')")
    @Log(title ="新增船舶资料",value = OperateTypeEnum.INSERT)
    public Map<String, Object> add(@RequestBody MShipDTO mShipDTO) {
        final String methodName = "MShipController:add";
        LOGGER.enter(methodName + "[start]", "mShipDTO:" +  mShipDTO);

        boolean flag = mShipBlackService.doSave(mShipDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 修改
     * @param mShipDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:ship:update')")
    public Map<String, Object> update(@RequestBody MShipDTO mShipDTO) {
        final String methodName = "MShipController:update";
        LOGGER.enter(methodName + "[start]", "mShipDTO:" +  mShipDTO);
        boolean flag = mShipBlackService.doSave(mShipDTO);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }


    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('master:ship:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "MShipController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mShipBlackService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }
}
