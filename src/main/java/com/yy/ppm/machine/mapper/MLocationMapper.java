package com.yy.ppm.machine.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.machine.bean.dto.MLocationDTO;
import com.yy.ppm.machine.bean.dto.MLocationSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 实时车辆表(MLocation)Mapper
 * @Description
 * @createTime 2023年10月25日 10:21:00
 */
@Repository
public interface MLocationMapper {


    /**
     * 导出实时车辆表列表
     *
     * @param mLocationSearchDTO
     * @return
     */
    List<MLocationDTO> exportList(MLocationSearchDTO mLocationSearchDTO);

    /**
     * 根据id获取实时车辆表
     *
     * @param macId
     * @return
     */
    MLocationDTO getByMacId(String macId);


    /**
     * 新增实时车辆表
     *
     * @param mLocationDTO
     * @return
     */
    @Edit
    int insert(MLocationDTO mLocationDTO);

    /**
     * 修改实时车辆表
     *
     * @param mLocationDTO
     * @return
     */
    @Edit
    int update(MLocationDTO mLocationDTO);



}

