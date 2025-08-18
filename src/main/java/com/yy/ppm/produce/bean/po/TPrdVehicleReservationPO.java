package com.yy.ppm.produce.bean.po;

import com.yy.ppm.common.bean.po.BasePO;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-10-16 11:22
 */
@Setter
@Getter
public class TPrdVehicleReservationPO extends BasePO {

    /**
     * ID 主键ID
     **/
    private Long id;

    /**
     * 指令ID
     **/
    private Long trustId;

    /**
     * 集疏港指令车队预约id
     **/
    private Long trustTradeReservationId;

    /**
     * 计划号
     **/
    private String planNo;

    /**
     * 子计划号
     **/
    private String subPlanNo;

    /**
     * 任务号
     **/
    private String taskNo;

    /**
     * 中文船名
     **/
    private String vesselNameCn;

    /**
     * 英文船名
     **/
    private String vesselNameEn;

    /**
     * 航次
     **/
    private String voyage;

    /**
     * 货物代码
     **/
    private String cargoCode;

    /**
     * 业务类型(0散货1件货)
     **/
    private String assignType;

    /**
     * 物流公司名称
     **/
    private String consigneeName;

    /**
     * 物流公司代码
     **/
    private String consigneeCode;

    /**
     * 物流公司社会统一编码
     **/
    private String consigneeLicenseNumber;

    /**
     * 货主公司名称
     **/
    private String consignorName;

    /**
     * 货主公司代码
     **/
    private String consignorCode;

    /**
     * 货主社会统一编码
     **/
    private String consignorLicenseNumber;

    /**
     * 委托状态(0自提 1委托)
     **/
    private String entrustStatus;

    /**
     * 计划开始时间
     **/
    private String planStartTime;

    /**
     * 计划结束时间
     **/
    private String planEndTime;

    /**
     * 车号
     **/
    private String vehicleNo;

    /**
     * 司机1
     **/
    private String driverNameOne;

    /**
     * 身份证号1
     **/
    private String driverNoOne;

    /**
     * 手机号1
     **/
    private String driverPhoneOne;

    /**
     * 司机2
     **/
    private String driverNameTwo;

    /**
     * 身份证号2
     **/
    private String driverNoTwo;

    /**
     * 手机号2
     **/
    private String driverPhoneTwo;

    /**
     * 进港次数
     **/
    private String arrivalNum;

    /**
     * 库场中文/货位
     **/
    private String yardCode;

    /**
     * 库场code/货位
     **/
    private String locCode;

    /**
     * 规格,件货时必填,多个以英文逗号隔开
     **/
    private String specs;

    /**
     * 数量,件货时必填,多个以英文逗号隔开
     **/
    private String quantity;

    /**
     * 重量,散货时必填,多个以英文逗号隔开
     **/
    private String weight;

    /**
     * 计划状态
     **/
    private String status;

    /**
     * 激活状态
     **/
    private String activationState;

    /**
     * 指令票货ID
     **/
    private Long trustCargoId;

    /**
     * 业务号
     */
    private String businessNo;
    /**
     * 货物名称
     */
    private String cargoName;
    /**
     * 排放标准
     */
    private String emissionStandard;
    /**
     * 随车环保清单
     */
    private String envProtectChecklist;
    /**
     * 是否是新能源车辆
     */
    private Integer energyType;
    /**
     * 车辆行驶证
     */
    private String vehicleLicense;
    /**
     * 删除标识（0未删除；1已删除）
     */
    private Integer delFlag;
    /**
     * 状态备份
     */
    private String statusBackups;
    /**
     * 磅单备注
     */
    private String poundRemark;
}
