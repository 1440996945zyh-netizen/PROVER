package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther linqi
 * @Description
 * @Date 2023-08-22 14:32
 */
public interface MPieceWorkTeamMapper {

    @Edit
    int insertPieceWorkTeam(MPieceWorkTeamPO pieceWorkTeam);

    Page<MPieceWorkTeamPO> listPieceWorkTeam(MPieceWorkTeamPO query);

    @Edit
    int updatePieceWorkTeam(MPieceWorkTeamPO pieceWorkTeam);

    int deletePieceWorkTeam(@Param("ids") List<Long> ids);
}
