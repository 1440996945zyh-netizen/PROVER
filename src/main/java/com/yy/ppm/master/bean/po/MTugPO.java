package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName 拖轮资料(MTug)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 14:20:00
 */
@Data
public class MTugPO extends BasePO implements Serializable {

    private static final long serialVersionUID = -36919662944632888L;

    /** 主键 */
    private Long id;
    /** 拖轮编号 */
    private String tugCode;
    /** 拖轮名称 */
    private String tugName;
    
}

