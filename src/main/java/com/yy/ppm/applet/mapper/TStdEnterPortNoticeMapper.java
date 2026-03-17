package com.yy.ppm.applet.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeDTO;
import com.yy.ppm.applet.bean.dto.TStdEnterPortNoticeSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author makejava
 * @version 1.0.0
 * @ClassName 入港公告(TStdEnterPortNotice)Mapper
 * @Description
 * @createTime 2023年12月01日 14:08:00
 */
@Repository
public interface TStdEnterPortNoticeMapper {

    /**
     * 获取入港公告列表
     *
     * @param tStdEnterPortNoticeSearchVo
     * @return
     */
    Page<TStdEnterPortNoticeDTO> getPageList(TStdEnterPortNoticeSearchDTO tStdEnterPortNoticeSearchVo);


    /**
     * 导出入港公告列表
     *
     * @param tStdEnterPortNoticeSearchDTO
     * @return
     */
    List<TStdEnterPortNoticeDTO> exportList(TStdEnterPortNoticeSearchDTO tStdEnterPortNoticeSearchDTO);

    List<TStdEnterPortNoticeDTO> getLatestOne(TStdEnterPortNoticeSearchDTO tStdEnterPortNoticeSearchDTO);

    /**
     * 根据id获取入港公告
     *
     * @param id 主键
     * @return
     */
    TStdEnterPortNoticeDTO getById(Long id);


    /**
     * 新增入港公告
     *
     * @param tStdEnterPortNoticeDTO
     * @return
     */
    @Edit
    int insert(TStdEnterPortNoticeDTO tStdEnterPortNoticeDTO);

    /**
     * 批量新增入港公告
     *
     * @param tStdEnterPortNoticeDTOS
     * @return
     */
    @Edit
    int insertList(@Param("tStdEnterPortNoticeDTOS") List<TStdEnterPortNoticeDTO> tStdEnterPortNoticeDTOS);


    /**
     * 修改入港公告
     *
     * @param tStdEnterPortNoticeDTO
     * @return
     */
    @Edit
    int update(TStdEnterPortNoticeDTO tStdEnterPortNoticeDTO);

    /**
     * 批量修改
     *
     * @param tStdEnterPortNoticeDTOS
     * @return
     */
    @Edit
    int updateListById(@Param("tStdEnterPortNoticeDTOS") List<TStdEnterPortNoticeDTO> tStdEnterPortNoticeDTOS);


    /**
     * 根据id删除入港公告
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 批量删除
     * 根据id删除入港公告
     *
     * @param ids 主键
     * @return
     */
    int deleteListByIds(@Param("ids") List<Long> ids);

    /**
     * 批量删除
     * 根据id删除入港公告
     *
     * @param tStdEnterPortNoticeDTO
     * @return
     */
    int deleteByCondition(TStdEnterPortNoticeDTO tStdEnterPortNoticeDTO);

}

