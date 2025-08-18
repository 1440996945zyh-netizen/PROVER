package com.yy.ppm.statement.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.statement.bean.dto.busHandoverlist.*;
import com.yy.ppm.statement.bean.po.TBusHandoverlistPO;
import com.yy.ppm.statement.bean.po.TBusHandoverlistTransferPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Auther wangxd
 * @Description
 * @Date 2024-01-04 10:59
 */
@Component
public interface TBusHandoverlistTransferMapper {

    @Edit
    int insertBusHandoverlist(@Param("entity") TBusHandoverlistTransferPO busHandoverlist);


    @Edit
    int updateHandoverListById(@Param("entity") TBusHandoverlistTransferPO busHandoverlist);

    int deleteByCargoInfoId(Long cargoInfoId);

}
