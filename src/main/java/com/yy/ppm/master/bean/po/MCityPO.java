package com.yy.ppm.master.bean.po;


import lombok.Data;
import com.yy.ppm.common.bean.po.BasePO;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName (MCity)PO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月30日 13:29:00
 */
@Data
public class MCityPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 864609104717093354L;

        /**  */
    private Long id;
            /**  */
    private String provinceCode;
            /**  */
    private String cityCode;
            /**  */
    private String areaCode;
            /**  */
    private String name;
            /**  */
    private String code;
            /**  */
    private String recNam;
            /**  */
    private String recTim;
            /**  */
    private String updNam;
            /**  */
    private String updTim;
            /**  */
    private String idevVersionuse;
            /**  */
    private String obj;
            /**  */
    private String map;
            /**  */
    private String isCk;

}

