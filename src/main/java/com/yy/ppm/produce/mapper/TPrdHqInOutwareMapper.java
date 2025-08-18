package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.largescreen.bean.dto.SInportCarExportDTO;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareDTO;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareExportDTO;
import com.yy.ppm.produce.bean.dto.TPrdHqInOutwareSearchDTO;
import org.apache.ibatis.cursor.Cursor;
import org.springframework.stereotype.Repository;

@Repository
public interface TPrdHqInOutwareMapper {

    Cursor<TPrdHqInOutwareExportDTO> getExportList(TPrdHqInOutwareSearchDTO searchDTO);

    Page<TPrdHqInOutwareDTO> getList(TPrdHqInOutwareSearchDTO searchDTO);

    TPrdHqInOutwareDTO getById(Long id);

    @Edit
    int update(TPrdHqInOutwareDTO tPrdHqInOutwareDTO);

    int deleteById(Long id);
}
