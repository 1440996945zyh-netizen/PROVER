package com.yy.ppm.largescreen.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (SPortStorage)SearchDTO
 * @Description TODO
 * @createTime 2024年03月14日 23:13:00
 */
@Data
public class SPortStorageSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -53707793441587640L;

    /***/
    private Long id;
    /**
     * 创建者-姓名
     */
    private String createByName;
    /**
     * 港区代码（10：潍坊港，20：寿光港，30：东营港，40：滨州港）
     */
    private String portCode;
    /**
     * 货名
     */
    private String cargoName;
    /**
     * 计划量
     */
    private Long ton;
    /**
     * 货物类型（1：件2：散）（新增，查询，删除）
     */
    private String cargoType;
}

