package com.yy.ppm.dispatch.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailDTO;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailExcelDTO;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;

/**
 * @ClassName 封航记录表(TDisCloseSail)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:54:00
 */
@Repository
public interface TDisCloseSailMapper {

   /**
     * 获取封航记录表列表
     * @param tDisCloseSailSearchVo
     * @return
     */
    public Page<TDisCloseSailDTO> getList(TDisCloseSailSearchDTO tDisCloseSailSearchVo);

   /**
     * 导出封航记录表列表
     * @param tDisCloseSailSearchDTO
     * @return
     */
    public Cursor<TDisCloseSailExcelDTO> exportCursor(TDisCloseSailSearchDTO tDisCloseSailSearchDTO);
    public List<TDisCloseSailExcelDTO> exportList(TDisCloseSailSearchDTO tDisCloseSailSearchDTO);

    /**
     * 根据id获取封航记录表
     * @param id 主键
     * @return
     */
    public TDisCloseSailDTO getById(Long id);

    /**
     * 新增封航记录表
     * @param tDisCloseSailDTO
     * @return
     */
    @Edit
    public int insert(TDisCloseSailDTO tDisCloseSailDTO);

    /**
     * 修改封航记录表
     * @param tDisCloseSailDTO
     * @return
     */
    @Edit
    public int update(TDisCloseSailDTO tDisCloseSailDTO);


    /**
     * 根据id删除封航记录表
     * @param id 主键
     * @return
     */
    public int deleteById(Long id);
}

