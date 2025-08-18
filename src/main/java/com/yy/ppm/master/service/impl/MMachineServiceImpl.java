package com.yy.ppm.master.service.impl;

import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.UserHelper;

import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.master.service.MMachineService;
import com.yy.ppm.master.mapper.MMachineMapper;
import com.yy.ppm.master.bean.dto.MMachineDTO;
import com.yy.ppm.master.bean.dto.MMachineSearchDTO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import cn.hutool.core.lang.Snowflake;

import jakarta.annotation.Resource;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 机械信息(MMachine)ServiceImpl
 * @Description
 * @createTime 2023年06月05日 17:28:00
 */
@Service
public class MMachineServiceImpl implements MMachineService {

    @Resource
    private MMachineMapper mMachineMapper;

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
    public Pages<MMachineDTO> getList(MMachineSearchDTO searchDTO) {

        Pages<MMachineDTO> pages = PageHelperUtils.limit(searchDTO, () -> {
            return mMachineMapper.getList(searchDTO);
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
    public MMachineDTO getDetail(Long id) {
        return mMachineMapper.getById(id);
    }

    /**
     * 保存
     *
     * @param dto
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean doSave(MMachineDTO dto) {

        // 机械code name验证重复
        commonService.isRepeate("M_MACHINE", "MAC_CODE", dto.getMacCode(), StringUtil.getString(dto.getId()), "机械编号", null);
        commonService.isRepeate("M_MACHINE", "MAC_NAME", dto.getMacName(), StringUtil.getString(dto.getId()), "机械名称", null);
        // 新增
        if (dto.getId() == null) {
            dto.setId(snowflake.nextId());
            return mMachineMapper.insert(dto) == 1;

            // 修改
        } else {
            return mMachineMapper.update(dto) == 1;
        }

    }

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean deleteById(Long id) {

        //检验有没有理货
        int count1 = mMachineMapper.getTallyById(id);
        if(count1>0){
            throw new BusinessRuntimeException("已被使用，无法删除，请停用");
        }
        //检验有没有签票
        int count2= mMachineMapper.getTicketById(id);
        if(count2>0){
            throw new BusinessRuntimeException("已被使用，无法删除，请停用");
        }

        return mMachineMapper.deleteById(id) == 1;

    }
}

