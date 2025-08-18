package com.yy.ppm.statement.bean.dto.busHandoverlist;

import com.yy.ppm.common.bean.dto.SysFileDTO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-09-07 11:04
 */
@Setter
@Getter
public class TBusHandoverlistDTO extends TBusHandoverlistPO {

    private String shipVoyageName;

    private Long companyId;

    private String companyName;
    private String customerId;
    private String customerName;

    /**
     * 是否完货
     */
    private String isClear;

    private List<SysFileDTO> mattachmentInfoList;

    /**
     * 件杂
     */
    private String workType;
}
