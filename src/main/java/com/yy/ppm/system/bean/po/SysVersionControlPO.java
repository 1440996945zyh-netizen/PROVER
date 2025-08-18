package com.yy.ppm.system.bean.po;

import java.io.Serializable;

import com.yy.ppm.common.bean.po.BasePO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * APP版本控制表
 * @author zcc
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SysVersionControlPO extends BasePO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7748810909245989416L;
	private Long id;
	private String versionName;
	private Integer versionCode;
	private String linkAddress;
	private Long fileId;
	private String releaseState;
	private String versionType;
	private String remark;

}
