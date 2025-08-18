package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.auth.bean.dto.UserInfo;
import com.yy.ppm.produce.bean.dto.*;
import com.yy.ppm.produce.bean.po.TPoundPO;
import com.yy.ppm.produce.bean.po.TPrdOddWorkPlanDetailPO;
import com.yy.ppm.statement.bean.dto.costShipWaterElectricity.TBusTrustDTO;
import com.yy.ppm.system.bean.dto.SysDeptDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName 零工申请
 * @author wangxd
 * @version 1.0.0
 * @Description
 * @createTime 2023年12月12日 11:21:00
 */
@Component
public interface TPrdOddWorkPlanMapper {

    Page<TPrdOddResultDTO> getList(TPrdOddSearchDTO dto);

    List<TPrdOddLogResultDTO> getLogList(Long id);

    List<TPrdOddResultDTO> getAllList(TPrdOddSearchDTO dto);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    TPrdOddResultDTO getDetail(@Param("id") Long id);
    List<TPrdOddResultDTO> getListByIds(@Param("ids") List<Long> ids);
    @Edit
    int insert(TPrdOddSaveDTO dto);
    @Edit
    int update(TPrdOddSaveDTO dto);
    @Edit
    int updateConfirm(TPrdOddSaveDTO dto);
    @Edit
    int updateReject(TPrdOddSaveDTO dto);
    @Edit
    int updateAbandoned(TPrdOddSaveDTO dto);
    @Edit
    int updateFirstApprove(TPrdOddSaveDTO dto);
    @Edit
    int updateSecondApprove(TPrdOddSaveDTO dto);

    int deleteById(@Param("id") Long id);

    List<SysDeptDTO> getDeptByType(@Param("level")Integer level, @Param("type")String type);

    @Edit
    int cancelConfirm(TPrdOddSaveDTO dto);

    @Edit
    int cancelFirstApprove(TPrdOddSaveDTO dto);

    @Edit
    int cancelSecondApprove(TPrdOddSaveDTO dto);

    @Edit
    int updateThirdApprove(TPrdOddSaveDTO dto);

    @Edit
    int cancelThirdApprove(TPrdOddSaveDTO dto);

    @Edit
    int updateOddPlanNo(TPrdOddSaveDTO dto);

    List<TPrdOddDetailResultDTO> getOddByMacTime(@Param("macId") String macId,
                                                 @Param("oddId") Long oddId,
                                                 @Param("startTime") Date startTime,
                                                 @Param("endTime") Date endTime);
    List<TPrdOddDetailResultDTO> getOddByMacHour(@Param("macId") String macId,
                                                 @Param("oddId") Long oddId,
                                                 @Param("startHour") BigDecimal startHour,
                                                 @Param("endHour") BigDecimal endHour);

    List<TOddWorkPlanAttendanceDTO> queryAttendanceByOdd(@Param("classCode") String classCode,
                                                         @Param("deptNo") String deptNo,
                                                         @Param("workDate") Date workDate);

    List<UserInfoDTO> getUserInfoByUserIds(@Param("deptId") String deptId);

    String getDeptIdByDeptNo(@Param("deptNo") String workDeptId);

    DeptDTO getDeptByUserId(@Param("userId")String s);
}
