package com.yy.ppm.applet.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeDTO;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeSearchDTO;
import com.yy.ppm.applet.service.TStdEnterPortNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 入港公告(TStdEnterPortNotice)Controller
 * @Description
 * @createTime 2023年12月01日 14:08:00
 */
@RestController
@RequestMapping("/api/v1/internal/tStdEnterPortNotice")
public class TStdEnterPortNoticeController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TStdEnterPortNoticeController.class);

    @Autowired
    private TStdEnterPortNoticeService tStdEnterPortNoticeService;


    /**
     * 查询港口信息列表
     *
     * @param searchDTO  港口信息实体类
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getList")
    @Log(title="入港公告查询",value=OperateTypeEnum.QUERY)
//    @PreAuthorize("hasAuthority('master:portNotice:query')")
    public Map<String, Object> getList(TStdEnterPortNoticeSearchDTO searchDTO){
        final String methodName = "MPortController:getList";
        LOGGER.enter(methodName, "查询港口[start] po"+searchDTO);
        Pages<TStdEnterPortNoticeDTO> pages = tStdEnterPortNoticeService.getPageList(searchDTO);
        LOGGER.exit(methodName, "查询港口[end]");
        return Response.SUCCESS.newBuilder().toResult(pages);
    }

    /**
     * 查询单条记录
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/detailByCondition")
    public Map<String, Object> detailByCondition(TStdEnterPortNoticeSearchDTO searchDTO) {
        final String methodName = "TStdEnterPortNoticeController:getDetailByCondition";
        LOGGER.enter(methodName + "[start]", "tStdEnterPortNoticeDTO:" + searchDTO);
        List<TStdEnterPortNoticeDTO> list = tStdEnterPortNoticeService.getListByCondition(searchDTO);
        if(!CollectionUtils.isEmpty(list)){
            LOGGER.exit(methodName + "list:" + list);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(list.get(0));
        }else{
            LOGGER.exit(methodName + "list:" + list);
            return Response.SUCCESS.newBuilder().out("查询成功").toResult();
        }
    }

    /**
     * 查询最新一条
     * @param searchDTO
     * @return
     */
    @GetMapping("/getLatestOne")
    public Map<String, Object> getLatestOne(TStdEnterPortNoticeSearchDTO searchDTO) {
        List<TStdEnterPortNoticeDTO> list = tStdEnterPortNoticeService.getLatestOne(searchDTO);
        if(!CollectionUtils.isEmpty(list)){
            return Response.SUCCESS.newBuilder().out("查询成功").toResult(list.get(0));
        }else{
            return Response.SUCCESS.newBuilder().out("查询成功").toResult();
        }
    }

    /**
     * 新建
     *
     * @param tStdEnterPortNoticeDTO
     * @return
     */
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody @Validated TStdEnterPortNoticeDTO tStdEnterPortNoticeDTO, BindingResult result) {
        final String methodName = "TStdEnterPortNoticeController:add";
        LOGGER.enter(methodName + "[start]", "tStdEnterPortNoticeDTO:" + tStdEnterPortNoticeDTO);
        if (result.hasErrors()) {
            String msg = result.getFieldError().getDefaultMessage();
            LOGGER.warn("参数校验失败,msg: " + msg);
            return Response.FAIL.newBuilder().out(msg).toResult();
        }
        boolean flag = tStdEnterPortNoticeService.doSave(tStdEnterPortNoticeDTO);
        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
    }

    /**
     * 删除入港公告
     * @return
     * @paramt
     */
    @DeleteMapping("/deleteNotice")
    @Log(title="入港公告删除",value=OperateTypeEnum.QUERY)
    public Map<String, Object> deleteNotice(Long id) {
        final String methodName = "deleteNotice";
        LOGGER.enter(methodName, "删除入港公告, id: " + id);
        tStdEnterPortNoticeService.deleteById(id);
        return Response.SUCCESS.newBuilder().out("入港公告删除成功").toResult();
    }
}

