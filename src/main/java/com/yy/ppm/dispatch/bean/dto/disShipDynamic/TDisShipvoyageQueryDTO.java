package com.yy.ppm.dispatch.bean.dto.disShipDynamic;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yy.common.util.DateUtils;
import com.yy.ppm.dispatch.bean.po.TDisShipvoyagePO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * @Author linqi
 * @Description
 * @Date 2023-07-04 11:13
 */
@Setter
@Getter
public class TDisShipvoyageQueryDTO extends TDisShipvoyagePO {

    /**
     * 宽泛状态编码
     */
//    @NotBlank(message = "宽泛状态编码不能为空")
    private String shipStatusBroadCode;

    /**
     * 状态编码
     */
    private List<String> shipStatusCodes;

    /**
     * 船名航次
     */
    private String shipNameVoyage;

    /**
     * 离港时间
     */
    private List<String> timeRange;
    private Date startLeaveTime;
    private Date endLeaveTime;

    /**
     * 货种名称
     */
    private String cargoCategoryName;


    /**
     * 标准化大屏条转的时候用作时间筛选条件
     */
    private String time;
}
