package com.yy.ppm.runpile.service;

import java.util.List;

import com.yy.common.page.Pages;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionDTO;
import com.yy.ppm.runpile.bean.dto.MStorageStackPositionSearchDTO;

public interface TRunPileHistoryService {

	Pages<MStorageStackPositionDTO> getRunPileHistoryList(MStorageStackPositionSearchDTO mStorageStackPositionSearchDTO);

	List<MStorageStackPositionDTO> getList(String stackId);
}
