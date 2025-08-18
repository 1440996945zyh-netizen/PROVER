package com.yy.ppm.dispatch.mapper;


import com.yy.framework.annotation.Edit;
import com.yy.ppm.dispatch.bean.dto.ShipVoyageDto;
import com.yy.ppm.dispatch.bean.dto.TDisCloseSailShipDTO;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName 封航影响航次(TDisCloseSailShip)Mapper
 * @author yy
 * @version 1.0.0
 * @Description
 * @createTime 2023年07月12日 11:46:00
 */
@Repository
public interface TDisCloseSailShipMapper {

  /**
    * 获取封航影响航次列表
    * @param closeSailId
    * @return
    */
   public List<TDisCloseSailShipDTO> getList(Long closeSailId);

   /**
    * 根据id获取封航影响航次
    * @param id 主键
    * @return
    */
   public TDisCloseSailShipDTO getById(Long id);


    /**
     * 根据封航id获取航次信息
     * @param closeSailId
     * @return
     */
    public List<Long> getInfoByCloseSailId(Long closeSailId);
   /**
    * 新增封航影响航次
    * @param tDisCloseSailShipDTO
    * @return
    */
   @Edit
   public int insert(TDisCloseSailShipDTO tDisCloseSailShipDTO);

   /**
    * 修改封航影响航次
    * @param tDisCloseSailShipDTO
    * @return
    */
   @Edit
   public int update(TDisCloseSailShipDTO tDisCloseSailShipDTO);


   /**
    * 根据id删除封航影响航次
    * @param id 主键
    * @return
    */
   public int deleteById(Long id);

  /**
   * 查询航次泊位信息
   * @param shipId
   * @return
   */
  public ShipVoyageDto selectBerth(Long shipId);
}

