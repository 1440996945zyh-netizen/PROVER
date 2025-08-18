package com.yy.ppm.businessKH.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yy.ppm.businessKH.model.CargoInfoFhks;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName CargoInfoFhksMapper.java
 * @Description TODO 分货控数
 * @createTime 2023年01月31日 14:06:00
 */
@Mapper
@Repository
public interface CargoInfoFhksMapper extends BaseMapper<CargoInfoFhks> {


    /**
     * @description 查询分货控数
     * @title getCargoInfoFhksList
     * @author lihuijie
     * @updateTime 2023-01-31 14:07
     * @throws
     * @param cargokey:  货健
     * @return java.util.List<com.spgtech.businessKH.dao.CargoInfoFhksMapper>
     */
    List<CargoInfoFhks> getCargoInfoFhksList(String cargokey);

    /**
     * @description 查询该货健有没有存在分货控数
     * @title getFhksCount
     * @author lihuijie
     * @updateTime 2023-09-15 11:24
     * @throws
     * @param cargokey:  货健
     * @return java.lang.String
     */
    String getFhksCount(String cargokey);

}
