package com.yy.ppm.flowable.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerDTO;
import com.yy.ppm.flowable.bean.dto.BpmProcessListenerSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BpmProcessListenerMapper {

    /**
     * 分页查询列表
     */
    Page<BpmProcessListenerDTO> getList(BpmProcessListenerSearchDTO searchDTO);

    /**
     * 新增
     */
    @Edit
    void insert(BpmProcessListenerDTO dto);

    /**
     * 修改
     * @param dto
     */
    @Edit
    void update(BpmProcessListenerDTO dto);

    /**
     * 删除
     * @param id
     * @return
     */
    @Edit
    Integer deleteById(@Param("id") Long id);

    /**
     * 查看详情
     * @param id
     * @return
     */
    BpmProcessListenerDTO getDetail(@Param("id") Long id);
}
