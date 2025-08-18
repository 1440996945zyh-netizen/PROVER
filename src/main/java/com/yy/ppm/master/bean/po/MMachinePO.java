package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 机械信息(MMachine)PO
 * @Description
 * @createTime 2023年06月05日 17:28:00
 */
@Data
public class MMachinePO extends BasePO implements Serializable {

    private static final long serialVersionUID = -66246212212459689L;

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
    private String macModelCode;
    /**
     * 所属部门
     */
    private Long deptId;
    /**
     * 所属部门NAME
     */
    private String deptName;
    /**
     * 绑定设备imei
     */
    private String imei;
    /**
     * 状态 1在用 0停用
     */
    private Long status;
    /**
     * 机械型号Name
     */
    private String macModelName;
    /**
     * 机械类型名称
     */
    private String macTypeName;
    /**
     * 是否启电子围栏(0:否 1:是)
     */
    private String isElectronFence;

}

