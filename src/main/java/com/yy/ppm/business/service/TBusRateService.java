package com.yy.ppm.business.service;



import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.*;

import java.util.List;

/**
 * @ClassName 费率(TBusRate)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月03日 16:48:00
 */
public interface TBusRateService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TBusRateDTO> getList(TBusRateSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TBusRateDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tBusRateDTO
     * @return 是否成功
     */
    public boolean doSave(TBusRateDTO tBusRateDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

     boolean busRatePass(Long id);
     boolean busRateCancle(Long id);

     List<TBusServiceDTO> getListService(BusServiceSearchDTO busServiceSearchDTO);

    public void doSaveCargo(List<TBusRateDTO> list);
    public void updateCargo(TBusRateUpdateDTO dto);

    List<TBusRateDTO> getDetailCargo(TBusRateSearchDTO tBusRateSearchDTO);

    Pages<TBusRateDTO> getListCargo(TBusRateSearchDTO searchDTO);

    void busRatePassCargo(TBusRateDTO tBusRateDTO);
    void busRateRevokeCargo(TBusRateDTO tBusRateDTO);
    void delRateCargo(TBusRateDTO tBusRateDTO);
}

