package com.yy.ppm.produce.service;

import com.yy.ppm.produce.bean.dto.workTicket.PoundToPortStorageDTO;
import com.yy.ppm.produce.bean.po.TPoundPO;

import java.util.Map;
import java.util.List;

/**
 * @ClassName 地磅接口,每次过磅地磅系统调用生产系统，如果是二次过磅生产系统调用海关
 * @author ningjp
 * @version 1.0.0
 * @Description
 * @createTime 2023年10月26日 08:21:00
 */
public interface TPoundService {

    Map<String, Object> pound2PPM(String unionNo);

    List<PoundToPortStorageDTO> getTallyByParams(Map<String,Object> params);

    void updatePortStage(List<PoundToPortStorageDTO> list);

    Map<String, Object> ppm2Pound(String poundNo, String gateNo, String seqNo);

	//void XMLInfoWLJKRet(CustomsBaseMessage baseMessage);

	Map<String, Object> grGateInOut(String poundNo, String gateNo, String state, String truckno, String informno);

	Map<String, Object> grGateIfPass(String gateNo, String state);
}
