package com.yy.ppm.produce.bean.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyClassResDTO implements Serializable {

    private String id;
    private String deptName;
    private String deptLevel;

    private String companyId;
    private String companyName;
    private String classId;
    private String className;

    private String companyDeptId;
    private String companyDeptName;
    private String level;
}
