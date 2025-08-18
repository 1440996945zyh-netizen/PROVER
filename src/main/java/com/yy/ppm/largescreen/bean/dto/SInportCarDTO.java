package com.yy.ppm.largescreen.bean.dto;


import com.yy.ppm.largescreen.bean.po.SInportCarPO;
import lombok.Data;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 在港车辆表(SInportCar)DTO
 * @Description
 * @createTime 2024年03月14日 10:42:00
 */
@Data
public class SInportCarDTO extends SInportCarPO {

    private static final long serialVersionUID = -11427050629149298L;

    private Integer status;//0代表删除，1代表新增，2代表更新

}
