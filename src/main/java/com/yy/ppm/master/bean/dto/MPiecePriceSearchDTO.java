package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 计件单价(MPiecePrice)SearchDTO
 * @Description TODO
 * @createTime 2023年09月15日 11:32:00
 */
@Data
public class MPiecePriceSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 235003615258948998L;

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
    /**
     * 部门
     */
    private String deptName;
    /**
     * 部门
     */
    private Long deptId;
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
    private Long price;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 更新者-姓名
     */
    private String updateByName;
}

