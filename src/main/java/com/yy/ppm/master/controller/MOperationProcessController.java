package com.yy.ppm.master.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.validate.AddGroup;
import com.yy.common.validate.EditGroup;
import com.yy.framework.annotation.Log;
import com.yy.ppm.master.bean.dto.MOperationProcessDTO;
import com.yy.ppm.master.bean.dto.MOperationSubProcessDTO;
import com.yy.ppm.master.bean.po.MOperationProcessPO;
import com.yy.ppm.master.bean.po.MOperationSubProcessPO;
import com.yy.ppm.master.service.MOperationProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/internal/operationprocess")
public class MOperationProcessController {

    private static final MicroLogger LOGGER = new MicroLogger(MCargoController.class);
    private final MOperationProcessService operationProcessService;

    /**
     * 子过程查询
     */
    @GetMapping("/listsubprocess")
    @PreAuthorize("hasAuthority('master:process:list')")
    @Log(OperateTypeEnum.QUERY)
    public Map<String, Object> listSubProcess(String processCode, PageParameter pageQuery, String processName) {
        final String methodName = "MOperationProcessController: listSubProcess";
        LOGGER.enter(methodName + "[start]", "processCode:" +
                processCode+",processName:"+processName);

        Pages<MOperationSubProcessDTO> result = operationProcessService.listSubProcess(processCode, pageQuery, processName);

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 作业过程查询
     */
    @GetMapping("/listprocess")
    @PreAuthorize("hasAuthority('master:process:listprocess')")
    @Log(OperateTypeEnum.QUERY)
    public Map<String, Object>  listProcess(PageParameter pageQuery, String name) {
        final String methodName = "MOperationProcessController: listProcess";
        LOGGER.enter(methodName + "[start]", "name:" +
                name);

        Pages<MOperationProcessDTO> result = operationProcessService.listOperationProcess(pageQuery, name);

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据id查询作业过程
     */
    @GetMapping("/selectone")
    @PreAuthorize("hasAuthority('master:process:selectone')")
    @Log(OperateTypeEnum.QUERY)
    public Map<String, Object> selectOneById(Long id) {
        final String methodName = "MOperationProcessController: selectone";
        LOGGER.enter(methodName + "[start]", "id" + id);

        MOperationProcessDTO result = operationProcessService.selectOneById(id);

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 根据id查询某个子作业过程
     */
    @GetMapping("/selectonesub")
    @PreAuthorize("hasAuthority('master:process:selectonesub')")
    @Log(OperateTypeEnum.QUERY)
    public Map<String, Object> selectOneSubById(Long id) {
        final String methodName = "MOperationProcessController: selectOneSubById";
        LOGGER.enter(methodName + "[start]", "id" + id);

        MOperationSubProcessPO result = operationProcessService.selectOneSubById(id);

        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新增作业过程
     */
    @PostMapping("/addprocess")
    @PreAuthorize("hasAuthority('master:process:addprocess')")
    public Map<String, Object> addOperationProcess(@Validated(AddGroup.class) @RequestBody MOperationProcessPO bo, BindingResult result) {
        final String methodName = "MOperationProcessController: addOperationProcess";
        LOGGER.enter(methodName + "[start]", "bo" + bo);

        if(result.hasErrors()){
            StringBuffer buffer = new StringBuffer();
            result.getAllErrors().forEach(error->{
                buffer.append(error.getDefaultMessage()).append(";");
            });

           return Response.FAIL.newBuilder().out(buffer.toString()).toResult();
        }

        operationProcessService.insertByBo(bo);

        LOGGER.exit(methodName + "新增成功");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 修改作业过程
     */
    @PutMapping("/updateprocess")
    @PreAuthorize("hasAuthority('master:process:updateprocess')")
    public Map<String, Object> updateOperationProcess(@Validated(EditGroup.class) @RequestBody MOperationProcessPO bo, BindingResult result) {
        final String methodName = "MOperationProcessController: updateOperationProcess";
        LOGGER.enter(methodName + "[start]", "bo" + bo);

        if(result.hasErrors()){
            StringBuffer buffer = new StringBuffer();
            result.getAllErrors().forEach(error->{
                buffer.append(error.getDefaultMessage()).append(";");
            });
            return Response.FAIL.newBuilder().out(buffer.toString()).toResult();
        }

        operationProcessService.updateProcess(bo);

        LOGGER.exit(methodName + "修改成功");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除作业过程
     */
    @DeleteMapping("/deleteprocess/{ids}")
    @PreAuthorize("hasAuthority('master:process:deleteprocess')")
    public Map<String, Object> removeOperationProcess(@PathVariable List<Long> ids) {
        final String methodName = "MOperationProcessController: removeOperationProcess";
        LOGGER.enter(methodName + "[start]", "ids" + ids);

        int i = operationProcessService.deleteById(ids);

        if(i==1){
            LOGGER.exit(methodName + "删除成功");
            return Response.SUCCESS.newBuilder().out("删除成功").toResult();
        }else if(i==0){
            LOGGER.exit(methodName + "当前过程下存在作业工艺，无法删除");
            return Response.FAIL.newBuilder().out("当前过程下存在作业工艺，无法删除").toResult();
        }else{
            LOGGER.exit(methodName + "当前过程下存在子过程，无法删除");
            return Response.FAIL.newBuilder().out("当前过程下存在子过程，无法删除").toResult();
        }
    }

    /**
     * 新增子作业过程
     */
    @PostMapping("/addsubprocess")
    @PreAuthorize("hasAuthority('master:process:addsubprocess')")
    public Map<String, Object> addSubProcess(@Validated(AddGroup.class) @RequestBody MOperationSubProcessPO bo, BindingResult result) {
        final String methodName = "MOperationProcessController: addSubProcess";
        LOGGER.enter(methodName + "[start]", "bo" + bo);

        if(result.hasErrors()){
            StringBuffer buffer = new StringBuffer();
            result.getAllErrors().forEach(error->{
                buffer.append(error.getDefaultMessage()).append(";");
            });
            return Response.SUCCESS.newBuilder().out(buffer.toString()).toResult();
        }

        operationProcessService.insertSub(bo);

        LOGGER.exit(methodName + "新增成功");
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }
    /**
     * 修改子过程
     */
    @PutMapping("/updatesubprocess")
    @PreAuthorize("hasAuthority('master:process:updatesubprocess')")
    public Map<String, Object> updateSubOperationProcess(@Validated(EditGroup.class) @RequestBody MOperationSubProcessPO bo, BindingResult result) {
        final String methodName = "MOperationProcessController: updateSubOperationProcess";
        LOGGER.enter(methodName + "[start]", "bo" + bo);

        if(result.hasErrors()){
            StringBuffer buffer = new StringBuffer();
            result.getAllErrors().forEach(error->{
                buffer.append(error.getDefaultMessage()).append(";");
            });
            return Response.SUCCESS.newBuilder().out(buffer.toString()).toResult();
        }
        operationProcessService.updateSubProcess(bo);

        LOGGER.exit(methodName + "修改成功");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除子过程
     */
    @DeleteMapping("/deletesubprocess/{id}")
    @PreAuthorize("hasAuthority('master:process:deletesubprocess')")
    public Map<String, Object> removeSubOperationProcess(@PathVariable Long id) {
        final String methodName = "MOperationProcessController: removeSubOperationProcess";
        LOGGER.enter(methodName + "[start]", "id" + id);

        int i = operationProcessService.deleteSubById(id);
        if(i==1){
            LOGGER.exit(methodName + "删除成功");
            return Response.SUCCESS.newBuilder().out("删除成功").toResult();
        }else{
            LOGGER.exit(methodName + "删除失败");
            return Response.FAIL.newBuilder().out("删除失败").toResult();
        }
    }

    /**
     * 作业过程查询(不分页)
     */
    @GetMapping("/selectprocess")
    @PreAuthorize("hasAuthority('master:process:selectprocess')")
    public Map<String, Object> selectProcess(String name) {
        final String methodName = "MOperationProcessController: selectProcess";
        LOGGER.enter(methodName + "[start]", "name" + name);

        List<MOperationProcessPO> resultList = operationProcessService.selectOperationProcess(name);

        LOGGER.exit(methodName + "result:" + resultList);
        return Response.SUCCESS.newBuilder().toResult(resultList);
    }

    /**
     * 查询所有作业过程及子过程
     */
    @GetMapping("/selectall")
    @PreAuthorize("hasAuthority('master:process:selectall')")
    public Map<String, Object> selectAll(PageParameter pageQuery, String name) {
        final String methodName = "MOperationProcessController: selectAll";
        LOGGER.enter(methodName + "[start]", "name" + name);

        Pages<MOperationProcessDTO> result = operationProcessService.selectAll(pageQuery,name);

        LOGGER.exit(methodName + "result:" + result);
        return  Response.SUCCESS.newBuilder().toResult(result);
    }
}
