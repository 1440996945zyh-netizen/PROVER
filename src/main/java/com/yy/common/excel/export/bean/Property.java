package com.yy.common.excel.export.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


/**
 * @Author linqi
 * @Description
 * @Date 2023-05-22 10:17
 */
@Setter
@Getter
@ToString(exclude = "parent")
public class Property {

    private String name;

    private Integer index;

    private Object value;

    private String[] genericTypeNames;

    @JsonIgnore
    private Property parent;

    private List<Property> children;
}
