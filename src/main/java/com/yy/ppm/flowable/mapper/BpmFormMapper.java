package com.yy.ppm.flowable.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.flowable.bean.dto.BpmFormSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmFormPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 动态表单 Mapper
 */
@Mapper
public interface BpmFormMapper{

    /**
     * 新增表单
     * @param bpmFormPO
     * @return
     */
    @Edit
    int insert(BpmFormPO bpmFormPO);

    /**
     * 修改表单
     * @param updateObj
     * @return
     */
    @Edit
    int updateById(BpmFormPO updateObj);

    /**
     * 删除表单
     */
    int deleteById(Long id);

    /**
     * 获得动态表单
     *
     * @param id 编号
     * @return 动态表单
     */
    BpmFormPO selectById(Long id);

    /**
     * 获得动态表单分页
     * @param bpmFormSearchDTO
     * @return
     */
    Page<BpmFormPO> getList(BpmFormSearchDTO bpmFormSearchDTO);

    /**
     * 根据表单ids查看多个表单信息
     * @param ids
     * @return
     */
    List<BpmFormPO> selectByIds(@Param("list") Collection<Long> ids);
}
