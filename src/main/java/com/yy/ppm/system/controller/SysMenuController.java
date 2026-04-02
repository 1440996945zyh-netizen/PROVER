package com.yy.ppm.system.controller;


import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.ppm.system.bean.dto.SysMenuDTO;
import com.yy.ppm.system.bean.dto.SysMenuSearchDTO;
import com.yy.ppm.system.bean.dto.TreeSelectDTO;
import com.yy.ppm.system.service.SysMenuService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 菜单(SysMenu)表控制层
 *
 * @author 张超
 * @date 2021-02-26 15:44:40
 */
@RestController
@RequestMapping(value = "/api/internal/sysmenu")
@Validated
public class SysMenuController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(SysMenuController.class);
    /**
     * 服务对象
     */
    private final SysMenuService sysMenuService;

    public SysMenuController(SysMenuService sysMenuService){
        this.sysMenuService = sysMenuService;
    }

    /**
     * 根据parentid获取菜单
     *
     * @return
     */
    @GetMapping("/getbyparentgid/{parentgid}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Map<String, Object> getByParentGid(@PathVariable("parentgid") long parentGid) {
        final String methodName = "getByParentGid";
        LOGGER.enter("SysMenuController:" + methodName + "[start]", "parentGid:" + parentGid);

        List<SysMenuDTO> resultList = sysMenuService.getByParentGid(parentGid);

        LOGGER.exit("SysMenuController:" + methodName + "result:" + resultList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(resultList);
    }

    /**
     * 获取菜单列表*/
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Map<String, Object> list(SysMenuSearchDTO sysMenuSearchDTO){
        List<SysMenuDTO> menus = sysMenuService.selectMenuList(sysMenuSearchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(menus);
    }

    /**
     * 获取菜单列表App*/
    @GetMapping("/listApp")
    public Map<String, Object> listApp(SysMenuSearchDTO sysMenuSearchDTO){
        List<SysMenuDTO> menus = sysMenuService.listApp(sysMenuSearchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(menus);
    }

    /**
     * 获取菜单列表小程序*/
    @GetMapping("/listApplet")
    public Map<String, Object> listApplet(SysMenuSearchDTO sysMenuSearchDTO){
        List<SysMenuDTO> menus = sysMenuService.listApplet(sysMenuSearchDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(menus);
    }

    /**
     * 获取菜单列表*/
    @GetMapping("/getDetailById/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public Map<String, Object> getDetailById(@PathVariable("id") Long id){
        SysMenuDTO sysMenuDTO = sysMenuService.getDetailById(id);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysMenuDTO);
    }
    /**
     * 新增
     *
     * @param sysMenuDTO
     * @return
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('system:menu:insert')")
    public Map<String, Object> insert(@RequestBody SysMenuDTO sysMenuDTO) {
        final String methodName = "insert";
        LOGGER.enter("SysMenuController:" + methodName + "[start]", "sysMenuDTO:" + sysMenuDTO);

        long gid = sysMenuService.save(sysMenuDTO);

        LOGGER.exit("SysMenuController:" + methodName + "result:" + gid);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(gid);
    }

    /**
     * 修改
     *
     * @param sysMenuDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('system:menu:update')")
    public Map<String, Object> update(@RequestBody SysMenuDTO sysMenuDTO) {

        final String methodName = "update";
        LOGGER.enter("SysMenuController:" + methodName + "[start]", "sysMenuDTO:" + sysMenuDTO);

        long gid = sysMenuService.save(sysMenuDTO);

        LOGGER.exit("SysMenuController:" + methodName + "result:" + gid);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(gid);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteById/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "deleteById";
        LOGGER.enter("SysMenuController:" + methodName + "[start]", "id:" + id);

        int count = sysMenuService.deleteById(id);

        LOGGER.exit("SysMenuController:" + methodName + "result:" + count);

        return Response.SUCCESS.newBuilder().out(count > 0 ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 获取菜单下拉框数据源*/
    @GetMapping("/getSelectTree")
    public Map<String, Object> getSelectTree(){
        List<TreeSelectDTO> list = sysMenuService.getTreeSelect();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }
    /**
     * 查询所有的目录、菜单
     * */
    @GetMapping("/getContentsMenu")
    public Map<String, Object> getContentsMenu(){
        List<SysMenuDTO> list = sysMenuService.getContentsMenu();
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }
}
