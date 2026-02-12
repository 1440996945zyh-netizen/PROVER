package com.yy.framework.flowable.strategy.user;
import com.yy.common.flowable.enums.BpmTaskCandidateStrategyEnum;
import com.yy.common.flowable.utils.StrUtils;
import com.yy.framework.flowable.strategy.BpmTaskCandidateStrategy;
import com.yy.ppm.system.service.SysRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 角色 {@link com.yy.framework.flowable.strategy.BpmTaskCandidateStrategy} 实现类
 *
 * @author kyle
 */
@Component
public class BpmTaskCandidateRoleStrategy implements BpmTaskCandidateStrategy {

    @Resource
    private SysRoleService sysRoleService;

    @Override
    public BpmTaskCandidateStrategyEnum getStrategy() {
        return BpmTaskCandidateStrategyEnum.ROLE;
    }

    // 角色
    @Override
    public void validateParam(String param) {
        Set<Long> roleIds = StrUtils.splitToLongSet(param);
        sysRoleService.validRoleList(roleIds);
    }

    // 获得拥有多个角色的用户编号集合
    @Override
    public Set<Long> calculateUsers(String param) {
        Set<Long> roleIds = StrUtils.splitToLongSet(param);
        return sysRoleService.getUserIdListByRoleIds(roleIds);
    }

}
