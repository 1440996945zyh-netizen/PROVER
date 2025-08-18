package com.yy.ppm.produce.bean.dto;


import com.yy.ppm.produce.bean.po.TPrdWorkPlanPO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 零工工时
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
@Data
public class TOddWorkPlanAttendanceDTO implements Serializable {

    private static final long serialVersionUID = 537996643210379955L;

    private Long id;
    private Long companyId;
    private String companyName;
    private Long deptId;
    private String deptName;
    private Date workDate;
    private String classCode;
    private String className;

    private Long userId;

    private String userName;

    private Integer coefficient;

}
