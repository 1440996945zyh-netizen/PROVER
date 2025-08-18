package com.yy.ppm.business.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * (BusService)SearchDTO
 *
 * @author 韩旭
 * @date 2021-03-18 10:50:10
 */
@Getter
@Setter
@ToString
public class BusServiceSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -78394625474808107L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 服务名
     */
    private String serviceNm;
    /**
     * 助记码
     */
    private String shortCd;
    /**
     * 主过程拼串汇总
     */
    private String processNms;
    /**
     * 备注
     */
    private String remark;

    /*主过程cd*/
    private String workProcessCd;

}