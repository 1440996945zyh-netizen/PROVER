package com.yy.ppm.finance.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.finance.bean.dto.FFeeItemDTO;
import com.yy.ppm.finance.bean.dto.FFeeItemSearchDTO;

/**
 * (FFeeItem)Dao
 *
 * @author 韩旭
 * @date 2021-03-29 11:10:33
 */
public interface FFeeItemMapper {

    /**
     * 获取列表
     *
     * @param fFeeItemSearchDTO SearchDTO
     * @return
     */
    public Page<FFeeItemDTO> getList(FFeeItemSearchDTO fFeeItemSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    public FFeeItemDTO getById(Long id);

    /**
     * 新增
     *
     * @param fFeeItemDTO DTO
     * @return
     */
    @Edit
    public int insert(FFeeItemDTO fFeeItemDTO);

    /**
     * 修改
     *
     * @param fFeeItemDTO DTO
     * @return
     */
    @Edit
    public int update(FFeeItemDTO fFeeItemDTO);
}