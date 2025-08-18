package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MPiecePriceDTO;
import com.yy.ppm.master.bean.dto.MPiecePriceSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 计件单价(MPiecePrice)Mapper
 * @Description
 * @createTime 2023年09月15日 11:32:00
 */
@Repository
public interface MPiecePriceMapper {

    /**
     * 获取计件单价列表
     *
     * @param mPiecePriceSearchVo
     * @return
     */
    Page<MPiecePriceDTO> getList(MPiecePriceSearchDTO mPiecePriceSearchVo);

    /**
     * 导出计件单价列表
     *
     * @param mPiecePriceSearchDTO
     * @return
     */
    List<MPiecePriceDTO> exportList(MPiecePriceSearchDTO mPiecePriceSearchDTO);

    /**
     * 根据id获取计件单价
     *
     * @param id 主键
     * @return
     */
    MPiecePriceDTO getById(Long id);

    /**
     * 新增计件单价
     *
     * @param mPiecePriceDTO
     * @return
     */
    @Edit
    int insert(MPiecePriceDTO mPiecePriceDTO);

    /**
     * 修改计件单价
     *
     * @param mPiecePriceDTO
     * @return
     */
    @Edit
    int update(MPiecePriceDTO mPiecePriceDTO);


    /**
     * 根据id删除计件单价
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

