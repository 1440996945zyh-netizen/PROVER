package com.yy.ppm.dispatch.bean.dto;




import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.dispatch.bean.po.TAnchApplyPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 锚位申报(TAnchApply)DTO
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
@Data
public class TAnchApplyDTO extends TAnchApplyPO {
    /**
	 * failureReaso
	 */
	private static final long serialVersionUID = 5207859280923480395L;

	//更新实际起锚时间
	@JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date leaveAnchTime;

	//更新实际抵锚时间
	@JsonFormat(timezone="GMT+8",pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date anchTime;

	//动态id
	private Long dynamicId;
}
