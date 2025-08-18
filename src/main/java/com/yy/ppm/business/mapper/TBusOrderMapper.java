package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusOrderDTO;
import com.yy.ppm.business.bean.dto.TBusOrderItemDTO;
import com.yy.ppm.business.bean.dto.TBusOrderSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 委托单主表(TBusOrder)Mapper
 * @Description
 * @createTime 2024年10月23日 09:01:00
 */
@Repository
public interface TBusOrderMapper {

    /**
     * 获取委托单主表列表
     *
     * @param tBusOrderSearchVo
     * @return
     */
    Page<TBusOrderDTO> getList(TBusOrderSearchDTO tBusOrderSearchVo);

    /**
     * 导出委托单主表列表
     *
     * @param tBusOrderSearchDTO
     * @return
     */
    List<TBusOrderDTO> exportList(TBusOrderSearchDTO tBusOrderSearchDTO);

    /**
     * 根据id获取委托单主表
     *
     * @param id 主键
     * @return
     */
    TBusOrderDTO getById(@Param("id") Long id);

    List<Map<String,Object>> getDetailByOrderId(@Param("id") Long id);

    /**
     * 新增委托单主表
     *
     * @param tBusOrderDTO
     * @return
     */
    @Edit
    int insert(TBusOrderDTO tBusOrderDTO);

    /**
     * 修改委托单主表
     *
     * @param tBusOrderDTO
     * @return
     */
    @Edit
    int update(TBusOrderDTO tBusOrderDTO);

    @Edit
    int updateStatus(TBusOrderDTO tBusOrderDTO);


    /**
     * 根据id删除委托单主表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

