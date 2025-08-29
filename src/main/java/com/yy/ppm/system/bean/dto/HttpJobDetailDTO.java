package com.yy.ppm.system.bean.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.system.bean.po.HttpJobDetailPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class HttpJobDetailDTO extends HttpJobDetailPO implements Serializable {
    /**
     * corn表达式
     */
    private String cronExpression;

    /**
     *
     */
    private String jobStatusInfo;


    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFireTime;
}
