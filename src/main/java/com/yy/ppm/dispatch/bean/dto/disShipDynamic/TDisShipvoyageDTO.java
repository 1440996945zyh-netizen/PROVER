package com.yy.ppm.dispatch.bean.dto.disShipDynamic;

import com.yy.ppm.dispatch.bean.po.TDisShipvoyageItemPO;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 11:31
 */
@Setter
@Getter
public class TDisShipvoyageDTO extends TDisShipvoyagePO {

    private TDisShipvoyageItemPO in;

    private TDisShipvoyageItemPO out;

    /**
     * 前端控件用：变更状态时可选的装卸类型
     * <p>
     * 航次仅有卸：固定为卸
     * 航次仅有装：固定为装
     * 航次有卸和装：
     * - 未完工过：卸
     * - 已完工过：装
     */
    private String nextLoadUnload;

    /**
     * 前端控件用：当前状态为完工时，判断是否可以开工
     * <p>
     * 同变更状态时的校验逻辑
     */
    private Boolean allowStartWork;

    /**
     * 舷型label
     */
    private String berthTypeLabel;

    /**
     * 舱口数
     */
    private String hatchNum;


}
