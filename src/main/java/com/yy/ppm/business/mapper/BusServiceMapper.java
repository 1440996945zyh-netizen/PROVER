package com.yy.ppm.business.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;

/**
 * (BusService)Dao
 *
 * @author 韩旭
 * @date 2021-03-18 10:51:39
 */
public interface BusServiceMapper {

    /**
     * 获取列表
     *
     * @param busServiceSearchDTO SearchDTO
     * @return
     */
    public Page<TBusServiceDTO> getList(BusServiceSearchDTO busServiceSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    public TBusServiceDTO getById(Long id);

    /**
     * 新增
     *
     * @param busServiceDTO DTO
     * @return
     */
    @Edit
    public int insert(TBusServiceDTO busServiceDTO);

    /**
     * 修改
     *
     * @param busServiceDTO DTO
     * @return
     */
    @Edit
    public int update(TBusServiceDTO busServiceDTO);

    /**
     * 插入服务与主票货关系表
     *
     * @param busServiceDTO
     * @return
     */
    @Edit
    public int insertBusServiceProcess(TBusServiceDTO busServiceDTO);


}