package com.yy.ppm.business.service;



import com.yy.common.page.Pages;
import com.yy.ppm.business.bean.dto.TBusOrderDTO;
import com.yy.ppm.business.bean.dto.TBusOrderSearchDTO;

import java.util.Map;

/**
 * @ClassName 委托单主表(TBusOrder)Service
 * @author makejava
 * @version 1.0.0
 * @Description
 * @createTime 2024年10月23日 09:03:00
 */
public interface TBusOrderService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TBusOrderDTO> getList(TBusOrderSearchDTO searchDTO);
    
     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public Map<String,Object> getDetail(Long id);

    /**
     * 保存
     *
     * @param tBusOrderDTO
     * @return 是否成功
     */
    public boolean doSave(TBusOrderDTO tBusOrderDTO);


     /**
      * 更新
      * @param tBusOrderDTO
      * @return 是否成功
      */
    public boolean updateStatus(TBusOrderDTO tBusOrderDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

