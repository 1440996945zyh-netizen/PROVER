package com.yy.ppm.master.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.PageHelperUtils;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.master.mapper.MPieceWorkTeamMapper;
import com.yy.ppm.master.service.MPieceWorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-22 14:27
 */
@Service
public class MPieceWorkTeamServiceImpl implements MPieceWorkTeamService {

    @Autowired
    private MPieceWorkTeamMapper mPieceWorkTeamMapper;

    @Autowired
    private Snowflake snowflake;

    @Override
    public void insertPieceWorkTeam(MPieceWorkTeamPO pieceWorkTeam) {
        pieceWorkTeam.setId(snowflake.nextId());
        mPieceWorkTeamMapper.insertPieceWorkTeam(pieceWorkTeam);
    }

    @Override
    public Pages<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query, PageParameter parameter) {
        return PageHelperUtils.limit(parameter, () -> mPieceWorkTeamMapper.listPieceWorkTeam(query));
    }

    @Override
    public void updatePieceWorkTeam(MPieceWorkTeamPO pieceWorkTeam) {
        mPieceWorkTeamMapper.updatePieceWorkTeam(pieceWorkTeam);
    }

    @Override
    public void deletePieceWorkTeam(List<Long> ids) {
        mPieceWorkTeamMapper.deletePieceWorkTeam(ids);
    }
}

