package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MStorageDTO;
import com.yy.ppm.master.bean.dto.MStorageSearchDTO;
import com.yy.ppm.master.bean.dto.MStorageStackDTO;
import com.yy.ppm.master.mapper.MStorageMapper;
import com.yy.ppm.master.service.MStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 库场信息(MStorage)ServiceImpl
 * @Description
 * @createTime 2023年06月05日 17:38:00
 */
@Service
public class MStorageServiceImpl implements MStorageService {

    @Resource
    private MStorageMapper mStorageMapper;

    @Resource
    private Snowflake snowflake;

    @Resource
    private CommonService commonService;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MStorageDTO> getList(MStorageSearchDTO searchDTO) {

        Pages<MStorageDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mStorageMapper.getList(searchDTO);
        });

        return pages;
    }

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public MStorageDTO getDetail(Long id) {
        return mStorageMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MStorageDTO dto) {

        // 库场code name验证重复
        commonService.isRepeate("M_STORAGE", "STORAGE_CODE", dto.getStorageCode(), StringUtil.getString(dto.getId()), "库场编号~", null);
        commonService.isRepeate("M_STORAGE", "STORAGE_NAME", dto.getStorageName(), StringUtil.getString(dto.getId()), "库场名称~", null);

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mStorageMapper.insert(dto) == 1;

            // 修改
        } else {
            // 库场下有垛位信息，不允许修改
            int count = mStorageMapper.getChildrenData(dto.getStorageCode());
            if (count > 0) {
                throw new BusinessRuntimeException("库场下有垛位信息，不允许修改~");
            }
            return mStorageMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        MStorageDTO detail = getDetail(id);
        // 库场下有垛位信息，不允许删除
        int count = mStorageMapper.getChildrenData(detail.getStorageCode());
        if (count > 0) {
            throw new BusinessRuntimeException("库场下有垛位信息，不允许删除~");
        }
        return mStorageMapper.deleteById(id) == 1;

    }


    /**
     * 获取垛位列表（翻页）
     *
     * @param storageCode
     * @return 对象列表
     */
    @Override
    public List<MStorageStackDTO> getStackList(String storageCode, String stackName) {

        return mStorageMapper.getStackList(storageCode, stackName);
    }

    /**
     * 查询单条垛位记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public MStorageStackDTO getStackDetail(Long id) {
        return mStorageMapper.getStackById(id);
    }

    /**
     * 保存垛位
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSaveStack(MStorageStackDTO dto) {

        // 垛位code name验证重复
        commonService.isRepeate("M_STORAGE_STACK", "STACK_CODE", dto.getStackCode(), StringUtil.getString(dto.getId()), "垛位编号~", null);
        commonService.isRepeate("M_STORAGE_STACK", "STACK_NAME", dto.getStackName(), StringUtil.getString(dto.getId()), "垛位名称~", null);
        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mStorageMapper.insertStack(dto) == 1;

            // 修改
        } else {
            return mStorageMapper.updateStack(dto) == 1;
        }

    }

    /**
     * 删除垛位
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteStackById(Long id) {

        return mStorageMapper.deleteStackById(id) == 1;

    }
}

