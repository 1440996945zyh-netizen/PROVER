package com.yy.framework.flowable.strategy.user;

import cn.hutool.core.text.StrPool;
import com.yy.common.flowable.enums.BpmTaskCandidateStrategyEnum;
import com.yy.common.flowable.utils.StrUtils;
import com.yy.framework.flowable.strategy.BpmTaskCandidateStrategy;
import com.yy.ppm.system.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

/**
 * 用户 {@link com.yy.framework.flowable.strategy.BpmTaskCandidateStrategy} 实现类
 *
 * @author kyle
 */
@Component
public class BpmTaskCandidateUserStrategy implements BpmTaskCandidateStrategy {

    @Resource
    private SysUserService sysUserService;

    @Override
    public BpmTaskCandidateStrategyEnum getStrategy() {
        return BpmTaskCandidateStrategyEnum.USER;
    }

    @Override
    public void validateParam(String param) {
        sysUserService.validateUserList(StrUtils.splitToLongSet(param));
    }

    @Override
    public LinkedHashSet<Long> calculateUsers(String param) {
        return new LinkedHashSet<>(StrUtils.splitToLong(param, StrPool.COMMA));
    }

}
