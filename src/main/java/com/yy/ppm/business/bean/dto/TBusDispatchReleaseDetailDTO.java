package com.yy.ppm.business.bean.dto;


import com.yy.ppm.business.bean.po.TBusDispatchReleaseDetailPO;
import lombok.Data;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单子表(TBusDispatchReleaseDetail)DTO
 * @Description
 * @createTime 2024年04月17日 09:27:00
 */
@Data
public class TBusDispatchReleaseDetailDTO extends TBusDispatchReleaseDetailPO {

    private static final long serialVersionUID = 621457701480432779L;

    private Integer status;//0代表删除，1代表新增，2代表更新

}
