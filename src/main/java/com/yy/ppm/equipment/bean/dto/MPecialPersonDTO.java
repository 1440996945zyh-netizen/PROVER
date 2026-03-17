package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.equipment.bean.po.MPecialPersonPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 特种作业人员证书DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class MPecialPersonDTO extends MPecialPersonPO {

    private static final long serialVersionUID = 1L;

    /**
     * 使用部门名称（关联查询）
     */
    private String useOrgName;
}

