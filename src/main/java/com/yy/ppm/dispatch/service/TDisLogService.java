package com.yy.ppm.dispatch.service;



import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.TDisLogDTO;
import com.yy.ppm.dispatch.bean.dto.TDisLogSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @ClassName 调度日志(TDisLog)Service
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:45:00
 */
public interface TDisLogService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TDisLogDTO> getList(TDisLogSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param id
      * @return 实体
      */
     public TDisLogDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tDisLogDTO
     * @return 是否成功
     */
    public boolean doSave(TDisLogDTO tDisLogDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    public boolean deleteById(Long id);

}

