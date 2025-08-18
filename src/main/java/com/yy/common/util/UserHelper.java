package com.yy.common.util;

import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * 用户信息构建类
 *
 * @author
 **/
@Component
public final class UserHelper {

    @Resource
    private SecurityUtils securityUtils;

    /**
     * 更新创建人/创建人姓名/创建时间
     *
     * @param baseEntity
     * @return void
     * @author
     **/
    public void handleLoginUserInfo(BasePO baseEntity) {
        baseEntity.setNow(new Date());
        baseEntity.setLoginUserId(securityUtils.getLoginUserId());
        baseEntity.setLoginUserName(securityUtils.getLoginUserName());
    }
}
