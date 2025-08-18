package com.yy.ppm.master.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 计件单价(MPiecePrice)PO
 * @Description
 * @createTime 2023年09月15日 11:32:00
 */
@Data
public class MPiecePricePO extends BasePO implements Serializable {

    private static final long serialVersionUID = 889317957275011017L;

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 作业公司
     */
    private String companyName;
    /**
     * 作业公司id
     */
    private Long companyId;

    /**计件工资类型*/
    private String salaryTypeCode;

    /**计件工资类型名称*/
    private String salaryTypeName;

    /**
     * 主作业过程id
     */
    private Long workProcessId;
    /**
     * 主作业过程name
     */
    private String workProcessName;
    /**
     * 子作业过程id
     */
    private Long workProcessChildId;
    /**
     * 子作业过程name
     */
    private String workProcessChildName;
    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 所属部门
     */
    private Long deptId;

    private String deptName;

}

