package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 设备调拨查询DTO
 * @author system
 */
@Getter
@Setter
@ToString
public class EEquipAllocateSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 调拨编号
     */
    private String allocateCode;

    /**
     * 调拨标题
     */
    private String title;

    /**
     * 调入单位id
     */
    private Long toCompanyId;

    /**
     * 调入部门id
     */
    private Long toOrgId;

    /**
     * 审批状态
     */
    private Long status;

    /**
     * 设备选择查询参数
     */
    @Getter
    @Setter
    @ToString
    public static class EquipSelectSearchDTO extends PageParameter implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 设备大类id
         */
        private Long equipBigCategoryId;

        /**
         * 设备中类id
         */
        private Long equipMiddleCategoryId;

        /**
         * 设备小类id
         */
        private Long equipSmallCategoryId;

        /**
         * 设备名称
         */
        private String equipName;

        /**
         * 设备编码
         */
        private String equipCode;

        /**
         * 调出单位id
         */
        private Long useCompanyId;

        /**
         * 调出部门id
         */
        private Long useOrgId;

    }

}
