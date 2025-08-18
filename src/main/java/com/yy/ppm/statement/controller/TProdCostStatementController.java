package com.yy.ppm.statement.controller;

import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.business.bean.dto.TBusContractDTO;
import com.yy.ppm.business.bean.dto.TBusTrustDTO;
import com.yy.ppm.business.bean.po.TBusServicePO;
import com.yy.ppm.common.enums.HandoverlistTypeEnum;
import com.yy.ppm.common.enums.IsFinalEnum;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TBusHandoverlistQueryDTO;
import com.yy.ppm.statement.bean.dto.prodCostStatement.TCostStatementDTO;
import com.yy.ppm.statement.service.TProdCostStatementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 生产结算
 * @Date 2023-09-11 17:27
 */
@RestController
@RequestMapping("/api/external/costStatement")
@Validated
public class TProdCostStatementController {

    @Autowired
    private TProdCostStatementService tProdCostStatementService;

    /**
     * 交接清单列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listBusHandoverlist")
    public Map<String, Object> listBusHandoverlist(TBusHandoverlistQueryDTO query, PageParameter parameter) {
        if (StringUtils.isBlank(query.getType())) {
            throw new BusinessRuntimeException("交接清单类型不能为空");
        }
        Pages<TBusHandoverlistDTO> result = tProdCostStatementService.listBusHandoverlist(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 交接清单ID查结算单列表
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/listCostStatement")
    public Map<String, Object> listCostStatement(@NotNull(message = "交接清单ID不能为空") Long handoverlistId,Long isFinal ) {
        List<TCostStatementDTO> result = tProdCostStatementService.listCostStatement(handoverlistId,isFinal);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 交接清单ID查预结算量，按服务内容分组
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/listPreSettlement")
    public Map<String, Object> listPreSettlement(@NotNull(message = "交接清单ID不能为空") Long handoverlistId) {
        List<Map<String, BigDecimal>> result = tProdCostStatementService.listPreSettlement(handoverlistId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 交接清单ID查预结算量，按服务内容分组
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/getSettlementBasis")
    public Map<String, Object> getSettlementBasis(@NotNull(message = "交接清单ID不能为空") Long handoverlistId) {
        List<TBusContractDTO> result = tProdCostStatementService.getSettlementBasis(handoverlistId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 交接清单ID查预结算量，按服务内容分组
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/getSettlementBasisByHandover")
    public Map<String, Object> getSettlementBasisByHandover(@NotNull(message = "交接清单ID不能为空") Long handoverlistId) {
        TBusTrustDTO result = tProdCostStatementService.getSettlementBasisByHandover(handoverlistId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 根据交接清单查询直取作业量
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/getZQQuantity")
    public Map<String, Object> getZQQuantity(@NotNull(message = "交接清单ID不能为空") Long handoverlistId,
                                             @NotNull(message = "统计类型不能为空")@RequestParam("zqTallyStatistic") String zqTallyStatistic  ) {
        BigDecimal result = tProdCostStatementService.getZQQuantity(handoverlistId,zqTallyStatistic);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    /**
     * 过磅量查询
     *
     * @param handoverlistId
     * @return
     */
    @GetMapping("/getWeighCapacity")
    public Map<String, Object> getWeighCapacity(@NotNull(message = "交接清单ID不能为空") Long handoverlistId) {
        TBusHandoverlistDTO result = tProdCostStatementService.getWeighCapacity(handoverlistId);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 获取交接清单票货的直取作业量
     *
     * @param impExp
     * @param type
     * @return
     */
    @GetMapping("/listService")
    public Map<String, Object> listService(@NotBlank(message = "进出口编码不能为空") String impExp, @NotBlank(message = "交接清单类型不能为空") String type) {
        List<TBusServicePO> result = tProdCostStatementService.listService(impExp, type);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 结算
     *
     * @param dto
     * @return
     */
    @PostMapping("/statement")
    public Map<String, Object> statement(@RequestBody TCostStatementDTO dto) {
        if(!"2".equals(dto.getType())){
            ValidatorUtils.FieldBean bean = ValidatorUtils.validator(dto);
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
            bean = ValidatorUtils.validator(dto.getDetails());
            if (bean.isSuccess()) {
                throw new BusinessRuntimeException(bean.getMsg());
            }
        }

        if (!IsFinalEnum.isContains(dto.getIsFinal())) {
            throw new BusinessRuntimeException("错误的是否最终结算标记");
        }

        if("2".equals(dto.getType())){
            dto.setType(HandoverlistTypeEnum._20.getCode());
        }else{
            dto.setType(HandoverlistTypeEnum._10.getCode());

        }

        tProdCostStatementService.statement(dto);
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 撤销结算
     *
     * @param id
     * @return
     */
    @DeleteMapping("/cancelStatement")
    public Map<String, Object> cancelStatement(@NotNull(message = "结算单ID不能为空") Long id) {
        tProdCostStatementService.cancelStatement(id);
        return Response.SUCCESS.newBuilder().out("撤销成功").toResult();
    }

    /**
     * 回显结算单
     *
     * @param id
     * @return
     */
    @GetMapping("/getCostStatement")
    public Map<String, Object> getCostStatement(@NotNull(message = "结算单ID不能为空") Long id,Long bhId) {
        TCostStatementDTO costStatement = tProdCostStatementService.getCostStatement(id,bhId);
        return Response.SUCCESS.newBuilder().toResult(costStatement);
    }
    /**
     * 结算
     *
     * @param id
     * @return
     */
    @PostMapping("/printCostBill/{id}")
    public Map<String, Object> printCostBill(@PathVariable Long id) {

      /*  dto.setType(HandoverlistTypeEnum._10.getCode());

        tProdCostStatementService.printCostBill(id);*/
        return Response.SUCCESS.newBuilder().out("结算成功").toResult();
    }

    /**
     * 计算进保税区货量
     *
     * @param cargoInfoId
     * @return
     */
    @GetMapping("/getBondedAreaTon")
    public Map<String, Object> getBondedAreaTon(@NotNull(message = "票货号不能为空") Long cargoInfoId) {
        BigDecimal bondedAreaTon = tProdCostStatementService.getBondedAreaTon(cargoInfoId);
        return Response.SUCCESS.newBuilder().toResult(bondedAreaTon);
    }

    /**
     * 获取交接清单票货的直取作业量
     *
     * @return
     */
    @GetMapping("/getJSGWeight")
    public Map<String, Object> getJSGWeight(Long id) {
        TCostStatementDTO result = tProdCostStatementService.getJSGWeightByHandoverlistId(id);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
