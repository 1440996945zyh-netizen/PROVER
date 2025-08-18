package com.yy.ppm.master.controller;

import com.yy.ppm.common.enums.IsUpdateStorageEnum;
import com.yy.common.enums.Response;
import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.common.util.ValidatorUtils;
import com.yy.framework.exception.BusinessRuntimeException;
import com.yy.ppm.master.bean.po.MPieceWorkTeamPO;
import com.yy.ppm.master.service.MPieceWorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * @Auther linqi
 * @Description 计件工班
 * @Date 2023-08-22 14:18
 */
@RestController
@RequestMapping("/api/external/pieceWorkTeam")
@Validated
public class MPieceWorkTeamController {

    @Autowired
    private MPieceWorkTeamService mPieceWorkTeamService;

    /**
     * 新增计件工班
     *
     * @param pieceWorkTeam
     * @return
     */
    @PostMapping("/insertPieceWorkTeam")
    public Map<String, Object> insertPieceWorkTeam(@RequestBody MPieceWorkTeamPO pieceWorkTeam) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(pieceWorkTeam, true, "id");
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        boolean isContains = IsUpdateStorageEnum.isContains(pieceWorkTeam.getIsUpdateStorage());
        if (!isContains) {
            throw new BusinessRuntimeException("错误的是否更新港存标记");
        }

        mPieceWorkTeamService.insertPieceWorkTeam(pieceWorkTeam);
        return Response.SUCCESS.newBuilder().out("新增成功").toResult();
    }

    /**
     * 计件工班列表
     *
     * @param query
     * @param parameter
     * @return
     */
    @GetMapping("/listPieceWorkTeam")
    public Map<String, Object> listPieceWorkTeam(MPieceWorkTeamPO query, PageParameter parameter) {
        Pages<MPieceWorkTeamPO> result = mPieceWorkTeamService.listPieceWorkTeam(query, parameter);
        return Response.SUCCESS.newBuilder().toResult(result);
    }

    /**
     * 修改计件工班
     *
     * @param pieceWorkTeam
     * @return
     */
    @PutMapping("/updatePieceWorkTeam")
    public Map<String, Object> updatePieceWorkTeam(@RequestBody MPieceWorkTeamPO pieceWorkTeam) {
        ValidatorUtils.FieldBean bean = ValidatorUtils.validator(pieceWorkTeam);
        if (bean.isSuccess()) {
            throw new BusinessRuntimeException(bean.getMsg());
        }

        boolean isContains = IsUpdateStorageEnum.isContains(pieceWorkTeam.getIsUpdateStorage());
        if (!isContains) {
            throw new BusinessRuntimeException("错误的是否更新港存标记");
        }

        mPieceWorkTeamService.updatePieceWorkTeam(pieceWorkTeam);
        return Response.SUCCESS.newBuilder().out("修改成功").toResult();
    }

    /**
     * 删除计件工班
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/deletePieceWorkTeam")
    public Map<String, Object> deletePieceWorkTeam(@RequestParam("ids") @NotEmpty(message = "主键ID不能为空") List<Long> ids) {
        mPieceWorkTeamService.deletePieceWorkTeam(ids);
        return Response.SUCCESS.newBuilder().out("删除成功").toResult();
    }
}
