package com.yy.ppm.dispatch.bean.dto;


import com.yy.ppm.dispatch.bean.po.TDisCloseSailPO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName 封航记录表(TDisCloseSail)DTO
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:54:00
 */
@Data
public class TDisCloseSailDTO extends TDisCloseSailPO {

    private static final long serialVersionUID = 117881948318142906L;

    /** 封航航次ID信息 */
    List<Long> shipList;

    /** 封航航次ID信息 */
    List<String> shipNameList;

    /** 船名信息 */
    String shipNames;
    /** 艘次*/
    int shipVoyageNum;

    /** 影响艘次 */
    String effectShipvoyage;
    /** 状态，封航中：1，封航结束：0 */

    String status;


}
