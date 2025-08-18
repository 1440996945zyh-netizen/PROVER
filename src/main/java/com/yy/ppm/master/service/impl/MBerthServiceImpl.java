package com.yy.ppm.master.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;

import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.bean.dto.MBerthBollardDTO;
import com.yy.ppm.master.service.MBerthService;
import com.yy.ppm.master.mapper.MBerthMapper;
import com.yy.ppm.master.bean.dto.MBerthDTO;
import com.yy.ppm.master.bean.dto.MBerthSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 泊位信息(MBerth)ServiceImpl
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
@Service
public class MBerthServiceImpl implements MBerthService {

    @Resource
    private MBerthMapper mBerthMapper;

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
    public Pages<MBerthDTO> getList(MBerthSearchDTO searchDTO) {

        Pages<MBerthDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mBerthMapper.getList(searchDTO);
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
    public MBerthDTO getDetail(Long id) {
        return mBerthMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MBerthDTO dto) {

        // 泊位code name验证重复
        commonService.isRepeate("M_BERTH", "BERTH_CODE", dto.getBerthCode(), StringUtil.getString(dto.getId()), "泊位编号", null);
        commonService.isRepeate("M_BERTH", "BERTH_NAME", dto.getBerthName(), StringUtil.getString(dto.getId()), "泊位名称", null);

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mBerthMapper.insert(dto) == 1;

            // 修改
        } else {
            // 泊位下有揽庄不能修改
            int count = mBerthMapper.getChildrenData(dto.getId());
            if (count > 0 ) {
                throw new BusinessRuntimeException("泊位下存在缆桩信息，不允许修改~");
            }
            return mBerthMapper.update(dto) == 1;
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
        int count = mBerthMapper.getChildrenData(id);
        if (count > 0 ) {
            throw new BusinessRuntimeException("泊位下存在揽庄信息，不允许删除~");
        }
        return mBerthMapper.deleteById(id) == 1;

    }

    /**
     * 获取缆桩列表（翻页）
     *
     * @param berthId
     * @return 对象列表
     */
    @Override
    public List<MBerthBollardDTO> getBollardList(Long berthId, String bollardName) {

        return mBerthMapper.getBollardList(berthId, bollardName);
    }

    /**
     * 查询单条缆桩记录
     *
     * @param id
     * @return 实体
     */
    @Override
    public MBerthBollardDTO getBollardDetail(Long id) {
        return mBerthMapper.getBollardById(id);
    }

    /**
     * 保存缆桩
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSaveBollard(MBerthBollardDTO dto) {

        // 揽庄code name验证重复
        commonService.isRepeate("M_BERTH_BOLLARD", "BOLLARD_CODE", dto.getBollardCode(),StringUtil.getString(dto.getId()), "泊位缆桩编号", null);
        commonService.isRepeate("M_BERTH_BOLLARD", "BOLLARD_NAME", dto.getBollardName(),StringUtil.getString(dto.getId()), "泊位缆桩名称", null);

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mBerthMapper.insertBollard(dto) == 1;

            // 修改
        } else {
            return mBerthMapper.updateBollard(dto) == 1;
        }

    }

    /**
     * 删除缆桩
     *
     * @param id
     * @return 是否成功
     */
    @Override
    public boolean deleteBollardById(Long id) {

        return mBerthMapper.deleteBollardById(id) == 1;

    }

    /**
     * 查询主泊位信息
     * @return
     */
    @Override
    public List<MBerthDTO> getParentBerth() {
        return mBerthMapper.getParentBerth();
    }

    @Override
    public List<Map<String, Object>> getBollard(Long id) {
        List<Map<String, Object>> list = mBerthMapper.getBollardsByBerthId(id);
        if(!list.isEmpty()){
//            List<Map<String, Object>> result = Lists.newArrayList();
//            list.forEach(e->{
//                Map<String, Object> map = Maps.newHashMap();
//                map.put("label",e.get("LABEL"));
//                map.put("value",e.get("VALUE"));
//                result.add(map);
//            });
//            return result;
            return list;
        }
        MBerthDTO mBerthDTO = mBerthMapper.getById(id);
        String parentCode = mBerthDTO.getParentCode();
        MBerthDTO parentBerth = mBerthMapper.getByBerthCode(parentCode);
        if(ObjectUtil.isEmpty(parentBerth)){
            return Lists.newArrayList();
        }else{
            return mBerthMapper.getBollardsByBerthId(parentBerth.getId());
        }
    }

}

