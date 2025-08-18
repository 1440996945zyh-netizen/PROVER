package com.yy.ppm.dispatch.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.MHqDataLogDTO;
import com.yy.ppm.dispatch.bean.dto.MHqDataLogSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 海清货物变更日志表(MHqDataLog)Mapper
 * @Description
 * @createTime 2025年05月27日 18:20:00
 */
@Repository
public interface MHqDataLogMapper {

    /**
     * 获取海清货物变更日志表列表
     *
     * @param mHqDataLogSearchVo
     * @return
     */
    Page<MHqDataLogDTO> getList(MHqDataLogSearchDTO mHqDataLogSearchVo);

    /**
     * 导出海清货物变更日志表列表
     *
     * @param mHqDataLogSearchDTO
     * @return
     */
    List<MHqDataLogDTO> exportList(MHqDataLogSearchDTO mHqDataLogSearchDTO);

    /**
     * 根据id获取海清货物变更日志表
     *
     * @param id 主键
     * @return
     */
    MHqDataLogDTO getById(Long id);

    /**
     * 新增海清货物变更日志表
     *
     * @param mHqDataLogDTO
     * @return
     */
    @Edit
    int insert(MHqDataLogDTO mHqDataLogDTO);

    @Edit
    int insertList(List<MHqDataLogDTO> list);

    /**
     * 修改海清货物变更日志表
     *
     * @param mHqDataLogDTO
     * @return
     */
    @Edit
    int update(MHqDataLogDTO mHqDataLogDTO);


    /**
     * 根据id删除海清货物变更日志表
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

