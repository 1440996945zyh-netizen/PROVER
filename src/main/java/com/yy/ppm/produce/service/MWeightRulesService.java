package com.yy.ppm.produce.service;


import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.MWeightRulesDTO;
import com.yy.ppm.produce.bean.dto.MWeightRulesSearchDTO;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName (MWeightRules)Service
 * @Description
 * @createTime 2023年11月30日 17:20:00
 */
public interface MWeightRulesService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<MWeightRulesDTO> getList(MWeightRulesSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    MWeightRulesDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mWeightRulesDTO
     * @return 是否成功
     */
    boolean doSave(MWeightRulesDTO mWeightRulesDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

