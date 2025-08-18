package com.yy.ppm.system.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.system.bean.dto.SysRoleDTO;
import com.yy.ppm.system.bean.dto.SysRoleSearchDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 角色(SysRole)表控制层
 *
 * @author 张超
 * @date 2021-03-02 18:41:35
 */
@RestController
@RequestMapping(value = "/api/internal/sysRole")
public class SysRoleController {
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysRoleController.class);
    /**
     * 服务对象
     */
    @Resource
    private SysRoleService sysRoleService;

    /**
     * 根据实体类筛选数据列表
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Map<String, Object> getList(SysRoleSearchDTO sysRoleSearchDTO) {
        final String methodName = "SysRoleController:getList";
        LOGGER.enter(methodName + "[start]", "sysUserSearchDTO:" + sysRoleSearchDTO);

        Pages<SysRoleDTO> sysRoleList = sysRoleService.getList(sysRoleSearchDTO);

        LOGGER.exit(methodName + "result:" + sysRoleList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysRoleList);
    }

    /**
     * 查询全部角色，不分页
     * @param sysRoleSearchDTO
     * @return
     */
    @GetMapping("/getListNoPage")
    public Map<String, Object> getListNoPage(SysRoleSearchDTO sysRoleSearchDTO) {
        final String methodName = "SysRoleController:getListNoPage";
        LOGGER.enter(methodName + "[start]", "sysUserSearchDTO:" + sysRoleSearchDTO);

        List<SysRoleDTO> sysRoleList = sysRoleService.getListNoPage(sysRoleSearchDTO);

        LOGGER.exit(methodName + "result:" + sysRoleList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysRoleList);
    }

    /**
     * 根据id获取角色
     *
     * @return
     */
    @GetMapping("/getById/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "SysRoleController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        SysRoleDTO sysRoleDTO = sysRoleService.getById(id);

        LOGGER.exit(methodName + "result:" + sysRoleDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysRoleDTO);
    }

    /**
     * 新增
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('system:role:insert')")
    @Log(title ="角色新增",value = OperateTypeEnum.INSERT)
    public Map<String, Object> insert(@RequestBody SysRoleDTO sysRoleDTO) {
        final String methodName = "SysRoleController:insert";
        LOGGER.enter(methodName + "[start]", "sysRoleDTO:" + sysRoleDTO);

        int count = sysRoleService.save(sysRoleDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:role:update')")
    @Log(title ="角色权限更新",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> update(@RequestBody SysRoleDTO sysRoleDTO) {

        final String methodName = "SysRoleController:update";
        LOGGER.enter(methodName + "[start]", "sysRoleDTO:" + sysRoleDTO);

        int count = sysRoleService.save(sysRoleDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 修改状态
     */
    @PutMapping("/changeStatus")
    @PreAuthorize("hasAuthority('system:role:update')")
    @Log(title ="角色修改状态",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> changeStatus(@RequestBody SysRoleDTO sysRoleDTO) {

        final String methodName = "SysRoleController:update";
        LOGGER.enter(methodName + "[start]", "sysRoleDTO:" + sysRoleDTO);

        int count = sysRoleService.changeStatus(sysRoleDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 删除
     */
    @DeleteMapping("/deleteById/{idList}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @Log(title ="角色删除",value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteById(@PathVariable("idList") List<Long> idList) {
        final String methodName = "SysRoleController:deletebyid";
        LOGGER.enter(methodName + "[start]", "idList:" + idList);

        int count = sysRoleService.deleteById(idList);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }

    /**
     * 获取组织架构下拉框数据源
     * */
    @GetMapping("/getDeptTree")
    public Map<String, Object> getDeptTree(@RequestParam(value = "roleId", required = false) Long roleId){
        Map map = sysRoleService.getDeptTree(roleId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(map);
    }


    /**
     * 根据角色id获取菜单下拉列表
     **/
    @GetMapping("/getMenuTree")
    public Map<String, Object> getMenuTree(@RequestParam(value = "roleId", required = false) Long roleId){
        Map map = sysRoleService.getMenuTree(roleId);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(map);
    }


    @PutMapping("/dataScope")
    @PreAuthorize("hasAuthority('system:role:update')")
    public Map<String, Object> dataScope(@RequestBody SysRoleDTO sysRoleDTO) {

        final String methodName = "SysRoleController:dataScope";
        LOGGER.enter(methodName + "[start]", "sysRoleDTO:" + sysRoleDTO);

        int count = sysRoleService.dataScope(sysRoleDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 查询已授权和未授权用户列表
     * @param roleId
     * @return
     */
    @GetMapping("/allocatedOrUnallocatedList")
    public Map<String, Object> allocatedOrUnallocatedList(@RequestParam("roleId") Long roleId, @RequestParam(name = "nameOrAccount" , required = false)  String nameOrAccount, @RequestParam("flag") String flag) {
        final String methodName = "SysRoleController:allocatedOrUnallocatedList";
        LOGGER.enter(methodName + "[start]", "id:" + roleId);
        List<SysUserDTO> list = sysRoleService.allocatedOrUnallocatedList(roleId, nameOrAccount, flag);

        LOGGER.exit(methodName + "result:" + list);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 添加、取消用户授权
     * @param paramMap
     * @return
     */
    @PostMapping("/authUserInsertDelete")
    @PreAuthorize("hasAuthority('system:role:dispatchUser')")
    @Log(title ="添加、取消用户授权",value = OperateTypeEnum.UPDATE)
    public Map<String, Object> authUserInsertDelete(@RequestBody Map paramMap) {
        final String methodName = "SysRoleController:authUserInsertDelete";
        LOGGER.enter(methodName + "[start]", "paramMap:" + paramMap );

        int count = sysRoleService.authUserInsertDelete(paramMap);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    @GetMapping("/listByRoleClass")
    public Map<String,Object> listByRoleClass(String roleClass){
        final String methodName = "SysRoleController:listByRoleClass";
        LOGGER.enter(methodName + "[start]", "roleClass:" + roleClass );
        List<Map<String,Object>> result = sysRoleService.getListByRoleClass(roleClass);
        LOGGER.exit(methodName + "result:" + result);
        return Response.SUCCESS.newBuilder().out("成功").toResult(result);
    }

}
