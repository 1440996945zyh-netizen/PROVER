package com.yy.ppm.master.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MCargoCategoryDTO;
import com.yy.ppm.master.bean.dto.MCargoCategorySearchDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;
import com.yy.ppm.master.bean.po.MCargoPO;
import com.yy.ppm.master.service.MCargoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Stream;

/**
 * (MCargoType)表控制层
 *
 * @author makejava
 * @date 2021-03-08 11:20:41
 */
@RestController
@RequestMapping(value = "/api/internal/mcargo")
@Validated
@Tag(name = "基础数据.货物信息")
public class MCargoController {

    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(MCargoController.class);
    /**
     * 服务对象
     */
    @Autowired
    private MCargoService mCargoService;

    @Autowired
    private CommonService commonService;

    /**
     * 根据实体类筛选数据列表
     *
     * @param mCargoTypeSearchDTO 查询类
     * @return 统一数据封装
     */
    @GetMapping("/getListCargoCategory")
//    @PreAuthorize("hasAuthority('master:cargo:query')")
    public Map<String, Object> getListCargoCategory(MCargoCategorySearchDTO mCargoTypeSearchDTO) {
        final String methodName = "MCargoTypeController: getListCargoCategory";
        LOGGER.enter(methodName + "[start]", "mCargoTypeSearchDTO:" +
                mCargoTypeSearchDTO);

        mCargoTypeSearchDTO.setDelFlag('0');
        Pages<MCargoCategoryDTO> mCargoTypeList = mCargoService.getListCargoCategory(mCargoTypeSearchDTO);

        LOGGER.exit(methodName + "result:" + mCargoTypeList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mCargoTypeList);
    }

    /**
     * 新增 货种
     *
     * @param mCargoTypePO
     * @return
     */
    @PostMapping("/insertCargoCategory")
//    @PreAuthorize("hasAuthority('master:cargo:add')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> insertCargoCategory(@RequestBody MCargoCategoryDTO mCargoTypePO) {
        final String methodName = "MCargoTypeController: insertCargoCategory";
        LOGGER.enter(methodName + "[start]", "mCargoTypeDTO:" + mCargoTypePO);


        mCargoTypePO.setDelFlag('0');
        int count = mCargoService.saveCargoCategory(mCargoTypePO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
    }

