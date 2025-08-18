package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.StorageYardDTO;
import com.yy.ppm.master.service.StorageYardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * 库场
 */
@RestController
@RequestMapping(value = "/api/external/storageyard")
@Validated
public class StorageYardController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(StorageYardController.class);
    /**
     * 服务对象
     */
    @Autowired
    private StorageYardService storageYardService;

    @Autowired
    private CommonService commonService;

    /**
     * 根据id获取菜单
     *
     * @return
     */
    @GetMapping("/getbyid/{id}")
    @PreAuthorize("hasAuthority('master:storageStack:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "StorageYardController.getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        StorageYardDTO storageYardDTO = storageYardService.getById(id);

        LOGGER.exit(methodName + "result:" + storageYardDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(storageYardDTO);
    }

    /**
     * 根据parentid获取菜单
     *
     * @return
     */
    @GetMapping("/getbyparentid/{parentid}")
    @PreAuthorize("hasAuthority('master:storageStack:query')")
    public Map<String, Object> getByParentId(@PathVariable("parentid") long parentId) {
        final String methodName = "StorageYardController.getByParentId";
        LOGGER.enter(methodName + "[start]", "parentId:" + parentId);

        List<StorageYardDTO> resultList = storageYardService.getByParentId(parentId);

        LOGGER.exit(methodName + "result:" + resultList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 新增
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('master:storageStack:insert')")
    public Map<String, Object> insert(@RequestBody StorageYardDTO storageYardDTO) {
        final String methodName = "StorageYardController.insert";
        LOGGER.enter(methodName + "[start]", "storageYardDTO:" + storageYardDTO);

        // 名称重复
        Long tempId = storageYardDTO.getParentId() == null ? -1L : storageYardDTO.getParentId();
        List<CheckDTO> kayValue = new ArrayList<>();
        CheckDTO checkDTO = new CheckDTO();
        checkDTO.setKey("PARENT_ID");
        checkDTO.setValue(tempId);
        kayValue.add(checkDTO);
        commonService.isRepeate("M_STORAGE_YARD", "STORAGE_YARD_NM", storageYardDTO.getStorageYardNm(), getString(storageYardDTO.getId()), "名称", kayValue, "PARENT_ID = " + tempId);

        long id = storageYardService.save(storageYardDTO);

        LOGGER.exit(methodName + "result:" + id);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(id);
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:storageStack:update')")
    public Map<String, Object> update(@RequestBody StorageYardDTO storageYardDTO) {

        final String methodName = "StorageYardController:update";
        LOGGER.enter(methodName + "[start]", "storageYardDTO:" + storageYardDTO);

        // 名称重复
        Long tempId = storageYardDTO.getParentId() == null ? -1L : storageYardDTO.getParentId();
        commonService.isRepeate("M_STORAGE_YARD", "STORAGE_YARD_NM", storageYardDTO.getStorageYardNm(), getString(storageYardDTO.getId()), "名称", null, "PARENT_ID = " + tempId);

        long id = storageYardService.save(storageYardDTO);

        LOGGER.exit(methodName + "result:" + id);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(id);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deletebyid/{id}")
    @PreAuthorize("hasAuthority('master:storageStack:delete')")
    public Map<String, Object> deletebyid(@PathVariable("id") Long id) {
        final String methodName = "StorageYardController:deletebyid";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        int count = storageYardService.deleteById(id);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }

}
