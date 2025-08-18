package com.yy.ppm.business.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.reCargoName.ReCargoNameDTO;
import com.yy.ppm.business.service.ReCargoNameService;
import com.yy.ppm.master.bean.dto.MCargoCategorySearchDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author rzg
 * @version 1.0.0
 * @ClassName (TStdProcessStandardSystem)Controller
 * @Description
 * @createTime 2023年08月01日 14:27:00
 */
@RestController
@RequestMapping("/api/v1/internal/reCargoNameController")
public class ReCargoNameController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(ReCargoNameController.class);

    @Autowired
    private ReCargoNameService reCargoNameService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('reCargoName:system:query')")
    public Map<String, Object> getList(MCargoSearchDTO searchDTO) {

        Pages<MCargoDTO> pages = reCargoNameService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 修改
     * @param reCargoNameDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('reCargoName:system:update')")
    public Map<String, Object> update(@RequestBody ReCargoNameDTO reCargoNameDTO) {

        reCargoNameService.update(reCargoNameDTO);

        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

}

