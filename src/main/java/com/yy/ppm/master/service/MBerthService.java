package com.yy.ppm.master.service;


import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MBerthBollardDTO;
import com.yy.ppm.master.bean.dto.MBerthDTO;
import com.yy.ppm.master.bean.dto.MBerthSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 泊位信息(MBerth)Service
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
public interface MBerthService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<MBerthDTO> getList(MBerthSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    MBerthDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mBerthDTO
     * @return 是否成功
     */
    boolean doSave(MBerthDTO mBerthDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);


    /**
     * 获取缆桩列表（翻页）
     *
     * @param berthId
     * @return 对象列表
     */
    List<MBerthBollardDTO> getBollardList(Long berthId, String bollardName);

    /**
     * 查询单条缆桩记录
     *
     * @param id
     * @return 实体
     */
    MBerthBollardDTO getBollardDetail(Long id);

    /**
     * 保存缆桩
     *
     * @param mBerthBollardDTO
     * @return 是否成功
     */
    boolean doSaveBollard(MBerthBollardDTO mBerthBollardDTO);

    /**
     * 删除缆桩
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteBollardById(Long id);

    /**
     * 查询主泊位信息
     * @return
     */
    List<MBerthDTO> getParentBerth();


    /**
     * 获取缆桩信息
     * @return
     */
    List<Map<String,Object>> getBollard(Long id);

}

