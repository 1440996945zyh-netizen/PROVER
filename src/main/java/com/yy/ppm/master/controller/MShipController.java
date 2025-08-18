package com.yy.ppm.master.controller;

import com.alibaba.druid.util.StringUtils;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.master.bean.dto.FieldRemark;
import com.yy.ppm.master.bean.po.MShipLogPO;
import com.yy.ppm.master.service.MShipService;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @ClassName 海轮资料(MShip)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月27日 15:44:00
 */
@RestController
@RequestMapping("/api/v1/internal/mShip")
public class MShipController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(MShipController.class);

    @Autowired
    private MShipService mShipService;

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

        Pages<MShipDTO> pages = mShipService.getList(searchDTO);

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

        MShipDTO result = mShipService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getShipLog")
    @PreAuthorize("hasAuthority('master:ship:query')")
    public Map<String, Object> getShipLog(@RequestParam("id") Long id) {
        final String methodName = "MShipController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        List<MShipLogPO> result = mShipService.getShipLog(id);

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

        boolean flag = mShipService.doSave(mShipDTO);

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
        boolean flag = mShipService.doSave(mShipDTO);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }


    /**
     * 修改
     * @param mShipDTO
     * @return
     */
    @PutMapping("/reject")
    @PreAuthorize("hasAuthority('master:ship:reject')")
    public Map<String, Object> reject(@RequestBody MShipDTO mShipDTO) {
        final String methodName = "MShipController:reject";
        LOGGER.enter(methodName + "[start]", "mShipDTO:" +  mShipDTO);

        boolean flag = mShipService.doReject(mShipDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "驳回成功" : "驳回失败").toResult();
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

        boolean flag = mShipService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 船舶资料审核
     * @param id
     * @return
     */
    @GetMapping("publishmisc/{id}")
    public Map<String, Object> approveByIds(@PathVariable("id") Long id) {
        final String methodName = "MShipController:approveByIds";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mShipService.approveById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "审核成功" : "审核失败").toResult();
    }

/*
    @PreAuthorize("hasAuthority('business:customer:delete')")
*/
    @GetMapping("revokeMisc/{id}")
    public Map<String, Object> cancelById(@PathVariable("id") Long id) {
        final String methodName = "MShipController:cancelById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = mShipService.cancelById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "消审成功" : "消审失败").toResult();
    }


    /**
     * 获取船舶数据（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getData")
    @PreAuthorize("hasAuthority('master:ship:add')")
    public Map<String, Object> getData(MShipSearchDTO searchDTO) {
        return Response.SUCCESS.newBuilder().out("查询成功").toResult();
    }
    /**
     * 批量同步
     * @param list
     * @return
     */
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('master:ship:add')")
    public Map<String, Object> syncData(@RequestBody List<MShipDTO> list) {
        final String methodName = "MShipController:update";
        LOGGER.enter(methodName + "[start]", "list:" +  list);

        boolean flag = mShipService.sync(list);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "同步成功" : "同步失败").toResult();
    }

    /**
     * 判断是否黑名单
     * @param list
     * @return
     */
    @PostMapping("/getBlackShip")
    public Map<String, Object> getBlackShip(@RequestBody List<String> list) {

        String result = mShipService.getBlackShip(list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 查询附件信息
     * @param id
     * @return
     */
    @GetMapping("/queryBusFiles")
    @PreAuthorize("hasAuthority('master:ship:query')")
    public Map<String, Object> queryBusFiles(@RequestParam("id") Long id,@RequestParam("businessType")String businessType) {
        final String methodName = "MShipController:getDetail";
        LOGGER.enter(methodName + "[start]", "id:" + id);
//
//        List<Map<String, Object>> result = mShipService.queryBusFiles(id,businessType);
//
//        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult();
    }


    /**
     * 获取附件
     * @param id
     * @return
     */
    @GetMapping("/queryDownload")
    @PreAuthorize("hasAuthority('master:ship:query')")
    public Map<String, Object> queryDownload(@NotBlank(message ="文件ID不能为空") String id, HttpServletResponse resp) throws IOException {
        final String methodName = "MShipController:queryDownload";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        MShipDTO mShipDTO = new MShipDTO();
        mShipDTO.setId(Long.valueOf(id));
      //  InputStream result = mShipService.queryDownload(mShipDTO);

        byte[] buffer = new byte[1024]; // 创建一个缓冲区
        int bytesRead;
        ByteArrayOutputStream outputStreamTemp = new ByteArrayOutputStream();

//        while ((bytesRead = result.read(buffer)) != -1) {
//            outputStreamTemp.write(buffer, 0, bytesRead); // 将读取的数据写入到 ByteArrayOutputStream 中
//        }

        try (OutputStream outputStream = resp.getOutputStream()) {
            resp.setHeader("content-disposition",
                    "attachment;filename=" + URLEncoder.encode("测试", "UTF-8"));
            byte[] byteArray = outputStreamTemp.toByteArray();
            outputStream.write(outputStreamTemp.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.exit( methodName + "result:" );

        return Response.SUCCESS.newBuilder().out("查询成功").toResult();
    }

    /**
     * 审核
     * @param mShipDTO
     * @return
     */
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('master:ship:add')")
    @Log(title ="新增船舶资料",value = OperateTypeEnum.INSERT)
    public Map<String, Object> approve(@RequestBody MShipDTO mShipDTO) {
        final String methodName = "MShipController:add";
        LOGGER.enter(methodName + "[start]", "mShipDTO:" +  mShipDTO);

        boolean flag = mShipService.approve(mShipDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();
    }

}

