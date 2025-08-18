package com.yy.ppm.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.gis.dto.onSiteDynamics.Pile;
import com.yy.ppm.master.bean.dto.MStorageStackDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.runpile.bean.po.MStorageStackPositionPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author linqi
 * @Description
 * @Date 2023-06-06 17:19
 */
public interface OnSiteDynamicsMapper {

    /**
     * `艘次表`查询船舶航次
     *
     * @return
     */
    @DS("ag-qhd-imtos")
    List<Map<String, Object>> listVesselVoyage();

    /**
     * 艘次id查`船舶航次信息补充`表
     *
     * @param voyageIds
     * @return
     */
    @DS("master")
    List<Map<String, Object>> listVesselVoyageSupplement(@Param("voyageIds") List<String> voyageIds);

    /**
     * 揽桩id查`缆桩信息`表
     *
     * @param bollardCodes
     * @return
     */
    @DS("master")
    List<Map<String, Object>> listBerthBollard(@Param("bollardCodes") List<String> bollardCodes);

    /**
     * 查询`垛位信息`
     *
     * @return
     */
    List<Map<String, Object>> listStorageStack();
	List<Map<String, Object>> listStorageStack2();//  add by zcc 2023/10/27
	List<Map<String, Object>> listStorageStackForMac(@Param("workDate") String workDate, @Param("classCode") String classCode);//  add by zcc 2023/10/27

    /**
     * 库场code查`库场信息`
     *
     * @param storageCodes
     * @return
     */
    List<Map<String, Object>> listStorage(@Param("storageCodes") List<String> storageCodes);

    /**
     * 垛位code查`垛位坐标信息`
     *
     * @param stackCodes
     * @return
     */
    List<Map<String, Object>> listStorageStackBoundayr(@Param("stackCodes") List<String> stackCodes);

    Map<String, Object> getStack(Long id);

	Map<String, Object> getStackForMacApp(Long id);

    /**
     * 查询`实时车辆表`
     *
     * @return
     */
    List<Map<String, Object>> listLocation();

    /**
     * 车辆id查询`车辆历史表`
     *
     * @param macId
     * @param beginTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> listLocationHistory(@Param("macId") String macId, @Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

    @Edit
    int insertStackYardCoordinate(MStorageStackPositionPO storageStackPosition);

    int deleteStorageStackPosition(@Param("stackId") Long stackId,@Param("ids") List<Long> ids);

    List<TPrdPortStorageDetailPO> listPortStorageDetail(Long stackId);

    List<TPrdPortStorageDetailPO> _listPortStorageDetail(
            @Param("cargoInfoNo") String cargoInfoNo,
            @Param("massId") Long massId,
            @Param("beginWorkDate") Date beginWorkDate,
            @Param("beginClassCode") String beginClassCode,
            @Param("endWorkDate") Date endWorkDate,
            @Param("endClassCode") String endClassCode,
            @Param("processDetailCode") String processDetailCode
    );

    String getCargoInfoLabel(Long cargoInfoId);

    List<Pile> listPileWithBerth();

    List<MStorageStackDTO> getStackListByStackId(Long stackId);

    void addDelUserInfo(@Param("id") Long id,@Param("updateBy") Long updateBy,@Param("updateTime") Date updateTime);
}
