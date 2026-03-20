package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.EMaterialCodeDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarningConfigPO;

/**
 * @author FanQi
 * @data 2026/3/20 11:07
 * @version 1.0
 * @Description 物资预警配置
 */

public interface EMaterialWarningConfigService {

    /**
     * 主列表查询物资预警配置
     */
    Pages<EMaterialWarningConfigDTO> getList(EMaterialWarningConfigSearchDTO searchDTO);

    /**
     * 根据ID查询物资预警配置
     */
    EMaterialWarningConfigDTO getById(Long id);

    /**
     * 新增/修改资预警配置
     * @param dto
     */
    void save(EMaterialWarningConfigPO po);


    /**
     * 修改状态
     */
    void updateStatus(EMaterialWarningConfigPO po);

    /**
     * 删除资预警配置
     * @param id
     */
    void deleteById(Long id);


}
