package com.yy.ppm.dispatch.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.TAnchApplyDTO;
import com.yy.ppm.dispatch.bean.dto.TAnchApplySearchDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface TAnchApplyMapper {

	Page<TAnchApplyDTO> getList(TAnchApplySearchDTO tAnchApplySearchDTO);

	
	@Edit
	int verify(TAnchApplyDTO tAnchApplyDTO);

	int updateLeaveAnchTime(TAnchApplyDTO tAnchApplyDTO);

	TAnchApplyDTO getAnchTimeById(Long id);

	List<TAnchApplyDTO> getByShipvoyageId(@Param("shipvoyageId")Long shipvoyageId);

	List<TAnchApplyDTO> getByShipvoyageId2(@Param("shipvoyageId")Long shipvoyageId);

	int updateAnchTime(TAnchApplyDTO tAnchApplyDTO);

	int updateQiMaoTime(TAnchApplyDTO tAnchApplyDTO);

	int updateAnchTimeByDynamicId(TAnchApplyDTO tAnchApplyDTO);

	int deleteAnchTimeByDynamicId(TAnchApplyDTO tAnchApplyDTO);



}
