package com.yy.ppm.produce.bean.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
public class TPrdSalaryLogPO extends BasePO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 月份
     */
    private String examineMonth;

    /**
     * 错误日志
     */
    private String errMsg;
    /**
     * 部门
     */
    private Long deptId;
    /**
     * 操作类型 0 审核， 1 撤销
     */
    private String flag;

    @TableField(exist = false)
    private String deptName;

    /**
     * 开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    private Date startDate;
    /**
     * 结束日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    private Date endDate;
}

