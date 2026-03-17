package com.yy.ppm.equipment.service;

import com.yy.common.page.Pages;
import com.yy.ppm.equipment.bean.dto.MPecialPersonDTO;
import com.yy.ppm.equipment.bean.dto.MPecialPersonSearchDTO;

/**
 * 特种作业人员证书Service接口
 * @author system
 */
public interface MPecialPersonService {

    /**
     * 查询特种作业人员证书列表（分页）
     */
    Pages<MPecialPersonDTO> getList(MPecialPersonSearchDTO searchDTO);

    /**
     * 根据ID查询特种作业人员证书
     */
    MPecialPersonDTO getById(Long id);

    /**
     * 新增特种作业人员证书
     */
    void save(MPecialPersonDTO dto);

    /**
     * 删除特种作业人员证书
     */
    void deleteById(Long id);
}

