package com.yy.ppm.finance.service;


import com.yy.common.page.Pages;
import com.yy.ppm.finance.bean.dto.FFeeItemDTO;
import com.yy.ppm.finance.bean.dto.FFeeItemSearchDTO;

/**
 * (FFeeItem)表服务接口
 *
 * @author 韩旭
 * @date 2021-03-29 11:10:55
 */
public interface FFeeItemService {

    /**
     * 获取数据列表
     *
     * @param fFeeItemSearchDTO
     * @return
     */
    public Pages<FFeeItemDTO> getList(FFeeItemSearchDTO fFeeItemSearchDTO);

    /**
     * 根据ID获取
     *
     * @param id 主键
     * @return
     */
    public FFeeItemDTO getById(Long id);

    /**
     * 保存
     *
     * @param fFeeItemDTO
     * @return
     */
    public int save(FFeeItemDTO fFeeItemDTO);

}