    /**
     * 修改
     *
     * @param mCargoTypeDTO
     * @return
     */
    @PutMapping("/updateCargoCategory")
//    @PreAuthorize("hasAuthority('master:cargo:update')")
    @Log(OperateTypeEnum.UPDATE)
    public Map<String, Object> updateCargoCategory(@RequestBody MCargoCategoryDTO mCargoTypeDTO) {

        final String methodName = "MCargoTypeController: updateCargoCategory";
        LOGGER.enter(methodName + "[start]", "mCargoTypeDTO:" + mCargoTypeDTO);

        int count = mCargoService.saveCargoCategory(mCargoTypeDTO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * 根据id获取货种信息
     *
     * @param id
     * @return
     */
    @GetMapping("/getCargoCategoryById/{id}")
//    @PreAuthorize("hasAuthority('master:cargo:query')")
    public Map<String, Object> getCargoCategoryById(@PathVariable("id") Long id) {
        final String methodName = "MCargoTypeController:getCargoCategoryById";
        LOGGER.enter(methodName, "根据ID查询货种[start]");

        MCargoCategoryDTO cargoTypeDTO = mCargoService.getCargoCategoryById(id);

        LOGGER.exit(methodName, "根据ID查询货种[end]");
        return Response.SUCCESS.newBuilder().toResult(cargoTypeDTO);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteCargoCategory/{id}")
//    @PreAuthorize("hasAuthority('master:cargo:delete')")
    @Log(OperateTypeEnum.DELETE)
    public Map<String, Object> deleteCargoCategory(@PathVariable("id") Long id) {
        final String methodName = "MCargoTypeController: deleteCargoCategory";
        LOGGER.enter("MCargoTypeController:" + methodName + "[start]", "id:" + id);

        int count = mCargoService.deleteCargoCategory(id);

        LOGGER.exit("MCargoTypeController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }


    //↑ 货种操作

    //↓ 货物操作


    /**
     * 货物查询
     *
     * @param mCargoSearchDTO
     * @return
     */
    @GetMapping("/getListCargo")
//    @PreAuthorize("hasAuthority('master:cargo:query')")
    public Map<String, Object> getListCargo(MCargoSearchDTO mCargoSearchDTO) {
        final String methodName = "MCargoTypeController: getListCargo";
        LOGGER.enter(methodName + "[start]", "mCargoSearchDTO:"
                + mCargoSearchDTO);
        Pages<MCargoDTO> mCargoList = mCargoService.getListCargo(mCargoSearchDTO);

        LOGGER.exit(methodName + "result:" + mCargoList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mCargoList);
    }
    /**
     * 货物查询
     *
     * @param mCargoSearchDTO
     * @return
     */
    @GetMapping("/getListCargo/v1")
//    @PreAuthorize("hasAuthority('master:cargo:query')")
    public Map<String, Object> getListCargoNew(MCargoSearchDTO mCargoSearchDTO) {
        final String methodName = "MCargoTypeController: getListCargo";
        LOGGER.enter(methodName + "[start]", "mCargoSearchDTO:"
                + mCargoSearchDTO);
        Pages<MCargoDTO> mCargoList = mCargoService.getListCargoNew(mCargoSearchDTO);

        LOGGER.exit(methodName + "result:" + mCargoList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mCargoList);
    }

    /**
     * 外付货物
     * @param mCargoSearchDTO
     * @return
     */
    @GetMapping("/getOutwardGoods")
    public Map<String, Object> getOutwardGoods(MCargoSearchDTO mCargoSearchDTO) {
        final String methodName = "MCargoTypeController: getOutwardGoods";
        LOGGER.enter(methodName + "[start]", "getOutwardGoods:" + mCargoSearchDTO);
        Pages<MCargoDTO> mCargoList = mCargoService.getOutwardGoods(mCargoSearchDTO);
        LOGGER.exit(methodName + "result:" + mCargoList);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(mCargoList);
    }

    /**
     * 货物新增
     *
     * @param mCargoPO
     * @return
     */
    @PostMapping("/insertCargo")
//    @PreAuthorize("hasAuthority('master:cargo:add')")
    @Log(OperateTypeEnum.INSERT)
    public Map<String, Object> insertCargo(@RequestBody MCargoPO mCargoPO) {
        final String methodName = "MCargoTypeController: insertCargo";
        LOGGER.enter(methodName + "[start]", "mCargoPO:" + mCargoPO);

        // 验证货物名称重复
       /* commonService.isRepeate("M_CARGO", "CARGO_NAME",
                mCargoPO.getCargoName(), StringUtil.getString(mCargoPO.getId()), "货物名称", null);*/

        int count = mCargoService.insertCargo(mCargoPO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult(count);
    }

    /**
     * 修改
     *
     * @param MCargoPO
     * @return
     */
    @PutMapping("/updateCargo")
//    @PreAuthorize("hasAuthority('master:cargo:update')")
    @Log(OperateTypeEnum.UPDATE)
    public Map<String, Object> updateCargo(@RequestBody MCargoPO MCargoPO) {

        final String methodName = "MCargoTypeController: updateCargo";
        LOGGER.enter(methodName + "[start]", "MCargoPO:" + MCargoPO);
//        // 验证货物名称重复
//        commonService.isRepeate("M_CARGO", "CARGO_NAME",
//                MCargoPO.getCargoName(), StringUtil.getString(MCargoPO.getId()), "货物名称", null);
        int count = mCargoService.updateCargo(MCargoPO);

        LOGGER.exit(methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult(count);
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/getCargoById/{id}")
//    @PreAuthorize("hasAuthority('master:cargo:query')")
    public Map<String, Object> getById(@PathVariable("id") Long id) {
        final String methodName = "MCargoTypeController:getCargoById";
        LOGGER.enter(methodName, "根据ID查询货种[start]");

        MCargoDTO cargoDTO = mCargoService.getCargoById(id);

        LOGGER.exit(methodName, "根据ID查询货种[end]");
        return Response.SUCCESS.newBuilder().toResult(cargoDTO);
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/getDetailById/{id}")
    public Map<String, Object> getDetailById(@PathVariable("id") Long id) {
        final String methodName = "MCargoTypeController:getCargoById";
        LOGGER.enter(methodName, "根据ID查询货种[start]");

        MCargoDTO cargoDTO = mCargoService.getDetailById(id);

        LOGGER.exit(methodName, "根据ID查询货种[end]");
        return Response.SUCCESS.newBuilder().toResult(cargoDTO);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/deleteCargo/{id}")
//    @PreAuthorize("hasAuthority('master:cargo:delete')")
    @Log(OperateTypeEnum.DELETE)
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "MCargoTypeController: deleteCargo";
        LOGGER.enter("MCargoTypeController:" + methodName + "[start]", "id:" + id);

        int count = mCargoService.deleteCargo(id);

        LOGGER.exit("MCargoTypeController:" + methodName + "result:" + count);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult(count);
    }

    /**
     * 更新货物状态
     *
     * @param id
     * @param status
     * @return
     */
    @PutMapping("/updateStatus")
    public Map<String, Object> updateStatus(@NotNull Long id, @NotBlank String status) {
        boolean bool = Stream.of("0", "1").noneMatch(v1 -> v1.equals(status));
        if (bool) {
            throw new BusinessRuntimeException("无效的状态");
        }

        mCargoService.updateStatus(id, status);
        return Response.SUCCESS.newBuilder().out("更新成功").toResult();
    }
}
