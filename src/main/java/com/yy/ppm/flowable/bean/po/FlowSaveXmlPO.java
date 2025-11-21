package com.yy.ppm.flowable.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 流程规范xml
 */
@Getter
@Setter
@ToString
public class FlowSaveXmlPO extends BasePO implements Serializable {
    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程分类
     */
    private String category;

    /**
     * xml 文件
     */
    private String xml;

}
