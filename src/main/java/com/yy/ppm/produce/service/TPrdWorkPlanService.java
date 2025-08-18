package com.yy.ppm.produce.service;



import com.yy.common.page.Pages;
import com.yy.ppm.common.bean.dto.ResponsePopupTrustDTO;
import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanDTO;
import com.yy.ppm.produce.bean.dto.TPrdWorkPlanSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 作业计划表(TPrdWorkPlan)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月21日 16:21:00
 */
public interface TPrdWorkPlanService {

    /**
     * 获取工班计划列表(可编辑列表用）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public List<TPrdWorkPlanDTO> getWorkPlanList(TPrdWorkPlanSearchDTO searchDTO);

/*    *//**
     * 获取上班次工班计划列表(可编辑列表用）
     *
     * @param searchDTO
     * @return 对象列表
     *//*
    List<TPrdWorkPlanDTO> getLastWorkPlanList(TPrdWorkPlanSearchDTO searchDTO);*/

    /**
     * 获取工班计划列表（非可编辑列表用）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public List<TPrdWorkPlanDTO> getList(TPrdWorkPlanSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TPrdWorkPlanDTO getDetail(Long id);

    /**
     * 保存单条申请
     *
     * @param tPrdWorkPlanDTO
     * @return 是否成功
     */
    public boolean doSave(TPrdWorkPlanDTO tPrdWorkPlanDTO);

    /**
     * 新增船舶计划（导入指令）
     *
     * @return 是否成功
     */
    public boolean insertWorkPlan(List<Long> list, String workDate, String classCode, String className,String planType);

    public boolean insertWorkPlan(List<TPrdWorkPlanDTO> dto);

    /**
     * 修改其他计划
     *
     * @param list
     * @return 是否成功
     */
    public boolean updateWorkPlan(List<TPrdWorkPlanDTO> list);

    /**
     * 派工，派场地
     * @return
     */
    public boolean updateDispatch(TPrdWorkPlanDTO dto);

    /**
     * 复制、导入工班计划
     *
     * @param ids
     * @return 是否成功
     */
    public boolean importWorkPlan(List<Long> ids, String workDate, String classCode, String className);

    /**
     * 删除
     *
     * @param ids
     * @return 是否成功
     */
    public boolean deleteByIds(List<Long> ids);

    /**
     * 审核
     *
     * @param ids
     * @return 是否成功
     */
    public boolean approveByIds(List<Long> ids);


    /**
     * 撤销审核
     *
     * @param ids
     * @return 是否成功
     */
    public boolean cancelByIds(List<Long> ids);

    List<Map<String, String>> getProcessName();

    List<Map<String, String>> normalWorkProcess();

    boolean addBatch(List<Long> ids, String workDate, String classCode, String className);

    boolean insertOpenPortTrust(List<Long> trustIds, String workDate, String classCode, String className);

    List<Map<String, String>> workProcessType(Long type);

    List<Map<String, String>> workProcessType(Long type,String dictValue);

    boolean copyWorkPlan(List<Long> id);

    boolean copyJSGWorkPlan(List<Long> ids);

    boolean copyZYWorkPlan(List<Long> id);

    List<ResponsePopupTrustDTO> getJSGDayNightWorkPlanList(TPrdWorkPlanSearchDTO searchDTO);

    boolean addJSGBatch(List<TPrdWorkPlanDTO> dtos, String workDate, String classCode, String className);

    List<Map<String, Object>> getMassIdsWithPlanId(Long planId,String tmpParam);
}

