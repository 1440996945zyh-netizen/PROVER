package com.yy.ppm.business.bean.dto;


import com.yy.common.page.PageParameter;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @ClassName 合同(TBusContract)SearchDTO
 * @author yy
 * @version 1.0.0
 * @Description TODO
 * @createTime 2023年06月29日 10:48:00
 */
@Data
public class TBusContractSearchDTO extends PageParameter implements Serializable {

    private static final long serialVersionUID = 428098148114574696L;

            /**主键ID*/
    private Long id;
            /**合同编号*/
    private String contactNo;
            /**作业公司id，多选*/
    private String companyId;
            /**作业公司名称，多选*/
    private String companyName;
            /**父合同id*/
    private Long parentId;
            /**客户ID*/
    private Long customerId;
            /**客户名称*/
    private String customerName;
            /**签订日期*/
    private Date signTime;
            /**合同类型，1单笔、2年度、3.补充协议*/
    private String contractType;
            /**是否船舶作业,Y是N否*/
    private String isShip;
            /**结算依据代码(字典:SETTLEMENT_ BASIS)*/
    private String settlementBasisCode;
            /**结算依据名称（1.货物交接清单数、2.疏港过磅数、3.海关报关单数、4.集港过磅数5.水尺数）*/
    private String settlementBasisName;
            /**预缴依据代码(字典:DEPOSIT_ BASIS)*/
    private String depositBasisCode;
            /**预缴依据名称（1.货物交接清单数、2.海关报关单数、3.集港委托书）*/
    private String depositBasisName;
            /**预缴比例*/
    private BigDecimal depositRatio;
            /**付费方式：10：预付费客户 20：后付费客户 */
    private String payType;
            /**免堆存天数*/
    private Long freeStorageDays;
            /**有效期起*/
    private Date startTime;
            /**有效期止*/
    private Date endTime;
            /**航次ID*/
    private Long shipvoyageId;
            /**生效操作人-ID*/
    private String validBy;
            /**生效操作人-姓名*/
    private String validByName;
            /**生效操作时间*/
    private Date validTime;
            /**状态，10、签订、20、生效*/
    private String status;
            /**备注*/
    private String remark;
            /**创建者-ID*/
    private Long createBy;
            /**创建者-姓名*/
    private String createByName;
                            /**更新者-姓名*/
    private String updateByName;
    /**
     * 货物名称
     */
    private String cargoName;
    private String hasContractFile;
            }

