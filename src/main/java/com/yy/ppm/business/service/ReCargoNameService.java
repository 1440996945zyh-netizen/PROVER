package com.yy.ppm.business.service;


import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.BusServiceSearchDTO;
import com.yy.ppm.business.bean.dto.TBusServiceDTO;
import com.yy.ppm.business.bean.dto.reCargoName.ReCargoNameDTO;
import com.yy.ppm.master.bean.dto.MCargoDTO;
import com.yy.ppm.master.bean.dto.MCargoSearchDTO;

/**
 * (BusService)表服务接口
 *
 * @author 韩旭
 * @date 2021-03-18 10:52:04
 */
public interface ReCargoNameService {

    /**
     * 获取数据列表
     * @param searchDTO
     * @return
     */
    public Pages<MCargoDTO> getList(MCargoSearchDTO searchDTO);


    /**
     * 更新货名
     * @param reCargoNameDTO
     */
    public void update(ReCargoNameDTO reCargoNameDTO);


}