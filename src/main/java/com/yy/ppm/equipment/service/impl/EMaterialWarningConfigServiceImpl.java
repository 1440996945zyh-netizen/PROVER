package com.yy.ppm.equipment.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigDTO;
import com.yy.ppm.equipment.bean.dto.EMaterialWarningConfigSearchDTO;
import com.yy.ppm.equipment.bean.po.EMaterialWarningConfigPO;
import com.yy.ppm.equipment.mapper.EMaterialWarningConfigMapper;
import com.yy.ppm.equipment.service.EMaterialWarningConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author FanQi
 * @version 1.0
 * @data 2026/3/20 10:50
 * @Description 物资预警配置
 */
@Service
public class EMaterialWarningConfigServiceImpl implements EMaterialWarningConfigService {

   @Autowired
    private EMaterialWarningConfigMapper eMaterialWarningConfigMapper;

    @Resource
    private Snowflake snowflake;

    /**
     * 主列表查询物资预警配置
     * @param searchDTO
     * @return
     */
    @Override
    public Pages<EMaterialWarningConfigDTO> getList(EMaterialWarningConfigSearchDTO searchDTO) {
        Pages<EMaterialWarningConfigDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return eMaterialWarningConfigMapper.selectList(searchDTO);
        });
        return pages;
    }

    /**
     * 根据ID查询物资预警配置
     */
    @Override
    public EMaterialWarningConfigDTO getById(Long id) {
        return eMaterialWarningConfigMapper.selectById(id);
    }

    /**
     * 新增/修改资预警配置
     * @param po
     */
    @Override
    public void save(EMaterialWarningConfigPO po) {
        if(null == po.getId()){
            po.setId(snowflake.nextId());
            eMaterialWarningConfigMapper.add(po);
        }else{
            eMaterialWarningConfigMapper.update(po);
        }
    }

    /**
     * 修改状态
     */
    @Override
    public void updateStatus(EMaterialWarningConfigPO po) {
        eMaterialWarningConfigMapper.updateStatus(po);
    }

    /**
     * 删除资预警配置
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        eMaterialWarningConfigMapper.delete(id);
    }




}
