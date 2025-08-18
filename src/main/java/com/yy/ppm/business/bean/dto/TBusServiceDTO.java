package com.yy.ppm.business.bean.dto;

import com.yy.ppm.business.bean.po.TBusServicePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * (BusService)DTO
 *
 * @author 韩旭
 * @date 2021-03-18 10:50:10
 */
@Getter
@Setter
@ToString
public class TBusServiceDTO extends TBusServicePO implements Serializable {

    private static final long serialVersionUID = 459829896652832525L;

    /*前端传递到后端的选中*/
    private List processList;

    /*后端传递到前端的*/
    private String processStr;
}