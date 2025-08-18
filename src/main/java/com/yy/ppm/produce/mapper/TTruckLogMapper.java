package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.TTruckLogDTO;
import com.yy.ppm.produce.bean.po.TPoundPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName 车辆作业流水
 * @author ningjp
 * @version 1.0.0
 * @Description
 * @createTime 2023年11月17日 20:21:00
 */
@Component
public interface TTruckLogMapper {

    Page<TTruckLogDTO> getList(TTruckLogDTO query);

}
