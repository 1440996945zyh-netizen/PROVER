package com.yy.ppm.dispatch.controller;

import cn.hutool.core.io.IORuntimeException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.page.Pages;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;

import com.yy.ppm.business.bean.dto.TBusCargoInfoSearchDTO;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailExcelDTO;
import com.yy.ppm.dispatch.service.TDisCloseSailService;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailDTO;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailSearchDTO;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 封航记录表(TDisCloseSail)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:54:00
 */
@RestController
@RequestMapping("/api/v1/internal/tDisCloseSail")
public class TDisCloseSailController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TDisCloseSailController.class);

    @Autowired
    private TDisCloseSailService tDisCloseSailService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('dispatch:closeSail:query')")
    public Map<String, Object> getList(TDisCloseSailSearchDTO searchDTO) {
//    	final String methodName = "TDisCloseSailController:getList";
//		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TDisCloseSailDTO> pages = tDisCloseSailService.getList(searchDTO);

//        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 获取封航影响的船舶
     * @param searchDTO
     * @return
     */
    @GetMapping("/getShipVoyageList")
    @PreAuthorize("hasAuthority('dispatch:closeSail:query')")
    public Map<String, Object> getShipVoyageList(TDisCloseSailSearchDTO searchDTO) {
//    	final String methodName = "TDisCloseSailController:getList";
//		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        List<Map<String,Object>> list = tDisCloseSailService.getShipVoyageList(searchDTO);

//        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    @PreAuthorize("hasAuthority('dispatch:closeSail:query')")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {
//        final String methodName = "TDisCloseSailController:getDetail";
//		LOGGER.enter(methodName + "[start]", "id:" + id);

        TDisCloseSailDTO result = tDisCloseSailService.getDetail(id);

//        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tDisCloseSailDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('dispatch:closeSail:add')")
    public Map<String, Object> add(@RequestBody TDisCloseSailDTO tDisCloseSailDTO) {
//        final String methodName = "TDisCloseSailController:add";
//		LOGGER.enter(methodName + "[start]", "tDisCloseSailDTO:" +  tDisCloseSailDTO);

        boolean flag = tDisCloseSailService.doSave(tDisCloseSailDTO);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param tDisCloseSailDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('dispatch:closeSail:update')")
    public Map<String, Object> update(@RequestBody TDisCloseSailDTO tDisCloseSailDTO) {
//        final String methodName = "TDisCloseSailController:update";
//		LOGGER.enter(methodName + "[start]", "tDisCloseSailDTO:" +  tDisCloseSailDTO);

        boolean flag = tDisCloseSailService.doSave(tDisCloseSailDTO);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('dispatch:closeSail:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
//        final String methodName = "TDisCloseSailController:deleteById";
//		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tDisCloseSailService.deleteById(id);

//        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }


    /**
     * 导出
     *
     * @param searchDTO
     * @param response
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('dispatch:closeSail:query')")
    public void export(TDisCloseSailSearchDTO searchDTO, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "封航记录");
        try {
            byte[] bytes = tDisCloseSailService.export(searchDTO);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

}



