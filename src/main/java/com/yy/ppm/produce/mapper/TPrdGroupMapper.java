package com.yy.ppm.produce.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryExamineDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupDetailPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-12 10:03
 */
public interface TPrdGroupMapper {

    Page<TPrdGroupPO> listGroup(GroupQueryDTO query);

    @Edit
    int save(TPrdGroupPO tPrdGroupPO);

    @Edit
    int saveDetail(@Param("list") List<TPrdGroupDetailPO> list);

    @Edit
    int update(TPrdGroupPO tPrdGroupPO);

    TPrdGroupPO getById(Long id);

    List<Map<String,Object>> selectDetpId(Long id);

    List<Map<String, Object>> getProcess(String processCode);

    List<Map<String,Object>> getDeptId(@Param("workDate") String workDate, @Param("classCode") String classCode);

    List<TPrdGroupDetailPO> getNewGroup(TPrdGroupPO tPrdGroupPO);
}
