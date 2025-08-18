package com.yy.ppm.runpile.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.Page;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionSearchDTO;

@Repository
public interface TRunPileHistoryMapper {

	Page<MStorageStackPositionDTO> getRunPileHistoryList(MStorageStackPositionSearchDTO mStorageStackPositionSearchDTO);

	List<MStorageStackPositionDTO> getList(@Param("stackId") String stackId);
}