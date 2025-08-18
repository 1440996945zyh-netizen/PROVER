package com.yy.ppm.business.service;


import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;

import java.util.*;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单表(TBusDispatchRelease)Service
 * @Description
 * @createTime 2024年04月16日 16:03:00
 */
public interface TBusDispatchReleaseService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<Map<String,Object>> getPageList(TBusDispatchReleaseSearchDTO searchDTO);

    List<TBusDispatchReleaseDTO> getListByCondition(TBusDispatchReleaseSearchDTO searchDTO);

    List<TBusDispatchReleaseDetailDTO> cargoInfoListByCondition(TBusDispatchReleaseDetailSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TBusDispatchReleaseDTO getDetail(Long id);


    /**
     * 保存
     *
     * @param tBusDispatchReleaseDTO
     * @return 是否成功
     */
    boolean doSave(TBusDispatchReleaseDTO tBusDispatchReleaseDTO);


    /**
     * 批量保存
     *
     * @param tBusDispatchReleaseDTOS
     * @return 是否成功
     */
    Map<String, Object> doListSave(List<TBusDispatchReleaseDTO> tBusDispatchReleaseDTOS);

    void addCargoList(List<TBusTrustCargoDTO> trustCargoDTOS);

    void deleteDispatchRelease(List<TBusTrustCargoDTO> trustCargoDTOS);


    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除
     * List<Long> ids
     *
     * @param ids
     * @return 是否成功
     */
    boolean deleteListByIds(List<Long> ids);

    /**
     * 批量删除
     *
     * @param tBusDispatchReleaseDTO
     * @return 是否成功
     */
    boolean deleteByCondition(TBusDispatchReleaseDTO tBusDispatchReleaseDTO);

}

