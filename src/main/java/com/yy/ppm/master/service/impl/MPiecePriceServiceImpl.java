package com.yy.ppm.master.service.impl;

import com.github.pagehelper.Page;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import com.yy.ppm.master.mapper.MWorkProcessMapper;
import com.yy.ppm.master.service.MPiecePriceService;
import com.yy.ppm.master.mapper.MPiecePriceMapper;
import com.yy.ppm.master.bean.dto.MPiecePriceDTO;
import com.yy.ppm.master.bean.dto.MPiecePriceSearchDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 计件单价(MPiecePrice)ServiceImpl
 * @Description
 * @createTime 2023年09月15日 11:32:00
 */
@Service
public class MPiecePriceServiceImpl implements MPiecePriceService {

    @Resource
    private MPiecePriceMapper mPiecePriceMapper;

    @Resource
    private Snowflake snowflake;
    @Resource
    private MWorkProcessMapper mWorkProcessMapper;

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    @Override
    public Pages<MPiecePriceDTO> getList(MPiecePriceSearchDTO searchDTO) {

        Pages<MPiecePriceDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mPiecePriceMapper.getList(searchDTO);
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
    public MPiecePriceDTO getDetail(Long id) {
        return mPiecePriceMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MPiecePriceDTO dto) {

        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mPiecePriceMapper.insert(dto) == 1;

            // 修改
        } else {
            return mPiecePriceMapper.update(dto) == 1;
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

        return mPiecePriceMapper.deleteById(id) == 1;

    }

    /***
     * 获取主作业过程下拉列表框
     * @return
     */
    @Override
    public List<Map<String, Object>> getWorkProcessSelect(MWorkProcessSearchDTO mWorkProcessSearchDTO) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        Page<MWorkProcessDTO> list = mWorkProcessMapper.getList(mWorkProcessSearchDTO);
        if(CollectionUtils.isEmpty(list)){
            return result;
        }
        list.forEach(item->{
            HashMap<String, Object> tmpHashMap = new HashMap<>();
            tmpHashMap.put("label",item.getProcessNm());
            tmpHashMap.put("newProcess",item.getNewProcess());
            tmpHashMap.put("value",item.getId());
            result.add(tmpHashMap);
        });
        return result;
    }
}

