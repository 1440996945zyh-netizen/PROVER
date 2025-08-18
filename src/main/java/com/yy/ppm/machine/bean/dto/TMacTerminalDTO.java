package com.yy.ppm.machine.bean.dto;

import java.io.Serializable;

import com.yy.ppm.common.bean.po.BasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMacTerminalDTO extends BasePO implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3018223043866743937L;
	
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