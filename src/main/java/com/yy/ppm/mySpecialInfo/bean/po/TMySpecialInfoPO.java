package com.yy.ppm.mySpecialInfo.bean.po;


import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName 个人特别信息表(TMySpecialInfo)PO
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年01月17日 10:17:00
 */
@Data
public class TMySpecialInfoPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 279779899977404051L;

    /** 用户id */
    private Long userId;
    /** 项目ID */
    private Long businessId;

    private Long projectId;
    /** 类型（NOTICE：留意的项目类型） */
    private String type;
    /** 排序号 */
    private Long pageNum;

}

