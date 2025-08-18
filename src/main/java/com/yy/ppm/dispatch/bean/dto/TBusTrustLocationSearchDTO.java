package com.yy.ppm.dispatch.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 集疏港作业通知单位置表，传输渤海通使用(TBusTrustLocation)SearchDTO
 * @author makejava
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年09月27日 14:34:00
 */
@Data
public class TBusTrustLocationSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = -84760593295514347L;
    
            /**主键ID*/
    private Long id;
            /**通知单ID*/
    private Long trustId;
            /**库场ID*/
    private String storehouseId;
            /**库场名称*/
    private String storehouseName;
            /**区域ID*/
    private String regionId;
            /**区域名称*/
    private String regionName;
                    /**创建者-姓名*/
    private String createByName;
                            /**更新者-姓名*/
    private String updateByName;
            }

