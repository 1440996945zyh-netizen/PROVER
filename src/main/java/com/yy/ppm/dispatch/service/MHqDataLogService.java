package com.yy.ppm.dispatch.service;


import com.yy.common.page.Pages;
import com.yy.ppm.dispatch.bean.dto.MHqDataLogDTO;
import com.yy.ppm.dispatch.bean.dto.MHqDataLogSearchDTO;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清货物变更日志表(MHqDataLog)Service
 * @Description
 * @createTime 2025年05月27日 18:20:00
 */
public interface MHqDataLogService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<MHqDataLogDTO> getList(MHqDataLogSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    MHqDataLogDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mHqDataLogDTO
     * @return 是否成功
     */
    boolean doSave(MHqDataLogDTO mHqDataLogDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

}

