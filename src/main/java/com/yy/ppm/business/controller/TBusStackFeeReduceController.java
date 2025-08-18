package com.yy.ppm.business.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.log.MicroLogger;
import com.yy.framework.annotation.Log;
import com.yy.ppm.business.bean.dto.TBusStackFeeReduceDTO;
import com.yy.ppm.business.service.TBusStackFeeReduceService;
import com.yy.ppm.statement.bean.dto.storageFee.TCostStorageSettleDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/stackFeeReduce")
@Tag(name = "堆存费减免")
public class TBusStackFeeReduceController {
    @Autowired
    TBusStackFeeReduceService service;
    /**
     * 日志组件
     **/
    private static final MicroLogger LOGGER = new MicroLogger(TBusStackFeeReduceController.class);

    @PostMapping("/add")
    @Log(title = "堆存费减免新增", value = OperateTypeEnum.INSERT)
    public Map<String,Object> add(@RequestBody TBusStackFeeReduceDTO dto){
        final String methodName = "tBusStackFeeReduceController:add";
        LOGGER.enter(methodName + "[start]", "add:" + dto);
        boolean flag =dto.getId()==null;
        service.add(dto);
        LOGGER.exit( methodName + "新增结束" );

        return Response.SUCCESS.newBuilder().out(flag?"新增成功":"已更新").toResult();

    }


    @GetMapping("/getList/{cargoInfoId}")
    public Map<String,Object> getList(@NotNull(message="票货ID不能为空") @PathVariable Long cargoInfoId){
        TBusStackFeeReduceDTO tBusStackFeeReduceDTO = new TBusStackFeeReduceDTO();
        tBusStackFeeReduceDTO.setCargoInfoId(cargoInfoId);
        List<TBusStackFeeReduceDTO> result = service.getList(tBusStackFeeReduceDTO);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    @PostMapping("/getCargoInfoSettleList")
    @Log(title = "查询结算记录", value = OperateTypeEnum.INSERT)
    public Map<String,Object> getCargoInfoSettleList(@RequestBody TBusStackFeeReduceDTO dto){
        List<TCostStorageSettleDTO> result = service.getSettleList(dto.getCargoInfoId());
        return Response.SUCCESS.newBuilder().toResult(result);

    }

}
