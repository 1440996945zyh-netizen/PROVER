package com.yy.ppm.mySpecialInfo.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.mySpecialInfo.bean.dto.TMySpecialInfoDTO;
import com.yy.ppm.mySpecialInfo.service.TMySpecialInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName 个人特别信息表(TMySpecialInfo)Controller
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月17日 10:17:00
 */
@RestController
@RequestMapping("/api/v1/internal/tMySpecialInfo")
public class TMySpecialInfoController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TMySpecialInfoController.class);


    private final TMySpecialInfoService tMySpecialInfoService;

    public TMySpecialInfoController(TMySpecialInfoService tMySpecialInfoService){
        this.tMySpecialInfoService = tMySpecialInfoService;
    }

    /**
     * 查询单条记录
     * @param
     * @return
     */
    @GetMapping("/getPageNum")
    public Map<String, Object> getPageNum(TMySpecialInfoDTO dto) {
        final String methodName = "TMySpecialInfoController:getPageNum";
		LOGGER.enter(methodName );

        TMySpecialInfoDTO result = tMySpecialInfoService.getPageNum(dto);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 修改分页数
     * @param dto
     * @return
     */
    @PostMapping("/updatePageNum")
    public Map<String, Object> updatePageNum(@RequestBody TMySpecialInfoDTO dto) {
        final String methodName = "TMySpecialInfoController:add";
        LOGGER.enter(methodName + "[start]", "tMySpecialInfoDTO:" +  dto);

        boolean flag = tMySpecialInfoService.updatePageNum(dto);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();

    }

    /**
     * 关注项目
     * @param dto
     * @return
     */
    @PostMapping("/projectCare")
    public Map<String, Object> projectCare(@RequestBody TMySpecialInfoDTO dto) {
        final String methodName = "TMySpecialInfoController:add";
		LOGGER.enter(methodName + "[start]", "tMySpecialInfoDTO:" +  dto);

        boolean flag = tMySpecialInfoService.careSave(dto);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "关注成功" : "关注失败").toResult();

    }

    /**
     * 取关项目
     * @param dto
     * @return
     */
    @PostMapping("/projectNoCare")
    public Map<String, Object> projectNoCare(@RequestBody TMySpecialInfoDTO dto) {
        final String methodName = "TMySpecialInfoController:add";
        LOGGER.enter(methodName + "[start]", "tMySpecialInfoDTO:" +  dto);

        boolean flag = tMySpecialInfoService.noCareSave(dto);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "取消关注成功" : "取消关注失败").toResult();

    }

//    /**
//     * 批量新增
//     * @param list
//     * @return
//     */
//    @PostMapping("/projectAdd")
//    public Map<String, Object> batchAdd(@RequestBody List<TMySpecialInfoDTO> list) {
//        final String methodName = "TMySpecialInfoController:doBatchInsert";
//        LOGGER.enter(methodName + "[start]", "list:" +  list);
//
//        boolean flag = tMySpecialInfoService.projectInsert(list);
//
//        LOGGER.exit(methodName);
//
//        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
//
//    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TMySpecialInfoController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tMySpecialInfoService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

}

