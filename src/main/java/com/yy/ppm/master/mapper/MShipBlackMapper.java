package com.yy.ppm.master.mapper;

import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MShipDTO;
import com.yy.ppm.master.bean.dto.MShipSearchDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MShipBlackMapper {


    public Page<MShipDTO> getList(MShipSearchDTO mShipSearchVo);

    /**
     * 根据id获取海轮资料
     * @param id 主键
     * @return
     */
    public MShipDTO getById(Long id);

    List<MShipDTO> getCheckList(MShipSearchDTO mShipSearchDTO);

    /**
     * 新增海轮资料
     * @param mShipDTO
     * @return
     */
    @Edit
    public int insert(MShipDTO mShipDTO);

    /**
     * 修改海轮资料
     * @param mShipDTO
     * @return
     */
    @Edit
    public int update(MShipDTO mShipDTO);

    /**
     * 根据id删除海轮资料
     * @param id 主键
     * @return
     */
    public int deleteById(Long id);
}
