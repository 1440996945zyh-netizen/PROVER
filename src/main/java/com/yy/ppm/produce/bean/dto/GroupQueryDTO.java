package com.yy.ppm.produce.bean.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-12 10:08
 */
@Setter
@Getter
public class GroupQueryDTO {

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 作业过程代码
     */
    private String processCode;

    /**
     * 作业过程名称
     */
    private String processName;

    /**
     * 计划类型
     */
    private String planTypeCd;
}
