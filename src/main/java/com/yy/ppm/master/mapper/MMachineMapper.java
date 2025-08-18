package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MMachineDTO;
import com.yy.ppm.master.bean.dto.MMachineSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yy
 * @version 1.0.0
 * @ClassName 机械信息(MMachine)Mapper
 * @Description
 * @createTime 2023年06月05日 17:28:00
 */
@Repository
public interface MMachineMapper {

    /**
     * 获取机械信息列表
     *
     * @param mMachineSearchVo
     * @return
     */
    Page<MMachineDTO> getList(MMachineSearchDTO mMachineSearchVo);

    /**
     * 导出机械信息列表
     *
     * @param mMachineSearchDTO
     * @return
     */
    List<MMachineDTO> exportList(MMachineSearchDTO mMachineSearchDTO);

    /**
     * 根据id获取机械信息
     *
     * @param id 主键
     * @return
     */
    MMachineDTO getById(Long id);

    /**
     * 新增机械信息
     *
     * @param mMachineDTO
     * @return
     */
    @Edit
    int insert(MMachineDTO mMachineDTO);

    /**
     * 修改机械信息
     *
     * @param mMachineDTO
     * @return
     */
    @Edit
    int update(MMachineDTO mMachineDTO);


    /**
     * 根据id删除机械信息
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);

    int getTallyById(Long id);

    int getTicketById(Long id);
}

