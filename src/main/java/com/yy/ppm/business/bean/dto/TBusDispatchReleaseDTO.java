package com.yy.ppm.business.bean.dto;


import com.yy.common.util.str.StringUtil;
import com.yy.ppm.business.bean.po.TBusDispatchReleasePO;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单表(TBusDispatchRelease)DTO
 * @Description
 * @createTime 2024年04月16日 16:03:00
 */
@Data
public class TBusDispatchReleaseDTO extends TBusDispatchReleasePO {

    private static final long serialVersionUID = -23074211103953925L;

    private Integer status;//0代表删除，1代表新增，2代表更新

    private List<String> cargoInfoNoList;//票货信息

    private String cargoInfoNo;//票货信息

    public void setCargoInfoNo(String cargoInfoNo) {
        this.cargoInfoNo = cargoInfoNo;
        if(StringUtil.isNotEmpty(this.cargoInfoNo) && CollectionUtils.isEmpty(this.cargoInfoNoList)){
            this.cargoInfoNo = this.cargoInfoNo.replaceAll(",","，");
            List<String> list = Arrays.asList(this.cargoInfoNo.split("，"));
            this.cargoInfoNoList = list;
        }
    }
}
