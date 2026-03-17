package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.equipment.bean.po.EMaintInfoPO;
import lombok.Data;

import java.util.List;

/**
 * 设备维修派工信息 DTO
 *
 * @author system
 * @version 1.0.0
 */
@Data
public class EMaintInfoDTO extends EMaintInfoPO {

    private static final long serialVersionUID = 1L;

    /** 设备名称 */
    private String equipName;

    /** 设备编码 */
    private String equipCode;

    /** 故障图片文件ID列表 */
    private java.util.List<Long> faultImageIds;

    /** 维修时长（小时） */
    private java.math.BigDecimal maintDuration;

    /** 故障时长（小时） */
    private java.math.BigDecimal faultDuration;

    /** 配件更换列表 */
    private java.util.List<EMaintPartReplaceDTO> partReplaceList;

    /** 作业工时反馈列表 */
    private List<EMaintHourFeedbackDTO> hourFeedbackList;

    /** 操作日志列表 */
    private List<EMaintInfoLogDTO> operateLogList;

    /** 设备小类名称 */
    private String equipSmallCategoryName;

    /** 设备小类ID */
    private String equipSmallCategoryId;

    /** 使用部门名称 */
    private String useOrgName;

    /** 派工部位部件列表 */
    private List<EMaintInfoPartItemDTO> itemList;

    /** 退回状态 */
    private Integer returnStatus;
}
