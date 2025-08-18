package com.yy.ppm.produce.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.framework.annotation.Log;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.produce.bean.dto.CompanyClassResDTO;
import com.yy.ppm.produce.bean.dto.TPrdAttendanceDTO;
import com.yy.ppm.produce.bean.po.TPrdAttendanceUserPO;
import com.yy.ppm.produce.service.TPrdAttendanceService;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;

/**
 * @ClassName 出勤点名Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年08月15日
 */
@RestController
@RequestMapping("/api/v1/internal/tPrdAttendance")
@Validated
@Tag(name = "生产作业.出勤点名")
public class TPrdAttendanceController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TPrdAttendanceController.class);

    @Autowired
    private TPrdAttendanceService tPrdAttendanceService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getAttendanceList")
    public Map<String, Object> getAttendanceList(TPrdAttendanceDTO searchDTO) {

        final String methodName = "TPrdAttendanceController:getAttendanceList";

        List<TPrdAttendanceDTO> pages = tPrdAttendanceService.getList(searchDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 根据id获取获取考勤人员信息
     * @param id
     * @return
     */
    @GetMapping("/getAttendanceById")
    public Map<String, Object> getAttendanceById(@RequestParam("id")Long id) {

        final String methodName = "TPrdAttendanceController:getAttendanceById";

        if (id ==null){
            throw new RuntimeException("参数为空");
        }
        TPrdAttendanceDTO tPrdAttendanceDTO = new TPrdAttendanceDTO();
        tPrdAttendanceDTO.setId(id);

        TPrdAttendanceDTO attendanceById = tPrdAttendanceService.getAttendanceById(tPrdAttendanceDTO);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(attendanceById);
    }

    /**
     * 新增出勤点名
     * @return
     */
    @PostMapping("/insertAttendance")
    @Validated
    @Log(title ="新增出勤点名",value = OperateTypeEnum.INSERT)
    public Map<String, Object> insertAttendance(@RequestBody TPrdAttendanceDTO attendanceDTO) {
        final String methodName = "TPrdAttendanceController:insertAttendance";
        LOGGER.enter(methodName + "[start]", "attendanceDTO:" +  attendanceDTO);

        boolean flag = tPrdAttendanceService.insert(attendanceDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改出勤点名
     * @param attendanceDTO
     * @return
     */
    @PutMapping("/updateAttendance")
    @Validated
    @Log(title ="修改出勤点名",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> updateAttendance(@RequestBody TPrdAttendanceDTO attendanceDTO) {
        final String methodName = "TPrdAttendanceController:updateAttendance";
        LOGGER.enter(methodName + "[start]", "attendanceDTO:" +  attendanceDTO);

        boolean flag = tPrdAttendanceService.updateAttendance(attendanceDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 删除出勤点名信息
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @Validated
    @PreAuthorize("hasAuthority('business:contract:delete')")
    @Log(title ="删除出勤点名",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> deleteById(@Valid @NotNull(message = "id不能为空！") @PathVariable("id") Long id) {
        final String methodName = "TPrdAttendanceController:deleteById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        int count = tPrdAttendanceService.deleteById(id);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }

    /**
     * 根据id获取获取考勤人员信息（修改用）
     * @param deptId
     * @return
     */
    @GetMapping("/getAttendanceUserByDeptId")
    public Map<String, Object> getAttendanceUserByDeptId(@RequestParam("deptId")Long deptId) {

        final String methodName = "TPrdAttendanceController:getAttendanceUserByDeptId";
        LOGGER.enter(methodName + "[start]", "deptId:" + deptId);

        if (deptId ==null){
            throw new RuntimeException("参数为空");
        }

        List<TPrdAttendanceUserPO> attendaceUserByDeptId = tPrdAttendanceService.getAttendaceUserByDeptId(deptId);

        LOGGER.exit( methodName + "result:" + attendaceUserByDeptId);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(attendaceUserByDeptId);
    }

    /**
     * 根据id获取获取人员信息（新增用）
     * @param deptId
     * @return
     */
    @GetMapping("/getUserByDeptId")
    public Map<String, Object> getUserByDeptId(@RequestParam("deptId") Long deptId) {

        final String methodName = "TPrdAttendanceController:getUserList";
        LOGGER.enter(methodName + "[start]", "deptId:" + deptId);

//        if (deptId == null){
//            throw new RuntimeException("参数为空");
//        }

        List<SysUserDTO> users = tPrdAttendanceService.getUserByDeptId(deptId);

        LOGGER.exit( methodName + "result:" + users);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(users);
    }

    /**
     * 根据当前登录人获取当前登录人下部门的所有的人员信息
     * @return
     */
    @GetMapping("/getUserSelect")
    public Map<String, Object> getUserNew() {

        final String methodName = "TPrdAttendanceController:getUserNew";

//        if (deptId == null){
//            throw new RuntimeException("参数为空");
//        }

        List<SysUserDTO> users = tPrdAttendanceService.getUserNew();

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(users);
    }

    /**
     * 根据作业公司查作业工班
     * @param companyId
     * @return
     */
    @GetMapping("/getDeptByCompany")
    public Map<String, Object> getDeptByCompany(@RequestParam("companyId") Long companyId) {

        final String methodName = "TPrdAttendanceController:getDeptByCompany";

//        if (companyId ==null){
//            throw new RuntimeException("参数为空");
//        }

        List<SysDeptDTO> deptByCompany = tPrdAttendanceService.getDeptByCompany(companyId);


        return Response.SUCCESS.newBuilder().out("查询成功").toResult(deptByCompany);
    }

    @GetMapping("/getCompanyClass")
    public Map<String, Object> getCompanyClass() {

        final String methodName = "TPrdAttendanceController:getCompanyClass";

        CompanyClassResDTO result = tPrdAttendanceService.getCompanyClass();

        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @GetMapping("/getCompanyDeptClass")
    public Map<String, Object> getCompanyDeptClass() {

        final String methodName = "TPrdAttendanceController:getCompanyClass";

        CompanyClassResDTO result = tPrdAttendanceService.getCompanyDeptClass();

        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
