package com.yy.ppm.produce.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName 出勤人员(TPrdDispatch)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年08月15日
 */
@Data
public class TPrdAttendanceUserPO extends BasePO implements Serializable {

    /** 主键ID */
    private Long id;

    /** 出勤点名ID */
    private Long attendanceId;

    /** 出勤人员-ID */
    private Long userId;

    /** 出勤人员-姓名 */
    private String userName;

    /**
     * 分配系数
     */
    private Integer coefficient;

    /**
     * 是否上班
     */
    private String iswork;

}

