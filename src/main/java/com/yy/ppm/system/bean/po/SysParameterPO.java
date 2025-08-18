package com.yy.ppm.system.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 系统参数(SysParameter)PO
 *
 * @author 张超
 * @date 2021-03-02 16:30:21
 */
@Getter
@Setter
@ToString
public class SysParameterPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 549817790093496021L;

    /**
     * 主键
     */
    private Long id;
    /**
     * 参数编号
     */
    @NotBlank(message = "参数编号不能为空~")
    private String paramCd;
    /**
     * 参数名称
     */
    @NotBlank(message = "参数名称不能为空~")
    private String paramNm;
    /**
     * 参数值
     */
    @NotBlank(message = "参数值不能为空~")
    private String paramVal;
    /**
     * 备注
     */
    private String remark;

    /**
     * 标记是用户(1)还是系统管理员(2)
     */
    private String flag;

}
