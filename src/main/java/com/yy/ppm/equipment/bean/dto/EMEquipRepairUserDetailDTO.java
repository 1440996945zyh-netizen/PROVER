package com.yy.ppm.equipment.bean.dto;

import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.common.bean.po.SysFilePO;
import com.yy.ppm.equipment.bean.po.EMEquipRepairUserDetailPO;
import com.yy.ppm.equipment.bean.po.EMEquipRepairUserPO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 外修单位合同实体
 *
 * @author zhuhao
 * @date 2020/7/22
 * @description 描述
 **/
@Data
public class EMEquipRepairUserDetailDTO extends EMEquipRepairUserDetailPO implements Serializable {


    List<SysFileDTO>fileList;

    /**
     * 附件ID列表
     */
    private List<Long> fileIds;

}
