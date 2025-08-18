package com.yy.ppm.business.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusDispatchReleaseDetailDTO;
import com.yy.ppm.business.bean.dto.TBusDispatchReleaseDetailSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 放行单子表(TBusDispatchReleaseDetail)Mapper
 * @Description
 * @createTime 2024年04月17日 09:27:00
 */
@Repository
public interface TBusDispatchReleaseDetailMapper {

    /**
     * 获取放行单子表列表
     *
     * @param tBusDispatchReleaseDetailSearchVo
     * @return
     */
    Page<TBusDispatchReleaseDetailDTO> getPageList(TBusDispatchReleaseDetailSearchDTO tBusDispatchReleaseDetailSearchVo);


    /**
     * 导出放行单子表列表
     *
     * @param tBusDispatchReleaseDetailSearchDTO
     * @return
     */
    List<TBusDispatchReleaseDetailDTO> exportList(TBusDispatchReleaseDetailSearchDTO tBusDispatchReleaseDetailSearchDTO);

    /**
     * 根据id获取放行单子表
     *
     * @param id 主键
     * @return
     */
    TBusDispatchReleaseDetailDTO getById(Long id);


    /**
     * 新增放行单子表
     *
     * @param tBusDispatchReleaseDetailDTO
     * @return
     */
    @Edit
    int insert(TBusDispatchReleaseDetailDTO tBusDispatchReleaseDetailDTO);

    /**
     * 批量新增放行单子表
     *
     * @param tBusDispatchReleaseDetailDTOS
     * @return
     */
    @Edit
    int insertList(@Param("tBusDispatchReleaseDetailDTOS") List<TBusDispatchReleaseDetailDTO> tBusDispatchReleaseDetailDTOS);


    /**
     * 修改放行单子表
     *
     * @param tBusDispatchReleaseDetailDTO
     * @return
     */
    @Edit
    int update(TBusDispatchReleaseDetailDTO tBusDispatchReleaseDetailDTO);

    /**
     * 批量修改
     *
     * @param tBusDispatchReleaseDetailDTOS
     * @return
     */
    @Edit
    int updateListById(@Param("tBusDispatchReleaseDetailDTOS") List<TBusDispatchReleaseDetailDTO> tBusDispatchReleaseDetailDTOS);


    /**
     * 根据id删除放行单子表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 批量删除
     * 根据id删除放行单子表
     *
     * @param ids 主键
     * @return
     */
    int deleteListByIds(@Param("ids") List<Long> ids);

    /**
     * 批量删除
     * 根据id删除放行单子表
     *
     * @param tBusDispatchReleaseDetailDTO
     * @return
     */
    int deleteByCondition(TBusDispatchReleaseDetailDTO tBusDispatchReleaseDetailDTO);

}

