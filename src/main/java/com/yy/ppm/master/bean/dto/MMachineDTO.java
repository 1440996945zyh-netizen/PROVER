package com.yy.ppm.master.bean.dto;


import com.yy.ppm.master.bean.po.MMachinePO;
import lombok.Data;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 机械信息(MMachine)DTO
 * @Description
 * @createTime 2023年06月05日 17:28:00
 */
@Data
public class MMachineDTO extends MMachinePO {

    private static final long serialVersionUID = -47051207446761309L;

    /**
     * 机械类型code
     */
    private String macTypeName;
    /**
     * 机械型号
     */
    private String macModelName;

}
