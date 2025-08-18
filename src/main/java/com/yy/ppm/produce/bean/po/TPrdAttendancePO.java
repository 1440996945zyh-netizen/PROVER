package com.yy.ppm.produce.bean.po;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;

import lombok.Data;

/**
 * @ClassName 出勤点名(TPrdDispatch)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年08月15日
 */
@Data
public class TPrdAttendancePO extends BasePO implements Serializable {

    /** 主键ID */
    private Long id;

    /** 作业公司ID */
	@Valid @NotNull(message = "companyId不能为空！")
    private Long companyId;

    /** 作业公司 */
    private String companyName;

    /** 出勤部门id */
	@Valid @NotNull(message = "deptId不能为空！")
    private Long deptId;

    /** 出勤部门 */
    private String deptName;

    /** 日期 */
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date workDate;

    /** 班次code字典 WORK_SCHEDULE */
	@Valid @NotNull(message = "classCode不能为空！")
    private String classCode;

    /** 班次 */
    private String className;

    private List<TPrdAttendanceUserPO> attendanceUserPOList;
}

