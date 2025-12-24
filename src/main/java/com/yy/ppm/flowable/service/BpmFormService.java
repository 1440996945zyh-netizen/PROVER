package com.yy.ppm.flowable.service;

import com.yy.common.flowable.utils.CollectionUtils;
import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmFormSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 流程用表单 Service 接口
 */
public interface BpmFormService {
    /**
     * 创建表单
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    int createForm(BpmFormPO createReqVO);

    /**
     * 更新动态表单
     *
     * @param updateReqVO 更新信息
     */
    int updateForm(BpmFormPO updateReqVO);

    /**
     * 删除动态表单
     *
     * @param id 编号
     */
    int deleteForm(Long id);

    /**
     * 查看详情
     *
     * @param id 编号
     * @return 动态表单
     */
    BpmFormPO getDetail(Long id);


    /**
     * 获得动态表单列表
     *
     * @param ids 编号
     * @return 动态表单列表
     */
    List<BpmFormPO> getFormList(Collection<Long> ids);

    /**
     * 获得动态表单 Map
     *
     * @param ids 编号
     * @return 动态表单 Map
     */
    default Map<Long, BpmFormPO> getFormMap(Collection<Long> ids) {
        return CollectionUtils.convertMap(this.getFormList(ids), BpmFormPO::getId);
    }

    /**
     * 获得动态表单分页
     * @param bpmFormSearchDTO
     * @return
     */
    Pages<BpmFormPO> getList(BpmFormSearchDTO bpmFormSearchDTO);
}
