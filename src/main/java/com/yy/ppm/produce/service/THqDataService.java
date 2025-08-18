package com.yy.ppm.produce.service;


import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.THqDataDTO;
import com.yy.ppm.produce.bean.dto.THqDataSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清数据补录表(THqData)Service
 * @Description
 * @createTime 2025年04月24日 17:23:00
 */
public interface THqDataService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<THqDataDTO> getList(THqDataSearchDTO searchDTO);

    /**
     * 查询单条记录
     * @param id
     * @return 实体
     */
    THqDataDTO getDetail(Long id);

    List<Map<String,String>> getHqByCargoInfoId(List<Long> ids);

    /**
     * 保存
     * @param tHqDataDTO
     * @return 是否成功
     */
    boolean doSave(THqDataDTO tHqDataDTO);

    /**
     * 保存
     * @param list
     * @return 是否成功
     */
    boolean listSave(List<THqDataDTO> list);

    /**
     * 删除
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);


    boolean deleteByTallyId(Long id);

}

