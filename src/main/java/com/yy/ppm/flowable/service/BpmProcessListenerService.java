package com.yy.ppm.flowable.service;

import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmModelDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerSearchDTO;

import java.util.List;

/**
 * @Description BPM流程监听器Service接口
 */
public interface BpmProcessListenerService {

    /**
     * 分页查询列表
     */
    Pages<BpmProcessListenerDTO> getList(BpmProcessListenerSearchDTO searchDTO);

    /**
     * 新增
     */
    void insert(BpmProcessListenerDTO dto);

    /**
     * 修改
     */
    void update(BpmProcessListenerDTO dto);

    /**
     * 根据ID删除
     */
    void deleteById(Long id);

    /**
     * 根据ID查询详情
     */
    BpmProcessListenerDTO getDetail(Long id);

    List<BpmModelDTO> getListenerModel(Long id);
}
