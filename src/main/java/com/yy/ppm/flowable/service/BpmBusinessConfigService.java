package com.yy.ppm.flowable.service;


import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigDTO;
import com.yy.ppm.flowable.bean.dto.BpmBusinessConfigSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmProcessDefinitionInfoPO;

import java.util.List;

/**
 * @Description BPM业务配置Service接口
 */
public interface BpmBusinessConfigService {
    /**
     * 分页查询列表
     */
    Pages<BpmBusinessConfigDTO> getList(BpmBusinessConfigSearchDTO searchDTO);

    /**
     * 新增
     */
    void insert(BpmBusinessConfigDTO dto);

    /**
     * 修改
     */
    void update(BpmBusinessConfigDTO dto);

    /**
     * 根据ID删除
     */
    void deleteById(Long id);



    /**
     * 根据ID查询详情
     */
    BpmBusinessConfigDTO getDetail(Long id);

    /**
     * 根据菜单和流程业务类型获取流程定义
     */
    BpmProcessDefinitionInfoPO getProcDefInfo(Long businessId, String businessTypeCode);
}
