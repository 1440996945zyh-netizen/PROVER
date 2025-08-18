package com.yy.ppm.statement.controller;

import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.statement.bean.dto.CalculateStorageFeeDTO;
import com.yy.ppm.statement.bean.dto.FStorageFeeHisDTO;
import com.yy.ppm.statement.bean.dto.FStorageFieldDTO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.service.StorageChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;

/**
 * 堆存费controller
 * @author yangcl
 * */
@RestController
@RequestMapping("/api/external/storagecharge")
@Validated
public class StorageChargeController {

    private static final MicroLogger LOGGER = new MicroLogger(StorageChargeController.class);

    @Autowired
    StorageChargeService chargeService;

    /**
     * 查询费率列表
     * @return
     */
    @GetMapping("/gethandoverlist")
//    @PreAuthorize("hasAuthority('statement:miscbilling:query')")
    public Map<String, Object> getHandoverlist(TBusHandoverlistQueryDTO dto, PageParameter parameter) {
        final String methodName = "StorageChargeController:getHandoverlist";
        LOGGER.enter(methodName, "查询交接清单列表 ");

        Pages<TBusHandoverlistDTO> result = chargeService.getHandoverlist(dto, parameter);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据作业公司及货主等信息查询合同列表
     * @return
     */
    @GetMapping("/initstoragecharge")
//    @PreAuthorize("hasAuthority('statement:miscbilling:query')")
    public Map<String, Object> initStorageCharge(Long customerId,Long companyId ,Long cargoInfoId) {
        final String methodName = "StorageChargeController:getContractList";
        LOGGER.enter(methodName, "初始化堆存抽屉 ");

        FStorageFieldDTO result = chargeService.getContractList(customerId, companyId, cargoInfoId);

        LOGGER.exit(methodName);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 计算堆存费
     * @param dto
     * @return
     */
    @PostMapping("/calculatestoragefees")
    public Map<String, Object> calculateStorageFees(@RequestBody @Validated CalculateStorageFeeDTO dto, BindingResult result) {
        final String methodName = "calculateStorageFees";
        LOGGER.enter("calculateStorageFees:" + methodName + "[start]", "dto:" + dto);

        if (result.hasErrors()) {
            String msg = result.getFieldError().getDefaultMessage();
            LOGGER.warn("计算堆存费失败,msg: " + msg);
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }

        FStorageFeeHisDTO detailDto = null;
        try {
            detailDto = chargeService.calculateStorageFees(dto);
        } catch (ParseException e) {
            throw new BusinessRuntimeException("堆存费计算异常："+e.getMessage());
        }

        LOGGER.exit("StorageFeeController:" + methodName + "result:" +detailDto);
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(detailDto);
    }

    /**
     * 保存堆存费
     * */
    @PostMapping("/savestoragefeesdata")
    public Map<String, Object> saveStorageFeesData(@RequestBody @Validated FStorageFeeHisDTO dto, BindingResult result) {
        final String methodName = "saveStorageFeesData";
        LOGGER.enter("saveStorageFeesData:" + methodName + "[start]", "dto:" + dto);

        if (result.hasErrors()) {
            String msg = result.getFieldError().getDefaultMessage();
            LOGGER.warn("计算堆存费失败,msg: " + msg);
            return Response.FAIL.newBuilder().addGateWayCode(Response.GateWayCode.E0200).out(msg).toResult();
        }

        chargeService.saveStorageFeesData(dto);

        LOGGER.exit("StorageFeeController:" + methodName + "result:" );
        return Response.SUCCESS.newBuilder().out("保存成功").toResult();
    }

    /**
     * 根据历史结算Gid获取历史结算信息
     * @param historyGid
     * @return
     */
    @GetMapping("/gethistorybygid")
    public Map<String, Object> getHistoryByGid(Long historyGid) {
        final String methodName = "gethistorybygid";
        LOGGER.enter("StorageChargeController:" + methodName + "[start]", "historyGid:" + historyGid);

        FStorageFeeHisDTO dto = chargeService.getHistoryByGid(historyGid);

        LOGGER.exit("StorageChargeController:" + methodName + "result:" );
        return Response.SUCCESS.newBuilder().out("查询成功").toResult(dto);
    }

    /**
     * 根据历史结算Gid删除历史结算信息
     * @param historyGid
     * @return
     */
    @DeleteMapping("/deletehistorybygid")
    public Map<String, Object> deleteHistoryByGid(Long historyGid,Long cargoInfoId) {
        final String methodName = "deleteHistoryByGid";
        LOGGER.enter("StorageChargeController:" + methodName + "[start]", "historyGid:" + historyGid);

        chargeService.deleteHistoryByGid(historyGid,cargoInfoId);

        LOGGER.exit("StorageChargeController:" + methodName + "result:" );
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }

    @GetMapping("/generatestatement")
    public Map<String, Object> generateStatement(Long historyGid,Long cargoInfoId) {
        final String methodName = "generateStatement";
        LOGGER.enter("StorageChargeController:" + methodName + "[start]", "historyGid:" + historyGid);

        chargeService.generateStatement(historyGid,cargoInfoId);

        LOGGER.exit("StorageChargeController:" + methodName + "result:" );
        return Response.SUCCESS.newBuilder().out("生成结算单成功").toResult();
    }

    @PutMapping("/cancelstatement")
    public Map<String, Object> cancelStatement(Long historyGid) {
        final String methodName = "cancelStatement";
        LOGGER.enter("StorageChargeController:" + methodName + "[start]", "historyGid:" + historyGid);

        chargeService.cancelStatement(historyGid);

        LOGGER.exit("StorageChargeController:" + methodName + "result:");
        return Response.SUCCESS.newBuilder().out("取消审核成功").toResult();
    }

    @PutMapping("/confirm")
    public Map<String, Object> confirm(Long historyGid) {
        final String methodName = "confirm";
        LOGGER.enter("StorageChargeController:" + methodName + "[start]", "historyGid:" + historyGid);

        chargeService.confirm(historyGid);

        LOGGER.exit("StorageChargeController:" + methodName + "result:");
        return Response.SUCCESS.newBuilder().out("商务确认成功").toResult();
    }

    @PutMapping("/cancelConfirm")
    public Map<String, Object> cancelConfirm(Long historyGid) {
        final String methodName = "cancelConfirm";
        LOGGER.enter("StorageChargeController:" + methodName + "[start]", "historyGid:" + historyGid);

        chargeService.cancelConfirm(historyGid);

        LOGGER.exit("StorageChargeController:" + methodName + "result:");
        return Response.SUCCESS.newBuilder().out("取消商务确认成功").toResult();
    }
}
