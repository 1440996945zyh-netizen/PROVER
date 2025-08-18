package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName (MCity)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年06月30日 13:29:00
 */
@Data
public class MCitySearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -86156140435088963L;

            /***/
    private Long id;
            /***/
    private String provinceCode;
            /***/
    private String cityCode;
            /***/
    private String areaCode;
            /***/
    private String name;
            /***/
    private String code;
            /***/
    private String recNam;
            /***/
    private String recTim;
            /***/
    private String updNam;
            /***/
    private String updTim;
            /***/
    private String idevVersionuse;
            /***/
    private String obj;
            /***/
    private String map;
            /***/
    private String isCk;
    }

