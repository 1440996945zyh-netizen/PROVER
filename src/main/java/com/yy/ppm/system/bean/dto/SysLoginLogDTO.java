package com.yy.ppm.system.bean.dto;


import com.yy.ppm.system.bean.po.SysLoginLogPO;
import lombok.Data;

/**
 * @ClassName 登录日志表(SysLoginLog)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月29日 15:51:00
 */
@Data
public class SysLoginLogDTO extends SysLoginLogPO {

    private static final long serialVersionUID = -19117699497068946L;

    // 设置通用信息
    public void setCommonInfo(SysLoginLogDTO dto) {


        //LOGIN_TIME	DATE	Y			登录时间
        //LOGIN_IP	VARCHAR2(64)	Y			登录ip
        //CHANNEL_TYPE	VARCHAR2(32)	Y			登录渠道,字典:01-PC,02-安卓APP,03-IOS APP
        //UQ_MARK	NUMBER(20)	Y			登录用户唯一标记
        //OS	VARCHAR2(20)	Y			操作系统
        //BROWSER	VARCHAR2(20)	Y			浏览器
        //LOCATION	VARCHAR2(50)	Y			登录地点
    }

}
