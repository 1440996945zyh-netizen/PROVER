package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MWorkProcessDTO;
import com.yy.ppm.master.bean.dto.MWorkProcessSearchDTO;
import org.apache.ibatis.annotations.Param;

/**
 * (MWorkProcess)Dao
 *
 * @author 张超
 * @date 2021-03-10 13:56:36
 */
public interface MWorkProcessMapper {

    /**
     * 获取列表
     *
     * @param mWorkProcessSearchDTO SearchDTO
     * @return
     */
    public Page<MWorkProcessDTO> getList(MWorkProcessSearchDTO mWorkProcessSearchDTO);

    /**
     * 根据id获取
     *
     * @param id 主键
     * @return
     */
    public MWorkProcessDTO getById(Long id);

    /**
     * 新增
     *
     * @param mWorkProcessDTO DTO
     * @return
     */
    @Edit
    public int insert(MWorkProcessDTO mWorkProcessDTO);

    /**
     * 修改
     *
     * @param mWorkProcessDTO DTO
     * @return
     */
    @Edit
    public int update(MWorkProcessDTO mWorkProcessDTO);

    int isRepeateSubProcess(@Param("parentId")Long parentId,@Param("processNm")String processNm,@Param("processCd")String processCd);
}
