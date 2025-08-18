package com.yy.framework.aspect;

import cn.hutool.core.convert.Convert;
import com.yy.common.page.PageParameter;
import com.yy.common.util.SecurityUtils;
import com.yy.common.util.str.StringUtil;
import com.yy.framework.annotation.DataScope;
import com.yy.common.enums.CommonEnum;
import com.yy.ppm.auth.bean.dto.UserInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据过滤处理
 *
 * @author yy
 */
@Aspect
@Component
public class DataScopeAspect {
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";


    @Resource
    private SecurityUtils securityUtils;

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) {
        // 获取当前的用户
        UserInfo currentUser = securityUtils.getUserInfo();
        if (StringUtil.isNotNull(currentUser)) {
            // 如果是超级管理员，则不过滤数据
            if (StringUtil.isNotNull(currentUser) && CommonEnum.YesNoMode.YES.getCode().equals(currentUser.getIsSuperadmin())) {
                dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(),
                        controllerDataScope.userAlias(), controllerDataScope.permission());
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     * @param permission 权限字符
     */
    public static void dataScopeFilter(JoinPoint joinPoint, UserInfo user, String deptAlias, String userAlias, String permission) {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<String>();
        // 获取角色信息

        for (HashMap<String, Object> role : user.getRoleList()) {
            String dataScope = StringUtil.getString(role.get("dataScope"));
            // 不是自定义权限 && 权限列表conditions包括当前权限
            if (!DATA_SCOPE_CUSTOM.equals(dataScope) && conditions.contains(dataScope)) {
                continue;
            }
            // controller权限标识不为空 && 当前角色的功能权限不为空 && 当前角色功能权限列表不包含controller权限标识
            if (StringUtil.isNotEmpty(permission) && !StringUtil.isEmpty(role.get("permissions"))
                    && !StringUtil.containsAny((ArrayList<String>)role.get("permissions"), Convert.toStrArray(permission))) {
                continue;
            }
            if (DATA_SCOPE_ALL.equals(dataScope)) { // 全部数据权限
                sqlString = new StringBuilder();
                conditions.add(dataScope);
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(dataScope)) { // 自定义数据权限
                sqlString.append(StringUtil.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ", deptAlias,
                        StringUtil.getLong(role.get("id"))));
            } else if (DATA_SCOPE_DEPT.equals(dataScope)) { // 部门数据权限
                sqlString.append(StringUtil.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) { // 部门及以下数据权限
                // like  以user.getDeptId()开头的部门编号
                sqlString.append(StringUtil.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id like '{}%' )",
                        deptAlias, user.getDeptId(), user.getDeptId()));
            } else if (DATA_SCOPE_SELF.equals(dataScope)) { //仅本人数据权限
                if (StringUtil.isNotBlank(userAlias)) {
                    sqlString.append(StringUtil.format(" OR {}.id = {} ", userAlias, user.getId()));
                } else {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StringUtil.format(" OR {}.dept_id = 0 ", deptAlias));
                }
            }
            conditions.add(dataScope);
        }

        // 多角色情况下，所有角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (StringUtil.isEmpty(conditions)) {
            sqlString.append(StringUtil.format(" OR {}.dept_id = 0 ", deptAlias));
        }

        if (StringUtil.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (StringUtil.isNotNull(params) && params instanceof PageParameter) {
                PageParameter pageParameter = (PageParameter) params;
                pageParameter.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtil.isNotNull(params) && params instanceof PageParameter) {
            PageParameter pageParameter = (PageParameter) params;
            pageParameter.getParams().put(DATA_SCOPE, "");
        }
    }
}
