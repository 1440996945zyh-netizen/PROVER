package com.yy.ppm.master.bean.dto;

import java.io.Serializable;

import com.yy.ppm.master.bean.po.MWorkSchedulePO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * (MWorkSchedule)DTO
 *
 * @author yy
 * @date 2023-06-11 14:39:17
 */
@Data
public class MWorkScheduleDTO extends MWorkSchedulePO implements Serializable {

    private static final long serialVersionUID = 837337441940311600L;

    /**工班班次 （字典WORK_SCHEDULE）*/
    private String workScheduleLabel;

}
