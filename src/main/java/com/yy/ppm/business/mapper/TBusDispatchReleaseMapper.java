package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.*;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageDTO;
import com.yy.ppm.dispatch.bean.dto.disShipvoyage.TDisShipvoyageQueryDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单表(TBusDispatchRelease)Mapper
 * @Description
 * @createTime 2024年04月16日 16:03:00
 */
@Repository
public interface TBusDispatchReleaseMapper {

    /**
     * 获取放行单表列表
     *
     * @param tBusDispatchReleaseSearchVo
     * @return
     */
    Page<Map<String,Object>> getPageList(TBusDispatchReleaseSearchDTO tBusDispatchReleaseSearchVo);


    /**
     * 导出放行单表列表
     *
     * @param tBusDispatchReleaseSearchDTO
     * @return
     */
    List<TBusDispatchReleaseDTO> exportList(TBusDispatchReleaseSearchDTO tBusDispatchReleaseSearchDTO);

    /**
     * 根据id获取放行单表
     *
     * @param id 主键
     * @return
     */
    TBusDispatchReleaseDTO getById(Long id);

    List<TBusDispatchReleaseDTO> getByIds(List<Long> ids);

    /**
     * 根据id获取放行单表
     *
     * @param list 主键
     * @return
     */
    List<TBusCargoInfoDTO> getCargoInfoByNos(List<String> list);


    List<TBusTrustCargoDTO> getCargoInfoByTrustId(Long trustId);


    TBusTrustDTO getBusTrustDTO(Long shipvoyageItemId,String deliveryNumbers);


    /**
     * 新增放行单表
     *
     * @param tBusDispatchReleaseDTO
     * @return
     */
    @Edit
    int insert(TBusDispatchReleaseDTO tBusDispatchReleaseDTO);

    /**
     * 批量新增放行单表
     *
     * @param tBusDispatchReleaseDTOS
     * @return
     */
    @Edit
    int insertList(@Param("tBusDispatchReleaseDTOS") List<TBusDispatchReleaseDTO> tBusDispatchReleaseDTOS);

    /**
     * 批量新增放行单表
     * @param tBusDispatchReleaseDetailDTOS
     * @return
     */
    @Edit
    int insertDetailList(@Param("tBusDispatchReleaseDetailDTOS") List<TBusDispatchReleaseDetailDTO> tBusDispatchReleaseDetailDTOS);


    /**
     * 修改放行单表
     *
     * @param tBusDispatchReleaseDTO
     * @return
     */
    @Edit
    int update(TBusDispatchReleaseDTO tBusDispatchReleaseDTO);

    /**
     * 批量修改
     *
     * @param tBusDispatchReleaseDTOS
     * @return
     */
    @Edit
    int updateListById(@Param("tBusDispatchReleaseDTOS") List<TBusDispatchReleaseDTO> tBusDispatchReleaseDTOS);


    /**
     * 根据id删除放行单表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    int deleteCargoInfoIsNullById(Long id);


    /**
     * 批量删除
     * 根据id删除放行单表
     *
     * @param ids 主键
     * @return
     */
    int deleteListByIds(@Param("ids") List<Long> ids);


    /**
     * 根据id删除放行单表
     *
     * @param ids 主键
     * @return
     */
    int deleteDetailDispatchReleaseIds(@Param("ids") List<Long> ids);

    int deleteDetailByCondition(@Param("dispatchReleaseIds") List<Long> dispatchReleaseIds,@Param("cargoInfoNos") List<String> cargoInfoNos);

    /**
     * 批量删除
     * 根据id删除放行单表
     *
     * @param tBusDispatchReleaseDTO
     * @return
     */
    int deleteByCondition(TBusDispatchReleaseDTO tBusDispatchReleaseDTO);

}

