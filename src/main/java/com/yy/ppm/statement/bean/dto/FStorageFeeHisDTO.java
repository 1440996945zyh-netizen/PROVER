package com.yy.ppm.statement.bean.dto;

import com.yy.ppm.statement.bean.po.TStorageFeeDetailPO;
import com.yy.ppm.statement.bean.po.TStorageFeeHisPO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * 堆存费历史结算DTO
 * @author yangcl*/
@ToString
@Getter
@Setter
public class FStorageFeeHisDTO extends TStorageFeeHisPO implements Serializable {
    private static final long serialVersionUID = 467665355616187852L;

    private List<TStorageFeeDetailPO> detailList;

    //根据指令ID统计 每批次货的重量
    private HashMap<Long, BigDecimal> instructMap;

    //临时存放已堆存天数 key:作业指令ID value:已堆存天数
    private HashMap<Long,Long> tempDaysMap;

    //临时存放已过免堆存期的作业指令ID
    private HashMap<Long,Long> passDaysMap;

    /**
     * rateId
     */
    private Long rateId;

}

