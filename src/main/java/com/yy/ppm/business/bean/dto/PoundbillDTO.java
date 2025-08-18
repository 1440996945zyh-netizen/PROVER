package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TStdShipRecordPO;
import lombok.Data;

/**
 * @ClassName 单船测试记录(TStdShipRecord)DTO
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月31日 10:35:00
 */
@Data
public class PoundbillDTO extends TStdShipRecordPO {

    private static final long serialVersionUID = 902579493302425681L;
	
	private Integer status;//0代表删除，1代表新增，2代表更新
    
}
