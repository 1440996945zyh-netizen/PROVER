package com.yy.ppm.system.service.impl;

import com.yy.common.enums.RedisEnum;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.service.SysOnLineUserService;
import com.yy.ppm.system.mapper.SysOnLineUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author FanQi
 * @version 1.0
 * @date 2023/5/6 11:41
 */
@Service
public class SysOnLineUserServiceImpl implements SysOnLineUserService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SysOnLineUserMapper sysOnLineUserMapper;
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 查询在线用户
     * @return
     */
    @Override
    public List<SysUserDTO> getList(String userAccount,String userName) {

        Set<String> accountList = redisTemplate.opsForZSet().range(applicationName + ":"
                + RedisEnum.ONLINE_ACCOUNTS_PC.getCode(), 0, -1);

        List<SysUserDTO> userList = sysOnLineUserMapper.getList(accountList, userAccount, userName);

        for (SysUserDTO user:userList) {
            Long score = redisTemplate.opsForZSet().score(applicationName + ":"
                    + RedisEnum.ONLINE_ACCOUNTS_PC.getCode(), user.getUserAccount()).longValue();
            Date lastRequestTime = new Date(score);
            user.setLastRequestTime(lastRequestTime);

        }
        long currentTimeMillis = System.currentTimeMillis();
        long threeHoursAgoMillis = currentTimeMillis - TimeUnit.HOURS.toMillis(3);
        Date threeHoursAgo = new Date(threeHoursAgoMillis);
        List<SysUserDTO> collectList = userList.stream()
                .filter(x -> x.getLastRequestTime() != null && x.getLastRequestTime().after(threeHoursAgo))
                .collect(Collectors.toList());
        Collections.sort(collectList, Comparator.comparing(SysUserDTO::getLastRequestTime).reversed());

        return collectList;
    }

    /**
     * 对在线用户进行强制下线
     */
    @Override
    public void offLine(String userAccount,Long id) {

        // 移除在线用户
        redisTemplate.opsForZSet().remove(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_PC.getCode(),userAccount);
        redisTemplate.opsForZSet().remove(applicationName + ":" + RedisEnum.ONLINE_ACCOUNTS_APP.getCode(),userAccount);

        // 移除用户基本信息
        redisTemplate.delete(applicationName + ":" + RedisEnum.USER_INFO.getCode()
                + userAccount);
        // APP令牌
        redisTemplate.delete(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_APP.getCode()
                + userAccount);
        // PC令牌
        redisTemplate.delete(applicationName + ":" + RedisEnum.TOKEN_EXPIRES_ACCOUNT_PC.getCode()
                + userAccount);
    }
}
