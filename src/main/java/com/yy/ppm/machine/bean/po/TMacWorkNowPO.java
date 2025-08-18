package com.yy.ppm.machine.bean.po;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机械当前作业货位信息表
 * @author zcc
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMacWorkNowPO extends BasePO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8965967536019255641L;
	
	private Long id; // 主键
	private Long macId; // 设备id 
	private String macCode; // 设备code
	private String macName; // 设备名称
	private Long stackId; // 货垛ID
	private String stackCode; // 垛号
	private String stackName; // 垛名
    @JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date workDate; // 作业日期
	private String classCode; // 班次
	private String remark; // 备注
}