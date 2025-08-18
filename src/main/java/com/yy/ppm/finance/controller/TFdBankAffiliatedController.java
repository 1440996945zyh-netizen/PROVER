package com.yy.ppm.finance.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.finance.service.TFdBankAffiliatedService;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedDTO;
import com.yy.ppm.finance.bean.dto.TFdBankAffiliatedSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 关联银行维护(TFdBankAffiliated)Controller
 * @Description
 * @createTime 2023年09月13日 15:16:00
 */
@RestController
@RequestMapping("/api/v1/internal/tFdBankAffiliated")
public class TFdBankAffiliatedController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TFdBankAffiliatedController.class);

    @Autowired
    private TFdBankAffiliatedService tFdBankAffiliatedService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(TFdBankAffiliatedSearchDTO searchDTO) {
        final String methodName = "TFdBankAffiliatedController:getList";

        Pages<TFdBankAffiliatedDTO> pages = tFdBankAffiliatedService.getList(searchDTO);


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

        TFdBankAffiliatedDTO result = tFdBankAffiliatedService.getDetail(id);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param tFdBankAffiliatedDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody TFdBankAffiliatedDTO tFdBankAffiliatedDTO) {
        final String methodName = "TFdBankAffiliatedController:add";
        LOGGER.enter(methodName + "[start]", "tFdBankAffiliatedDTO:" + tFdBankAffiliatedDTO);

        boolean flag = tFdBankAffiliatedService.doSave(tFdBankAffiliatedDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param tFdBankAffiliatedDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TFdBankAffiliatedDTO tFdBankAffiliatedDTO) {
        final String methodName = "TFdBankAffiliatedController:update";
        LOGGER.enter(methodName + "[start]", "tFdBankAffiliatedDTO:" + tFdBankAffiliatedDTO);

        boolean flag = tFdBankAffiliatedService.doSave(tFdBankAffiliatedDTO);

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
        final String methodName = "TFdBankAffiliatedController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tFdBankAffiliatedService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

