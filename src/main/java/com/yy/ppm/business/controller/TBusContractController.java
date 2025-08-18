package com.yy.ppm.business.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.annotation.Log;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusContractSearchDTO;
import com.yy.ppm.business.bean.dto.contract.TBusTrateDTO;
import com.yy.ppm.business.service.TBusContractService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 合同(TBusContract)Controller
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 10:48:00
 */
@RestController
@RequestMapping("/api/v1/internal/tBusContract")
@Validated
@Tag(name = "合同管理")
public class TBusContractController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TBusContractController.class);

    @Autowired
    private TBusContractService tBusContractService;

    /**
     * 获取列表（翻页）
     * @param searchDTO
     * @return
     */
    @GetMapping("/getList")
    @PreAuthorize("hasAuthority('business:contract:query')")

    public Map<String, Object> getList(TBusContractSearchDTO searchDTO) {
    	final String methodName = "TBusContractController:getList";
		LOGGER.enter(methodName + "[start]", "searchDTO:" + searchDTO);

        Pages<TBusContractDTO> pages = tBusContractService.getList(searchDTO);

        LOGGER.exit( methodName + "result:" + pages);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(pages);
    }

    /**
     * 根据ParentId查询补充协议
     * @param parentId
     * @return
     */
    @GetMapping("/getListByParentId/{parentId}")
    @PreAuthorize("hasAuthority('business:contract:query')")
    public Map<String, Object> getListByParentId(@PathVariable("parentId") Long parentId) {
        final String methodName = "TBusContractController:getListByParentId";
        LOGGER.enter(methodName + "[start]", "parentId:" + parentId);

        List<TBusContractDTO> list = tBusContractService.getListByParentId(parentId);

        LOGGER.exit( methodName + "result:" + list);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(list);
    }

    /**
     * 查询单条记录
     * @param id
     * @return
     */
    @GetMapping("/getDetail/{id}")
    @PreAuthorize("hasAuthority('business:contract:query')")
    public Map<String, Object> getDetail(@PathVariable("id") Long id) {
        final String methodName = "TBusContractController:getDetail";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        TBusContractDTO result = tBusContractService.getDetail(id);

        LOGGER.exit( methodName + "result:" + result);

        return Response.SUCCESS.newBuilder().out("查询成功").toResult(result);
    }

    /**
     * 新建
     * @param tBusContractDTO
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('business:contract:add')")
    @Log(title="合同新增",value= OperateTypeEnum.QUERY)
    public Map<String, Object> add(@RequestBody TBusContractDTO tBusContractDTO) {
        final String methodName = "TBusContractController:add";
		LOGGER.enter(methodName + "[start]", "tBusContractDTO:" +  tBusContractDTO);

        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(tBusContractDTO);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(tBusContractDTO.getRateList());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        boolean noneMatch = tBusContractDTO.getRateList().stream().noneMatch(v1 -> "10".equals(v1.getType()));
        if (noneMatch) {
            throw new BusinessRuntimeException("货物费率不能为空");
        }
        boolean anyMatch = tBusContractDTO.getRateList().stream().filter(v1 -> "10".equals(v1.getType()) && "MS00240".equals(v1.getRateItemCode())).anyMatch(v1 -> v1.getFreeStorageDays() == null);
        if (anyMatch) {
            throw new BusinessRuntimeException("库场使用费免堆存期不能为空");
        }
        if (CollectionUtils.isNotEmpty(tBusContractDTO.getCustomers())) {
            bean = ValidatorUtils.validator(tBusContractDTO.getCustomers());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (!"1".equals(tBusContractDTO.getContractType())) {
            if (tBusContractDTO.getEndTime() == null) {
                throw new BusinessRuntimeException("合同非单笔时有效期止不能为空");
            }
        }
        if (StringUtils.isAllBlank(tBusContractDTO.getSettlementBasisCode(), tBusContractDTO.getExpSettlementBasisCode(), tBusContractDTO.getOuterSettlementBasisCode(), tBusContractDTO.getLuxiaoSettlementBasisCode())) {
            throw new BusinessRuntimeException("内贸进口、内贸出口、外贸、陆销结算依据必填其一");
        }

        boolean flag = tBusContractService.doSave(tBusContractDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "新增成功" : "新增失败").toResult();

    }

    /**
     * 修改
     * @param tBusContractDTO
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('business:contract:update')")
    @Log(title="合同修改",value= OperateTypeEnum.QUERY)
    public Map<String, Object> update(@RequestBody TBusContractDTO tBusContractDTO) {
        final String methodName = "TBusContractController:update";
		LOGGER.enter(methodName + "[start]", "tBusContractDTO:" +  tBusContractDTO);

        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(tBusContractDTO);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        bean = ValidatorUtils.validator(tBusContractDTO.getRateList());
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }
        boolean noneMatch = tBusContractDTO.getRateList().stream().noneMatch(v1 -> "10".equals(v1.getType()));
        if (noneMatch) {
            throw new BusinessRuntimeException("货物费率不能为空");
        }
        boolean anyMatch = tBusContractDTO.getRateList().stream().filter(v1 -> "10".equals(v1.getType()) && "MS00240".equals(v1.getRateItemCode())).anyMatch(v1 -> v1.getFreeStorageDays() == null);
        if (anyMatch) {
            throw new BusinessRuntimeException("库场使用费免堆存期不能为空");
        }
        if (CollectionUtils.isNotEmpty(tBusContractDTO.getCustomers())) {
            bean = ValidatorUtils.validator(tBusContractDTO.getCustomers());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }
        if (!"1".equals(tBusContractDTO.getContractType())) {
            if (tBusContractDTO.getEndTime() == null) {
                throw new BusinessRuntimeException("合同非单笔时有效期止不能为空");
            }
        }
        if (StringUtils.isAllBlank(tBusContractDTO.getSettlementBasisCode(), tBusContractDTO.getExpSettlementBasisCode(), tBusContractDTO.getOuterSettlementBasisCode(), tBusContractDTO.getLuxiaoSettlementBasisCode())) {
            throw new BusinessRuntimeException("内贸进口、内贸出口、外贸、陆销结算依据必填其一");
        }

        boolean flag = tBusContractService.doSave(tBusContractDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "修改成功" : "修改失败").toResult();
    }

    /**
     * 修改状态, 生效
     * @param tBusContractDTO
     * @return
     */
    @PutMapping("/updateStatus")
    @PreAuthorize("hasAuthority('business:contract:takeEffect')")
    @Log(title="合同生效",value= OperateTypeEnum.QUERY)
    public Map<String, Object> updateStatus(@RequestBody TBusContractDTO tBusContractDTO) {
        final String methodName = "TBusContractController:updateStatus";
		LOGGER.enter(methodName + "[start]", "tBusContractDTO:" +  tBusContractDTO);

        boolean flag = tBusContractService.updateStatus(tBusContractDTO);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "生效成功" : "失败失败").toResult();
    }

    /**
     * 修改状态, 取消生效
     *
     * @param id
     * @return
     */
    @PutMapping("/cancel")
