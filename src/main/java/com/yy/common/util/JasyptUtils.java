package com.yy.common.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;

/**
 * jasypt PBE基于口令加密
 **/
public final class JasyptUtils {

    /**
     * salt 随机盐
     **/
    private static final String SALT_PASSWORD = "yt_hdpt";

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final PooledPBEStringEncryptor ENCRYPTOR = new PooledPBEStringEncryptor();

    // init
    static {
        ENCRYPTOR.setPoolSize(CORE_SIZE);
        ENCRYPTOR.setPassword(SALT_PASSWORD);
        ENCRYPTOR.setAlgorithm("PBEWithMD5AndTripleDES");
    }

    /**
     * 加密
     *
     * @param val 加密字符串
     * @return 返回加密后的字符串
     **/
    public static String encrypt(String val) {
        return ENCRYPTOR.encrypt(val);
    }

    /**
     * 解密
     *
     * @param val 解密字符串
     * @return 返回解密后的字符串
     **/
    public static String decrypt(String val) {
        return ENCRYPTOR.decrypt(val);
    }
}
