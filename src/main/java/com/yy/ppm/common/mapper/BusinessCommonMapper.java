package com.yy.ppm.common.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import com.yy.ppm.produce.bean.po.TPrdPortStoragePO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 业务通用mapper
 */
public interface BusinessCommonMapper {

    /**
     * 查询票货id
     */
    Long getBusCargoInfoId(Map<String, Object> map);

    /**
     * 合并票货
     *
     * @param map
     * @return
     */
    int updateSurplusBusCargoInfo(Map<String, Object> map);

    /**
     * 根据航次子表信息获取航次主、子信息
     *
     * @return
     */
    public Map<String, Object> getVoyageInfoByItemId(@Param("id") Long id);

    /**
     * 机械配工信息(一次）
     */
    public List<Map<String, Object>> getWorkPlanEquipmentDispatch(Long workPlanId, String workPositionCode);

    List<Map<String, Object>> listStorehouse(@Param("storehouseIds") List<Long> storehouseIds);

    List<Map<String, Object>> listRegion(@Param("regionIds") List<Long> regionIds);

    List<Map<String, Object>> listMass(@Param("massIds") List<Long> massIds);

    TPrdPortStoragePO getPortStorage(Map<String, Object> portStorageCompositeKey);

    @Edit
    int insertPortStorage(TPrdPortStoragePO portStorage);

    @Edit
    int insertPortStorages(List<TPrdPortStoragePO> list);

    List<TPrdPortStorageDetailPO> listPortStorageDetail(Long portStorageId);

    @Edit
    void insertPortStorageDetail(@Param("prdPortStorageDetails") List<TPrdPortStorageDetailPO> prdPortStorageDetails);

    @Edit
    int updatePortStorage(TPrdPortStoragePO portStorage);
    @Edit
    int updatePortStorages(List<TPrdPortStoragePO> list);

    List<TPrdPortStorageDetailPO> listPortStorageDetailById(@Param("portStorageDetailIds") List<Long> portStorageDetailIds);

    int deletePortStorageDetail(@Param("portStorageDetailIds") List<Long> portStorageDetailIds);

    int deletePortStorage(Long portStorageId);

    TBusCargoInfoDTO getCargoInfoById(@Param("cargoInfoId") Long cargoInfoId);
}
