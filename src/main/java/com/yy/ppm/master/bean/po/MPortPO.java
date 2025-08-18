package com.yy.ppm.master.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 港口信息PO
 * @author yangcl
 * */
@Data
public class MPortPO extends BasePO implements Serializable {
    private static final long serialVersionUID = -6782398281202003664L;
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 港口编号
     */
    @NotEmpty(message = "港口编号不可为空")
    private String portCode;

    /**
     * 港口名称
     */
    @NotEmpty(message = "港口名称不可为空")
    private String portName;

    /**
     * 国籍CD （字典：NATION)
     */
    private String nationCode;

    /**
     * 国籍名称
     */
    private String nationName;

    /**
     * 排序号
     */

    private Integer sortNum;

    /**
     * 是否国内  国内：1；国外：0
     */
    private String isDomestic;

    /**
     * 省编码 暂时不维护
     */
    private String provinceCode;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 市 编码
     */
    private String cityCode;

    /**
     * 市 名称
     */
    private String cityName;

    /**
     * 航线编码
     */
    private String routeCode;

    /**
     * 航线名称
     */
    private String routeName;

    /**
     * 助记码
     */
    private String shorthandCode;
}

