package com.yy.ppm.master.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-22 14:27
 */
public interface MPieceWorkTeamService {

    void insertPieceWorkTeam(MPieceWorkTeamPO pieceWorkTeam);

    Pages<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query, PageParameter parameter);

    void updatePieceWorkTeam(MPieceWorkTeamPO pieceWorkTeam);

    void deletePieceWorkTeam(List<Long> ids);
}
