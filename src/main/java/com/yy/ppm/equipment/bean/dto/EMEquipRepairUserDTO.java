package com.yy.ppm.equipment.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.ppm.common.bean.po.BasePO;
import com.yy.ppm.equipment.bean.po.EMEquipRepairUserPO;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 外修单位合同实体
 *
 * @author zhuhao
 * @date 2020/7/22
 * @description 描述
 **/
@Data
public class EMEquipRepairUserDTO extends EMEquipRepairUserPO implements Serializable {


    private String repairContarctName;




    List<EMEquipRepairUserDetailDTO> list;

}
