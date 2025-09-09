package com.yy.ppm.system.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.framework.config.MinioConfig;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.system.bean.dto.SysVersionControlDTO;
import com.yy.ppm.system.bean.po.SysVersionControlPO;
import com.yy.ppm.system.service.SysVersionControlService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * APP版本控制
 * @author zcc
 */
@RestController
@RequestMapping(value = "/api/internal/sysVersion")
@Tag(name = "系统管理.APP版本控制")
@Validated
public class SysVersionControlController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(SysVersionControlController.class);

    @Autowired
    private SysVersionControlService sysVersionControlService;

    @Resource
    private MinioConfig minIoConfig;

    /**
     * 列表查询
     * @param sysUserSearchDTO
     * @return
     */
	@GetMapping("/getlist")
	@PreAuthorize("hasAuthority('system:version:query')")
	@Log(OperateTypeEnum.QUERY)
	public Map<String, Object> getList(SysVersionControlDTO sysVersionControlDTO) {
		final String methodName = "SysVersionControlController:getList";
		LOGGER.enter(methodName + "[start]", "sysVersionControlDTO:" + sysVersionControlDTO);

		Pages<SysVersionControlDTO> sysVersionList = sysVersionControlService.getList(sysVersionControlDTO);

		LOGGER.exit( methodName + "result:" + sysVersionList);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysVersionList);
	}

	/**
	 * 根据id获取版本信息
	 * @param id
	 * @return
	 */
	@GetMapping("/getbyid/{id}")
	@PreAuthorize("hasAuthority('system:version:query')")
	public Map<String, Object> getById(@PathVariable("id") Long id) {
		final String methodName = "SysVersionControlController:getById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

		SysVersionControlDTO sysVersionControlDTO = sysVersionControlService.getById(id);

		LOGGER.exit(methodName + "result:" + sysVersionControlDTO);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysVersionControlDTO);
	}

	/**
	 * 保存版本信息
	 * @param sysVersionControlDTO
	 * @return
	 */
	@PostMapping("/insert")
	@PreAuthorize("hasAuthority('system:version:add')")
	public Map<String, Object> insert(@RequestBody SysVersionControlPO sysVersionControlPO) {
		final String methodName = "SysVersionControlController:insert";
		LOGGER.enter(methodName + "[start]", "sysVersionControlPO:" + sysVersionControlPO);

		int count = sysVersionControlService.save(sysVersionControlPO);

		LOGGER.exit(methodName + "result:" + count);
		return Response.SUCCESS.newBuilder().out(count > 0 ? "新增成功" : "新增失败").toResult(count);
	}

	/**
	 * 修改版本信息
	 * @param sysVersionControlDTO
	 * @return
	 */
	@PutMapping("/update")
	@PreAuthorize("hasAuthority('system:version:update')")
	public Map<String, Object> update(@RequestBody SysVersionControlPO sysVersionControlPO) {

		final String methodName = "SysVersionControlController:update";
		LOGGER.enter(methodName + "[start]", "sysVersionControlPO:" + sysVersionControlPO);

		int count = sysVersionControlService.save(sysVersionControlPO);

		LOGGER.exit(methodName + "result:" + count);
		return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
	}

	/**
	 * 修改版本信息状态
	 * @param sysVersionControlDTO
	 * @return
	 */
	@PutMapping("/updateStatus")
	@PreAuthorize("hasAuthority('system:version:update')")
	public Map<String, Object> updateStatus(@RequestBody SysVersionControlPO sysVersionControlPO) {

		final String methodName = "SysVersionControlController:updateStatus";
		LOGGER.enter(methodName + "[start]", "sysVersionControlPO:" + sysVersionControlPO);

		int count = sysVersionControlService.updateStatus(sysVersionControlPO);

		LOGGER.exit(methodName + "result:" + count);
		return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
	}

	/**
	 * 删除
	 * @param idList
	 * @return
	 */
	@DeleteMapping("/deletebyid/{idList}")
	@PreAuthorize("hasAuthority('system:version:delete')")
	public Map<String, Object> deleteById(@PathVariable("idList") List<Long> idList) {

		final String methodName = "SysVersionControlController:deletebyid";
		LOGGER.enter(methodName + "[start]", "idList:" + idList);

		int count = sysVersionControlService.deleteById(idList);

		LOGGER.exit(methodName + "result:" + count);
		return Response.SUCCESS.newBuilder().out("删除失败~").toResult(count);
	}

    /**
     * 获取车载最后版本号
     * @return
     */
    @GetMapping("/getVersion")
    @Validated
    public Map<String, Object> getVersion(@Valid @NotBlank(message = "versionType不能为空！") String versionType) {
        final String methodName = "SysVersionControlController:getVersion";
        LOGGER.enter(methodName + "[start], versionType: " + versionType);

        SysVersionControlPO sysVersionControlPO = sysVersionControlService.getVersion(versionType);

        LOGGER.exit(methodName + "result:" + sysVersionControlPO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysVersionControlPO);
    }

	/**
	 * 获取车载最后版本号
	 * @return
	 */
	@GetMapping("/getWfmacVersion")
	@Validated
	public Map<String, Object> getWfmacVersion(@Valid @NotBlank(message = "versionType不能为空！") String versionType) {
		final String methodName = "SysVersionControlController:getWfmacVersion";
		LOGGER.enter(methodName + "[start], versionType: " + versionType);

		SysVersionControlPO sysVersionControlPO = sysVersionControlService.getWfmacVersion(versionType);

		LOGGER.exit(methodName + "result:" + sysVersionControlPO);
		return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysVersionControlPO);
	}

    /**
     * 跑垛APP更新
     * @param id
     * @param resp
     * @return
     * @throws Exception
     */
    @GetMapping("/downloadRunPileApp")
    @Validated
    public Map<String, Object> downloadRunPileApp(@Valid @NotBlank(message = "附件fileId必填~") String fileId, HttpServletResponse resp) throws Exception {
        final String methodName = "SysVersionControlController:downloadRunPileApp";
        LOGGER.enter(methodName + "[start], fileId: " + fileId);

        //根据fileId获取文件信息
        SysFilePO sysFilePO = sysVersionControlService.getFileById(Long.valueOf(fileId));

        if (null == sysFilePO) {
            LOGGER.warn("不存在的附件~");
            throw new BusinessRuntimeException("不存在的附件~");
        }

        //因为是根据fileId查询，所以最多只有一个文件
        String bucketName = sysFilePO.getFileBucket();
        String path = sysFilePO.getFilePath();
        String saveName = sysFilePO.getFileSaveName();
        byte[] fileByteArray = minIoConfig.getObject(bucketName, path + saveName);
        resp.setHeader("content-disposition",
                "attachment;filename=" + URLEncoder.encode(sysFilePO.getFileName(), "UTF-8"));
        try (OutputStream outputStream = resp.getOutputStream()) {
            outputStream.write(fileByteArray);
        }

        LOGGER.exit(methodName + "result");
        return Response.SUCCESS.newBuilder().toResult();
    }

    /**
     * 车载APP更新
     * @param id
     * @param resp
     * @return
     * @throws Exception
     */
    @GetMapping("/downloadMacApp")
    @Validated
    public Map<String, Object> downloadMacApp(@Valid @NotBlank(message = "附件fileId必填~") String fileId, HttpServletResponse resp) throws Exception {
        final String methodName = "SysVersionControlController:downloadMacApp";
        LOGGER.enter(methodName + "[start], fileId: " + fileId);

        //根据fileId获取文件信息
        SysFilePO sysFilePO = sysVersionControlService.getFileById(Long.valueOf(fileId));

        if (null == sysFilePO) {
            LOGGER.warn("不存在的附件~");
            throw new BusinessRuntimeException("不存在的附件~");
        }

        //因为是根据fileId查询，所以最多只有一个文件
        String bucketName = sysFilePO.getFileBucket();
        String path = sysFilePO.getFilePath();
        String saveName = sysFilePO.getFileSaveName();
        byte[] fileByteArray = minIoConfig.getObject(bucketName, path + saveName);
        resp.setHeader("content-disposition",
                "attachment;filename=" + URLEncoder.encode(sysFilePO.getFileName(), "UTF-8"));
        try (OutputStream outputStream = resp.getOutputStream()) {
            outputStream.write(fileByteArray);
        }

        LOGGER.exit(methodName + "result");
        return Response.SUCCESS.newBuilder().toResult();
    }

    /**
     * 手持APP更新
     * @param id
     * @param resp
     * @return
     * @throws Exception
     */
    @GetMapping("/downloadApp")
    @Validated
    public Map<String, Object> downloadApp(@Valid @NotBlank(message = "附件fileId必填~") String fileId, HttpServletResponse resp) throws Exception {
        final String methodName = "SysVersionControlController:downloadApp";
        LOGGER.enter(methodName + "[start], fileId: " + fileId);

        //根据fileId获取文件信息
        SysFilePO sysFilePO = sysVersionControlService.getFileById(Long.valueOf(fileId));

        if (null == sysFilePO) {
            LOGGER.warn("不存在的附件~");
            throw new BusinessRuntimeException("不存在的附件~");
        }

        //因为是根据fileId查询，所以最多只有一个文件
        String bucketName = sysFilePO.getFileBucket();
        String path = sysFilePO.getFilePath();
        String saveName = sysFilePO.getFileSaveName();
        byte[] fileByteArray = minIoConfig.getObject(bucketName, path + saveName);
        resp.setHeader("content-disposition",
                "attachment;filename=" + URLEncoder.encode(sysFilePO.getFileName(), "UTF-8"));
        try (OutputStream outputStream = resp.getOutputStream()) {
            outputStream.write(fileByteArray);
        }

        LOGGER.exit(methodName + "result");
        return Response.SUCCESS.newBuilder().toResult();
    }
}
