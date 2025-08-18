package com.yy.ppm.system.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysParameterLogPO extends BasePO implements Serializable {

    /**
     * 主键
     */
    private Long id;
    /**
     * 修改后参数编号
     */
    private String paramCdNew;
    /**
     * 修改后参数名称
     */
    private String paramNmNew;
    /**
     * 修改后参数值
     */
    private String paramValNew;
    /**
     * 修改后备注
     */
    private String remarkNew;

    /**
     * 标记是用户(1)还是系统管理员(2)
     */
    private String flag;
    /**
     * 修改前参数编号
     */
    private String paramCdOld;
    /**
     * 修改前参数名称
     */
    private String paramNmOld;
    /**
     * 修改前参数值
     */
    private String paramValOld;
    /**
     * 修改前备注
     */
    private String remarkOld;

    /** 操作类型 （1:新增，2:修改，0:删除）*/
    private Long operationType;
}
