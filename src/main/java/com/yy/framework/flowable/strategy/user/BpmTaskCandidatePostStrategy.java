package com.yy.framework.flowable.strategy.user;
import com.yy.common.flowable.enums.BpmTaskCandidateStrategyEnum;
import com.yy.common.flowable.utils.StrUtils;
import com.yy.framework.flowable.strategy.BpmTaskCandidateStrategy;
import com.yy.ppm.system.service.SysRoleService;
import com.yy.ppm.system.service.SysUserService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色 {@link BpmTaskCandidateStrategy} 实现类
 *
 * @author kyle
 */
@Component
public class BpmTaskCandidatePostStrategy implements BpmTaskCandidateStrategy {

    @Resource
    private SysUserService sysUserService;

    @Override
    public BpmTaskCandidateStrategyEnum getStrategy() {
        return BpmTaskCandidateStrategyEnum.POST;
    }

    // 岗位
    @Override
    public void validateParam(String param) {
        Set<String> postKeys = changeStrToSet(param);
        sysUserService.validPostList(postKeys);
    }

    // 获得拥有多个岗位的用户编号集合
    @Override
    public Set<Long> calculateUsers(String param) {
        Set<String> postKeys = changeStrToSet(param);
        return sysUserService.getUserIdListByPostKeys(postKeys);
    }

    Set<String> changeStrToSet(String param){
        Set<String> postKeys = new HashSet<>();
        if (StringUtils.isNotBlank(param)) { // 先判断参数非空，避免空指针
            // split 方法：按逗号拆分（可根据实际分隔符调整，比如空格、竖线 | 等）
            String[] keyArray = param.split(",");
            // 过滤空字符串（避免拆分出 "" 这种无效值）
            for (String key : keyArray) {
                String trimKey = key.trim(); // 去除首尾空格
                if (StringUtils.isNotBlank(trimKey)) {
                    postKeys.add(trimKey);
                }
            }
        }
        return postKeys;
    }

}
