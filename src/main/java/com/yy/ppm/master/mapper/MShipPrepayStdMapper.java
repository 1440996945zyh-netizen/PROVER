package com.yy.ppm.master.mapper;


import com.github.pagehelper.Page;
import com.yy.framework.annotation.Edit;
import com.yy.ppm.master.bean.dto.MShipPrepayStdDTO;
import com.yy.ppm.master.bean.dto.MShipPrepayStdSearchDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lizx
 * @version 1.0.0
 * @ClassName 船舶预缴标准, (MShipPrepayStd)Mapper
 * @Description
 * @createTime 2023年10月23日 15:50:00
 */
@Repository
public interface MShipPrepayStdMapper {

    /**
     * 获取船舶预缴标准,列表
     *
     * @param mShipPrepayStdSearchVo
     * @return
     */
    Page<MShipPrepayStdDTO> getList(MShipPrepayStdSearchDTO mShipPrepayStdSearchVo);

    /**
     * 导出船舶预缴标准,列表
     *
     * @param mShipPrepayStdSearchDTO
     * @return
     */
    List<MShipPrepayStdDTO> exportList(MShipPrepayStdSearchDTO mShipPrepayStdSearchDTO);

    /**
     * 根据id获取船舶预缴标准,
     *
     * @param id 主键
     * @return
     */
    MShipPrepayStdDTO getById(Long id);

    /**
     * 新增船舶预缴标准,
     *
     * @param mShipPrepayStdDTO
     * @return
     */
    @Edit
    int insert(MShipPrepayStdDTO mShipPrepayStdDTO);

    /**
     * 修改船舶预缴标准,
     *
     * @param mShipPrepayStdDTO
     * @return
     */
    @Edit
    int update(MShipPrepayStdDTO mShipPrepayStdDTO);


    /**
     * 根据id删除船舶预缴标准,
     *
     * @param id 主键
     * @return
     */
    int deleteById(Long id);
}

