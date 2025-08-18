package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MBerthBollardDTO;
import com.yy.ppm.master.bean.dto.MBerthDTO;
import com.yy.ppm.master.bean.dto.MBerthSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 泊位信息(MBerth)Mapper
 * @Description
 * @createTime 2023年06月05日 16:06:00
 */
@Repository
public interface MBerthMapper {

    /**
     * 获取泊位信息列表
     *
     * @param mBerthSearchVo
     * @return
     */
    Page<MBerthDTO> getList(MBerthSearchDTO mBerthSearchVo);

    /**
     * 导出泊位信息列表
     *
     * @param mBerthSearchDTO
     * @return
     */
    List<MBerthDTO> exportList(MBerthSearchDTO mBerthSearchDTO);

    /**
     * 根据id获取泊位信息
     *
     * @param id 主键
     * @return
     */
    MBerthDTO getById(Long id);

    MBerthDTO getByBerthCode(String berthCode);

    List<Map<String,Object>> getBollardsByBerthId(Long berthId);

    /**
     * 新增泊位信息
     *
     * @param mBerthDTO
     * @return
     */
    @Edit
    int insert(MBerthDTO mBerthDTO);

    /**
     * 修改泊位信息
     *
     * @param mBerthDTO
     * @return
     */
    @Edit
    int update(MBerthDTO mBerthDTO);


    /**
     * 根据id删除泊位信息
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);


    /**
     * 获取泊位缆桩信息列表
     *
     * @param berthId
     * @return
     */
    List<MBerthBollardDTO> getBollardList(Long berthId, String bollardName);

    /**
     * 根据id获取泊位缆桩信息
     *
     * @param id 主键
     * @return
     */
    MBerthBollardDTO getBollardById(Long id);

    /**
     * 新增泊位缆桩信息
     *
     * @param mBerthDTO
     * @return
     */
    @Edit
    int insertBollard(MBerthBollardDTO mBerthDTO);

    /**
     * 修改泊位缆桩信息
     *
     * @param mBerthDTO
     * @return
     */
    @Edit
    int updateBollard(MBerthBollardDTO mBerthDTO);


    /**
     * 根据id删除泊位缆桩信息
     *
     * @param id 主键
     * @return
     */
    int deleteBollardById(Long id);

    /**
     * 查询主泊位信息
     * @return
     */
    List<MBerthDTO> getParentBerth();

    /**
     * 查询泊位下揽庄数量
     * @param id
     * @return
     */
    int getChildrenData(Long id);
}