//    @PreAuthorize("hasAuthority('business:contract:cancel')")
    @Log(title="合同失效",value= OperateTypeEnum.QUERY)
    public Map<String, Object> cancel(@NotNull(message = "合同ID不能为空") Long id) {
        final String methodName = "TBusContractController:cancel";
        LOGGER.enter(methodName + "修改状态, 取消生效[start]", "id:" + id);

        tBusContractService.cancel(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out("取消成功").toResult();
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('business:contract:delete')")
    @Log(title="合同删除",value= OperateTypeEnum.QUERY)
    public Map<String, Object> deleteById(@PathVariable("id") Long id) {
        final String methodName = "TBusContractController:deleteById";
		LOGGER.enter(methodName + "[start]", "id:" + id);

        boolean flag = tBusContractService.deleteById(id);

        LOGGER.exit(methodName);

        return Response.SUCCESS.newBuilder().out(flag ? "删除成功" : "删除失败").toResult();
    }

    /**
     * 生成货物费率
     *
     * @param startTime
     * @param cargoCodes
     * @return
     */
    @GetMapping("/listCargoRate")
    public Map<String, Object> listCargoRate(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @NotNull(message = "生成货物费率须选择合同有效期（起）~") Date startTime,
            @RequestParam("cargoCodes") @NotEmpty(message = "生成货物费率须选择货物~") List<String> cargoCodes
    ) {
        List<Map<String, Object>> list = tBusContractService.listCargoRate(startTime, cargoCodes);
        return Response.SUCCESS.newBuilder().toResult(list);
    }

    /**
     * 匹配阶梯费率
     *
     * @param contractNo
     * @param customerId
     * @param cargoCode
     * @return
     */
    @GetMapping("/matchTrate")
    public Map<String, Object> matchTrate(
            @NotBlank(message = "合同编号不能为空") String contractNo,
            @NotNull(message = "客户ID不能为空") Long customerId,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @NotNull(message = "有效期起不能为空") Date startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @NotNull(message = "有效期止不能为空") Date endTime,
            @NotBlank(message = "货物编码不能为空") String cargoCode
    ) {
        List<TBusTrateDTO> result = tBusContractService.matchTrate(contractNo, customerId, startTime, endTime, cargoCode);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
}

