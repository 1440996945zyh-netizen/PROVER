package com.yy.ppm.flowable.service;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmCategorySearchDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import static com.yy.common.flowable.utils.CollectionUtils.convertMap;

/**
 * BPM 流程分类 Service 接口
 *
 * @author 芋道源码
 */
public interface BpmCategoryService {

    /**
     * 创建流程分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    int createCategory(BpmCategoryPO createReqVO);

    /**
     * 更新流程分类
     *
     * @param updateReqVO 更新信息
     */
    int updateCategory(@Valid BpmCategoryPO updateReqVO);

    /**
     * 删除流程分类
     *
     * @param id 编号
     */
    int deleteCategory(Long id);

    /**
     * 获得流程分类
     *
     * @param id 编号
     * @return BPM 流程分类
     */
    BpmCategoryPO getDetail(Long id);

    /**
     * 获得流程分类分页
     *
     * @param pageReqVO 分页查询
     * @return 流程分类分页
     */
    Pages<BpmCategoryPO> getList(BpmCategorySearchDTO pageReqVO);

    /**
     * 获得流程分类 Map，基于指定编码
     *
     * @param codes 编号数组
     * @return 流程分类 Map
     */
    default Map<String, BpmCategoryPO> getCategoryMap(Collection<String> codes) {
        return convertMap(getCategoryListByCode(codes), BpmCategoryPO::getCode);
    }

    /**
     * 获得流程分类列表，基于指定编码
     *
     * @return 流程分类列表
     */
    List<BpmCategoryPO> getCategoryListByCode(Collection<String> codes);




}
