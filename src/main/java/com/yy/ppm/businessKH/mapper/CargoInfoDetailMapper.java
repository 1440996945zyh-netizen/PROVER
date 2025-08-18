package com.yy.ppm.businessKH.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yy.ppm.businessKH.model.CargoInfoDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author lihuijie
 * @version 1.0.0
 * @ClassName CargoInfoMapper.java
 * @Description TODO 控货管理
 * @createTime 2022年04月24日 14:58:00
 */
@Mapper
@Repository
public interface CargoInfoDetailMapper extends BaseMapper<CargoInfoDetail> {

}
