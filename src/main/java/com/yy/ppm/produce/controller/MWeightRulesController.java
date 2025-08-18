package com.yy.ppm.produce.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.MWeightRulesDTO;
import com.yy.ppm.produce.bean.dto.MWeightRulesSearchDTO;
import com.yy.ppm.produce.mapper.TPoundMapper;
import com.yy.ppm.produce.service.MWeightRulesService;
import com.yy.ppm.produce.service.TPoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (MWeightRules)Controller
 * @Description
 * @createTime 2023年11月30日 17:20:00
 */
@RestController
@RequestMapping("/api/v1/internal/mWeightRules")
public class MWeightRulesController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MWeightRulesController.class);

    @Autowired
    private MWeightRulesService mWeightRulesService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    public Map<String, Object> getList(MWeightRulesSearchDTO searchDTO) {
        final String methodName = "MWeightRulesController:getList";

        Pages<MWeightRulesDTO> pages = mWeightRulesService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

//    @Autowired
//    private TPoundService tPoundService;
//    @Resource
//    private TPoundMapper tPoundMapper;

//    @GetMapping("/updateStorage")
//    public Map<String, Object> updateStorage(Long noteId) {
//
//        try{
//            LOGGER.enter("10:00==>地磅港存更新==开始==>");
//            Map<String,Object> params = Maps.newHashMap();
//            params.put("startDate",null);
//            params.put("endDate",null);
//            params.put("noteId",noteId);
//            tPoundService.updatePortStage(tPoundMapper.getTallyByParams(params));
//            LOGGER.info("10:00==>地磅港存更新==结束==>");
//            return Response.SUCCESS.newBuilder().out("查询成功").toResult();
//        }catch (Exception e){
//            LOGGER.error(e.getMessage());
//            return null;
//        }
//    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
        final String methodName = "MWeightRulesController:getDetail";

        MWeightRulesDTO result = mWeightRulesService.getDetail(id);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     *
     * @param mWeightRulesDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody MWeightRulesDTO mWeightRulesDTO) {
        final String methodName = "MWeightRulesController:add";
        LOGGER.enter(methodName + "[start]", "mWeightRulesDTO:" + mWeightRulesDTO);

        boolean flag = mWeightRulesService.doSave(mWeightRulesDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     *
     * @param mWeightRulesDTO
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody MWeightRulesDTO mWeightRulesDTO) {
        final String methodName = "MWeightRulesController:update";
        LOGGER.enter(methodName + "[start]", "mWeightRulesDTO:" + mWeightRulesDTO);

        boolean flag = mWeightRulesService.doSave(mWeightRulesDTO);

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
        final String methodName = "MWeightRulesController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mWeightRulesService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

