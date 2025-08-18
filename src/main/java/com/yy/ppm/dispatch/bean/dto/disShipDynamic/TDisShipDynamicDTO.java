package com.yy.ppm.dispatch.bean.dto.disShipDynamic;

import com.yy.ppm.dispatch.bean.po.TDisShipDynamicPO;
import com.yy.ppm.dispatch.bean.po.TDisTugServiceRecordPO;
import com.yy.ppm.statement.bean.dto.busHandoverlist.UpdateBusHandoverlistDTO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-12 11:21
 */
@Setter
@Getter
public class TDisShipDynamicDTO extends TDisShipDynamicPO {

    private List<TDisTugServiceRecordPO> tugs;
    private UpdateBusHandoverlistDTO busHandoverListDto;
    private String trustRemark;
}
