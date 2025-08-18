package com.yy.ppm.business.controller;

import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.business.service.PoundbillService;
import com.yy.ppm.business.bean.dto.PoundbillDTO;
import com.yy.ppm.business.bean.dto.PoundbillSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 单船测试记录(TStdShipRecord)Controller
 * @Description
 * @createTime 2023年12月31日 10:35:00
 */
@RestController
@RequestMapping("/api/v1/internal/poundbill")
public class PoundbillController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(PoundbillController.class);

    @Autowired
    private PoundbillService tStdShipRecordService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getPageList")
    public Map<String, Object> getPageList(PoundbillSearchDTO searchDTO) {
        final String methodName = "PoundbillController:getPageList";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<PoundbillDTO> pages = tStdShipRecordService.getPageList(searchDTO);

        LOGGER.exit(methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 查询单条记录
     * @param searchDTO
     * @return
     */
    @GetMapping("/detailListByCondition")
    public Map<String, Object> detailListByCondition(PoundbillSearchDTO searchDTO) {
        final String methodName = "PoundbillController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<PoundbillDTO> result = tStdShipRecordService.getDetailListByCondition(searchDTO);

        LOGGER.exit(methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

}

