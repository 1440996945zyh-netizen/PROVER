package com.yy.ppm.flowable.service;

import com.yy.common.page.Pages;
import com.yy.ppm.flowable.bean.dto.BpmUserExpressionSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmUserExpressionPO;

import java.util.List;

/**
 * BPM 用户常用审批语 Service 接口
 */
public interface BpmUserExpressionService {

    /**
     * 创建常用审批语
     * @param expressionPO 审批语信息
     * @return 插入条数
     */
    int createExpression(BpmUserExpressionPO expressionPO);

    /**
     * 更新常用审批语
     * @param expressionPO 审批语信息
     * @return 更新条数
     */
    int updateExpression(BpmUserExpressionPO expressionPO);

    /**
     * 删除常用审批语
     * @param id 编号
     * @return 删除条数
     */
    int deleteExpression(Long id);

    /**
     * 获取常用审批语详情
     * @param id 编号
     * @return 审批语信息
     */
    BpmUserExpressionPO getDetail(Long id);

    /**
     * 获取常用审批语分页列表
     * @param searchDTO 查询参数
     * @return 分页结果
     */
    Pages<BpmUserExpressionPO> getList(BpmUserExpressionSearchDTO searchDTO);


    /**
     * 获取常用审批语精简列表
     * @param searchDTO 查询参数
     * @return 常用审批语精简列表
     */
     List<BpmUserExpressionPO> selectSimpleList(BpmUserExpressionSearchDTO searchDTO);
}
