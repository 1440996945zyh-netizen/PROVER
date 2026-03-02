package com.yy.ppm.flowable.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.flowable.bean.dto.BpmUserExpressionSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmUserExpressionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * BPM 用户常用审批语 Mapper
 */
@Mapper
public interface BpmUserExpressionMapper {

    /**
     * 新增常用审批语
     *
     * @param expressionPO 审批语信息
     * @return 影响行数
     */
    @Edit
    int insert(BpmUserExpressionPO expressionPO);

    /**
     * 更新常用审批语
     *
     * @param expressionPO 更新对象
     * @return 影响行数
     */
    @Edit
    int updateById(BpmUserExpressionPO expressionPO);

    /**
     * 根据 ID 删除常用审批语
     *
     * @param id 编号
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 根据 ID 查询常用审批语
     *
     * @param id 编号
     * @return 审批语信息
     */
    BpmUserExpressionPO selectById(Long id);

    /**
     * 分页查询常用审批语
     *
     * @param searchDTO 查询参数
     * @return 常用审批语分页
     */
    Page<BpmUserExpressionPO> getList(BpmUserExpressionSearchDTO searchDTO);

    /**
     * 获取当前用户可用的常用审批语精简列表
     *
     * @param userId 当前登录用户ID
     * @return 常用审批语列表
     */
    List<BpmUserExpressionPO> selectSimpleList(@Param("userId") Long userId);

}
