package com.yy.ppm.produce.mapper;

import java.util.List;
import java.util.Map;
import java.lang.String;

import com.yy.ppm.produce.bean.dto.workTicket.PoundToPortStorageDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import org.apache.ibatis.annotations.Param;

import com.yy.ppm.appWork.bean.po.TYardTallyPO;
import com.yy.ppm.business.bean.po.TBusVehicleReservationPO;
import com.yy.ppm.business.bean.po.TBusVehicleTransferPO;
import com.yy.ppm.produce.bean.po.CustomsLog;
import com.yy.ppm.produce.bean.po.TPoundPO;

/**
 * @ClassName 地磅接口,每次过磅地磅系统调用生产系统，如果是二次过磅生产系统调用海关
 * @author ningjp
 * @version 1.0.0
 * @Description
 * @createTime 2023年10月26日 08:21:00
 */
public interface TPoundMapper {

    TPoundPO getWeighByUnionNo(@Param("unionNo") String unionNo);
    List<TPoundPO> getWeighByDate(@Param("startDate") String startDate,@Param("endDate") String endDate);

	List<PoundToPortStorageDTO> getTallyByUnionId(@Param("noteId") Long noteId);
	List<PoundToPortStorageDTO> getTallyByParams(Map<String,Object> params);
	List<TPrdPortStorageDetailPO> getDetailTallyById(@Param("cargoTallyDetailId") Long cargoTallyDetailId);

    void addLog(Map<String,Object> logParams);

    /**
     * 根据任务号获取实际进港次数
     * @param taskNo
     * @return
     */
    Integer getEnterPortCount(@Param("taskNo") String taskNo);

	int insertCustomsLog(CustomsLog customsLog);

	CustomsLog getCustomsLogBySeqNo(@Param("seqNo") String seqNo);

	CustomsLog getCustomsLogByPoundNoAndGateNo(@Param("poundNo") String poundNo, @Param("gateNo") String gateNo, @Param("seqNo") String seqNo);

	CustomsLog grGateIfPass(@Param("gateNo") String gateNo, @Param("state") String state);

	List<Map<String, Object>> grGateInOut_1(@Param("poundNo") String poundNo);

	TBusVehicleReservationPO getBusVehicleReservation(@Param("truckno") String truckno, @Param("informno") String informno);

	List<TBusVehicleTransferPO> getTBusVehicleTransferPOList(@Param("truckno") String truckno, @Param("informno") String informno);

	TPoundPO getWeighByCondition(@Param("truckno") String truckno, @Param("informno") String informno);

	TYardTallyPO getYardTallyByTruckNo(@Param("truckno") String truckno, @Param("informno") String informno);

	int updateStatus(@Param("taskNo") String taskNo);

	int updateSundryStatus(@Param("truckPlate") String truckPlate);
}
