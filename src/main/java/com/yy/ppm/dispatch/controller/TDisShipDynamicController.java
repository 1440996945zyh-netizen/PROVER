package com.yy.ppm.dispatch.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import cn.hutool.core.io.IORuntimeException;
import com.yy.common.excel.export.utils.ResponseUtils;
import com.yy.common.util.DateUtils;
import com.yy.ppm.dispatch.bean.po.TDisLowerDoorPO;
import com.yy.ppm.produce.bean.dto.workTicket.TPrdWorkPlanQuery;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.enums.BusinessType;
import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.enums.LoadUnloadEnum;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipDynamicQueryDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipDynamic.TDisShipvoyageQueryDTO;
import com.yy.ppm.dispatch.bean.po.TDisLowerCabinPO;
import com.yy.ppm.dispatch.bean.po.TDisShipDynamicPO;
import com.yy.ppm.dispatch.service.TDisShipDynamicService;

import lombok.Getter;

import static com.yy.ppm.common.enums.ShipStatusEnum.*;

/**
 * @Author linqi
 * @Description 船舶动态
 * @Date 2023-07-12 11:03
 */
@RestController
@RequestMapping("/api/external/disShipDynamic")
@Validated
public class TDisShipDynamicController {

    @Autowired
    private TDisShipDynamicService tDisShipDynamicService;

    /**
     * 宽泛船舶状态枚举
     * <p>
     * 预到 包含：接收
     * 锚地 包含：抵锚、起锚
     * 在港 包含：靠泊、移泊、开工、停工、复工、完工
     * 离泊 包含：离泊、离港
     * <p>
     * *不包含作废状态*
     * *不包含船舶动态之前的状态：预报*
     * *移泊是“操作”而不是“状态”，也不包含*
     */
    @Getter
    private enum ShipStatusBroadEnum {

        YUDAO("00", "预到"),

        MAODI("10", "锚地"),

        ZAIGANG("20", "在港"),

        LIBO("30", "离泊");

        private final String code;

        private final String name;

        ShipStatusBroadEnum(String code, String comment) {
            this.code = code;
            this.name = comment;
        }

        public static boolean isContains(String code) {
            return Arrays.stream(ShipStatusBroadEnum.values()).anyMatch(v1 -> v1.getCode().equals(code));
        }
    }

