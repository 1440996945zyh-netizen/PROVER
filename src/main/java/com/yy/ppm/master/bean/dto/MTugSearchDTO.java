package com.yy.ppm.master.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 拖轮资料(MTug)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年07月12日 14:20:00
 */
@Data
public class MTugSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 657958015879232061L;
    
            /**主键*/
    private Long id;
            /***/
    private String tugCode;
            /***/
    private String tugName;
            /**创建者-ID*/
    private String createBy;
            /**创建者-姓名*/
    private String createByName;
                            /**更新着-姓名*/
    private String updateByName;
            }

