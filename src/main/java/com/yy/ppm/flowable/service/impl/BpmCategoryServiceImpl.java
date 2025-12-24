package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.ObjUtil;
import com.yy.common.flowable.utils.BeanUtils;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.flowable.bean.dto.BpmCategorySearchDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.mapper.BpmCategoryMapper;
import com.yy.ppm.flowable.service.BpmCategoryService;
import com.yy.ppm.flowable.service.BpmModelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.yy.common.flowable.constants.ErrorCodeConstants.*;
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;

/**
 * BPM 流程分类 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class BpmCategoryServiceImpl implements BpmCategoryService {

    @Resource
    private BpmCategoryMapper bpmCategoryMapper;

    @Resource
    private BpmModelService modelService;

    @Resource
    private CommonService commonService;

    @Resource
    private Snowflake snowflake;

    /**
     * 创建流程分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    @Override
    public int createCategory(BpmCategoryPO createReqVO) {
        // 校验唯一
        commonService.isRepeate("bpm_category", "name", createReqVO.getName(), StringUtil.getString(createReqVO.getId()), "流程分类名称", null);
        commonService.isRepeate("bpm_category", "code", createReqVO.getCode(), StringUtil.getString(createReqVO.getId()), "流程分类编号", null);

        // 插入
        createReqVO.setId(snowflake.nextId());
        return bpmCategoryMapper.insert(createReqVO);
    }

    /**
     * 更新流程分类
     *
     * @param updateReqVO 更新信息
     */
    @Override
    public int updateCategory(BpmCategoryPO updateReqVO) {
        // 校验存在防止并发
        validateCategoryExists(updateReqVO.getId());
        // 重复性验证
        commonService.isRepeate("bpm_category", "name", updateReqVO.getName(), StringUtil.getString(updateReqVO.getId()), "流程分类名称", null);
        commonService.isRepeate("bpm_category", "code", updateReqVO.getCode(), StringUtil.getString(updateReqVO.getId()), "流程分类编号", null);
        // 更新
        return bpmCategoryMapper.updateById(updateReqVO);
    }

    /**
     * 删除流程分类
     *
     * @param id 编号
     */
    @Override
    public int deleteCategory(Long id) {
        // 校验存在
        BpmCategoryPO category = validateCategoryExists(id);
        // 校验是否被流程模型使用
        Long count = modelService.getModelCountByCategory(category.getCode());
        if (count > 0) {
            throw exception(CATEGORY_DELETE_FAIL_MODEL_USED, category.getName());
        }
        // 删除
        return bpmCategoryMapper.deleteById(id);
    }

    private BpmCategoryPO validateCategoryExists(Long id) {
        BpmCategoryPO category = bpmCategoryMapper.selectById(id);
        if (category == null) {
            throw exception(CATEGORY_NOT_EXISTS);
        }
        return category;
    }

    /**
     * 查看详情
     *
     * @param id 编号
     * @return BPM 流程分类
     */
    @Override
    public BpmCategoryPO getDetail(Long id) {
        return bpmCategoryMapper.selectById(id);
    }

    /**
     * 获得流程分类分页
     *
     * @param pageReqVO 分页查询
     * @return 流程分类分页
     */
    @Override
    public Pages<BpmCategoryPO> getList(BpmCategorySearchDTO pageReqVO) {
        Pages<BpmCategoryPO> pages = PageHelperUtils.limit(pageReqVO,()->{
            return bpmCategoryMapper.getList(pageReqVO);
        });
        return pages;
    }

    /**
     * 获得流程分类列表，基于指定编码
     *
     * @return 流程分类列表
     */
    @Override
    public List<BpmCategoryPO> getCategoryListByCode(Collection<String> codes) {
        if (CollUtil.isEmpty(codes)) {
            return Collections.emptyList();
        }
        return bpmCategoryMapper.selectListByCode(codes);
    }
}
