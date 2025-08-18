package com.yy.ppm.dispatch.service;



import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailDTO;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 封航记录表(TDisCloseSail)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:54:00
 */
public interface TDisCloseSailService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TDisCloseSailDTO> getList(TDisCloseSailSearchDTO searchDTO);

    byte[] export(TDisCloseSailSearchDTO searchDTO);

    public List<Map<String,Object>> getShipVoyageList(TDisCloseSailSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TDisCloseSailDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tDisCloseSailDTO
     * @return 是否成功
     */
    public boolean doSave(TDisCloseSailDTO tDisCloseSailDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

