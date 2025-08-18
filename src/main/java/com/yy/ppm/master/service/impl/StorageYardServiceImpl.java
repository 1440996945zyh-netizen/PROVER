package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.log.MicroLogger;
import com.yy.common.util.PinYin4jUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.mapper.CommonMapper;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.StorageYardDTO;
import com.yy.ppm.master.mapper.StorageYardMapper;
import com.yy.ppm.master.service.StorageYardService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class StorageYardServiceImpl implements StorageYardService {

    /**
     * 日志组件
     */
    private static final MicroLogger LOGGER = new MicroLogger(StorageYardServiceImpl.class);

    @Autowired
    private Snowflake snowflake;

    @Resource
    private StorageYardMapper storageYardMapper;

    @Resource
    private CommonMapper commonMapper;

    @Resource
    private CommonService commonService;

    @Override
    public StorageYardDTO getById(Long id) {
        final String methodName = "StorageYardServiceImpl:getById";
        LOGGER.enter(methodName, "业务执行");

        StorageYardDTO storageYardDTO = storageYardMapper.getById(id);

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return storageYardDTO;
    }

    /**
     * 查询菜单TreeTable
     *
     * @param parentId 父id
     * @return
     */
    @Override
    public List<StorageYardDTO> getByParentId(Long parentId) {
        final String methodName = "StorageYardServiceImpl:getByParentId";
        LOGGER.enter(methodName, "业务执行");

        List<StorageYardDTO> resultList = storageYardMapper.getByParentId(parentId);
        resultList.forEach(item -> {
            if (storageYardMapper.getByParentId(item.getId()).size() > 0) {
                item.setIsLeaf(false);
            } else {
                item.setIsLeaf(true);
            }
        });

        LOGGER.exit(methodName, StringUtils.EMPTY);
        return resultList;
    }

    /**
     * 保存库场
     */
    @Override
    @Transactional
    public Long save(StorageYardDTO storageYardDTO) {
        final String methodName = "StorageYardServiceImpl:save";
        LOGGER.enter(methodName, "业务执行");

        // 如果没有排序号，强制设置
        if (storageYardDTO.getSortNum() == null) {
            storageYardDTO.setSortNum(commonService.getNextValue("m_storage_yard", "sort_num", ""));
        }

        // 如果速记码没有强制设置
        if (StringUtils.isEmpty(storageYardDTO.getShortCd())) {
            storageYardDTO.setShortCd(PinYin4jUtils.convertCnzhToPinYinVal(storageYardDTO.getStorageYardNm()));
        }

        // 垛的场合
        if ("3".equals(storageYardDTO.getStorageYardLevel())) {
            storageYardDTO.setIsInnerStorageYard("");
            storageYardDTO.setIsRent("");
        }

        // 新增的场合
        if (storageYardDTO.getId() == null) {
            //id
            storageYardDTO.setId(snowflake.nextId());
            // 如果没有等级就是第一级，场
            if (StringUtils.isEmpty(storageYardDTO.getStorageYardLevel())) {
                storageYardDTO.setStorageYardLevel("1");
                storageYardDTO.setParentId(-1L);
            }
            storageYardMapper.insert(storageYardDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
            // 修改的场合
        } else {

            storageYardMapper.update(storageYardDTO);

            LOGGER.exit(methodName, StringUtils.EMPTY);
        }
        return storageYardDTO.getId();
    }

    /**
     * 删除
     */
    @Override
    @Transactional
    public Integer deleteById(Long id) {

        final String methodName = "StorageYardServiceImpl:deleteById";
        LOGGER.enter(methodName, "业务执行");

        // 查询是否有子级别
        if (commonMapper.getCountByParentId("m_storage_yard", id) > 0) {
            throw new BusinessRuntimeException("有子区域，不能删除。");
        }

        // 删除库场
        int count = commonMapper.deleteById("m_storage_yard", id);
        // 删除库场坐标
        commonMapper.delete("m_storage_yard_coordinate", "STORAGE_YARD_GID", String.valueOf(id));

        LOGGER.exit(methodName, StringUtils.EMPTY);

        return count;

    }


}
