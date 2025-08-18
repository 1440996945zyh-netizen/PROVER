package com.yy.ppm.master.bean.dto;

import com.yy.common.page.PageParameter;
import com.yy.ppm.master.bean.po.MPortPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 港口信息DTO
 * @author yangcl
 * */
@Getter
@Setter
@ToString
public class MPortSearchDTO extends PageParameter implements Serializable {
    private static final long serialVersionUID = -7328782298769814176L;


    /**
     * 是否国内
     */
    private String isDomestic;

    /**
     * 港口管理
     */
    private String portName;

    /**
     * 速记码
     */
    private String shorthandCode;

}
