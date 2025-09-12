package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 字典及字典类型操作mapper
 *
 * @author
 * @date
 */
public interface MPrintMapper {

    @Edit
    void insert(MPrintDTO po);


     Page<MPrintDTO> getList(MPrintSearchDTO mPrintSearchDTO);
}
