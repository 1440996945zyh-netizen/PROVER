package com.yy.ppm.machine.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.machine.bean.dto.MLocationHistoryDTO;
import com.yy.ppm.machine.bean.dto.MLocationHistorySearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 车辆历史表(MLocationHistory)Mapper
 * @Description
 * @createTime 2023年10月25日 10:46:00
 */
@Repository
public interface MLocationHistoryMapper {


    /**
     * 导出车辆历史表列表
     *
     * @param mLocationHistorySearchDTO
     * @return
     */
    List<MLocationHistoryDTO> exportList(MLocationHistorySearchDTO mLocationHistorySearchDTO);

    /**
     * 根据id获取车辆历史表
     *
     * @param macId 主键
     * @return
     */
    MLocationHistoryDTO getByMacId(String macId);


    /**
     * 新增车辆历史表
     *
     * @param mLocationHistoryDTO
     * @return
     */
    @Edit
    int insert(MLocationHistoryDTO mLocationHistoryDTO);

    /**
     * 修改车辆历史表
     *
     * @param mLocationHistoryDTO
     * @return
     */
    @Edit
    int update(MLocationHistoryDTO mLocationHistoryDTO);

    /**
     * 批量删除
     * 根据id删除车辆历史表
     *
     * @param mLocationHistoryDTO
     * @return
     */
    int deleteByCondition(MLocationHistoryDTO mLocationHistoryDTO);

}

