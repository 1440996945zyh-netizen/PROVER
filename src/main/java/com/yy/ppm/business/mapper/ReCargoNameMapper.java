package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.business.bean.dto.reCargoName.ReCargoNameDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;

/**
 * (BusService)Dao
 *
 * @author 韩旭
 * @date 2021-03-18 10:51:39
 */
public interface ReCargoNameMapper {

    /**
     * 获取列表
     * @param searchDTO
     * @return
     */
    public Page<MCargoDTO> getList(MCargoSearchDTO searchDTO);

    @Edit
    void update(ReCargoNameDTO dto);

    @Edit
    void save(ReCargoNameDTO dto);


}