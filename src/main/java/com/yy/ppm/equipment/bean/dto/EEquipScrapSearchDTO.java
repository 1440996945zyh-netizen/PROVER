package com.yy.ppm.equipment.bean.dto;

import com.yy.common.page.PageParameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: fanxianjin
 * @Desc: 设备报废查询DTO
 * @Date: 2026/2/28 14:30
 */
@Getter
@Setter
@ToString
public class EEquipScrapSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单号
     */
    private String scrapCode;

    /**
     * 标题
     */
    private String title;

    /**
     * 所属公司ID
     */
    private Long useCompanyId;

    /**
     * 所属部门ID
     */
    private Long useOrgId;

    /**
     * 审批状态
     */
    private Long status;

    /**
     * 设备选择查询DTO（内部类，用于查询可报废设备）
     * @author system
     */
    @Getter
    @Setter
    @ToString
    public static class EquipSelectSearchDTO extends PageParameter implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 设备大类ID
         */
        private Long equipBigCategoryId;

        /**
         * 设备中类ID
         */
        private Long equipMiddleCategoryId;

        /**
         * 设备小类ID
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
         * 所属公司ID
         */
        private Long useCompanyId;

        /**
         * 所属部门ID
         */
        private Long useOrgId;
    }

}
