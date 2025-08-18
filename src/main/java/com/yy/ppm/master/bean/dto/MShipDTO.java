package com.yy.ppm.master.bean.dto;


import com.yy.ppm.master.bean.po.MShipPO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 海轮资料(MShip)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年06月27日 15:44:00
 */
@Data
public class MShipDTO extends MShipPO {

    private static final long serialVersionUID = 785701430060712373L;


    @FieldRemark(value = "船舶类型")
    private String shipKindLabel;
    @FieldRemark(value = "船籍代码")
    private String nationLabel;
    @FieldRemark(value = "船型")
    private String shipTypeLabel;
    @FieldRemark(value = "状态")
    private String statusLabel;
    @FieldRemark(value = "是否重点船舶")
    private String isFocusShip;
    /** 附件 */
    @FieldRemark(value = "附件")
    private List<Long> fileIds;

    private String bhtCustomerName;

}
