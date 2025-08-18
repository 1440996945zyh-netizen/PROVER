package com.yy.ppm.business.service;


import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;

/**
 * (BusService)表服务接口
 *
 * @author 韩旭
 * @date 2021-03-18 10:52:04
 */
public interface BusServiceService {

    /**
     * 获取数据列表
     *
     * @param busServiceSearchDTO
     * @return
     */
    public Pages<TBusServiceDTO> getList(BusServiceSearchDTO busServiceSearchDTO);

    /**
     * 根据ID获取
     *
     * @param id 主键
     * @return
     */
    public TBusServiceDTO getById(Long id);

    /**
     * 保存
     *
     * @param busServiceDTO
     * @return
     */
    public int save(TBusServiceDTO busServiceDTO);

}