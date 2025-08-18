package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 机械信息(MMachine)SearchDTO
 * @Description TODO
 * @createTime 2023年06月05日 17:28:00
 */
@Data
public class MMachineSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -43066211555665234L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 机械code
     */
    private String macCode;
    /**
     * 机械名称
     */
    private String macName;
    /**
     * 机械类型code
     */
    private String macTypeCode;
    /**
     * 机械型号
     */
    private Long macModelCode;
    /**
     * 所属部门
     */
    private Long deptId;
    /**
     * 绑定设备imei
     */
    private String imei;
    /**
     * 状态 1在用 0停用
     */
    private Long status;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private Long updateBy;
    /**
     * 修改时间
     */
    private Date updateTime;
    /**
     * 机械型号name
     */
    private String macModelName;
    /**
     * 机械类型Name
     */
    private String macTypeName;
    /**
     * 是否启电子围栏(0:否 1:是)
     */
    private String isElectronFence;

}

