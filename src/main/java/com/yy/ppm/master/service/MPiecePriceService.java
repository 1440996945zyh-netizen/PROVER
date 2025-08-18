package com.yy.ppm.master.service;


import com.yy.common.page.Pages;
import com.yy.ppm.master.bean.dto.MPiecePriceDTO;
import com.yy.ppm.master.bean.dto.MPiecePriceSearchDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;

import java.util.List;
import java.util.Map;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 计件单价(MPiecePrice)Service
 * @Description
 * @createTime 2023年09月15日 11:32:00
 */
public interface MPiecePriceService {

    /**
     * 获取列表（翻页）
     *
     * @param searchDTO
     * @return 对象列表
     */
    Pages<MPiecePriceDTO> getList(MPiecePriceSearchDTO searchDTO);

    /**
     * 查询单条记录
     *
     * @param id
     * @return 实体
     */
    MPiecePriceDTO getDetail(Long id);

    /**
     * 保存
     *
     * @param mPiecePriceDTO
     * @return 是否成功
     */
    boolean doSave(MPiecePriceDTO mPiecePriceDTO);

    /**
     * 删除
     *
     * @param id
     * @return 是否成功
     */
    boolean deleteById(Long id);

    List<Map<String, Object>> getWorkProcessSelect(MWorkProcessSearchDTO mWorkProcessSearchDTO);
}

