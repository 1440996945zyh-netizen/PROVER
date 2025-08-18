package com.yy.common.jwt;

import com.yy.common.util.Assert;
import com.yy.common.util.JSONUtils;
import com.yy.common.util.JasyptUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于JWT概念设计实现, 基于PBE混淆加密
 *
 * @author
 **/
public final class Jwt {

    private final JwtBean bean;

    public Jwt(JwtBean bean) {
        this.bean = bean;
    }

    /**
     * 创建
     **/
    public static JwtBuilder create() {
        return new JwtBuilder();
    }

    /**
     * 构建者
     **/
    public static class JwtBuilder {

        /**
         * 用户账号
         **/
        private String account;

        /**
         * 用户名称
         **/
        private String userName;

        /**
         * 是否超级管理源
         **/
        private String isSuperadmin;


        /**
         * 用户类型 APP PC
         **/
        private String loginType;

        /**
         * Token过期时间,单位:分
         **/
        private short expires;
        /**
         * 其他附属数据
         **/
        private Map<String, Object> kv;

        public JwtBuilder() {
            this.kv = new HashMap<>(8);
        }

        public JwtBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public JwtBuilder setLoginType(String loginType) {
            this.loginType = loginType;
            return this;
        }

        public JwtBuilder setAccount(String account) {
            this.account = account;
            return this;
        }

        public JwtBuilder setExpires(short expires) {
            this.expires = expires;
            return this;
        }

        public JwtBuilder setIsSuperadmin(String isSuperadmin) {
            this.isSuperadmin = isSuperadmin;
            return this;
        }

        public JwtBuilder addKv(String key, String value) {
            kv.put(key, value);
            return this;
        }

        public JwtBuilder addAllKv(Map<String, Object> allKv) {
            kv.putAll(allKv);
            return this;
        }

        public Jwt build() {
            Assert.isNotBlank(account, "account is empty!");
            Assert.isNotBlank(userName, "userName is empty!");
            Assert.isNotBlank(loginType, "loginType is empty!");
            Assert.neq(this.expires, 0, "expires is ZERO!");
            Assert.gt(this.expires, 0, "expires lt is ZERO!");

            DateTime expiresDate = new DateTime().plusMinutes(expires);
            JwtBean bean = new JwtBean();
            bean.setAccount(account);
            bean.setUserName(userName);
            bean.setLoginType(loginType);
            bean.setExpires(expires);
            bean.setIsSuperadmin(isSuperadmin);
            bean.setExpiresDate(expiresDate.toDate().getTime());
            bean.setKv(kv);
            return new Jwt(bean);
        }
    }

    /**
     * Jwt消息Bean
     **/
    @Getter
    @Setter
    @ToString
    public static class JwtBean {

        /**
         * 用户账号
         **/
        private String account;

        /**
         * 用户名称
         **/
        private String userName;

        /**
         * 登录类型
         **/
        private String loginType;

        /**
         * 是否超级管理源
         **/
        private String isSuperadmin;

        /**
         * Token过期时间,单位:分
         **/
        private short expires;

        /**
         * token失效日期
         **/
        private long expiresDate;

        /**
         * 其他附属数据
         **/
        private Map<String, Object> kv;
    }

    /**
     * 签名生成Token
     *
     * @return 签名token
     * @author
     **/
    public String sign() {
        String token = JasyptUtils.encrypt(JSONUtils.NON_NULL.toJSONString(bean));
        return token;
    }
}
