package com.yy.ppm.business.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TBusCargoMixRecordQueryDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageDTO;
import com.yy.ppm.business.bean.dto.TBusCargoMix.TPrdPortStorageQueryDTO;

import java.util.List;
import java.util.Map;

public interface TBusCargoMixService {

    /**
     * 查询港存
     *
     * @param query
     * @return
     */
    List<TPrdPortStorageDTO> listPortStorage(TPrdPortStorageQueryDTO query);

    /**
     * 查询合同
     *
     * @param cargoInfoIds
     * @return
     */
    List<Map<String, Object>> contracts(List<Long> cargoInfoIds);

    /**
     * 混配新票货
     *
     * @param dto
     */
    void mix(TBusCargoMixRecordDTO dto);

    /**
     * 查询混配记录
     *
     * @param parameter
     * @param query
     * @return
     */
    Pages<TBusCargoMixRecordDTO> listMix(PageParameter parameter, TBusCargoMixRecordQueryDTO query);

    /**
     * 回显
     *
     * @param id
     * @return
     */
    TBusCargoMixRecordDTO getMix(Long id);

    /**
     * 删除混配
     *
     * @param id
     */
    void deleteMix(Long id);

    /**
     * 审核
     *
     * @param id
     */
    void review(Long id);

    /**
     * 销审
     *
     * @param id
     */
    void cancelReview(Long id);
}
