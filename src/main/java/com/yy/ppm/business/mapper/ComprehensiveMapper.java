package com.yy.ppm.business.mapper;

import com.yy.ppm.business.bean.dto.CargoTypeDTO;
import com.yy.ppm.business.bean.dto.ComprehensiveDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprehensiveMapper {

    ComprehensiveDTO getList(String planDate);

    List<CargoTypeDTO> getPieList(String planDate);
}
