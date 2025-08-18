package com.yy.ppm.produce.service;


import com.yy.common.page.Pages;
import com.yy.ppm.produce.bean.dto.TPrdDySumDTO;
import com.yy.ppm.produce.bean.dto.TPrdDySumSearchDTO;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName (TPrdDySum)Service
 * @Description
 * @createTime 2024年12月03日 17:07:00
 */
public interface TPrdDySumService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TPrdDySumDTO> getList(TPrdDySumSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TPrdDySumDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param tPrdDySumDTO
     * @return 是否成功
     */
    boolean doSave(TPrdDySumDTO tPrdDySumDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

