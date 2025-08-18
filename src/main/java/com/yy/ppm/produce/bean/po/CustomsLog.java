package com.yy.ppm.produce.bean.po;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomsLog {

    private String seqNo;
    private String poundNo;// 磅单号
    private String sendText;// 向海关平台发送的xml
    private String receiveText;// 海关平台返回的放行指令
    private Date recTim;// 记录时间
    private Date updTim;// 海关返回时间
    private String gateNo;// 闸口号
    private String release;// 是否放行（放行：y；不放行：n）
    private String reason;// 不放行原因	
    private String ioFlag;// 'o'	进出标识（i 进；o 出）
}