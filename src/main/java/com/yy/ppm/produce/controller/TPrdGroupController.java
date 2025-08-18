package com.yy.ppm.produce.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryExamineDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupDetailPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import com.yy.ppm.produce.service.TPrdGroupService;
import com.yy.ppm.produce.service.TPrdSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.yy.common.util.str.StringUtil.getString;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-12 10:01
 */
@RestController
@RequestMapping("/api/external/group")
@Validated
public class TPrdGroupController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TPrdGroupController.class);

    @Autowired
    private TPrdGroupService tPrdGroupService;


    @Autowired
    private CommonService commonService;

    /**
     * 分组列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listGroup")
    @PreAuthorize("hasAuthority('master:groupInfo:query')")
    public Map<String, Object> listGroup(GroupQueryDTO query, PageParameter parameter) {
        Pages<TPrdGroupPO> result = tPrdGroupService.listGroup(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 分组列表(不分页)
     *
     * @param query
     * @return
     */
    @GetMapping("/listGroupNo")
    @PreAuthorize("hasAuthority('master:groupInfo:query')")
    public Map<String, Object> listGroupNo(GroupQueryDTO query) {
        List<TPrdGroupPO> result = tPrdGroupService.listGroupNo(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 新增/修改
     *
     * @param tPrdGroupPO
     * @return
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('master:groupInfo:add')")
    public Map<String, Object> insert(@RequestBody TPrdGroupPO tPrdGroupPO) {
        final String methodName = "insert";
        LOGGER.enter("TPrdGroupController:" + methodName + "[start]", "tPrdGroupPO:" + tPrdGroupPO);
        // 验证服务名重复
        commonService.isRepeate("T_PRD_GROUP", "GROUP_NAME", tPrdGroupPO.getGroupName(), getString(tPrdGroupPO.getId()), "分组名", null);
        int count = tPrdGroupService.save(tPrdGroupPO);
        LOGGER.exit("TPrdGroupController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("操作成功").toResult(count);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('master:groupInfo:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") String id) {
        final String methodName = "deleteById";
        LOGGER.enter("TPrdGroupController:" + methodName + "[start]", "id:" + id);
        //删除服务表
        int count = commonService.delete("T_PRD_GROUP", "id", id);
        //删除服务主过程关系表
        count = commonService.delete("T_PRD_GROUP_DETAIL", "GROUP_ID", id);
        LOGGER.exit("TPrdGroupController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }

    /**
     * 根据id获取
     *
     * @return
     */
    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAuthority('master:groupInfo:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "getById";
        LOGGER.enter("TPrdGroupController:" + methodName + "[start]", "id:" + id);
        TPrdGroupPO tPrdGroupPO = tPrdGroupService.getById(id);
        LOGGER.exit("TPrdGroupController:" + methodName + "result:" + tPrdGroupPO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(tPrdGroupPO);
    }

    /**
     * 分组选择
     *
     * @param tPrdGroupPO
     * @return
     */
    @GetMapping("/insertGroup")
    @PreAuthorize("hasAuthority('master:groupInfo:add')")
    public Map<String, Object> insertGroup( TPrdGroupPO tPrdGroupPO) {
        final String methodName = "insert";
        LOGGER.enter("TPrdGroupController:" + methodName + "[start]", "tPrdGroupPO:" + tPrdGroupPO);
        List<TPrdGroupDetailPO> result = tPrdGroupService.insertGroup(tPrdGroupPO);
        LOGGER.exit("TPrdGroupController:" + methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


}