    @GetMapping("/listDisShipVoyage")
    @PreAuthorize("hasAuthority('dispatch:boatingDispatch:query')")
    public Map<String, Object> listShipDynamic(TDisShipvoyageQueryDTO query, PageParameter parameter) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(query, false, "shipStatusBroadCode")).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if(!CollectionUtils.isEmpty(query.getTimeRange())){
            query.setStartLeaveTime(DateUtils.parseDate(query.getTimeRange().get(0),"yyyy-MM-dd HH:mm"));
            query.setEndLeaveTime(DateUtils.parseDate(query.getTimeRange().get(1),"yyyy-MM-dd HH:mm"));
        }
        if (!ShipStatusBroadEnum.isContains(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(JIESHOU.getCode(),KAOBO.getCode(), YIBO.getCode(),LIBO.getCode(), LIGANG.getCode(), DIMAO.getCode(), QIMAO.getCode(),KAIGONG.getCode(), TINGGONG.getCode(), FUGONG.getCode(), WANGONG.getCode()));
        }
        if (ShipStatusBroadEnum.YUDAO.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Collections.singletonList(JIESHOU.getCode()));
        }
        if (ShipStatusBroadEnum.MAODI.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(DIMAO.getCode(), QIMAO.getCode(),LIBO.getCode()));
        }
        if (ShipStatusBroadEnum.ZAIGANG.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(KAOBO.getCode(), YIBO.getCode(), KAIGONG.getCode(), TINGGONG.getCode(), FUGONG.getCode(), WANGONG.getCode()));
        }
        if (ShipStatusBroadEnum.LIBO.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(LIGANG.getCode()));
        }

        Pages<TDisShipvoyageDTO> result = tDisShipDynamicService.listDisShipVoyage(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    @GetMapping("/listDisShipVoyageApp")
    @PreAuthorize("hasAuthority('dispatch:boatingDispatch:query')")
    public Map<String, Object> listDisShipVoyageApp(TDisShipvoyageQueryDTO query, PageParameter parameter) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(query, false, "shipStatusBroadCode")).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        if (!ShipStatusBroadEnum.isContains(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(JIESHOU.getCode(),KAOBO.getCode(), YIBO.getCode(),LIBO.getCode(), LIGANG.getCode(), DIMAO.getCode(), QIMAO.getCode(),KAIGONG.getCode(), TINGGONG.getCode(), FUGONG.getCode(), WANGONG.getCode()));
        }
        if (ShipStatusBroadEnum.YUDAO.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Collections.singletonList(JIESHOU.getCode()));
        }
        if (ShipStatusBroadEnum.MAODI.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(DIMAO.getCode(), QIMAO.getCode()));
        }
        if (ShipStatusBroadEnum.ZAIGANG.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(KAOBO.getCode(), YIBO.getCode(), KAIGONG.getCode(), TINGGONG.getCode(), FUGONG.getCode(), WANGONG.getCode()));
        }
        if (ShipStatusBroadEnum.LIBO.getCode().equals(query.getShipStatusBroadCode())) {
            query.setShipStatusCodes(Arrays.asList(LIBO.getCode(), LIGANG.getCode()));
        }

        Pages<TDisShipvoyageDTO> result = tDisShipDynamicService.listDisShipVoyageApp(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }


    @PutMapping("/updateDisShipvoyageStatus")
    public Map<String, Object> updateDisShipvoyageStatus(@RequestBody TDisShipDynamicDTO disShipDynamic) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(disShipDynamic)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        if (!Arrays.asList(
                DIMAO.getCode(), QIMAO.getCode(), KAOBO.getCode(), YIBO.getCode(),
                KAIGONG.getCode(), TINGGONG.getCode(), FUGONG.getCode(), WANGONG.getCode(),
                LIBO.getCode(), LIGANG.getCode(), TE_SHU_TING_BO_FEI.getCode()
        ).contains(disShipDynamic.getDynamicTypeCode())) {
            throw new BusinessRuntimeException("错误的状态编码");
        }

        if (Stream.of(KAOBO, YIBO).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()))) {
            if (disShipDynamic.getBerthId() == null) {
                throw new BusinessRuntimeException("泊位id不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getBerthName())) {
                throw new BusinessRuntimeException("泊位编码不能为空");
            }
//            if (StringUtils.isBlank(disShipDynamic.getBollardNoStart())) {
//                throw new BusinessRuntimeException("首榄编码不能为空");
//            }
//            if (StringUtils.isBlank(disShipDynamic.getBollardNoEnd())) {
//                throw new BusinessRuntimeException("尾榄编码不能为空");
//            }
            if (StringUtils.isBlank(disShipDynamic.getBerthType())) {
                throw new BusinessRuntimeException("舷靠不能为空");
            }
        }

        if (YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            if (disShipDynamic.getDynamicEndTime() == null) {
                throw new BusinessRuntimeException("动态结束时间不能为空");
            }
        }

        boolean isVoyageRelated = Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
        if (isVoyageRelated) {
            if (disShipDynamic.getShipvoyageItemId() == null) {
                throw new BusinessRuntimeException("航次子表id不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getVoyage())) {
                throw new BusinessRuntimeException("航次不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getLoadUnload())) {
                throw new BusinessRuntimeException("装卸不能为空");
            }
            if (!LoadUnloadEnum.isContains(disShipDynamic.getLoadUnload())) {
                throw new BusinessRuntimeException("错误的装卸");
            }
        }

        if (TINGGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            if (StringUtils.isBlank(disShipDynamic.getStopTypeCode())) {
                throw new BusinessRuntimeException("停时类型编码不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getStopTypeName())) {
                throw new BusinessRuntimeException("停时类型名称不能为空");
            }
            if (disShipDynamic.getStopId() == null) {
                throw new BusinessRuntimeException("停工原因id不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getStopName())) {
                throw new BusinessRuntimeException("停工原因名称不能为空");
            }
        }

        boolean isBerthRelated = Stream.of(KAOBO, YIBO, LIBO).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
        if (isBerthRelated) {
//            if (CollectionUtils.isEmpty(disShipDynamic.getTugs())) {
//                throw new BusinessRuntimeException("拖轮服务记录不能为空");
//            }
            if ((bean = ValidatorUtils.validator(disShipDynamic.getTugs())).isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if("130".equals(disShipDynamic.getDynamicTypeCode())){
            tDisShipDynamicService.updateTeShuTingBoFei(disShipDynamic);
        }else{
            tDisShipDynamicService.updateDisShipvoyageStatus(disShipDynamic);
        }
        return Response.SUCCESS.newBuilder().out("状态更新成功").toResult();
    }

    @PutMapping("/updateDisShipvoyageStatusApp")
    @Log(title = "APP-修改船舶状态", businessType = BusinessType.UPDATE)
    public Map<String, Object> updateDisShipvoyageStatusApp(@RequestBody TDisShipDynamicDTO disShipDynamic) {
        ValidatorUtils.FieldBean bean;
        if ((bean = ValidatorUtils.validator(disShipDynamic)).isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        if (!Arrays.asList(
                DIMAO.getCode(), QIMAO.getCode(), KAOBO.getCode(), YIBO.getCode(),
                KAIGONG.getCode(), TINGGONG.getCode(), FUGONG.getCode(), WANGONG.getCode(),
                LIBO.getCode(), LIGANG.getCode()
        ).contains(disShipDynamic.getDynamicTypeCode())) {
            throw new BusinessRuntimeException("错误的状态编码");
        }

        if (Stream.of(KAOBO, YIBO).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()))) {
            if (disShipDynamic.getBerthId() == null) {
                throw new BusinessRuntimeException("泊位id不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getBerthName())) {
                throw new BusinessRuntimeException("泊位编码不能为空");
            }
//            if (StringUtils.isBlank(disShipDynamic.getBollardNoStart())) {
//                throw new BusinessRuntimeException("首榄编码不能为空");
//            }
//            if (StringUtils.isBlank(disShipDynamic.getBollardNoEnd())) {
//                throw new BusinessRuntimeException("尾榄编码不能为空");
//            }
            if (StringUtils.isBlank(disShipDynamic.getBerthType())) {
                throw new BusinessRuntimeException("舷靠不能为空");
            }
        }

        if (YIBO.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            if (disShipDynamic.getDynamicEndTime() == null) {
                throw new BusinessRuntimeException("动态结束时间不能为空");
            }
        }

        boolean isVoyageRelated = Stream.of(KAIGONG, TINGGONG, FUGONG, WANGONG).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
        if (isVoyageRelated) {
            if (disShipDynamic.getShipvoyageItemId() == null) {
                throw new BusinessRuntimeException("航次子表id不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getVoyage())) {
                throw new BusinessRuntimeException("航次不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getLoadUnload())) {
                throw new BusinessRuntimeException("装卸不能为空");
            }
            if (!LoadUnloadEnum.isContains(disShipDynamic.getLoadUnload())) {
                throw new BusinessRuntimeException("错误的装卸");
            }
        }

        if (TINGGONG.getCode().equals(disShipDynamic.getDynamicTypeCode())) {
            if (StringUtils.isBlank(disShipDynamic.getStopTypeCode())) {
                throw new BusinessRuntimeException("停时类型编码不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getStopTypeName())) {
                throw new BusinessRuntimeException("停时类型名称不能为空");
            }
            if (disShipDynamic.getStopId() == null) {
                throw new BusinessRuntimeException("停工原因id不能为空");
            }
            if (StringUtils.isBlank(disShipDynamic.getStopName())) {
                throw new BusinessRuntimeException("停工原因名称不能为空");
            }
        }

        boolean isBerthRelated = Stream.of(KAOBO, YIBO, LIBO).anyMatch(v1 -> v1.getCode().equals(disShipDynamic.getDynamicTypeCode()));
        if (isBerthRelated) {
//            if (CollectionUtils.isEmpty(disShipDynamic.getTugs())) {
//                throw new BusinessRuntimeException("拖轮服务记录不能为空");
//            }
//            if ((bean = ValidatorUtils.validator(disShipDynamic.getTugs())).isSuccess()) {
//                throw new BusinessRuntimeException(bean.getMsg());
//            }
        }

        StringBuffer cargoNos = tDisShipDynamicService.updateDisShipvoyageStatusApp(disShipDynamic);
        return Response.SUCCESS.newBuilder().out("状态更新成功").toResult(cargoNos);
    }

    @GetMapping("/listDisShipDynamic")
    @PreAuthorize("hasAuthority('dispatch:boatingDispatch:query')")
    public Map<String, Object> listDisShipDynamic(TDisShipDynamicQueryDTO query) {
        if (query.getShipvoyageId() == null) {
            throw new BusinessRuntimeException("航次id不能为空");
        }

        List<TDisShipDynamicDTO> result = tDisShipDynamicService.listDisShipDynamic(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    @GetMapping("/getTrustRemark")
    public Map<String, Object> getTrustRemark(TDisShipDynamicQueryDTO query) {
        if (query.getShipvoyageId() == null) {
            throw new BusinessRuntimeException("航次id不能为空");
        }

        String result = tDisShipDynamicService.getTrustRemark(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 修改船舶动态中的一条数据
     *
     * @param disShipDynamic
     * @return
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody TDisShipDynamicPO disShipDynamic) {

        boolean flag = tDisShipDynamicService.updateDynamic(disShipDynamic);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 通过id查询船舶动态里面的数据
     * @param id
     * @return
     */
    @GetMapping("/getDetail")
    public Map<String, Object> getDetail(@RequestParam("id") Long id) {

        TDisShipDynamicDTO result = tDisShipDynamicService.getDetail(id);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }


    @DeleteMapping("/deleteDisShipDynamic")
    @Log(title ="船舶动态删除",value = OperateTypeEnum.DELETE)
    public Map<String, Object> deleteDisShipDynamic(@NotNull(message = "船舶动态id不能为空") Long id) {
        tDisShipDynamicService.deleteDisShipDynamic(id);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    /**
     * 查询机械下舱
     * @param
     * @param tDisLowerCabinPO 实例对象
     * @return
     */
    @GetMapping("/queryAll")
    public Map<String, Object> queryAll( TDisLowerCabinPO tDisLowerCabinPO){
//        final String methodName = "queryAll";
        List<TDisLowerCabinPO> list = tDisShipDynamicService.queryAll(tDisLowerCabinPO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询门机记录
     * @param
     * @param tDisLowerCabinPO 实例对象
     * @return
     */
    @GetMapping("/queryAllDoor")
    public Map<String, Object> queryAllDoor( TDisLowerCabinPO tDisLowerCabinPO){
//        final String methodName = "queryAllDoor";
        List<TDisLowerCabinPO> list = tDisShipDynamicService.queryAllDoor(tDisLowerCabinPO);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询设备信息
     * @param
     * @return
     */
    @GetMapping("/getListDevice")
    public Map<String, Object> getListDevice(Long equipmentTypeId,String macName){
        List<Map<String,Object>> list = tDisShipDynamicService.getListDevice(equipmentTypeId,macName);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 机械下舱新增
     * @param tDisLowerCabinPO 实例对象
     * @param result
     * @return
     */

    @PostMapping("/addLowerCabin")
    public Map<String, Object> insert(@RequestBody @Valid TDisLowerCabinPO tDisLowerCabinPO, BindingResult result){
        tDisShipDynamicService.insert(tDisLowerCabinPO);
        return Response.SUCCESS.newBuilder().out("机械信息新增成功").toResult();
    }

    /**
     * 门机记录新增/修改
     * @param tDisLowerDoorPO 实例对象
     * @param result
     * @return
     */

    @PostMapping("/addDoor")
    public Map<String, Object> addDoor(@RequestBody @Valid TDisLowerDoorPO tDisLowerDoorPO, BindingResult result){
        tDisShipDynamicService.addDoor(tDisLowerDoorPO);
        return Response.SUCCESS.newBuilder().out("门机记录处理成功").toResult();
    }

    /**
     * 机械下舱修改
     * @param tDisLowerCabinPO 实例对象
     * @param result
     * @return
     */

    @PutMapping("/updateJxXc")
    public Map<String, Object> updateJxXc(@RequestBody @Valid TDisLowerCabinPO tDisLowerCabinPO, BindingResult result){
        tDisShipDynamicService.updateJxXc(tDisLowerCabinPO);
        return Response.SUCCESS.newBuilder().out("机械信息修改成功").toResult();
    }

    /**
     * 删除机械下舱
     * @param id 主键id
     * @return
     */
    @DeleteMapping("/deleteLowerCabin")
    public Map<String, Object> deleteById(String id){
        tDisShipDynamicService.deleteById(Long.parseLong(id));
        return Response.SUCCESS.newBuilder().out("删除机械下舱信息成功").toResult();
    }

    /**
     * 门机记录导出
     *
     * @param shipvoyageId
     * @param response
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel( Long shipvoyageId, HttpServletResponse response) {
        ResponseUtils.compliantWithExcel(response, "门机记录");
        try {
            byte[] bytes = tDisShipDynamicService.exportExcel(shipvoyageId);
            try {
                response.getOutputStream().write(bytes);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        } catch (Exception e) {
            ResponseUtils.resetCompliant(response);
            throw e;
        }
    }

}
