package com.yy.ppm.applet.service;


import com.yy.common.page.Pages;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeDTO;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 入港公告(TStdEnterPortNotice)Service
 * @Description
 * @createTime 2023年12月01日 14:08:00
 */
public interface TStdEnterPortNoticeService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<TStdEnterPortNoticeDTO> getPageList(TStdEnterPortNoticeSearchDTO searchDTO);

    List<TStdEnterPortNoticeDTO> getListByCondition(TStdEnterPortNoticeSearchDTO searchDTO);

    List<TStdEnterPortNoticeDTO> getLatestOne(TStdEnterPortNoticeSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    TStdEnterPortNoticeDTO getDetail(Long id);


    /**
     * 保存
     *
     * @param tStdEnterPortNoticeDTO
     * @return 是否成功
     */
    boolean doSave(TStdEnterPortNoticeDTO tStdEnterPortNoticeDTO);


    /**
     * 批量保存
     *
     * @param tStdEnterPortNoticeDTOS
     * @return 是否成功
     */
    Map<String, Object> doListSave(List<TStdEnterPortNoticeDTO> tStdEnterPortNoticeDTOS);


    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    /**
     * 批量删除
     * List<Long> ids
     *
     * @param ids
     * @return 是否成功
     */
    boolean deleteListByIds(List<Long> ids);

    /**
     * 批量删除
     *
     * @param tStdEnterPortNoticeDTO
     * @return 是否成功
     */
    boolean deleteByCondition(TStdEnterPortNoticeDTO tStdEnterPortNoticeDTO);

}

