package com.yy.ppm.master.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.ppm.common.bean.dto.CheckDTO;
import com.yy.ppm.common.enums.AutoNumEnum;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.master.service.MWorkProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * (MWorkProcess)表控制层
 *
 * @author 张超
 * @date 2021-03-10 13:57:27
 */
@RestController
@RequestMapping(value = "/api/external/workprocess")
@Validated
public class MWorkProcessController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MWorkProcessController.class);
    /**
     * 服务对象
     */
    @Autowired
    private MWorkProcessService mWorkProcessService;

    @Autowired
    private CommonService commonService;

    /**
     * 根据实体类筛选数据列表
     *
     * @param mWorkProcessSearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getlist")
    @PreAuthorize("hasAuthority('master:process:query')")
    public Map<String, Object> getList(MWorkProcessSearchDTO mWorkProcessSearchDTO) {
        final String methodName = "MWorkProcessController: getList";
        LOGGER.enter(methodName + "[start]", "sysUserSearchDTO:" + mWorkProcessSearchDTO);

        Pages<MWorkProcessDTO> mWorkProcessList = mWorkProcessService.getList(mWorkProcessSearchDTO);

        LOGGER.exit(methodName + "result:" + mWorkProcessList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mWorkProcessList);
    }

    /**
     * 新增
     *
     * @param mWorkProcessDTO
     * @return
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('master:process:add')")
    public Map<String, Object> insert(@RequestBody MWorkProcessDTO mWorkProcessDTO) {
        final String methodName = "MWorkProcessController: insert";
        LOGGER.enter(methodName + "[start]", "mWorkProcessDTO:" + mWorkProcessDTO);

        // 验证过程名重复
        Long tempId = mWorkProcessDTO.getParentId() == null ? -1L : mWorkProcessDTO.getParentId();
        List<CheckDTO> kayValue = new ArrayList<>();
        CheckDTO checkDTO = new CheckDTO();
        checkDTO.setKey("PARENT_ID");
        checkDTO.setValue(tempId);
        kayValue.add(checkDTO);
        commonService.isRepeate("M_WORK_PROCESS", "PROCESS_NM", mWorkProcessDTO.getProcessNm(), getString(mWorkProcessDTO.getId()), "作业过程名称", kayValue,"作业过程名称", null, "PARENT_ID=-1");

        //获取编号
        String processCd = commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.WORK_PROCESS, null);
        mWorkProcessDTO.setProcessCd(processCd);

        int count = mWorkProcessService.save(mWorkProcessDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
    }

    /**
     * 修改
     *
     * @param mWorkProcessDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:process:update')")
    public Map<String, Object> update(@RequestBody MWorkProcessDTO mWorkProcessDTO) {

        final String methodName = "MWorkProcessController: update";
        LOGGER.enter(methodName + "[start]", "mWorkProcessDTO:" + mWorkProcessDTO);

        // 验证过程名重复
        Long tempId = mWorkProcessDTO.getParentId() == null ? -1L : mWorkProcessDTO.getParentId();
        List<CheckDTO> kayValue = new ArrayList<>();
        CheckDTO checkDTO = new CheckDTO();
        checkDTO.setKey("PARENT_ID");
        checkDTO.setValue(tempId);
        kayValue.add(checkDTO);
        commonService.isRepeate("M_WORK_PROCESS", "PROCESS_NM", mWorkProcessDTO.getProcessNm(), getString(mWorkProcessDTO.getId()), "作业过程名称", kayValue, "PARENT_ID=-1");

        int count = mWorkProcessService.save(mWorkProcessDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 新增子过程
     *
     * @param mWorkProcessDTO
     * @return
     */
    @PostMapping("/insertchildprocess")
    @PreAuthorize("hasAuthority('master:process:add')")
    public Map<String, Object> insertChildProcess(@RequestBody MWorkProcessDTO mWorkProcessDTO) {
        final String methodName = "MWorkProcessController: insertChildProcess";
        LOGGER.enter(methodName + "[start]", "mWorkProcessDTO:" + mWorkProcessDTO);

        // 验证过程名重复
        mWorkProcessService.isRepeateSubProcess(mWorkProcessDTO);

        //获取编号
        String processCd = commonService.getAutoNum(AutoNumEnum.BusinessAutoEnum.SUB_WORK_PROCESS, mWorkProcessDTO.getParentProcessCd());
        mWorkProcessDTO.setProcessCd(processCd);

        int count = mWorkProcessService.save(mWorkProcessDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
    }

    /**
     * 修改子过程
     *
     * @param mWorkProcessDTO
     * @return
     */
    @PutMapping("/updatechildprocess")
    @PreAuthorize("hasAuthority('master:process:update')")
    public Map<String, Object> updateChildProcess(@RequestBody MWorkProcessDTO mWorkProcessDTO) {

        final String methodName = "MWorkProcessController: updateChildProcess";
        LOGGER.enter(methodName + "[start]", "mWorkProcessDTO:" + mWorkProcessDTO);

        // 验证过程名重复
        mWorkProcessService.isRepeateSubProcess(mWorkProcessDTO);

        int count = mWorkProcessService.save(mWorkProcessDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deletebyid/{id}")
    @PreAuthorize("hasAuthority('master:process:delete')")
    public Map<String, Object> deletebyid(@PathVariable("id") String id) {
        final String methodName = "MWorkProcessController: deletebyid";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        int count = commonService.delete("m_work_process", "id", id);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }
}
