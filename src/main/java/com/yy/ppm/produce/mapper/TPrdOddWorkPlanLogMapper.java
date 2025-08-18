package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName 零工申请
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月12日 11:21:00
 */
@Component
public interface TPrdOddWorkPlanLogMapper {

    Page<TPrdOddLogResultDTO> getList(TPrdOddLogSearchDTO dto);
    @Edit
    int insert(TPrdOddLogSaveDTO dto);
}
