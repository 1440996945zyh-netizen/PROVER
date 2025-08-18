package com.yy.ppm.runpile.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.yy.framework.annotation.Edit;
import com.yy.ppm.machine.bean.dto.TMacTerminalStackPositionDTO;
import com.yy.ppm.produce.bean.dto.portStorage.TPrdPortStorageDetailDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.TRunPilePortStorageDetailDTO;
import com.yy.ppm.runpile.bean.dto.TStorageYardDTO;
import com.yy.ppm.runpile.bean.po.MStorageStackPositionPO;

@Repository
public interface TRunPileMapper {

	List<TStorageYardDTO> getStorageYardList(@Param("storageYardLevel") String storageYardLevel, @Param("parentId") String parentId);

	List<TMacTerminalStackPositionDTO> getStackPositionList();
	List<TMacTerminalStackPositionDTO> listByCondition(MStorageStackPositionPO storageStackPositionPO);

	@Edit
	int saveStackPosition(MStorageStackPositionPO storageStackPositionPO);

	int updateStackPositionDelFlag(MStorageStackPositionDTO storageStackPositionDTO);

	Integer getNeedRunpileQuantity();

	List<TRunPilePortStorageDetailDTO> getPortStorageDetailList(@Param("massId") Long massId);

	List<TRunPilePortStorageDetailDTO> getPrePortStorageDetailList(@Param("massId") Long massId);
	
}