package com.yy.ppm.example.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleDTO;
import com.yy.ppm.example.bean.dto.BpmApplicationExampleSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description BPM应用示例Mapper
 */
@Mapper
public interface BpmApplicationExampleMapper {

    /**
     * 新增
     */
    @Edit
    void insert(BpmApplicationExampleDTO dto);

    /**
     * 修改
     */
    @Edit
    void update(BpmApplicationExampleDTO dto);

    /**
     * 根据ID删除
     */
    @Edit
    Integer deleteById(Long id);

    /**
     * 根据ID查询详情
     */
    BpmApplicationExampleDTO getDetail(Long id);

    /**
     * 分页查询列表
     */
    Page<BpmApplicationExampleDTO> getList(BpmApplicationExampleSearchDTO searchDTO);

    /**
     * 查询所有数据
     */
    List<BpmApplicationExampleDTO> getAllList();
}