package com.yy.ppm.dispatch.service;


import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.TAnchApplyDTO;
import com.yy.ppm.dispatch.bean.dto.TAnchApplySearchDTO;

public interface TAnchApplyService {

	Pages<TAnchApplyDTO> getList(TAnchApplySearchDTO tAnchApplySearchDTO);


	int verify(TAnchApplyDTO tAnchApplyDTO);


	boolean updateLeaveAnchTime(TAnchApplyDTO tAnchApplyDTO);
}
