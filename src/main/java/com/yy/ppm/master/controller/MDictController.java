package com.yy.ppm.master.controller;

import java.util.Map;

import jakarta.annotation.Resource;
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
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.master.bean.dto.MDictTypeDTO;
import com.yy.ppm.master.bean.dto.MDictTypeSearchDTO;
import com.yy.ppm.master.bean.po.MDictTypePO;
import com.yy.ppm.master.service.MDictService;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @Description 字典及字典类型操作controller类
 *
 * @author 孙琦
 * @date 2023-4-26 16:57:35
 */
@RestController
@RequestMapping(value = "/api/internal/dict")
@Validated
@Tag(name = "基础数据.字典管理")
public class MDictController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MDictController.class);

    @Resource
    MDictService dictService;

    /**
     * 新增字典类型
     *
     * @param po  字典类型实体类
     * @return 响应数据
     * @throws Exception
     */
    @PostMapping("/insertDictType")
    @PreAuthorize("hasAuthority('master:dict:insert')")
	@Log(OperateTypeEnum.INSERT)
    public Map<String, Object> insertDictType(@RequestBody MDictTypeDTO po)
            throws Exception {
        final String methodName = "DictController:insertDictType";
        LOGGER.enter(methodName, "新增字典类型[start]");

        dictService.insertOrUpdateDictType(po);

        LOGGER.exit(methodName, "新增字典类型[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }


    /**
     * 根据id查询字典类型
     *
     * @param id  字典实体类
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getDictTypeById/{id}")
    @PreAuthorize("hasAuthority('master:dict:query')")
	@Log(OperateTypeEnum.QUERY)
    public Map<String, Object> getDictTypeById(@PathVariable("id") Long id)
            throws Exception {
        final String methodName = "DictController:getDictTypeById";
        LOGGER.enter(methodName, "根据ID查询字典类型[start]");

        MDictTypePO dictTypePO = dictService.getDictTypeById(id);

        LOGGER.exit(methodName, "根据ID查询字典类型[end]");
        return Response.SUCCESS.newBuilder().toResult(dictTypePO);
    }



    /**
     * 根据ID删除字典类型
     *
     * @param id  字典类型实体类
     * @return 响应数据
     * @throws Exception
     */
    @DeleteMapping("/deleteDictTypeById/{id}")
    @PreAuthorize("hasAuthority('master:dict:delete')")
	@Log(OperateTypeEnum.DELETE)
    public Map<String, Object> deleteDictTypeById(@PathVariable("id") Long id)
            throws Exception {
        final String methodName = "DictController:deleteDictTypeById";
        LOGGER.enter(methodName, "删除字典类型[start]");

        dictService.deleteDictTypeById(id);

        LOGGER.exit(methodName, "删除字典类型[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 修改字典类型
     *
     * @param po  字典类型实体类
     * @return 响应数据
     * @throws Exception
     */
    @PutMapping("/updateDictType")
    @PreAuthorize("hasAuthority('master:dict:update')")
	@Log(OperateTypeEnum.UPDATE)
    public Map<String, Object> updateDictType(@RequestBody MDictTypeDTO po)
            throws Exception {
        final String methodName = "DictController:updateDictType";
        LOGGER.enter(methodName, "修改字典类型[start]");

        dictService.insertOrUpdateDictType(po);

        LOGGER.exit(methodName, "修改字典类型[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }


    /**
     * 查询字典类型列表
     *
     * @param mDictTypeSearchDTO
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getAllDictTypeList")
    @PreAuthorize("hasAuthority('master:dict:query')")
	@Log(OperateTypeEnum.QUERY)
    public Map<String, Object> getDictTypeList(MDictTypeSearchDTO mDictTypeSearchDTO)
            throws Exception {
        final String methodName = "DictController:getAllDictTypeList";
        LOGGER.enter(methodName, "查询字典列表[start]");

        Pages<MDictTypeDTO> list = dictService.getDictTypeList(mDictTypeSearchDTO);

        LOGGER.exit(methodName, "查询字典列表[end]");
        return Response.SUCCESS.newBuilder().toResult(list);
    }


//字典类型操作↑
//字典操作↓

    /**
     * 查询字典列表
     *
     * @param po  字典实体类
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getDictList")
    @PreAuthorize("hasAuthority('master:dict:query')")
	@Log(OperateTypeEnum.QUERY)
    public Map<String, Object> getDictList(PageParameter pageParameter, MDictDataDTO po)
            throws Exception {
        final String methodName = "DictController:getDictList";
        LOGGER.enter(methodName, "查询字典[start]");

        Pages<MDictDataDTO> list = dictService.getDictList(pageParameter,po);

        LOGGER.exit(methodName, "查询字典[end]");
        return Response.SUCCESS.newBuilder().toResult(list);
    }

    /**
     * 新增字典
     *
     * @param po  字典类型实体类
     * @return 响应数据
     * @throws Exception
     */
    @PostMapping("/insertDict")
    @PreAuthorize("hasAuthority('master:dict:insert')")
	@Log(OperateTypeEnum.INSERT)
    public Map<String, Object> insertDict(@RequestBody MDictDataDTO po)
            throws Exception {
        final String methodName = "DictController:insertDict";
        LOGGER.enter(methodName, "新增字典[start]");

        dictService.insertOrUpdateDict(po);

        LOGGER.exit(methodName, "新增字典[end]");
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 修改字典
     *
     * @param po  字典类型实体类
     * @return 响应数据
     * @throws Exception
     */
    @PutMapping("/updateDict")
    @PreAuthorize("hasAuthority('master:dict:update')")
	@Log(OperateTypeEnum.UPDATE)
    public Map<String, Object> updateDict(@RequestBody MDictDataDTO po)
            throws Exception {
        final String methodName = "DictController:updateDict";
        LOGGER.enter(methodName, "修改字典[start]");

        dictService.insertOrUpdateDict(po);

        LOGGER.exit(methodName, "修改字典[end]");
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 根据类型查询字典列表
     *
     * @param typeCd  字典类型
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getDictListByType")
    @PreAuthorize("hasAuthority('master:dict:query')")
	@Log(OperateTypeEnum.QUERY)
    public Map<String, Object> getDictListByType(PageParameter pageParameter,String typeCd)
            throws Exception {
        final String methodName = "DictController:getDictListByType";
        LOGGER.enter(methodName, "根据类型查询字典[start]");

        Pages<MDictDataDTO> list = dictService.getDictListByType(pageParameter,typeCd);

        LOGGER.exit(methodName, "根据类型查询字典[end]");
        return Response.SUCCESS.newBuilder().toResult(list);
    }

    /**
     * 根据ID删除字典
     *
     * @param id  字典类型实体类
     * @return 响应数据
     * @throws Exception
     */
    @DeleteMapping("/deleteDictById/{id}")
    @PreAuthorize("hasAuthority('master:dict:delete')")
	@Log(OperateTypeEnum.DELETE)
    public Map<String, Object> deleteDictById(@PathVariable("id") Long id)
            throws Exception {
        final String methodName = "DictController:deleteDictById";
        LOGGER.enter(methodName, "根据id删除字典[start]");

        dictService.deleteDictById(id);

        LOGGER.exit(methodName, "根据id删除字典[end]");
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }


    /**
     * 根据id查询字典
     *
     * @param id  字典实体类
     * @return 响应数据
     * @throws Exception
     */
    @GetMapping("/getDictById/{id}")
    @PreAuthorize("hasAuthority('master:dict:query')")
	@Log(OperateTypeEnum.QUERY)
    public Map<String, Object> getDictById(@PathVariable("id")Long id)
            throws Exception {
        final String methodName = "DictController:getDictByType";
        LOGGER.enter(methodName, "根据ID查询字典[start]");

        MDictDataDTO list = dictService.getDictById(id);

        LOGGER.exit(methodName, "根据ID查询字典[end]");
        return Response.SUCCESS.newBuilder().toResult(list);
    }

}
