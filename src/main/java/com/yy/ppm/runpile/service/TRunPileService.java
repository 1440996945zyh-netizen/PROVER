package com.yy.ppm.runpile.service;

import java.util.List;

import com.yy.ppm.machine.bean.dto.TMacTerminalStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.TRunPilePortStorageDetailDTO;
import com.yy.ppm.runpile.bean.dto.TStorageYardDTO;

public interface TRunPileService {

	List<TStorageYardDTO> getStorageYardList(String storageYardLevel, String parentId);

	List<TMacTerminalStackPositionDTO> getStackPositionList();

	int saveStackPosition(MStorageStackPositionDTO storageStackPositionDTO);

	int deleteStackPosition(MStorageStackPositionDTO storageStackPositionDTO);

	List<TRunPilePortStorageDetailDTO> getRunPileNeedList(Long massId, String runPileState);
}
