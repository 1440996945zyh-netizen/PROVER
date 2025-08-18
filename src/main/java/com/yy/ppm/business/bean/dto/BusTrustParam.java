package com.yy.ppm.business.bean.dto;

import com.yy.common.page.PageParameter;
import com.yy.common.util.str.StringUtil;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class BusTrustParam extends PageParameter implements Serializable {

    /**
     * 企业代码（货主）	Y
     */
//    @NotEmpty(message = "企业代码（货主）不能为空")
    private String companyCode;

    /**
     * 英文船名	N
     */
    private String vesselNameCn;

    /**
     * 进口航次	N
     */
    private String voyageImport;

    public void setVesselNameCn(String vesselNameCn) {
        this.vesselNameCn = vesselNameCn;
    }

    public void setVoyageImport(String voyageImport) {
        this.voyageImport = voyageImport;
    }

    /**
     * 计划号	N
     */
    private String planNo;

    /**
     * 子计划号	Y
     */
    private String subPlanNo;

    /**
     * 状态	Y
     */
    private Integer status;

    private String trustCargoBHTId;

    /**
     * 货物代码	N
     */
    private String cargoCode;

    /**
     * 业务类型	Y	提货：SG  集港/存:JG
     */
    @NotEmpty(message = "业务类型不能为空")
    private String billType;
    private String JG;
    private String SG;

    /**
     * 页码	Y	默认1
     */
//    @NotEmpty(message = "页码不能为空")
    private Integer pageNum;

    /**
     * 每页记录数	Y	默认20
     */
//    @NotEmpty(message = "每页记录数不能为空")
    private Integer pageSize;

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        super.setStartPage(this.pageNum);
    }


    public void setBillType(String billType) {
        this.billType = billType;
        if("JG".equals(billType)){
            this.JG = "JG";
        }
        if("SG".equals(billType)){
            this.SG = "SG";
        }
    }
}
