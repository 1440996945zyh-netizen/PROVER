package com.yy.ppm.master.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.master.service.MPiecePriceService;
import com.yy.ppm.master.bean.dto.MPiecePriceDTO;
import com.yy.ppm.master.bean.dto.MPiecePriceSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 计件单价(MPiecePrice)Controller
 * @Description
 * @createTime 2023年09月15日 11:32:00
 */
@RestController
@RequestMapping("/api/v1/internal/mPiecePrice")
public class MPiecePriceController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MPiecePriceController.class);

    @Autowired
    private MPiecePriceService mPiecePriceService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MPiecePriceSearchDTO searchDTO) {
        final String methodName = "MPiecePriceController:getList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<MPiecePriceDTO> pages = mPiecePriceService.getList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail{id}")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "MPiecePriceController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MPiecePriceDTO result = mPiecePriceService.getDetail(id);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mPiecePriceDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MPiecePriceDTO mPiecePriceDTO) {
        final String methodName = "MPiecePriceController:add";
        LOGGER.enter(methodName + "[start]", "mPiecePriceDTO:" + mPiecePriceDTO);

        boolean flag = mPiecePriceService.doSave(mPiecePriceDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param mPiecePriceDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MPiecePriceDTO mPiecePriceDTO) {
        final String methodName = "MPiecePriceController:update";
        LOGGER.enter(methodName + "[start]", "mPiecePriceDTO:" + mPiecePriceDTO);

        boolean flag = mPiecePriceService.doSave(mPiecePriceDTO);

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
        final String methodName = "MPiecePriceController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mPiecePriceService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 列表下拉选
     * @param mWorkProcessSearchDTO
     * @return
     */

    @GetMapping("/getWorkProcessSelect")
    public Map<String, Object> getWorkProcessSelect(MWorkProcessSearchDTO mWorkProcessSearchDTO) {
        final String methodName = "MPiecePriceController:getWorkProcessSelect";
        LOGGER.enter(methodName + "[start]");

        List<Map<String,Object>> result = mPiecePriceService.getWorkProcessSelect(mWorkProcessSearchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


}

