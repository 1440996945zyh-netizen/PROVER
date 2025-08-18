package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdOddLogResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddSaveDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddSearchDTO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;

import java.util.List;

/**
 * @ClassName 零工申请
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月12日 11:21:00
 */
public interface TPrdOddWorkPlanService {


    /**
     * 分页查询
     * @param dto
     * @param parameter
     * @return
     */
    Pages<TPrdOddResultDTO> getList(TPrdOddSearchDTO dto, PageParameter parameter);

    /**
     * 分页查询日志
     * @param id
     * @return
     */
    List<TPrdOddLogResultDTO> getLogList(Long id, PageParameter parameter);

    /**
     * 保存
     * @param dto
     */
    void doSave(TPrdOddSaveDTO dto);

    /**
     * 确认
     * @param dto
     */
    void confirm(TPrdOddSaveDTO dto);

    /**
     * 驳回
     * @param dto
     */
    void reject(TPrdOddSaveDTO dto);

    /**
     * 作废
     * @param dto
     */
    void abandoned(TPrdOddSaveDTO dto);

    /**
     * 取消确认
     * @param id
     */
    void cancelConfirm(Long id);

    /**
     * 取消一级审核
     * @param id
     */
    void cancelFirstApprove(Long id);

    /**
     * 取消二级审核
     * @param ids
     */
    void cancelSecondApprove(List<Long> ids);

    /**
     * 第一次审批
     * @param dto
     */
    void firstApprove(TPrdOddSaveDTO dto);

    /**
     * 第二次审批
     * @param ids
     */
    void secondApprove(List<Long> ids);

    /**
     * 删除
     * @param id
     */
    boolean deleteById(Long id);

    /**
     * 查询机械队或装卸队
     * level 部门等级；type 部门类型，machine 机械队；labor 装卸队
     * @param level
     * @param type
     * @return
     */
    List<SysDeptDTO> getDeptByType(Integer level, String type);

    /**
     * 查询详情
     * @param id
     * @return
     */
    TPrdOddResultDTO getDetail(Long id);

    /**
     * 第三次审批
     * @param ids
     */
    void thirdApprove(List<Long> ids);

    /**
     * 取消三级审核
     * @param ids
     */
    void cancelThirdApprove(List<Long> ids);

    /**
     * 自动生成零工单号（刷新老数据）
     */
    void autoOddPlanNo();
}
