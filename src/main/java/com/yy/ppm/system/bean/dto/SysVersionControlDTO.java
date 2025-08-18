package com.yy.ppm.system.bean.dto;

import java.io.Serializable;
import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * APP版本控制表
 * @author zcc
 */
@Getter
@Setter
@ToString
public class SysVersionControlDTO extends PageParameter implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = -2579207413205054679L;
	private Long id;
	private String versionName;
	private Integer versionCode;
	private String linkAddress;
	private Long fileId;
	private String releaseState;
	private String versionType;
	private String remark;
}
