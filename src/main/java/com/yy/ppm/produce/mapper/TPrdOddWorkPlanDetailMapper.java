package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.TPrdOddDetailResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddResultDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddSaveDTO;
import com.yy.ppm.produce.bean.dto.TPrdOddSearchDTO;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailPO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
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
public interface TPrdOddWorkPlanDetailMapper {

    List<TPrdOddWorkPlanDetailPO> getList(@Param("oddPlanId") Long oddPlanId);

    @Edit
    int insert(List<TPrdOddWorkPlanDetailPO> detailList);

    int deleteByOddPlanId(@Param("oddPlanId") Long oddPlanId);

    List<TPrdOddDetailResultDTO> getReportDuration(List<Long> oddPlanIds);

}
