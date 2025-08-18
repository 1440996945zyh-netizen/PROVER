package com.yy.ppm.produce.controller;

import com.yy.common.enums.OperateTypeEnum;
import com.yy.common.enums.Response;
import com.yy.common.util.SpringUtils;
import com.yy.ppm.produce.service.impl.CaroStorageInfoServiceImpl;
import com.yy.framework.annotation.Log;
import com.yy.ppm.produce.bean.dto.WorkTicketTableDTO;
import com.yy.ppm.produce.service.WaiFuExService;
import com.yy.ppm.statement.mapper.storageAmountCalculate.StorageAmountCalculateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther linqi
 * @Description 签票
 * @Date 2023-08-14 15:23
 */
@RestController
@RequestMapping("/api/external/WaiFuExController")
@Validated
public class WaiFuExController {

   @Autowired
    private WaiFuExService waiFuExService;


    @Autowired
    private TransactionTemplate transactionTemplate;

    private static final int CURSOR_LIMIT = 100;

    private static final ThreadPoolTaskExecutor TASK_EXECUTOR = new ThreadPoolTaskExecutor();

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    static {
        TASK_EXECUTOR.setQueueCapacity(Integer.MAX_VALUE);
        TASK_EXECUTOR.setCorePoolSize(CORE_SIZE * 2);
        TASK_EXECUTOR.setMaxPoolSize(CORE_SIZE * 2);
        TASK_EXECUTOR.setThreadNamePrefix("STORAGE_AMOUNT_CALCULATE_TASK_");
        TASK_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        TASK_EXECUTOR.initialize();
        TASK_EXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
    }

    @GetMapping("/newQrCode")
    public Map<String,Object> newQrCode(String qrText){
        return Response.SUCCESS.newBuilder().out("审核成功").toResult(
                SpringUtils.getBean(CaroStorageInfoServiceImpl.class).createQRcode(qrText,"")
        );
    }


   @GetMapping("/prdEx")
   @Log(title="外付生产审核",value= OperateTypeEnum.UPDATE)
   public Map<String, Object> prdEx(WorkTicketTableDTO query) {
       Map<String, Object> result=null;
       result = waiFuExService.v2PrdEx(query);
       return Response.SUCCESS.newBuilder().out("审核成功").toResult(result);
   }
   @GetMapping("/prdExV")
   @Log(title="外付生产销审",value= OperateTypeEnum.UPDATE)
   public Map<String, Object> prdExV(WorkTicketTableDTO query) {
       Map<String, Object> result=null;
       result = waiFuExService.v2PrdExV(query);
       return Response.SUCCESS.newBuilder().out("审核成功").toResult(result);
   }


   @GetMapping("/hrEx")
   @Log(title="外付Hr审核",value= OperateTypeEnum.UPDATE)
   public Map<String, Object> hrEx(WorkTicketTableDTO query) {
       Map<String, Object> result=null;
       result = waiFuExService.v2HrEx(query);
       return Response.SUCCESS.newBuilder().out("审核成功").toResult(result);
   }
   @GetMapping("/hrExV")
   @Log(title="外付Hr销审",value= OperateTypeEnum.UPDATE)
   public Map<String, Object> HrExV(WorkTicketTableDTO query) {
       Map<String, Object> result=null;
       result = waiFuExService.v2HrExV(query);
       return Response.SUCCESS.newBuilder().out("审核成功").toResult(result);
   }

   @GetMapping("/hrNewEx")
   @Log(title="外付金额明细审核",value= OperateTypeEnum.UPDATE)
   public Map<String, Object> hrNewEx(WorkTicketTableDTO query) {
       Map<String, Object> result=null;
       result = waiFuExService.v2HrNewEx(query);
       return Response.SUCCESS.newBuilder().out("审核成功").toResult(result);
   }
   @GetMapping("/HrNewExV")
   @Log(title="外付金额明细销审",value= OperateTypeEnum.UPDATE)
   public Map<String, Object> HrNewExV(WorkTicketTableDTO query) {
       Map<String, Object> result=null;
       result = waiFuExService.v2HrNewExV(query);
       return Response.SUCCESS.newBuilder().out("审核成功").toResult(result);
   }

   @GetMapping("/getDepts")
   public Map<String, Object> getDeptList() {
       return Response.SUCCESS.newBuilder().out("审核成功").toResult(waiFuExService.getDeptList());
   }
    @GetMapping("/getNowUser")
    public Map<String, Object> getNowUser() {
        Map<String, Object> result=null;
        result = waiFuExService.getNowUser();
        return Response.SUCCESS.newBuilder().toResult(result);
    }
    @GetMapping("/isTrueCompany")
    public Map<String, Object> isTrueCompany(WorkTicketTableDTO query) {
        Map<String, Object> result=null;
        result = waiFuExService.isTrueCompany(query);
        return Response.SUCCESS.newBuilder().toResult(result);
    }
}
