package com.yy.ppm.example.service;

import com.yy.common.page.Pages;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleDTO;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleSearchDTO;

import java.util.List;

/**
 * @Description BPM应用示例Service接口
 */
public interface BpmApplicationExampleService {

    /**
     * 新增
     */
    void insert(BpmApplicationExampleDTO dto);

    /**
     * 修改
     */
    void update(BpmApplicationExampleDTO dto);

    /**
     * 根据ID删除
     */
    void deleteById(Long id);

    /**
     * 根据ID查询详情
     */
    BpmApplicationExampleDTO getDetail(Long id);

    /**
     * 分页查询列表
     */
    Pages<BpmApplicationExampleDTO> getList(BpmApplicationExampleSearchDTO searchDTO);

    /**
     * 查询所有数据
     */
    List<BpmApplicationExampleDTO> getAllList();
}