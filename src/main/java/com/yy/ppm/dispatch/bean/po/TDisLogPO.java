package com.yy.ppm.dispatch.bean.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName 调度日志(TDisLog)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
@Data
public class TDisLogPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 799444000471196646L;

        /** 主键ID */
    private Long id;
            /** 交班日期 */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date shiftDate;
            /** 交班班次ID */
    private String shiftClassCode;
            /** 交班班次name */
    private String shiftClassName;
            /** 交班人id */
    private Long shiftBy;
            /** 交班人name */
    private String shiftByName;
            /** 接班人id */
    private Long acceptBy;
            /** 接班人name */
    private String acceptByName;
            /** 水文信息 */
    private String hydrologic;
            /** 潮汐信息 */
    private String tide;
            /** 注意事项 */
    private String remark;
            /** 创建者-ID */
    private Long createBy;
            /** 创建者-姓名 */
    private String createByName;
                    /** 更新者-姓名 */
    private String updateByName;
    /** 附件 */
    private List<Long> fileIds;

}

