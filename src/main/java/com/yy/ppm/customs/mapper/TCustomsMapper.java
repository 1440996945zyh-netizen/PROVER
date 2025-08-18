package com.yy.ppm.customs.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.yy.ppm.business.bean.dto.TBusCargoInfoDTO;
import com.yy.ppm.customs.bean.TCustomsDTO;
import com.yy.ppm.customs.bean.TDriverDTO;
import com.yy.ppm.customs.bean.TosPlanDTO;

/**
 * @ClassName 海关相关
 * @author ningjp
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月03日 08:21:00
 */
public interface TCustomsMapper {

    String getLastTime();

    List<TCustomsDTO> getList(@Param("lastTime") String lastTime, @Param("currentTime") String currentTime);

    List<TDriverDTO> getDriverList(@Param("planNo") String planNo);

    List<TDriverDTO> getDyDriverList(String id);

	void deleteTosPlanList(String[] ids);

	List<Map<String, Object>> getTosPlanList();

	TBusCargoInfoDTO getCargoIngnById(Long id);

	void insert(TosPlanDTO tosPlanDTO);
}
