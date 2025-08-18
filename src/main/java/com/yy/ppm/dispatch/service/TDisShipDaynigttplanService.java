package com.yy.ppm.dispatch.service;

import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.TDisShipDaynigttplanDTO;
import com.yy.ppm.dispatch.bean.dto.TDisShipDaynigttplanSearchDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO2;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 船舶昼夜计划(TDisShipDaynigttplan)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月17日 10:31:00
 */
public interface TDisShipDaynigttplanService {


    /**
     * 按子表ID查询工作量
     *
     * @param
     * @return 实体
     */
    public Long getWorkNum(Long shipvoyageItemId,String planDate);


    /**
     * 按日期查询昼夜计划详情
     *
     * @param planDate
     * @return 实体
     */
    public List<TDisShipDaynigttplanDTO> getList(String planDate);

    /**
     * 按日期的上一天查询昼夜计划详情
     *
     * @param planDate
     * @return 实体
     */
    public List<TDisShipDaynigttplanDTO> getList2(String planDate);
    /**
     * 保存
     * @param list
     * @Param planDate
     * @return 是否成功
     */
    public boolean doSave(List<TDisShipDaynigttplanDTO> list, String strPlanDate);

    /**
     * 删除
     *
     * @param  planDate
     * @return 是否成功
     */
    public boolean deleteByPlanDate(String planDate);

    List<Map<String, String>> getShipVoyage();

}

