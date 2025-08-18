package com.yy.ppm.statement.bean.dto.busHandoverlist;

import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-08 14:37
 */
@Setter
@Getter
public class TBusHandoverListUnloadReqDTO extends TBusHandoverlistPO {

    /**
     * 附件ID
     */
    private List<Long> fileIds;

    /**
     * 海关报关单附件ID
     */
    private List<Long> hgFileIds;

    /**
     * 第三方检测报告附件ID
     */
    private List<Long> jcFileIds;

    /**
     * 交接清单
     */
    @NotEmpty(message = "交接清单不能为空")
    private List<TBusHandoverlistPO> handoverlists;
}
