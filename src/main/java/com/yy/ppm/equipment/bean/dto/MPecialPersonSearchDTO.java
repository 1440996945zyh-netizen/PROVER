package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;

/**
 * 特种作业人员证书查询DTO
 * @author system
 * @version 1.0.0
 * @Description
 */
@Data
public class MPecialPersonSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 证书所属人
     */
    private String certifiUser;

    /**
     * 证书编号
     */
    private String certifiNumber;

    /**
     * 档案编号
     */
    private String certifiCode;

    /**
     * 所属部门
     */
    private Long useOrgId;
}

