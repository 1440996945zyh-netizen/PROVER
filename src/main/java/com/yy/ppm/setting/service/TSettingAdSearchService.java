package com.yy.ppm.setting.service;



import com.yy.common.page.Pages;
import com.yy.ppm.setting.bean.dto.TSettingAdSearchDTO;
import com.yy.ppm.setting.bean.dto.TSettingAdSearchSearchDTO;

/**
 * @ClassName 高級查詢配置表(TSettingAdSearch)Service
 * @author zws
 * @version 1.0.0
 * @Description
 * @createTime 2025年09月25日 15:35:00
 */
public interface TSettingAdSearchService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    public Pages<TSettingAdSearchDTO> getList(TSettingAdSearchSearchDTO searchDTO);

     /**
      * 查询单条记录
      *
      * @param menuId,tableId
      * @return 实体
      */
     public TSettingAdSearchDTO getDetail(Long menuId,String tableId);

    /**
     * 保存
     *
     * @param tSettingAdSearchDTO
     * @return 是否成功
     */
    public boolean doSave(TSettingAdSearchDTO tSettingAdSearchDTO);


 }

