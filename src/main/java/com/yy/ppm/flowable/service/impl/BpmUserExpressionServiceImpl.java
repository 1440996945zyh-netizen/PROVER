package com.yy.ppm.flowable.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.common.service.CommonService;
import com.yy.ppm.flowable.bean.dto.BpmUserExpressionSearchDTO;
import com.yy.ppm.flowable.bean.po.BpmCategoryPO;
import com.yy.ppm.flowable.bean.po.BpmUserExpressionPO;
import com.yy.ppm.flowable.mapper.BpmUserExpressionMapper;
import com.yy.ppm.flowable.service.BpmUserExpressionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static com.yy.common.flowable.constants.ErrorCodeConstants.*; // 假设你有类似的错误常量
import static com.yy.common.flowable.utils.ServiceExceptionUtil.exception;

/**
 * BPM 用户常用审批语 Service 实现类
 */
@Service
@Validated
public class BpmUserExpressionServiceImpl implements BpmUserExpressionService {



    @Resource
    private BpmUserExpressionMapper userExpressionMapper;

    @Resource
    private CommonService commonService;

    @Resource
    private Snowflake snowflake;

    private SecurityUtils securityUtils;
    /**
     * 创建常用审批语
     * @param expressionPO 审批语信息
     * @return
     */
    @Override
    public int createExpression(BpmUserExpressionPO expressionPO) {
        // TODO:填充用户信息和ID

        expressionPO.setId(snowflake.nextId());
        return userExpressionMapper.insert(expressionPO);
    }

    /**
     * 更新常用审批语
     * @param expressionPO 审批语信息
     * @return 更新条数
     */
    @Override
    public int updateExpression(BpmUserExpressionPO expressionPO) {
        // 校验存在性和权限
        validateExpressionExistsAndPermission(expressionPO.getId());

        return userExpressionMapper.updateById(expressionPO);
    }

    /**
     * 删除常用审批语
     * @param id 编号
     * @return 删除条数
     */
    @Override
    public int deleteExpression(Long id) {
        // 校验存在性和权限
        validateExpressionExistsAndPermission(id);
        // 删除
        return userExpressionMapper.deleteById(id);
    }

    /**
     * 获取常用审批语详情
     * @param id 编号
     * @return 审批语信息
     */
    @Override
    public BpmUserExpressionPO getDetail(Long id) {
        BpmUserExpressionPO expression = userExpressionMapper.selectById(id);
        if (expression == null) {
            return null;
        }
        // 权限校验：只能查看自己的或全局的
        Long loginUserId = securityUtils.getLoginUserId(); // TODO: 获取当前登录用户ID
        if (expression.getUserId() != null && !Objects.equals(expression.getUserId(), loginUserId)) {
            throw new RuntimeException("无权查看该审批语");
        }
        return expression;
    }

    /**
     * 获取常用审批语列表
     * @param searchDTO 查询参数
     * @return 审批语列表
     */
    @Override
    public Pages<BpmUserExpressionPO> getList(BpmUserExpressionSearchDTO searchDTO) {
        Pages<BpmUserExpressionPO> pages = PageHelperUtils.limit(searchDTO,()->{
            Long loginUserId = securityUtils.getLoginUserId(); // TODO: 获取当前登录用户ID
            searchDTO.setUserId(loginUserId);
            return userExpressionMapper.getList(searchDTO);
        });
        return pages;
    }


    /**
     * 获取常用审批语精简列表
     * @param searchDTO 查询参数
     * @return 常用审批语精简列表
     */
    @Override
    public List<BpmUserExpressionPO> selectSimpleList(BpmUserExpressionSearchDTO searchDTO) {
        Long loginUserId = securityUtils.getLoginUserId(); // TODO: 获取当前登录用户ID
        searchDTO.setUserId(loginUserId);
        return userExpressionMapper.selectSimpleList(searchDTO);
    }


    /**
     * 校验常用审批语是否存在，并且当前用户有权操作
     *
     * @param id 编号
     * @return 审批语信息
     */
    private BpmUserExpressionPO validateExpressionExistsAndPermission(Long id) {
        BpmUserExpressionPO expression = userExpressionMapper.selectById(id);
        if (expression == null) {
            throw new BusinessRuntimeException("该常用审批语不存在！");
        }
        Long loginUserId = securityUtils.getLoginUserId();

        // 个人常用语：只能本人操作
        if (expression.getUserId() != null && !Objects.equals(expression.getUserId(), loginUserId)) {
            throw new BusinessRuntimeException("无权操作他人审批语！");
        }

        // 全局常用语：仅管理员可操作
//        if (expression.getUserId() == null && !isAdmin) {
//            throw new BusinessRuntimeException("无权操作全局审批语！");
//        }

        return expression;
    }



}
