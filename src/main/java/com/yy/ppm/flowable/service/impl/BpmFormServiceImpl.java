package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Snowflake;
import com.yy.common.flowable.constants.ErrorCodeConstants;
import com.yy.common.flowable.utils.BeanUtils;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.flowable.bean.dto.BpmFormSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import com.yy.ppm.flowable.mapper.BpmFormMapper;
import com.yy.ppm.flowable.service.BpmFormService;
import com.yy.ppm.master.bean.dto.MDictDataDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import java.util.*;

import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;

/**
 * 动态表单 Service 实现类
 */
@Service
@Validated
public class BpmFormServiceImpl implements BpmFormService {

    @Resource
    private BpmFormMapper formMapper;

    @Resource
    private  Snowflake snowflake;

    /**
     * 获得动态表单分页
     * @param bpmFormSearchDTO
     * @return
     */
    @Override
    public Pages<BpmFormPO> getList(BpmFormSearchDTO bpmFormSearchDTO) {

        Pages<BpmFormPO> pages = PageHelperUtils.limit(bpmFormSearchDTO, () -> {
            return formMapper.getList(bpmFormSearchDTO);
        });

        return pages;
    }

    /**
     * 创建动态表单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    @Override
    public int createForm(BpmFormPO createReqVO) {
        createReqVO.setId(snowflake.nextId());
        // 返回
        return formMapper.insert(createReqVO);
    }

    /**
     * 更新动态表单
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public int updateForm(BpmFormPO updateReqVO) {
        // 校验存在
        validateFormExists(updateReqVO.getId());
        // 更新
        return formMapper.updateById(updateReqVO);
    }

    /**
     * 删除动态表单
     *
     * @param id 编号
     */
    @Override
    public int deleteForm(Long id) {
        // 校验存在
        this.validateFormExists(id);
        // 删除
        return formMapper.deleteById(id);
    }

    private void validateFormExists(Long id) {
        if (formMapper.selectById(id) == null) {
            throw exception(ErrorCodeConstants.FORM_NOT_EXISTS);
        }
    }

    /**
     * 查看详情
     *
     * @param id 编号
     * @return 动态表单
     */
    @Override
    public BpmFormPO getDetail(Long id) {
        return formMapper.selectById(id);
    }


    @Override
    public List<BpmFormPO> getFormList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return formMapper.selectByIds(ids);
    }


}
