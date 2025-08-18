package com.yy.ppm.produce.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yy.common.log.MicroLogger;
import com.yy.ppm.produce.service.TPoundService;

/**
 * @ClassName 地磅接口Controller
 * @author ningjp
 * @version 1.0.0
 * @Description
 * @createTime 2023年10月26日 08:21:00
 */
@RestController
@RequestMapping("/api/v1/interface/tPoundInterface")
public class TPoundController {

	/**
	 * 日志组件
	 **/
	private static final MicroLogger LOGGER = new MicroLogger(TPoundController.class);

    @Autowired
    private TPoundService tPoundService;

    /**
     * 地磅过磅调用生产系统
     * @param unionNo
     * @return
     */
    @PostMapping("/pound2PPM")
    public Map<String, Object> pound2PPM(String unionNo) {
        final String methodName = "TPoundController:pound2PPM";
		LOGGER.enter(methodName + "[start]", "unionNo:" +  unionNo);

		Map<String, Object> res = tPoundService.pound2PPM(unionNo);

        LOGGER.exit(methodName);
        return res;
    }

    @PostMapping("/ppm2Pound")
    public Map<String, Object> ppm2Pound(String poundNo, String gateNo, String seqNo) {
        final String methodName = "TPoundController:ppm2Pound";
        LOGGER.enter(methodName + "[start]", "poundNo:" +  poundNo + ", gateNo:" +  gateNo + ", seqNo:" +  seqNo);

        Map<String, Object> res  = tPoundService.ppm2Pound(poundNo, gateNo, seqNo);

        LOGGER.exit(methodName);
        //return Response.SUCCESS.newBuilder().out("操作成功").toResult();
        return res;
    }
    
    @PostMapping("/grGateInOut")
    public Map<String, Object> grGateInOut(String unionNo, String gateNo, String state, String truckno, String informno) {

        final String methodName = "TPoundController:grGateInOut";
        LOGGER.enter(methodName + "[start]", "unionNo:" +  unionNo
        		 + ", gateNo:" +  gateNo
        		 + ", state:" +  state
        		 + ", truckno:" +  truckno
        		 + ", informno:" +  informno);

        Map<String, Object> res  = tPoundService.grGateInOut(unionNo, gateNo, state, truckno, informno);

        LOGGER.exit(methodName);
        return res;    
    }
    
    @PostMapping("/grGateIfPass")
    public Map<String, Object> grGateIfPass(String gateNo, String state) {
        final String methodName = "TPoundController:grGateIfPass";
        LOGGER.enter(methodName + "[start]", "gateNo:" +  gateNo + ", state:" +  state);

        Map<String, Object> res  = tPoundService.grGateIfPass(gateNo, state);

        LOGGER.exit(methodName);
        return res;  
    }
    
    
}

