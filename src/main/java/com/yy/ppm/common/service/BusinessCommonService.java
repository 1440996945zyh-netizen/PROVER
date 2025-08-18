package com.yy.ppm.common.service;

import com.yy.ppm.produce.bean.po.TPrdPortStorageDetailPO;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 业务用通用Service
 */
@Validated
public interface BusinessCommonService {

    /**
     * 票货验证
     *
     * @return
     */
    public Long getBusCargoInfoId(Map<String, Object> map);

    /**
     * 票货验证
     *
     * flag + -
     * @return
     */
    public int updateSurplusBusCargoInfo(Long id, Long quantity, BigDecimal ton, String flag);

    /**
     * 根据航次子表信息获取航次主、子信息
     *
     * @return
     */
    public Map<String, Object> getVoyageInfoByItemId(Long id);

    /**
     * 机械配工信息
     */
    public List<Map<String, Object>> getWorkPlanEquipmentDispatch(Long workPlanId, String workPositionCode);

    /**
     * 新增港存明细
     *
     * @param portStorageDetails
     */
    void insertPortStorageDetail(@NotEmpty(message = "港存明细不能为空") List<TPrdPortStorageDetailPO> portStorageDetails);

    /**
     * 删除港存明细
     *
     * @param portStorageDetailIds
     */
    void deletePortStorageDetail(@NotEmpty(message = "港存明细ID不能为空") List<Long> portStorageDetailIds);
}
