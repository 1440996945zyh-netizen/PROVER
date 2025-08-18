package com.yy.ppm.produce.service;

import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MMachineSearchDTO;
import com.yy.ppm.produce.bean.dto.DispatchSecondaryBatchReq;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondaryDTO;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondManResultType;
import com.yy.ppm.produce.bean.dto.TPrdDispatchSecondarySearchDTO;
import com.yy.ppm.system.bean.dto.SysUserDTO;
import com.yy.ppm.system.bean.dto.SysUserSearchDTO;

import java.util.List;

/**
 * @ClassName 作业计划派工表（二次配工）(TPrdDispatchSecondary)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月30日 18:16:00
 */
public interface TPrdDispatchSecondaryService {

    /**
     * 获取二次派工信息
     *
     * @param searchDTO
     * @return 对象列表
     */
    public List<TPrdDispatchSecondaryDTO> getList(TPrdDispatchSecondarySearchDTO searchDTO);
    
    /**
     * 获取二次派工信息(全部)
     *
     * @param searchDto
     * @return 对象列表
     */
	public List<TPrdDispatchSecondaryDTO> getAllList(TPrdDispatchSecondarySearchDTO searchDto);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TPrdDispatchSecondaryDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tPrdDispatchSecondaryDTO
     * @return 是否成功
     */
    public boolean doSave(TPrdDispatchSecondaryDTO tPrdDispatchSecondaryDTO);

    /**
     * 批量保存
     *
     * @param list
     * @return 是否成功
     */
//    public boolean doSaveBatch(List<TPrdDispatchSecondaryDTO> list, Long workPlanId, String dispatchType);

    /**
     * 批量保存
     * @param req
     * @return
     */
    public boolean doSaveBatch(DispatchSecondaryBatchReq req);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

    /**
     * 批量删除
     *
     * @param ids
     * @return 是否成功
     */
    public boolean deleteByIds(List<Long> ids);

    /**
     * 查询劳务
     * @param workPlanId
     * @return
     */
    List<TPrdDispatchSecondManResultType> getLaborList(Long workPlanId);

    List<TPrdDispatchSecondManResultType> getEchoLaborList(Long workPlanId);

    List<TPrdDispatchSecondManResultType> getLaborDeptList();

    List<TPrdDispatchSecondManResultType> getLaborGroupList(String deptParentId);
}

