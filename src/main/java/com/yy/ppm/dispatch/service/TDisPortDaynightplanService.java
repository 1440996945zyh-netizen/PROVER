package com.yy.ppm.dispatch.service;

import java.util.List;

import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanDTO;
import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanExportDTO;
import com.yy.ppm.dispatch.bean.dto.TDisPortDaynightplanSearch2DTO;

/**
 * @ClassName 集疏港昼夜计划(TDisPortDaynightplan)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年11月14日 10:31:00
 */
public interface TDisPortDaynightplanService {


    /**
     * 按日期查询昼夜计划详情
     *
     * @param query
     * @return 实体
     */
    public List<TDisPortDaynightplanDTO> getList(TDisPortDaynightplanSearch2DTO query);

    /**
     * 按日期查询未作计划的票货数据
     *
     * @param dto
     * @return 实体
     */
    public List<TDisPortDaynightplanDTO> getTrustCargoDetail(TDisPortDaynightplanDTO dto);

    /**
     * 保存
     * @param list
     * @Param planDate
     * @return 是否成功
     */
    public boolean doSave(List<TDisPortDaynightplanDTO> list, String strPlanDate);

    /**
     * 导入昨日计划到今天并返回
     *
     * @param planDate
     * @return 实体
     */
    public List<TDisPortDaynightplanDTO> importTodayPlan(String planDate);
    /**
     * 审批通过
     * @Param id
     * @return 是否成功
     */
    public boolean approveById(Long id);

    /**
     * 删除
     * @Param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);
    /**
     * 审批撤销
     * @Param id
     * @return 是否成功
     */
    public boolean revokeById(Long id);
    
    /**
     * 每日 18：00查询集疏港昼夜计划 推送开始结束时间到渤海通接口
     */
	public void tosToBoHaiTongDayNightPlanTask(Long id);

    void approveList(List<TDisPortDaynightplanDTO> list);

    void approveListRevoke(List<TDisPortDaynightplanDTO> list);

    List<TDisPortDaynightplanExportDTO> exportPlan(String planDate);

    TDisPortDaynightplanDTO getCount(String businessNo);

    List<TDisPortDaynightplanDTO> importYesterdayPlan(String planDate, String businessNo);
}