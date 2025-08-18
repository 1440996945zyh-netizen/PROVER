package com.yy.ppm.produce.service;

import com.yy.common.page.PageParameter;
import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.GroupQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryDTO;
import com.yy.ppm.produce.bean.dto.salary.SalaryQueryExamineDTO;
import com.yy.ppm.produce.bean.po.TPrdGroupDetailPO;
import com.yy.ppm.produce.bean.po.TPrdGroupPO;
import com.yy.ppm.produce.bean.po.TPrdSalaryPO;

import java.util.List;

/**
 * @Auther chenfs
 * @Description
 * @Date 2023-10-12 10:03
 */
public interface TPrdGroupService {

    Pages<TPrdGroupPO> listGroup(GroupQueryDTO query, PageParameter parameter);
    List<TPrdGroupPO> listGroupNo(GroupQueryDTO query);

    int save(TPrdGroupPO tPrdGroupPO);

    TPrdGroupPO getById(Long id);

    List<TPrdGroupDetailPO> insertGroup(TPrdGroupPO tPrdGroupPO);
}
