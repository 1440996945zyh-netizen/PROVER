package com.yy.ppm.produce.mapper;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailLogPO;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName 零工申请
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月27日 18:21:00
 */
@Component
public interface TPrdOddWorkPlanDetailLogMapper {

    List<TPrdOddWorkPlanDetailLogPO> getList(@Param("operateId") Long oddPlanId);

    @Edit
    int insert(List<TPrdOddWorkPlanDetailLogPO> detailList);



}
