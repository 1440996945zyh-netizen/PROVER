package com.yy.ppm.master.controller;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.yy.common.util.JasyptUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.system.bean.dto.ProfileDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;
import com.yy.ppm.system.service.SysUserService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 劳务管理
 */
@RestController
@RequestMapping(value = "/api/internal/mlabor")
@Tag(name = "基础数据.劳务管理")
public class MLaborController {


    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MLaborController.class);

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private CommonService baseService;
    @Resource
    private SecurityUtils securityUtils;

    /**
     * 根据实体类筛选数据列表
     *
     * @param sysUserSearchDTO 实体类
     * @return 统一数据封装
     */
    @GetMapping("/getlist")
    @PreAuthorize("hasAuthority('master:labor:query')")
    @Log(OperateTypeEnum.QUERY)
    public Map<String, Object> getList(SysUserSearchDTO sysUserSearchDTO) {
        final String methodName = "SysUserController:getList";
        LOGGER.enter(methodName + "[start]", "sysUserSearchDTO:" + sysUserSearchDTO);

        //是否劳保设置为1
        sysUserSearchDTO.setIsLabor("1");

        Pages<SysUserDTO> sysUserList = sysUserService.getList(sysUserSearchDTO);

        LOGGER.exit( methodName + "result:" + sysUserList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysUserList);
    }

    /**
     * 根据id获取人员信息
     * @return
     */
    @GetMapping("/getbyid/{id}")
    @PreAuthorize("hasAuthority('master:labor:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "SysUserController:getById";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        SysUserDTO sysUserDTO = sysUserService.getById(id);

        LOGGER.exit(methodName + "result:" + sysUserDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(sysUserDTO);
    }

    /**
     * 新增
     * @param sysUserDTO
     * @return
     */
    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('master:labor:update')")
    public Map<String, Object> insert(@RequestBody SysUserDTO sysUserDTO) {
        final String methodName = "SysUserController:insert";
        LOGGER.enter(methodName + "[start]", "sysUserDTO:" + sysUserDTO);

        //劳务始终为1
        sysUserDTO.setIsLabor("1");

        int count = sysUserService.save(sysUserDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "新增成功" : "新增失败").toResult(count);
    }

    /**
     * 修改
     * @param sysUserDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('master:labor:update')")
    public Map<String, Object> update(@RequestBody SysUserDTO sysUserDTO) {

        final String methodName = "SysUserController:update";
        LOGGER.enter(methodName + "[start]", "sysUserDTO:" + sysUserDTO);

        int count = sysUserService.save(sysUserDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
    }

    /**
     * 删除
     * @param idList
     * @return
     */
    @DeleteMapping("/deletebyid/{idList}")
    @PreAuthorize("hasAuthority('master:labor:delete')")
    public Map<String, Object> deleteById(@PathVariable("idList") List<Long> idList) {

        final String methodName = "SysUserController:deletebyid";
        LOGGER.enter(methodName + "[start]", "idList:" + idList);

        int count = sysUserService.deleteById(idList);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除失败~").toResult(count);
    }

    /**
     * 重置密码
     * @param id
     * @return
     */
    @PutMapping("/resetpassword/{id}")
    @PreAuthorize("hasAuthority('master:labor:resetPsd')")
    public Map<String, Object> resetPassword(@PathVariable("id") Long id) {

        final String methodName = "SysUserController:resetPassword";
        LOGGER.enter(methodName + "[start]", "id:" + id);

        int count = sysUserService.resetpassword(id);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("重置密码成功").toResult(count);
    }

    /**
     * 修改手机号
     * @param sysUserDTO
     * @return
     */
    @PutMapping("/updatephone")
    @PreAuthorize("hasAuthority('master:labor:query')")
    public Map<String, Object> updatePhone(SysUserDTO sysUserDTO) {
        final String methodName = "SysUserController:updatePhone";
        LOGGER.enter(methodName + "[start]", "sysUserDTO:" + sysUserDTO);

        // 手机号重复
        baseService.isRepeate("SYS_USER", "MOBILE", sysUserDTO.getMobile(), StringUtil.getString(sysUserDTO.getId()), "", null, " status != '0' ", "手机号已被使用~");

        int count = sysUserService.updatePhone(sysUserDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
    }

    /**
     * 修改邮箱
     * @param sysUserDTO
     * @return
     */
    @PutMapping("/updateemail")
    @PreAuthorize("hasAuthority('master:labor:query')")
    public Map<String, Object> updateEmail(SysUserDTO sysUserDTO) {
        final String methodName = "SysUserController:updateEmail";
        LOGGER.enter(methodName + "[start]", "sysUserDTO:" + sysUserDTO);

        // 手机号重复
        baseService.isRepeate("SYS_USER", "EMAIL", sysUserDTO.getEmail(),  StringUtil.getString(sysUserDTO.getId()), "", null, " status != '0' ", "邮箱已被使用~");

        int count = sysUserService.updateEmail(sysUserDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
    }

    /**
     * 修改密码
     * @param sysUserDTO
     * @return
     */
    @PutMapping("/updatePassword")
    @PreAuthorize("hasAuthority('master:labor:resetPsd')")
    public Map<String, Object> updatePassword(SysUserDTO sysUserDTO) {
        final String methodName = "SysUserController:updatePassword";
        LOGGER.enter(methodName + "[start]", "sysUserDTO:" + sysUserDTO);

        // 旧信息 各自查各自库
        SysUserDTO oldData = sysUserService.getById(securityUtils.getLoginUserId());

        if (!JasyptUtils.decrypt(oldData.getPasswd()).equals(sysUserDTO.getOldPassword())) {
            throw new BusinessRuntimeException("原密码错误~");
        }
        if(!isValid(sysUserDTO.getNewPassword())){
            throw new BusinessRuntimeException("密码必须包含数字、大写字母、小写字母、特殊字符，且密码长度大于8位~");
        }
        // 设置新密码
        oldData.setPasswd(sysUserDTO.getNewPassword());
        int count = sysUserService.updatePassword(oldData);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
    }

    /**
     * 密码由四种元素组成（数字、大写字母、小写字母、特殊字符），且必须包含全部四种元素；密码长度大于等于8个字符。
     */
    public static boolean isValid(String password) {
        // 正则表达式的内容如下:
        // ^(?![0-9A-Za-z]+$)(?![0-9A-Z\W]+$)(?![0-9a-z\W]+$)(?![A-Za-z\W]+$)[0-9A-Za-z~!@#$%^&*()__+`\-={}|[\]\\:";'<>?,./]{8,}$
        // 在 Java 中使用，需要转义；转义后的结果如下。
        String pattern = "^(?![0-9A-Za-z]+$)(?![0-9A-Z\\W]+$)(?![0-9a-z\\W]+$)(?![A-Za-z\\W]+$)[0-9A-Za-z~!@#$%^&*()_+`\\-={}|\\[\\]\\\\:\";'<>?,./]{8,}$";
        return Pattern.matches(pattern, password);
    }

    /**
     * 根据id获取人员信息
     * @return
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('master:labor:query')")
    public Map<String, Object> profile() {
        final String methodName = "SysUserController:profile";
        LOGGER.enter(methodName + "[start]");

        ProfileDTO profileDTO = sysUserService.profile();

        LOGGER.exit(methodName + "result:" + profileDTO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(profileDTO);
    }

    /**
     * 修改邮箱
     * @param sysUserDTO
     * @return
     */
    @PutMapping("/updateStatus")
    @PreAuthorize("hasAuthority('master:labor:updateStatus')")
    public Map<String, Object> updateStatus(@RequestBody SysUserDTO sysUserDTO) {
        final String methodName = "SysUserController:updateStatus";
        LOGGER.enter(methodName + "[start]", "sysUserDTO:" + sysUserDTO);

        int count = sysUserService.updateStatus(sysUserDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out(count > 0 ? "修改成功" : "修改失败").toResult(count);
    }

}
